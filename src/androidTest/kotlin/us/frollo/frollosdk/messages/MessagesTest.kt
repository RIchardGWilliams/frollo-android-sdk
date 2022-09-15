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

import com.jraska.livedata.test
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.base.PaginatedResult
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.model.coredata.messages.ContentType
import us.frollo.frollosdk.model.coredata.messages.MessageFilter
import us.frollo.frollosdk.model.coredata.messages.MessageHTML
import us.frollo.frollosdk.model.coredata.messages.MessageImage
import us.frollo.frollosdk.model.coredata.messages.MessageSortType
import us.frollo.frollosdk.model.coredata.messages.MessageText
import us.frollo.frollosdk.model.coredata.messages.MessageVideo
import us.frollo.frollosdk.model.coredata.shared.OrderType
import us.frollo.frollosdk.model.testMessageNotificationPayload
import us.frollo.frollosdk.model.testMessageResponseData
import us.frollo.frollosdk.network.api.MessagesAPI
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MessagesTest : BaseAndroidTest() {

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    @Test
    fun testFetchMessageByID() {
        initSetup()

        val data = testMessageResponseData()
        val list = mutableListOf(testMessageResponseData(), data, testMessageResponseData())
        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessage(data.messageId).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(data.messageId, testObserver.value().data?.messageId)

        tearDown()
    }

    @Test
    fun testFetchAllMessages() {
        initSetup()

        val data1 = testMessageResponseData(read = false)
        val data2 = testMessageResponseData(read = true)
        val data3 = testMessageResponseData(read = false)
        val data4 = testMessageResponseData(read = true)
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(4, testObserver.value()?.size)

        tearDown()
    }

    @Test
    fun testFetchUnreadMessages() {
        initSetup()

        val data1 = testMessageResponseData(read = false)
        val data2 = testMessageResponseData(read = true)
        val data3 = testMessageResponseData(read = false)
        val data4 = testMessageResponseData(read = true)
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages(messageFilter = MessageFilter(read = false)).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(2, testObserver.value()?.size)

        tearDown()
    }

    @Test
    fun testFetchMessagesByMessageType() {
        initSetup()

        val data1 = testMessageResponseData(types = mutableListOf("survey"))
        val data2 = testMessageResponseData(types = mutableListOf("event"))
        val data3 = testMessageResponseData(types = mutableListOf("survey", "welcome"))
        val data4 = testMessageResponseData(types = mutableListOf("dashboard_survey"))
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages(messageFilter = MessageFilter(messageTypes = listOf("survey"))).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(2, testObserver.value()?.size)

        tearDown()
    }

    @Test
    fun testFetchMessagesByContentType() {
        initSetup()

        val data1 = testMessageResponseData(type = ContentType.TEXT, read = true)
        val data2 = testMessageResponseData(type = ContentType.TEXT, read = false)
        val data3 = testMessageResponseData(type = ContentType.VIDEO, read = false)
        val data4 = testMessageResponseData(type = ContentType.TEXT, read = false)
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages(messageFilter = MessageFilter(read = false, contentTypes = listOf(ContentType.TEXT))).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(2, testObserver.value()?.size)

        tearDown()
    }

    @Test
    fun testFetchMessagesCount() {
        initSetup()

        val data1 = testMessageResponseData(read = false)
        val data2 = testMessageResponseData(read = true)
        val data3 = testMessageResponseData(read = false)
        val data4 = testMessageResponseData(read = true)
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        messages.fetchMessagesCount(messageFilter = MessageFilter(read = true)) { resource ->
            assertEquals(2L, resource.data)
        }

        tearDown()
    }

    @Test
    fun testRefreshMessagesWithPaginationOrderDesc() {
        initSetup()

        val requestPath1 = "${MessagesAPI.URL_MESSAGES}?message_types=bill_alerts%2Cdashboard_alerts&content_types=text&design_types=dash_1%2Cdash_2&sort=created_at&order=desc&size=25"
        val requestPath2 = "${MessagesAPI.URL_MESSAGES}?message_types=bill_alerts%2Cdashboard_alerts&content_types=text&design_types=dash_1%2Cdash_2&sort=created_at&order=desc&after=804460&size=25"
        val requestPath3 = "${MessagesAPI.URL_MESSAGES}?message_types=bill_alerts%2Cdashboard_alerts&content_types=text&design_types=dash_1%2Cdash_2&sort=created_at&order=desc&after=786537&size=25"

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == requestPath1) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.messages_desc_page1))
                    } else if (request.trimmedPath == requestPath2) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.messages_desc_page2))
                    } else if (request.trimmedPath == requestPath3) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.messages_desc_page3))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        // Insert some stale messages greater than first page first message's created_date
        val data1 = testMessageResponseData(msgId = 101, createdDate = "2022-09-01T12:29:35.103+10:00", types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1")

        // Insert some stale messages lesser than first page first message's created_date
        // and greater than first page last message's created_date
        val data2 = testMessageResponseData(msgId = 102, createdDate = "2022-08-23T10:29:35.103+10:00", types = listOf("dashboard_alerts"), type = ContentType.TEXT, designType = "dash_1")

        // Insert some stale messages between second page first message's created_date
        // and second page last message's created_date
        val data3 = testMessageResponseData(msgId = 103, createdDate = "2022-08-14T10:29:35.103+10:00", types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_2")

        // Insert some stale messages lesser than last page last message's created_date
        val data4 = testMessageResponseData(msgId = 104, createdDate = "2022-08-11T10:29:35.103+10:00", types = listOf("dashboard_alerts"), type = ContentType.TEXT, designType = "dash_2")

        // Insert some stale messages between first page first message's created_date and
        // and first page last message's created_date but different message type
        // and ensure that this message is not deleted after refresh
        val data5 = testMessageResponseData(msgId = 105, createdDate = "2022-08-23T10:29:35.103+10:00", types = listOf("message_task"), type = ContentType.TEXT, designType = "dash_1")

        val list = mutableListOf(data1, data2, data3, data4, data5)
        database.messages().insertAll(*list.toTypedArray())

        val messageFilter = MessageFilter(
            messageTypes = listOf("bill_alerts", "dashboard_alerts"),
            contentTypes = listOf(ContentType.TEXT),
            designTypes = listOf("dash_1", "dash_2"),
            size = 25,
            sortBy = MessageSortType.CREATED_AT,
            orderBy = OrderType.DESC
        )
        messages.refreshMessagesWithPagination(messageFilter) { result1 ->
            assertTrue(result1 is PaginatedResult.Success)
            assertNull((result1 as PaginatedResult.Success).paginationInfo?.before)
            assertEquals(804460L, result1.paginationInfo?.after)

            messageFilter.after = result1.paginationInfo?.after?.toString()
            messages.refreshMessagesWithPagination(messageFilter) { result2 ->
                assertTrue(result2 is PaginatedResult.Success)
                assertEquals(804459L, (result2 as PaginatedResult.Success).paginationInfo?.before)
                assertEquals(786537L, result2.paginationInfo?.after)

                messageFilter.after = result2.paginationInfo?.after?.toString()
                messages.refreshMessagesWithPagination(messageFilter) { result3 ->
                    assertTrue(result3 is PaginatedResult.Success)
                    assertEquals(786536L, (result3 as PaginatedResult.Success).paginationInfo?.before)
                    assertNull(result3.paginationInfo?.after)

                    // Fetch all messages in DB
                    val testObserver = messages.fetchMessages(MessageFilter()).test()
                    testObserver.awaitValue()
                    val models = testObserver.value()
                    assertNotNull(models)
                    assertEquals(52, models?.size)

                    // Verify that the stale messages are deleted from the database
                    assertEquals(0, models?.filter { it.messageId in listOf(101L, 102L, 103L, 104L) }?.size)

                    // Verify that the message_id = 105 in not deleted from the database
                    assertEquals(1, models?.filter { it.messageId == 105L }?.size)

                    signal.countDown()
                }
            }
        }

        signal.await(120, TimeUnit.SECONDS)

        assertEquals(3, mockServer.requestCount)

        tearDown()
    }

    @Test
    fun testRefreshMessagesWithPaginationOrderAsc() {
        initSetup()

        val requestPath1 = "${MessagesAPI.URL_MESSAGES}?message_types=bill_alerts&content_types=text&design_types=dash_1&sort=created_at&order=asc&size=25"
        val requestPath2 = "${MessagesAPI.URL_MESSAGES}?message_types=bill_alerts&content_types=text&design_types=dash_1&sort=created_at&order=asc&after=644589&size=25"

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == requestPath1) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.messages_asc_page1))
                    } else if (request.trimmedPath == requestPath2) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.messages_asc_page2))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        // Insert some stale messages lesser than first page first message's created_date
        val data1 = testMessageResponseData(msgId = 101, createdDate = "2022-04-05T12:29:35.103+10:00", types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1")

        // Insert some stale messages greater than first page first message's created_date
        // and lesser than first page last message's created_date
        val data2 = testMessageResponseData(msgId = 102, createdDate = "2022-04-18T10:29:35.103+10:00", types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1")

        // Insert some stale messages between second page first message's created_date
        // and second page last message's created_date
        val data3 = testMessageResponseData(msgId = 103, createdDate = "2022-05-25T10:29:35.103+10:00", types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1")

        // Insert some stale messages greater than last page last message's created_date
        val data4 = testMessageResponseData(msgId = 104, createdDate = "2022-05-27T10:29:35.103+10:00", types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1")

        // Insert some stale messages between first page first message's created_date and
        // and first page last message's created_date but different message type
        // and ensure that this message is not deleted after refresh
        val data5 = testMessageResponseData(msgId = 105, createdDate = "2022-04-11T10:29:35.103+10:00", types = listOf("message_task"), type = ContentType.TEXT, designType = "dash_1")

        val list = mutableListOf(data1, data2, data3, data4, data5)
        database.messages().insertAll(*list.toTypedArray())

        val messageFilter = MessageFilter(
            messageTypes = listOf("bill_alerts"),
            contentTypes = listOf(ContentType.TEXT),
            designTypes = listOf("dash_1"),
            size = 25,
            sortBy = MessageSortType.CREATED_AT,
            orderBy = OrderType.ASC
        )
        messages.refreshMessagesWithPagination(messageFilter) { result1 ->
            assertTrue(result1 is PaginatedResult.Success)
            assertNull((result1 as PaginatedResult.Success).paginationInfo?.before)
            assertEquals(644589L, result1.paginationInfo?.after)

            messageFilter.after = result1.paginationInfo?.after?.toString()
            messages.refreshMessagesWithPagination(messageFilter) { result2 ->
                assertTrue(result2 is PaginatedResult.Success)
                assertEquals(644590L, (result2 as PaginatedResult.Success).paginationInfo?.before)
                assertNull(result2.paginationInfo?.after)

                // Fetch all messages in DB
                val testObserver = messages.fetchMessages(MessageFilter()).test()
                testObserver.awaitValue()
                val models = testObserver.value()
                assertNotNull(models)
                assertEquals(50, models?.size)

                // Verify that the stale messages are deleted from the database
                assertEquals(0, models?.filter { it.messageId in listOf(101L, 102L, 103L, 104L) }?.size)

                // Verify that the message_id = 105 in not deleted from the database
                assertEquals(1, models?.filter { it.messageId == 105L }?.size)

                signal.countDown()
            }
        }

        signal.await(3, TimeUnit.SECONDS)

        assertEquals(2, mockServer.requestCount)

        tearDown()
    }

    @Test
    fun testRefreshMessagesFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        messages.refreshMessagesWithPagination(MessageFilter()) { result ->
            assertTrue(result is PaginatedResult.Error)
            assertNotNull((result as PaginatedResult.Error).error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshMessageByID() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.message_id_12345)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "messages/12345") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        messages.refreshMessage(12345L) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = messages.fetchMessage(12345L).test()
            testObserver.awaitValue()
            val model = testObserver.value().data
            assertNotNull(model)
            assertEquals(12345L, model?.messageId)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("messages/12345", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshMessageByIDFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        messages.refreshMessage(12345L) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshUnreadMessages() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.messages_unread)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == MessagesAPI.URL_UNREAD) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        messages.refreshUnreadMessages { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = messages.fetchMessages(messageFilter = MessageFilter(read = false)).test()
            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)
            assertEquals(7, models?.size)
            models?.forEach { message ->
                when (message.contentType) {
                    ContentType.HTML -> assertTrue(message is MessageHTML)
                    ContentType.VIDEO -> assertTrue(message is MessageVideo)
                    ContentType.IMAGE -> assertTrue(message is MessageImage)
                    ContentType.TEXT -> assertTrue(message is MessageText)
                }
            }
            val metadata = models?.last()?.metadata
            assertEquals("holiday", metadata?.get("category")?.asString)
            assertEquals(true, metadata?.get("subcategory")?.asBoolean)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(MessagesAPI.URL_UNREAD, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshUnreadMessagesFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        messages.refreshUnreadMessages { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateMessage() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.message_id_12345)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "messages/12345") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        messages.updateMessage(12345L, true, true) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = messages.fetchMessage(12345L).test()
            testObserver.awaitValue()
            val model = testObserver.value().data
            assertNotNull(model)
            assertEquals(12345L, model?.messageId)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("messages/12345", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateMessageFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        messages.updateMessage(12345L, true, true) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateMessagesInBulk() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.messages_update_in_bulk)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == MessagesAPI.URL_MESSAGES_BULK) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        val data1 = testMessageResponseData(msgId = 606522, types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1", read = false)
        val data2 = testMessageResponseData(msgId = 606525, types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1", read = false)
        val data3 = testMessageResponseData(msgId = 606531, types = listOf("message_task"), type = ContentType.TEXT, designType = "dash_1", read = false)
        val data4 = testMessageResponseData(msgId = 104, types = listOf("bill_alerts"), type = ContentType.TEXT, designType = "dash_1", read = false)
        val data5 = testMessageResponseData(msgId = 105, types = listOf("message_task"), type = ContentType.TEXT, designType = "dash_1", read = false)
        val list = mutableListOf(data1, data2, data3, data4, data5)
        database.messages().insertAll(*list.toTypedArray())

        messages.updateMessagesInBulk(listOf(606522, 606525, 606531), true) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = messages.fetchMessages(messageFilter = MessageFilter()).test()
            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)

            assertEquals(5, models?.size)

            assertEquals(
                3,
                models?.filter {
                    it.messageId in listOf(606522L, 606525L, 606531L)
                }?.filter {
                    it.read
                }?.size
            )

            assertEquals(
                2,
                models?.filter {
                    it.messageId in listOf(104L, 105L)
                }?.filter {
                    !it.read
                }?.size
            )

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(MessagesAPI.URL_MESSAGES_BULK, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testMarkMessagesAsRead() {
        initSetup()

        // TODO

        tearDown()
    }

    @Test
    fun testHandlePushMessage() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.message_id_12345)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "messages/12345") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        messages.handleMessageNotification(testMessageNotificationPayload()) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = messages.fetchMessage(12345L).test()
            testObserver.awaitValue()
            val model = testObserver.value().data
            assertNotNull(model)
            assertEquals(12345L, model?.messageId)
        }

        val request = mockServer.takeRequest()
        assertEquals("messages/12345", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }
}
