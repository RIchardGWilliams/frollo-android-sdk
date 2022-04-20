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

/** Data representation of the breakdown of Assets of the Financial Passport */
data class FPAssetBreakdown(

    /** The name of assets breakdown (Optional) */
    @SerializedName("name") val name: String?,
    /** The type of assets breakdown (Optional) */
    @SerializedName("type") val type: String?,
    /** The total of assets breakdown (Optional) */
    @SerializedName("total") val total: BigDecimal?,
    /** A list of accounts in assets breakdown (Optional) */
    @SerializedName("accounts") val accounts: List<FPAssetsBreakdownAccounts>?
)
