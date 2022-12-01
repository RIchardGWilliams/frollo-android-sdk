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

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.IAdapterModel
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategoryType
import java.math.BigDecimal

/** Data representation of a group in cashflow report by base type response */
data class CashflowBaseTypeGroup(

    /** Id of the group, If say the grouping parameter is by merchant, this would be a merchantID */
    @SerializedName("id")val id: Long,

    /** Name of the group*/
    @SerializedName("name")val name: String,

    /** Category type of the group (Optional) */
    @SerializedName("category_type")val categoryType: TransactionCategoryType?,

    /** Income of the group */
    @SerializedName("income")val income: Boolean,

    /** Balance of credit and debit transactions summed together */
    @SerializedName("value")val value: BigDecimal,

    /** Transaction Ids of the group */
    @SerializedName("transaction_ids")val transactionIds: List<Long>,

    /** image URL for this group (Optional) */
    @SerializedName("image_url")val imageUrl: String?
) : IAdapterModel
