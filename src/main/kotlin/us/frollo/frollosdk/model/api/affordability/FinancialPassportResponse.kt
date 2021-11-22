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

/**
Financial Passport Model

Holds information about the user's FinancialPassport
 */
data class FinancialPassportResponse(

    /** Date at which financial passport was created */
    @SerializedName("created_at") val createdAt: String,
    /**  From date of financial passport */
    @SerializedName("from_date") val fromDate: String,
    /**  To date of financial passport */
    @SerializedName("to_date") val toDate: String,
    /**  List of [FPAccounts] of financial passport; Optional*/
    @SerializedName("accounts") val accounts: List<FPAccounts>?,
    /**  Summary of  of financial passport; Optional */
    @SerializedName("summary") val summary: FPSummary?,
    /**  An object to represent expenses in financial passport; Optional */
    @SerializedName("expenses") val expenses: FPIncomeExpenses?,
    /**  An object to represent income in financial passport; Optional */
    @SerializedName("income") val income: FPIncomeExpenses?,
    /**   An object to represent assets in financial passport; Optional */
    @SerializedName("assets") val assetsLiabilities: FPAssetsLiabilities?,
    /**  An object to represent liabilities in financial passport; Optional*/
    @SerializedName("liabilities") val liabilities: FPAssetsLiabilities?,
    /**  List of [FPRiskScores] of financial passport; Optional*/
    @SerializedName("risk_scores") val riskScores: List<FPRiskScores>?
) {
    companion object {

        /** Date format for dates associated with FinancialPassportResponse */
        const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    }
}
