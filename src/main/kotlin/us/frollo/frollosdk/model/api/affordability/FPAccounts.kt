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
import java.math.BigDecimal

/** Data representation of the Accounts in the Financial Passport */
data class FPAccounts(

    /**  The account ID of the Account.  */
    @SerializedName("account_id") val accountId: Long?,
    /**  The name of the Account. */
    @SerializedName("account_name") val accountName: String?,
    /**  The bsb of the Account. */
    @SerializedName("bsb") val bsb: String?,
    /**   The provider of the Account. */
    @SerializedName("provider") val provider: String?,
    /**   The holder of the Account. */
    @SerializedName("holder_name") val holderName: String?,
    /**  The asset of the Account. */
    @SerializedName("asset_name") val assetName: String?,
    /**   The description of the Account.  */
    @SerializedName("description") val description: String?,
    /**  The opening balance of the Account. */
    @SerializedName("opening_balance") val openingBalance: BigDecimal?,
    /**  The closing balance of the Account. */
    @SerializedName("closing_balance") val closingBalance: BigDecimal?,
    /**  The number of the Account */
    @SerializedName("account_number") val number: String?
)