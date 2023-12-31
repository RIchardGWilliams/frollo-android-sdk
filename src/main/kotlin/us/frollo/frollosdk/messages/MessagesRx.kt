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

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.extensions.sqlForMessages
import us.frollo.frollosdk.mapping.toMessage
import us.frollo.frollosdk.model.coredata.messages.Message
import us.frollo.frollosdk.model.coredata.messages.MessageFilter

/**
 * Fetch message by ID from the cache
 *
 * @param messageId Unique message ID to fetch
 *
 * @return Rx Observable object of Message which can be observed using an Observer for future changes as well.
 */
fun Messages.fetchMessageRx(messageId: Long): Observable<Message?> {
    return db.messages().loadRx(messageId).map { it.toMessage() }
}

/**
 * Advanced method to fetch messages by SQL query from the cache
 *
 * @param query SimpleSQLiteQuery: Select query which fetches messages from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<Message> which can be observed using an Observer for future changes as well.
 */
fun Messages.fetchMessagesRx(query: SimpleSQLiteQuery): Observable<List<Message>> {
    return db.messages().loadByQueryRx(query).map { list ->
        list.mapNotNull {
            it.toMessage()
        }
    }
}

/**
 * Fetch messages from the cache with filters
 *
 * Fetches all messages if no params are passed.
 *
 * @param messageFilter [MessageFilter] object to apply filters
 *
 * @return Rx Observable object of List<Message> which can be observed using an Observer for future changes as well.
 */
fun Messages.fetchMessagesRx(messageFilter: MessageFilter = MessageFilter()): Observable<List<Message>> {
    return db.messages().loadByQueryRx(sqlForMessages(messageFilter)).map { list ->
        list.mapNotNull {
            it.toMessage()
        }
    }
}
