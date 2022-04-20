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

/** Data representation of the Accounts of Liabilities of the Financial Passport */
data class FPLiabilitiesBreakdownAccounts(

    /** The ID of the Account. You can retrieve the Account via the Get Accounts API. */
    @SerializedName("id") val id: Long?,
    /** Repayment frequency of an account */
    @SerializedName("frequency") val repaymentFrequency: FPFrequency?,
    /** Repayment type of an account */
    @SerializedName("repayment_type") val repaymentType: FPRepaymentType?,
    /**
     * Loan start date (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("loan_start_date") val loanStartDate: String?,
    /**
     * Loan end date (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("loan_end_date") val loanEndDate: String?,
    /** Count of missed payments */
    @SerializedName("missed_repayment_count") val missedRepaymentCount: Long?,
    /** Interest */
    @SerializedName("interest") val interest: FPAccountInterest?,
    /** Minimum required repayment amounts */
    @SerializedName("minimum_repayment_amount") val minimumRepaymentAmount: FPMinimumRepaymentAmount?,
)
