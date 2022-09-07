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

package us.frollo.frollosdk.model.api.cdr

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.cdr.CDRModel
import us.frollo.frollosdk.model.coredata.cdr.CDRParty
import us.frollo.frollosdk.model.coredata.cdr.CDRPermission
import us.frollo.frollosdk.model.coredata.cdr.SharingDuration

internal data class CDRConfigurationResponse(
    @SerializedName("id") val configId: Long,
    @SerializedName("adr_id") val adrId: String,
    @SerializedName("adr_name") val adrName: String,
    @SerializedName("support_email") val supportEmail: String,
    @SerializedName("sharing_durations") val sharingDurations: List<SharingDuration>,
    @SerializedName("permissions") val permissions: List<CDRPermission>?,
    @SerializedName("external_id") val externalId: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("cdr_policy_url") val cdrPolicyUrl: String,
    @SerializedName("model") val model: CDRModel,
    @SerializedName("related_parties") val relatedParties: List<CDRParty>,
    @SerializedName("sharing_use_duration") val sharingUseDuration: Long,
    @SerializedName("initial_sync_window_weeks") val initialSyncWindowWeeks: Int?,
)
