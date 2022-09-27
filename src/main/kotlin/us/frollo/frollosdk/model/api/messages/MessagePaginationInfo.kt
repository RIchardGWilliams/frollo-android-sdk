/*
 * Copyright 2020 Frollo
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

package us.frollo.frollosdk.model.api.messages

/**
 * Result with information of contact pagination
 *
 * @param before Before cursor (ID of data) for pagination (Optional)
 * @param after After cursor (ID of data) for pagination (Optional)
 * @param total Total count (Optional)
 * @param afterCreatedDate Created date of the last message in the page (Optional). Date format for this field is ISO8601. Ex: 2011-12-03T10:15:30+01:00
 */
data class MessagePaginationInfo(
    val before: Long? = null,
    val after: Long? = null,
    val total: Long? = null,
    val afterCreatedDate: String? = null,
)
