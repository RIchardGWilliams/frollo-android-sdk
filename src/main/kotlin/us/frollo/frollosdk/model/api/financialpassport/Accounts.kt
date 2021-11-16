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
package us.frollo.frollosdk.model.api.financialpassport
import com.google.gson.annotations.SerializedName

data class Accounts(

    @SerializedName("account_id") val account_id: Int,
    @SerializedName("account_name") val account_name: String,
    @SerializedName("provider") val provider: String,
    @SerializedName("holder_name") val holder_name: String,
    @SerializedName("asset_name") val asset_name: String,
    @SerializedName("description") val description: String,
    @SerializedName("opening_balance") val opening_balance: Double,
    @SerializedName("closing_balance") val closing_balance: Double
)
