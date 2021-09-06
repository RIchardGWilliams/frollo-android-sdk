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

import us.frollo.frollosdk.model.api.servicestatus.ServiceOutageResponse
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutageType
import us.frollo.frollosdk.model.coredata.servicestatus.StatusOutageMessage
import us.frollo.frollosdk.testutils.randomElement
import us.frollo.frollosdk.testutils.randomNumber
import us.frollo.frollosdk.testutils.randomString

internal fun testServiceOutageResponseData(
    type: ServiceOutageType? = null,
    startDate: String? = null,
    endDate: String? = null,
    message: StatusOutageMessage? = null
): ServiceOutageResponse {
    return ServiceOutageResponse(
        type = type ?: ServiceOutageType.values().randomElement(),
        startDate = startDate ?: "2011-12-03T10:15:30+01:00",
        endDate = endDate ?: "2011-12-04T10:15:30+01:00",
        duration = randomNumber().toLong(),
        message = message ?: StatusOutageMessage(
            title = randomString(20),
            summary = randomString(20),
            description = randomString(20),
            actionName = randomString(20),
            actionUrl = randomString(20)
        )
    )
}
