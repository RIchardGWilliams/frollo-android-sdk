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

package us.frollo.frollosdk.model.coredata.reports

import androidx.room.*
import us.frollo.frollosdk.extensions.toBudgetCategory
import us.frollo.frollosdk.model.IAdapterModel
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory

/** History Transactions Group Report with associated data */
data class ReportGroupTransactionHistoryRelation(

        /** Group Report */
        @Embedded
        var groupReport: ReportGroupTransactionHistory? = null,

        /** Associated Overall Report
         *
         * Even though its a list this will have only one element. It is requirement of Room database for this to be a list.
         */
        @Relation(parentColumn = "report_id", entityColumn = "report_id", entity = ReportTransactionHistory::class)
        var overallReports: List<ReportTransactionHistory>? = null,

        /** Associated Transaction Category
         *
         * Even though its a list this will have only one element. It is requirement of Room database for this to be a list.
         */
        @Relation(parentColumn = "linked_id", entityColumn = "transaction_category_id", entity = TransactionCategory::class)
        var transactionCategories: List<TransactionCategory>? = null,

        /** Associated Merchant
         *
         * Even though its a list this will have only one element. It is requirement of Room database for this to be a list.
         */
        @Relation(parentColumn = "linked_id", entityColumn = "merchant_id", entity = Merchant::class)
        var merchants: List<Merchant>? = null

): IAdapterModel {

        /** Associated Overall Report */
        val overall: ReportTransactionHistory?
                get() {
                        val models = overallReports
                        return if (models?.isNotEmpty() == true) models[0] else null
                }

        /** Associated Budget Category if applicable. See [ReportGrouping] */
        val budgetCategory: BudgetCategory?
                get() {
                        return groupReport?.name?.toBudgetCategory()
                }

        /** Associated Transaction Category */
        val transactionCategory: TransactionCategory?
                get() {
                        val models = transactionCategories
                        return if (models?.isNotEmpty() == true) models[0] else null
                }

        /** Associated Merchant */
        val merchant: Merchant?
                get() {
                        val models = merchants
                        return if (models?.isNotEmpty() == true) models[0] else null
                }
}