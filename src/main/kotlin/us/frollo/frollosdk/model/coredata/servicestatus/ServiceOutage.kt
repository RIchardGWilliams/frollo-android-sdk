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

package us.frollo.frollosdk.model.coredata.servicestatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import us.frollo.frollosdk.model.IAdapterModel

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "service_outage",
)

/** Data representation of a Service Outage */
data class ServiceOutage(

    /** Type of the outage */
    @ColumnInfo(name = "type") var type: ServiceOutageType,

    /** Start date of the outage
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "start_date") var startDate: String,

    /** End date of the outage
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "end_date") var endDate: String,

    /** Outage time in seconds (Optional) */
    @ColumnInfo(name = "duration") var duration: Long,

    /** Information message about outage */
    @Embedded(prefix = "message_") var message: StatusOutageMessage

) : IAdapterModel {

    /** Unique ID of the outage */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "outage_id") var outageId: Long = 0

    /** Indicates if the outage message is read or not */
    @ColumnInfo(name = "read") var read: Boolean = false
}
