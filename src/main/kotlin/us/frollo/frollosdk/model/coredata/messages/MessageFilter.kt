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

package us.frollo.frollosdk.model.coredata.messages

import us.frollo.frollosdk.model.coredata.shared.OrderType

/**
 * Represents a model that contains all the filters to apply on list of messages
 *
 * @param messageTypes Filter messages by list of message types (Optional)
 * @param contentTypes Filter messages by list of [ContentType] (Optional)
 * @param designTypes Filter messages by list of design types (Optional)
 * @param event Filter messages by triggering event name (Optional)
 * @param read Filter the messages by read state (Optional)
 * @param interacted Filter the messages by interacted state (Optional)
 * @param sortBy Sort the messages by [MessageSortType]. Default is [MessageSortType.CREATED_AT]
 * @param orderBy Order the messages by [OrderType]. Default is [OrderType.DESC]
 * @param after after field to get next list in pagination. Format is "<message_id>"
 * @param before before field to get previous list in pagination. Format is "<message_id>"
 * @param size Count of objects to returned from the API (page size)
 **/
data class MessageFilter(
    var messageTypes: List<String>? = null,
    var contentTypes: List<ContentType>? = null,
    var designTypes: List<String>? = null,
    var event: String? = null,
    var read: Boolean? = null,
    var interacted: Boolean? = null,
    var sortBy: MessageSortType = MessageSortType.CREATED_AT,
    var orderBy: OrderType = OrderType.DESC,
    var after: String? = null,
    var before: String? = null,
    var size: Long? = null
) {

    /**
     * Convert [MessageFilter] to query map
     */
    fun getQueryMap(): Map<String, String> {
        val queryMap = mutableMapOf<String, String>()
        messageTypes?.let { queryMap["message_types"] = it.joinToString(",") }
        contentTypes?.let {
            queryMap["content_types"] = it.joinToString(",") { type ->
                type.toString()
            }
        }
        designTypes?.let { queryMap["design_types"] = it.joinToString(",") }
        event?.let { queryMap["event"] = it }
        read?.let { queryMap["read"] = it.toString() }
        interacted?.let { queryMap["interacted"] = it.toString() }
        queryMap["sort"] = sortBy.toString()
        queryMap["order"] = orderBy.toString()
        after?.let { if (it.isNotBlank()) queryMap["after"] = it }
        before?.let { if (it.isNotBlank()) queryMap["before"] = it }
        size?.let { queryMap["size"] = it.toString() }
        return queryMap
    }
}
