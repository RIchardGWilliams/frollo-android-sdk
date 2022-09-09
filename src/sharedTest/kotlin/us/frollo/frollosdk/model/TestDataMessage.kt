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

package us.frollo.frollosdk.model

import us.frollo.frollosdk.model.api.messages.MessageContent
import us.frollo.frollosdk.model.api.messages.MessageResponse
import us.frollo.frollosdk.model.coredata.messages.Action
import us.frollo.frollosdk.model.coredata.messages.ContentType
import us.frollo.frollosdk.model.coredata.messages.OpenMode
import us.frollo.frollosdk.testutils.randomBoolean
import us.frollo.frollosdk.testutils.randomNumber
import us.frollo.frollosdk.testutils.randomString
import kotlin.random.Random

internal fun testMessageResponseData(
    type: ContentType? = null,
    types: List<String>? = null,
    read: Boolean? = null,
    msgId: Long? = null,
    createdDate: String? = null,
    deliveredDate: String? = null,
    interactedDate: String? = null
): MessageResponse {
    val htmlContent = MessageContent(
        footer = randomString(20),
        header = randomString(20),
        main = "<html></html>"
    )
    val imageContent = MessageContent(
        height = randomNumber(1..1000).toDouble(),
        url = "https://example.com/image.png",
        width = randomNumber(1..1000).toDouble()
    )
    val textContent = MessageContent(
        designType = "information",
        footer = randomString(20),
        header = randomString(20),
        imageUrl = "https://example.com/image.png",
        text = randomString(200)
    )
    val videoContent = MessageContent(
        autoplay = randomBoolean(),
        autoplayCellular = randomBoolean(),
        height = randomNumber(1..1000).toDouble(),
        iconUrl = "https://example.com/image.png",
        muted = randomBoolean(),
        url = "https://example.com/video.mp4",
        width = randomNumber(1..1000).toDouble()
    )

    val contentType = type?.let { it } ?: ContentType.values()[Random.nextInt(ContentType.values().size)]
    val content = when (contentType) {
        ContentType.TEXT -> textContent
        ContentType.IMAGE -> imageContent
        ContentType.VIDEO -> videoContent
        ContentType.HTML -> htmlContent
    }

    return MessageResponse(
        messageId = msgId ?: randomNumber().toLong(),
        action = Action(link = "frollo://dashboard", openMode = OpenMode.INTERNAL, title = randomString(30)),
        contentType = contentType,
        content = content,
        event = randomString(30),
        interacted = randomBoolean(),
        messageTypes = types ?: mutableListOf("home_nudge"),
        persists = randomBoolean(),
        placement = randomNumber(1..1000).toLong(),
        autoDismiss = randomBoolean(),
        read = read ?: randomBoolean(),
        title = randomString(100),
        userEventId = randomNumber(1..100000).toLong(),
        metadata = null,
        createdDate = createdDate ?: "2011-12-04T10:15:30+01:00",
        deliveredDate = deliveredDate ?: "2011-12-04T10:15:30+01:00",
        interactedDate = interactedDate ?: "2011-12-04T10:15:30+01:00"
    )
}

internal fun MessageResponse.testModifyUserResponseData(newTitle: String? = null, types: List<String>? = null, messageContent: MessageContent? = null): MessageResponse {
    return MessageResponse(
        messageId = messageId,
        action = action,
        contentType = contentType,
        content = messageContent ?: content,
        event = event,
        interacted = interacted,
        messageTypes = types ?: messageTypes,
        persists = persists,
        placement = placement,
        autoDismiss = randomBoolean(),
        read = read,
        title = newTitle ?: title,
        userEventId = userEventId,
        metadata = null,
        createdDate = "2011-12-04T10:15:30+01:00",
        deliveredDate = "2011-12-04T10:15:30+01:00",
        interactedDate = "2011-12-04T10:15:30+01:00"
    )
}
