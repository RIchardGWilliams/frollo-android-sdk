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
Financial Passport Risk Score
 */
data class FPRiskScores(

    /**  The after pay count for financial passport risk score; optional */
    @SerializedName("afterpay_count") val afterpay_count: String?,
    /**  The atm withdrawals average in month for financial passport risk score; optional */
    @SerializedName("atm_withdrawals_average_month") val atm_withdrawals_average_month: String?,
    /**  The centerlink average in month for financial passport risk score; optional */
    @SerializedName("centrelink_average_month") val centrelink_average_month: String?,
    /**  The credit limit sum of financial passport risk score; optional */
    @SerializedName("credit_limit_sum") val credit_limit_sum: String?,
    /**  The credits number transfer in 30 days for financial passport risk score; optional */
    @SerializedName("credits_no_transfers_30_days") val credits_no_transfers_30_days: String?,
    /**  The debit number transfer in 30 days for financial passport risk score; optional */
    @SerializedName("debits_no_transfers_30_days") val debits_no_transfers_30_days: String?,
    /**  The debits count over 500 for financial passport risk score; optional */
    @SerializedName("debits_over_500_count") val debits_over_500_count: String?,
    /**  The dishonours average in month for financial passport risk score; optional */
    @SerializedName("dishonours_average_month") val dishonours_average_month: String?,
    /**  The count of dishonours for financial passport risk score; optional */
    @SerializedName("dishonours_count") val dishonours_count: String?,
    /**  The count of dishonours in 30 days for financial passport risk score; optional */
    @SerializedName("dishonours_count_30_days") val dishonours_count_30_days: String?,
    /**  The count of dishonours in last 30 days for financial passport risk score; optional */
    @SerializedName("dishonours_count_last_30_days") val dishonours_count_last_30_days: String?,
    /**  The count of dishonours in last 90 days for financial passport risk score; optional */
    @SerializedName("dishonours_count_last_90_days") val dishonours_count_last_90_days: String?,
    /**  The count of dishonours without fees for financial passport risk score; optional */
    @SerializedName("dishonours_without_fee_count") val dishonours_without_fee_count: String?,
    /**  The count of entertainment in 30 days for financial passport risk score; optional */
    @SerializedName("entertainment_count_30_days") val entertainment_count_30_days: String?,
    /**   The average of gambling in month for financial passport risk score; optional */
    @SerializedName("gambling_average_month") val gambling_average_month: String?,
    /**  The average of gambling in week for financial passport risk score; optional */
    @SerializedName("gambling_average_week") val gambling_average_week: String?,
    /**  The count of gambling for financial passport risk score; optional */
    @SerializedName("gambling_count") val gambling_count: String?,
    /**  The expenditure of gambling for financial passport risk score; optional */
    @SerializedName("gambling_expenditure") val gambling_expenditure: String?,
    /**  The count of large debits count for financial passport risk score; optional */
    @SerializedName("large_debits_count") val large_debits_count: String?,
    /**  The count of large non wage credits for financial passport risk score; optional */
    @SerializedName("large_non_wages_credit_count") val large_non_wages_credit_count: String?,
    /**  The average of loan debits in month for financial passport risk score; optional */
    @SerializedName("loan_debits_average_month") val loan_debits_average_month: String?,
    /**  The loans in 30 days for financial passport risk score; optional */
    @SerializedName("loans_30_days") val loans_30_days: String?,
    /**  The average of loans in month for financial passport risk score; optional */
    @SerializedName("loans_average_month") val loans_average_month: String?,
    /**  The count of loans in 30 days for financial passport risk score; optional */
    @SerializedName("loans_count_30_days") val loans_count_30_days: String?,
    /**  The average of other income in month for financial passport risk score; optional */
    @SerializedName("other_income_average_month") val other_income_average_month: String?,
    /**  The average of overdrawn in month for financial passport risk score; optional */
    @SerializedName("overdrawn_average_month") val overdrawn_average_month: String?,
    /**  The average of pension payment in month for financial passport risk score; optional */
    @SerializedName("pension_payments_average_month") val pension_payments_average_month: String?,
    /**   The average of salary in month for inancial passport risk score; optional */
    @SerializedName("salary_average_month") val salary_average_month: String?
)
