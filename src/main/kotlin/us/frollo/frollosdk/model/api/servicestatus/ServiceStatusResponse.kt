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

package us.frollo.frollosdk.model.api.servicestatus

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceStatusType
import us.frollo.frollosdk.model.coredata.servicestatus.StatusOutageMessage

/** Data representation of Service status response */
data class ServiceStatusResponse(

    /** Status of the service */
    @SerializedName("status") val status: ServiceStatusType,

    /** Start date of the service status (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("start_date") val startDate: String?, // ISO8601 format Eg: 2011-12-03T10:15:30+01:00

    /** End date of the service status (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("end_date") val endDate: String?, // ISO8601 format Eg: 2011-12-03T10:15:30+01:00

    /** When service status was last updated (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("updated") val lastUpdated: String?, // ISO8601 format Eg: 2011-12-03T10:15:30+01:00

    /** Service status time in seconds (Optional) */
    @SerializedName("duration") val duration: Long?,

    /** Information message about service status */
    @SerializedName("message") val message: StatusOutageMessage
)
