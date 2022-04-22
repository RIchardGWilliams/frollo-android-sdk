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
 * Financial Passport Responsible Lending Indicators
 */
data class FPResponsibleLendingIndicators(

    /** Monthly Expenses in Gambling (Optional) */
    @SerializedName("monthly_exp_gambling") val monthlyExpGambling: BigDecimal?,
    /** Monthly Expenses in Buy Now Pay Later (Optional) */
    @SerializedName("monthly_exp_bnpl") val monthlyExpBnpl: BigDecimal?,
    /** Monthly Expenses in ATM Withdrawals (Optional) */
    @SerializedName("monthly_exp_atm_withdraw") val monthlyExpAtmWithdraw: BigDecimal?,
    /** Monthly Expenses in Missed Payments (Optional) */
    @SerializedName("monthly_exp_missed_payments") val monthlyExpMissedPayments: BigDecimal?,
    /** Monthly Expenses in Takeaways (Optional) */
    @SerializedName("monthly_exp_takeaway") val monthlyExpTakeaway: BigDecimal?
)
