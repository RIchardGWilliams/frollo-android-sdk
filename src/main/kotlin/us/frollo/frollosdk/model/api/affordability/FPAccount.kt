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
package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountOwnerType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountSubType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountType
import java.math.BigDecimal

/** Data representation of the Accounts in the Financial Passport */
data class FPAccount(

    /** The account ID of the Account (Optional) */
    @SerializedName("account_id") val accountId: Long?,
    /** The name of the Account (Optional) */
    @SerializedName("account_name") val accountName: String?,
    /** The asset name of the Account (Optional) */
    @SerializedName("name") val assetName: String?,
    /** The bsb of the Account (Optional) */
    @SerializedName("bsb") val bsb: String?,
    /** The number of the Account (Optional) */
    @SerializedName("account_number") val number: String?,
    /** The provider ID of the Account (Optional) */
    @SerializedName("provider_id") val providerId: String?,
    /** The provider name of the Account (Optional) */
    @SerializedName("provider_name") val providerName: String?,
    /** The holder of the Account (Optional) */
    @SerializedName("holder_name") val holderName: String?,
    /** The description of the Account (Optional) */
    @SerializedName("description") val description: String?,
    /** The available balance of the Account (Optional) */
    @SerializedName("available_balance") val availableBalance: BigDecimal?,
    /** The current balance of the Account (Optional) */
    @SerializedName("current_balance") val currentBalance: BigDecimal?,
    /** Account Type */
    @SerializedName("container") val accountType: AccountType?,
    /** Indicates if this is a joint account (Optional) */
    @SerializedName("joint_account") val jointAccount: Boolean?,
    /** Account owner type (Optional) */
    @SerializedName("owner_type") val ownerType: AccountOwnerType?,
    /** Account Sub Type */
    @SerializedName("type") val accountSubType: AccountSubType
)
