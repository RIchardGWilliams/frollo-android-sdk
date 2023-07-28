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

package us.frollo.frollosdk.model.coredata.aggregation.transactions

import us.frollo.frollosdk.model.IAdapterModel
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Balance
import us.frollo.frollosdk.model.coredata.aggregation.merchants.MerchantDetails
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.CategoryDetails
import us.frollo.frollosdk.model.coredata.payments.NPPServiceIdType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory

/** Data representation of a Transaction */
data class Transaction(

    /** Unique ID of the transaction */
    val transactionId: Long,

    /** Transaction Base Type */
    val baseType: TransactionBaseType,

    /** Status of the transaction */
    val status: TransactionStatus,

    /** Date the transaction occurred, localized */
    val transactionDate: String, // yyyy-MM-dd

    /** Date the transaction was posted, localized (optional) */
    val postDate: String?, // yyyy-MM-dd

    /** Amount the transaction is for */
    val amount: Balance,

    /** Description */
    var description: TransactionDescription?,

    /** Transaction's associated budget category. See [BudgetCategory] */
    var budgetCategory: BudgetCategory,

    /** Included in budget */
    var included: Boolean,

    /** Memo or notes added to the transaction (optional) */
    var memo: String?,

    /** Parent account ID */
    val accountId: Long,

    /** Transaction Category details related to the transaction */
    val category: CategoryDetails,

    /** Merchant details related to the transaction */
    val merchant: MerchantDetails,

    /** Bill ID related to the transaction */
    var billId: Long?,

    /** Bill Payment ID related to the transaction */
    var billPaymentId: Long?,

    /** All tags applied to this transaction */
    var userTags: List<String>?,

    /** External ID of the aggregator */
    val externalId: String,

    /** Goal ID associated with the transaction */
    var goalId: Long?,

    /** Reference for transaction (Optional) */
    val reference: String?,

    /** Reason of cancelled or rejected transaction (Optional) */
    val reason: String?,

    /** Service ID used for the transaction Eg: "x2p1.02" (Optional) */
    val serviceId: String?,

    /** Service Type used for the transaction Eg: "sct", "x2p1" (Osko) (Optional) */
    val serviceType: NPPServiceIdType?

) : IAdapterModel {

    companion object {
        /** Date format for dates associated with Transaction */
        const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    }
}
