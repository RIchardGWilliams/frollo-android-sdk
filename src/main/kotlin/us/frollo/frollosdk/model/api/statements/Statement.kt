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

package us.frollo.frollosdk.model.api.statements

import com.google.gson.annotations.SerializedName

data class Statement(
    @SerializedName("id") val id: Long,
    @SerializedName("account_id") val accountId: Long,
    @SerializedName("reference_id") val referenceId: String,
    @SerializedName("type") val type: StatementType,
    @SerializedName("start_date") val startDate: String, // Eg: 2021-01-01
    @SerializedName("end_date") val endDate: String?, // Eg: 2021-01-01
    @SerializedName("issued_date") val issuedDate: String? // 2021-01-01
)
