/*
 * Copyright 2019 Frollo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.frollo.frollosdk.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.sqlite.db.SimpleSQLiteQuery
import us.frollo.frollosdk.base.PaginatedResult
import us.frollo.frollosdk.base.PaginationInfo
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.base.doAsync
import us.frollo.frollosdk.base.uiThread
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.extensions.fetchMessages
import us.frollo.frollosdk.extensions.sqlForMessageIdsToGetStaleIds
import us.frollo.frollosdk.extensions.sqlForMessages
import us.frollo.frollosdk.extensions.sqlForMessagesCount
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.mapping.toMessage
import us.frollo.frollosdk.model.api.messages.MessageBulkUpdateRequest
import us.frollo.frollosdk.model.api.messages.MessageResponse
import us.frollo.frollosdk.model.api.messages.MessageUpdateRequest
import us.frollo.frollosdk.model.api.shared.PaginatedResponse
import us.frollo.frollosdk.model.coredata.messages.Message
import us.frollo.frollosdk.model.coredata.messages.MessageFilter
import us.frollo.frollosdk.model.coredata.notifications.NotificationPayload
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.MessagesAPI

/**
 * Manages caching and refreshing of messages
 */
class Messages(network: NetworkService, internal val db: SDKDatabase) {

    companion object {
        private const val TAG = "Messages"
        private const val MAX_MESSAGES_UPDATE_COUNT = 200
    }

    private val messagesAPI: MessagesAPI = network.create(MessagesAPI::class.java)

    /**
     * Fetch message by ID from the cache
     *
     * @param messageId Unique message ID to fetch
     *
     * @return LiveData object of Resource<Message> which can be observed using an Observer for future changes as well.
     */
    fun fetchMessage(messageId: Long): LiveData<Resource<Message>> =
        Transformations.map(db.messages().load(messageId)) { response ->
            Resource.success(response?.toMessage())
        }

    /**
     * Fetch messages from the cache with filters
     *
     * Fetches all messages if no params are passed.
     *
     * @param messageFilter [MessageFilter] object to apply filters
     *
     * @return LiveData object of List<Message> which can be observed using an Observer for future changes as well.
     */
    fun fetchMessages(messageFilter: MessageFilter = MessageFilter()): LiveData<List<Message>> =
        Transformations.map(db.messages().loadByQuery(sqlForMessages(messageFilter))) { models ->
            mapMessageResponse(models)
        }

    /**
     * Advanced method to fetch messages by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches messages from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of Resource<List<Message>> which can be observed using an Observer for future changes as well.
     */
    fun fetchMessages(query: SimpleSQLiteQuery): LiveData<Resource<List<Message>>> =
        Transformations.map(db.messages().loadByQuery(query)) { response ->
            Resource.success(mapMessageResponse(response))
        }

    /**
     * Fetch messages count from the cache
     *
     * @param messageFilter [MessageFilter] object to apply filters
     * @param completion Completion handler with optional error if the request fails or the messages count if success
     */
    fun fetchMessagesCount(
        messageFilter: MessageFilter = MessageFilter(),
        completion: OnFrolloSDKCompletionListener<Resource<Long>>
    ) {
        doAsync {
            val count = db.messages().loadMessageCount(sqlForMessagesCount(messageFilter))
            uiThread { completion.invoke(Resource.success(count)) }
        }
    }

    /**
     * Refresh a specific message by ID from the host
     *
     * @param messageId ID of the message to fetch
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshMessage(messageId: Long, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        messagesAPI.fetchMessage(messageId).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    handleMessageResponse(response = resource.data, completion = completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshMessage", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }

    /**
     * Refresh all available messages from the host.
     *
     * @param messageFilter messageFilter to filter messages
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshMessagesWithPagination(
        messageFilter: MessageFilter,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>? = null
    ) {
        messagesAPI.fetchMessages(messageFilter).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    handleMessagesResponseWithPaginationResponse(resource.data, messageFilter, completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshMessagesWithPagination", resource.error?.localizedDescription)
                    completion?.invoke(PaginatedResult.Error(resource.error))
                }
            }
        }
    }

    /**
     * Refresh all unread messages from the host.
     *
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshUnreadMessages(completion: OnFrolloSDKCompletionListener<Result>? = null) {
        messagesAPI.fetchUnreadMessages().enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    handleMessagesResponse(response = resource.data, unread = true, completion = completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshUnreadMessages", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }

    /**
     * Update a message on the host
     *
     * @param messageId ID of the message to be updated
     * @param read Mark message read/unread
     * @param interacted Mark message interacted or not
     * @param messageId ID of the message to be updated
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun updateMessage(messageId: Long, read: Boolean, interacted: Boolean, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        messagesAPI.updateMessage(messageId, MessageUpdateRequest(read, interacted)).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    handleMessageResponse(response = resource.data, completion = completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#updateMessage", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }

    /**
     * Update messages in bulk on the host
     *
     * @param messageIds List of IDs of the messages to be updated
     * @param read Mark messages read/unread (Optional)
     * @param interacted Mark messages interacted or not (Optional)
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun updateMessagesInBulk(
        messageIds: List<Long>,
        read: Boolean? = null,
        interacted: Boolean? = null,
        completion: OnFrolloSDKCompletionListener<Result>? = null
    ) {
        if (messageIds.isEmpty() || messageIds.size > MAX_MESSAGES_UPDATE_COUNT) {
            completion?.invoke(Result.error(DataError(DataErrorType.API, DataErrorSubType.INVALID_DATA)))
        }
        val request = messageIds.map {
            MessageBulkUpdateRequest(
                messageId = it,
                read = read,
                interacted = interacted
            )
        }
        messagesAPI.updateMessagesInBulk(request).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    handleMessagesResponse(response = resource.data, completion = completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#updateMessagesInBulk", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }

    /**
     * Mark all unread messages for specified message types as read.
     * This method to loops through pages of unread messages
     * and mark them as read using Bulk Update API in batches of [MAX_MESSAGES_UPDATE_COUNT]
     *
     * @param messageTypes List of message types of the messages to be updated
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun markMessagesAsRead(
        messageTypes: List<String>,
        completion: OnFrolloSDKCompletionListener<Result>? = null
    ) {
        if (messageTypes.isEmpty()) {
            completion?.invoke(Result.error(DataError(DataErrorType.API, DataErrorSubType.INVALID_DATA)))
            return
        }
        fetchAllUnreadMessageIds(messageTypes) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    resource.data?.let { messageIds ->
                        updateMessagesAsReadChunked(
                            messageIds = messageIds,
                            completion = completion
                        )
                    } ?: run {
                        completion?.invoke(Result.success()) // Explicitly invoke completion callback if response is null.
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#markMessagesAsRead", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }

    private fun fetchAllUnreadMessageIds(
        messageTypes: List<String>,
        completion: OnFrolloSDKCompletionListener<Resource<List<Long>>>
    ) {
        fetchNextUnreadMessageIds(messageTypes = messageTypes, completion = completion)
    }

    private fun fetchNextUnreadMessageIds(
        messageTypes: List<String>,
        after: String? = null,
        combinedMessageIds: List<Long> = listOf(),
        completion: OnFrolloSDKCompletionListener<Resource<List<Long>>>
    ) {
        messagesAPI.fetchMessages(
            messageFilter = MessageFilter(messageTypes = messageTypes, read = false, after = after)
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    resource.data?.let { response ->
                        val messageIds = combinedMessageIds.plus(
                            response.data.map { it.messageId }
                        )
                        if (response.paging.cursors?.after == null) {
                            completion.invoke(Resource.success(messageIds))
                        } else {
                            fetchNextUnreadMessageIds(
                                messageTypes = messageTypes,
                                after = response.paging.cursors.after,
                                combinedMessageIds = messageIds,
                                completion = completion
                            )
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchNextUnreadMessageIds", resource.error?.localizedDescription)
                    completion.invoke(Resource.error(resource.error))
                }
            }
        }
    }

    private fun updateMessagesAsReadChunked(
        messageIds: List<Long>,
        completion: OnFrolloSDKCompletionListener<Result>? = null
    ) {
        val chunked = messageIds.chunked(MAX_MESSAGES_UPDATE_COUNT)
        var apiErrorOnce = false
        chunked.forEachIndexed { index, ids ->
            if (ids.isNotEmpty()) {
                updateMessagesInBulk(
                    messageIds = ids,
                    read = true
                ) { result ->
                    when (result.status) {
                        Result.Status.SUCCESS -> {
                            if (index == chunked.lastIndex) {
                                completion?.invoke(result)
                            }
                        }
                        Result.Status.ERROR -> {
                            // Do not set error state again if its already in error state.
                            // This is needed to avoid multiple error popups as we are executing multiple APIs calls in parallel
                            if (!apiErrorOnce) {
                                apiErrorOnce = true
                                completion?.invoke(result)
                            }
                            return@updateMessagesInBulk // Break the loop if one of the call errors out
                        }
                    }
                }
            }
        }
    }

    internal fun handleMessageNotification(notification: NotificationPayload, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        if (notification.userMessageID == null)
            return

        updateMessage(notification.userMessageID, read = true, interacted = true, completion = completion)
    }

    private fun handleMessagesResponse(response: List<MessageResponse>?, unread: Boolean = false, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        response?.let {
            doAsync {
                db.messages().insertAll(*response.toTypedArray())

                val apiIds = response.map { it.messageId }.toList()
                val staleIds = if (unread) {
                    db.messages().getUnreadStaleIds(apiIds.toLongArray())
                } else {
                    db.messages().getStaleIds(apiIds.toLongArray())
                }

                if (staleIds.isNotEmpty()) {
                    db.messages().deleteMany(staleIds.toLongArray())
                }

                uiThread { completion?.invoke(Result.success()) }
            }
        } ?: run { completion?.invoke(Result.success()) } // Explicitly invoke completion callback if response is null.
    }

    private fun handleMessagesResponseWithPaginationResponse(
        paginatedResponse: PaginatedResponse<MessageResponse>?,
        messageFilter: MessageFilter,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>?
    ) {
        paginatedResponse?.data?.let { messages ->
            if (messages.isEmpty()) {
                completion?.invoke(PaginatedResult.Success())
                return
            }
            doAsync {
                val firstMessageInPage = messages.first()
                val lastMessageInPage = messages.last()

                // Insert all messages from API response
                db.messages().insertAll(*messages.toTypedArray())

                // Fetch IDs from API response
                val apiIds = messages.map { it.messageId }.toHashSet()

                // Get IDs from database
                val messagesIds = db.messages().getIdsByQuery(
                    sqlForMessageIdsToGetStaleIds(
                        messageFilter = messageFilter,
                        firstMessageInPage = firstMessageInPage,
                        lastMessageInPage = lastMessageInPage,
                        after = paginatedResponse.paging.cursors?.after?.toLong(),
                        before = paginatedResponse.paging.cursors?.before?.toLong()
                    )
                ).toHashSet()

                // Get stale IDs that are not present in the API response
                val staleIds = messagesIds.minus(apiIds)

                // Delete the entries for these stale IDs from database if they exist
                if (staleIds.isNotEmpty()) {
                    db.messages().deleteMany(staleIds.toLongArray())
                }

                uiThread {
                    val paginationInfo = PaginationInfo(
                        before = paginatedResponse.paging.cursors?.before?.toLong(),
                        after = paginatedResponse.paging.cursors?.after?.toLong(),
                        total = paginatedResponse.paging.total
                    )
                    completion?.invoke(PaginatedResult.Success(paginationInfo))
                }
            }
        } ?: run { completion?.invoke(PaginatedResult.Success()) } // Explicitly invoke completion callback if response is null.
    }

    private fun handleMessageResponse(response: MessageResponse?, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        response?.let {
            doAsync {
                db.messages().insert(response)

                uiThread { completion?.invoke(Result.success()) }
            }
        } ?: run { completion?.invoke(Result.success()) } // Explicitly invoke completion callback if response is null.
    }

    private fun mapMessageResponse(models: List<MessageResponse>): List<Message> =
        models.mapNotNull { it.toMessage() }.toList()
}
