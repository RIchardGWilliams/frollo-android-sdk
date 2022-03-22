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

package us.frollo.frollosdk.model.api.aggregation.accounts

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountOwnerType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Balance
import us.frollo.frollosdk.model.coredata.aggregation.accounts.RelatedAccount
import us.frollo.frollosdk.model.coredata.aggregation.accounts.StatementOrPaymentFrequency
import java.math.BigDecimal

/**
 * Data representation of the Manual Account Create/Update Request
 */
data class AccountCreateUpdateRequest(

    /** Name of the account */
    @SerializedName("account_name") var accountName: String,

    /** Nickname given to the account for display and identification purposes (Optional) */
    @SerializedName("nick_name") var nickName: String? = null,

    /** Account Type */
    @SerializedName("container") var accountType: AccountType,

    /** Account BSB (Optional) */
    @SerializedName("bsb") var bsb: String? = null,

    /** Account number (Optional) */
    @SerializedName("account_number") var accountNumber: String? = null,

    /** Favourited (Optional) */
    @SerializedName("favourite") var favourite: Boolean? = null,

    /** Included in budget. Used to exclude accounts from counting towards the user's budgets (Optional) */
    @SerializedName("included") var included: Boolean? = null,

    /** Hidden. Used to hide the account in the UI (Optional) */
    @SerializedName("hidden") var hidden: Boolean? = null,

    /** Current balance (Optional) */
    @SerializedName("current_balance") var currentBalance: Balance,

    /** Interest rate (Optional) */
    @SerializedName("interest_rate") var interestRate: BigDecimal? = null,

    /** APR percentage (Optional) */
    @SerializedName("apr") var apr: BigDecimal? = null,

    /** Available balance (Optional) */
    @SerializedName("available_balance") var availableBalance: Balance? = null,

    /** Available cash (Optional) */
    @SerializedName("available_cash") var availableCash: Balance? = null,

    /** Available credit (Optional) */
    @SerializedName("available_credit") var availableCredit: Balance? = null,

    /** Total cash limit (Optional) */
    @SerializedName("total_cash_limit") var totalCashLimit: Balance? = null,

    /** Total credit line (Optional) */
    @SerializedName("total_credit_line") var totalCreditLine: Balance? = null,

    /** Amount due (Optional) */
    @SerializedName("amount_due") var amountDue: Balance? = null,

    /** Minimum amount due (Optional) */
    @SerializedName("minimum_amount_due") var minimumAmountDue: Balance? = null,

    /** Last payment amount (Optional) */
    @SerializedName("last_payment_amount") var lastPaymentAmount: Balance? = null,

    /** Interest total (optional) */
    @SerializedName("interest_total") val interestTotal: Balance? = null,

    /**
     * Due date (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("due_date") var dueDate: String? = null,

    /**
     * Last payment date (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("last_payment_date") var lastPaymentDate: String? = null,

    /** Related accounts in Frollo system (Optional) */
    @SerializedName("related_accounts") var relatedAccounts: List<RelatedAccount>? = null,

    /** Repayment frequency (Optional) */
    @SerializedName("frequency") var frequency: StatementOrPaymentFrequency? = null,

    /** Additional information to the account (Optional) */
    @SerializedName("details") var additionalDetails: AccountAdditionalDetailsRequest? = null,

    /** Indicates if this is a joint account (Optional) */
    @SerializedName("joint_account") var jointAccount: Boolean? = null,

    /** Account owner type (Optional) */
    @SerializedName("owner_type") var ownerType: AccountOwnerType? = null
)
