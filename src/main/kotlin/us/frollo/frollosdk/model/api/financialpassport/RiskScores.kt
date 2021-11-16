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

data class RiskScores(

    @SerializedName("afterpay_count") val afterpay_count: Double,
    @SerializedName("atm_withdrawals_average_month") val atm_withdrawals_average_month: Double,
    @SerializedName("centrelink_average_month") val centrelink_average_month: Double,
    @SerializedName("credit_limit_sum") val credit_limit_sum: Double,
    @SerializedName("credits_no_transfers_30_days") val credits_no_transfers_30_days: Double,
    @SerializedName("debits_no_transfers_30_days") val debits_no_transfers_30_days: Double,
    @SerializedName("debits_over_500_count") val debits_over_500_count: Double,
    @SerializedName("dishonours_average_month") val dishonours_average_month: Double,
    @SerializedName("dishonours_count") val dishonours_count: Double,
    @SerializedName("dishonours_count_30_days") val dishonours_count_30_days: Double,
    @SerializedName("dishonours_count_last_30_days") val dishonours_count_last_30_days: Double,
    @SerializedName("dishonours_count_last_90_days") val dishonours_count_last_90_days: Double,
    @SerializedName("dishonours_without_fee_count") val dishonours_without_fee_count: Double,
    @SerializedName("entertainment_count_30_days") val entertainment_count_30_days: Double,
    @SerializedName("gambling_average_month") val gambling_average_month: Double,
    @SerializedName("gambling_average_week") val gambling_average_week: Double,
    @SerializedName("gambling_count") val gambling_count: Double,
    @SerializedName("gambling_expenditure") val gambling_expenditure: Double,
    @SerializedName("large_debits_count") val large_debits_count: Double,
    @SerializedName("large_non_wages_credit_count") val large_non_wages_credit_count: Double,
    @SerializedName("loan_debits_average_month") val loan_debits_average_month: Double,
    @SerializedName("loans_30_days") val loans_30_days: Double,
    @SerializedName("loans_average_month") val loans_average_month: Double,
    @SerializedName("loans_count_30_days") val loans_count_30_days: Double,
    @SerializedName("other_income_average_month") val other_income_average_month: Double,
    @SerializedName("overdrawn_average_month") val overdrawn_average_month: Double,
    @SerializedName("pension_payments_average_month") val pension_payments_average_month: Double,
    @SerializedName("salary_average_month") val salary_average_month: Double
)
