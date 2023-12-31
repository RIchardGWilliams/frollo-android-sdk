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
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.IAdapterModel

/** Data representation of Service status or outage message */
data class StatusOutageMessage(

    /** Message title */
    @SerializedName("title") @ColumnInfo(name = "title") val title: String,

    /** Message summary */
    @SerializedName("summary") @ColumnInfo(name = "summary") val summary: String,

    /** Message description */
    @SerializedName("description") @ColumnInfo(name = "description") val description: String,

    /** Message action name (e.g. Copy for buttons) */
    @SerializedName("action") @ColumnInfo(name = "action") val actionName: String,

    /** Message action URL (Optional) */
    @SerializedName("url") @ColumnInfo(name = "url") val actionUrl: String?

) : IAdapterModel
