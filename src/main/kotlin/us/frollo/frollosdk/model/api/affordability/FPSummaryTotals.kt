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

/** Data representation of the breakdown totals of the Financial Passport's Summary */
data class FPSummaryTotals(

    /**  The total value of expenses over the report period; Optional */
    @SerializedName("expenses") val expenses: BigDecimal?,
    /**  The total value of income over the report period; Optional */
    @SerializedName("income") val monthly: BigDecimal?,
    /**  The total value of assets over the report period; Optional */
    @SerializedName("assets") val assets: BigDecimal?,
    /**  The total value of liabilities over the report period; Optional */
    @SerializedName("liabilities") val liabilities: BigDecimal?,
    /**  The total value of assets plus liabilities over the report period; Optional */
    @SerializedName("net_balance") val netBalance: BigDecimal?,
)
