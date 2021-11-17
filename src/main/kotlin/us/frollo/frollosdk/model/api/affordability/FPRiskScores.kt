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

/**
Financial Passport Risk Score
 */
data class FPRiskScores(

    /**  The after pay count for financial passport risk score; optional */
    @SerializedName("afterpay_count") val afterPayCount: BigDecimal?,
    /**  The atm withdrawals average in month for financial passport risk score; optional */
    @SerializedName("atm_withdrawals_average_month") val atmWithdrawalsAverageMonth: BigDecimal?,
    /**  The centerlink average in month for financial passport risk score; optional */
    @SerializedName("centrelink_average_month") val centrelinkAverageMonth: BigDecimal?,
    /**  The credit limit sum of financial passport risk score; optional */
    @SerializedName("credit_limit_sum") val creditLimitSum: BigDecimal?,
    /**  The credits number transfer in 30 days for financial passport risk score; optional */
    @SerializedName("credits_no_transfers_30_days") val creditsNoTransfers30Days: BigDecimal?,
    /**  The debit number transfer in 30 days for financial passport risk score; optional */
    @SerializedName("debits_no_transfers_30_days") val debitsNoTransfers30Days: BigDecimal?,
    /**  The debits count over 500 for financial passport risk score; optional */
    @SerializedName("debits_over_500_count") val debitsOver500Count: BigDecimal?,
    /**  The dishonours average in month for financial passport risk score; optional */
    @SerializedName("dishonours_average_month") val dishonoursAverageMonth: BigDecimal?,
    /**  The count of dishonours for financial passport risk score; optional */
    @SerializedName("dishonours_count") val dishonoursCount: BigDecimal?,
    /**  The count of dishonours in 30 days for financial passport risk score; optional */
    @SerializedName("dishonours_count_30_days") val dishonoursCount30Days: BigDecimal?,
    /**  The count of dishonours in last 30 days for financial passport risk score; optional */
    @SerializedName("dishonours_count_last_30_days") val dishonoursCountLast30Days: BigDecimal?,
    /**  The count of dishonours in last 90 days for financial passport risk score; optional */
    @SerializedName("dishonours_count_last_90_days") val dishonoursCountLast90Days: BigDecimal?,
    /**  The count of dishonours without fees for financial passport risk score; optional */
    @SerializedName("dishonours_without_fee_count") val dishonoursWithoutFeeCount: BigDecimal?,
    /**  The count of entertainment in 30 days for financial passport risk score; optional */
    @SerializedName("entertainment_count_30_days") val entertainmentCount30Days: BigDecimal?,
    /**   The average of gambling in month for financial passport risk score; optional */
    @SerializedName("gambling_average_month") val gamblingAverageMonth: BigDecimal?,
    /**  The average of gambling in week for financial passport risk score; optional */
    @SerializedName("gambling_average_week") val gamblingAverageWeek: BigDecimal?,
    /**  The count of gambling for financial passport risk score; optional */
    @SerializedName("gambling_count") val gamblingCount: BigDecimal?,
    /**  The expenditure of gambling for financial passport risk score; optional */
    @SerializedName("gambling_expenditure") val gamblingExpenditure: BigDecimal?,
    /**  The count of large debits count for financial passport risk score; optional */
    @SerializedName("large_debits_count") val largeDebitsCount: BigDecimal?,
    /**  The count of large non wage credits for financial passport risk score; optional */
    @SerializedName("large_non_wages_credit_count") val largeNonWagesCreditCount: BigDecimal?,
    /**  The average of loan debits in month for financial passport risk score; optional */
    @SerializedName("loan_debits_average_month") val loanDebitsAverageMonth: BigDecimal?,
    /**  The loans in 30 days for financial passport risk score; optional */
    @SerializedName("loans_30_days") val loans30Days: BigDecimal?,
    /**  The average of loans in month for financial passport risk score; optional */
    @SerializedName("loans_average_month") val loansAverageMonth: BigDecimal?,
    /**  The count of loans in 30 days for financial passport risk score; optional */
    @SerializedName("loans_count_30_days") val loansCount30Days: BigDecimal?,
    /**  The average of other income in month for financial passport risk score; optional */
    @SerializedName("other_income_average_month") val otherIncomeAverageMonth: BigDecimal?,
    /**  The average of overdrawn in month for financial passport risk score; optional */
    @SerializedName("overdrawn_average_month") val overdrawnAverageMonth: BigDecimal?,
    /**  The average of pension payment in month for financial passport risk score; optional */
    @SerializedName("pension_payments_average_month") val pensionPaymentsAverageMonth: BigDecimal?,
    /**   The average of salary in month for Financial passport risk score; optional */
    @SerializedName("salary_average_month") val salaryAverageMonth: BigDecimal?
)
