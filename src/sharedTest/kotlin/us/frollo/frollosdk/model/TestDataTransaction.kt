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

package us.frollo.frollosdk.model

import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionResponse
import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionsSummaryResponse
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Balance
import us.frollo.frollosdk.model.coredata.aggregation.merchants.MerchantDetails
import us.frollo.frollosdk.model.coredata.aggregation.tags.TransactionTag
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.CategoryDetails
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionBaseType
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionDescription
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionStatus
import us.frollo.frollosdk.model.coredata.payments.NPPServiceIdType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdk.testutils.randomNumber
import us.frollo.frollosdk.testutils.randomString
import us.frollo.frollosdk.testutils.randomUUID
import java.math.BigDecimal
import java.util.Date

internal fun testTransactionResponseData(
    transactionId: Long? = null,
    accountId: Long? = null,
    categoryId: Long? = null,
    merchantId: Long? = null,
    transactionDate: String? = null,
    included: Boolean? = null,
    userTags: List<String>? = null,
    amount: BigDecimal? = null,
    description: TransactionDescription? = null,
    budgetCategory: BudgetCategory? = null,
    baseType: TransactionBaseType? = null,
    status: TransactionStatus? = null
): TransactionResponse {
    return TransactionResponse(
        transactionId = transactionId ?: randomNumber(10000..20000).toLong(),
        accountId = accountId ?: randomNumber(10000..20000).toLong(),
        amount = if (amount != null) Balance(amount, "AUD") else Balance(amount = BigDecimal(1111), currency = "AUD"),
        baseType = baseType ?: TransactionBaseType.UNKNOWN,
        billId = randomNumber().toLong(),
        billPaymentId = randomNumber().toLong(),
        category = testCategoryDetails(categoryId),
        merchant = testMerchantDetails(merchantId),
        budgetCategory = budgetCategory ?: BudgetCategory.ONE_OFF,
        description = description ?: TransactionDescription(original = randomUUID(), user = null, simple = null),
        included = included ?: false,
        memo = randomUUID(),
        postDate = "2019-01-01",
        status = status ?: TransactionStatus.SCHEDULED,
        transactionDate = transactionDate ?: "2019-01-01",
        userTags = userTags,
        externalId = randomString(8),
        goalId = randomNumber().toLong(),
        reference = "",
        reason = "",
        serviceId = "x2p1.02",
        serviceType = NPPServiceIdType.X2P1
    )
}

internal fun testMerchantDetails(merchantId: Long? = null): MerchantDetails =
    MerchantDetails(
        id = merchantId ?: randomNumber(10000..20000).toLong(),
        name = randomUUID(),
        phone = randomUUID(),
        website = randomUUID(),
        location = null,
        imageUrl = null
    )

internal fun testTransactionsSummaryResponseData(count: Long? = null, sum: BigDecimal? = null): TransactionsSummaryResponse =
    TransactionsSummaryResponse(
        count = count ?: randomNumber().toLong(),
        sum = sum ?: randomNumber().toBigDecimal()
    )

internal fun testTransactionTagData(name: String? = null, createdAt: String? = null, lastUsedAt: String? = null): TransactionTag =
    TransactionTag(
        name = name ?: randomString(8),
        createdAt = createdAt ?: Date().toString(),
        lastUsedAt = lastUsedAt ?: Date().toString(),
        count = randomNumber().toLong()
    )

internal fun testCategoryDetails(categoryId: Long? = null): CategoryDetails =
    CategoryDetails(
        id = categoryId ?: randomNumber(10000..20000).toLong(),
        name = randomUUID(),
        imageUrl = randomUUID()
    )

internal fun testLargeTransactionIdsListData(): List<Long> =
    listOf(
        5555560, 5555558, 5555556, 5555554, 5555552, 5555550, 5555548, 5555546, 5555544, 5555542, 5555540,
        5555538, 5555536, 5555534, 5555532, 5555530, 5555527, 5555524, 5555522, 5555520, 5555518, 5555516,
        5555514, 5555512, 5555510, 5555508, 5555506, 5555504, 5555501, 5555498, 5555494, 5555491, 5555488,
        5555486, 5555484, 5555482, 5555480, 5555478, 5555476, 5555474, 5555472, 5555470, 5555468, 5555466,
        5555464, 5555462, 5555460, 5555458, 5555456, 5555454, 5558976, 5558978, 5558980, 5558982, 5555340,
        5555452, 5558984, 5558986, 5558060, 5558062, 5557658, 5557660, 5555450, 5558066, 5557662, 5555448,
        5557664, 5555328, 5555446, 5557666, 5555444, 5559192, 5558070, 5555442, 5558072, 5558074, 5557370,
        5555440, 5555438, 5559194, 5559196, 5558078, 5555436, 5558082, 5558080, 5558084, 5558086, 5557668,
        5557670, 5557672, 5555434, 5558098, 5558088, 5558090, 5555432, 5558064, 5559062, 5558106, 5557674,
        5557676, 5555430, 5558092, 5558094, 5555428, 5557678, 5555330, 5555426, 5558100, 5558104, 5558096,
        5557680, 5557682, 5555424, 5558102, 5555422, 5558108, 5558110, 5557880, 5555420, 5559058, 5557684,
        5555418, 5558112, 5559044, 5558114, 5558116, 5557686, 5555416, 5558118, 5557688, 5555414, 5559046,
        5559048, 5555412, 5559050, 5559052, 5559054, 5555410, 5559056, 5557690, 5555408, 5559060, 5557692,
        5557694, 5555406, 5559064, 5557656, 5555404, 5559068, 5559066, 5555402, 5559114, 5559070, 5555400,
        5559072, 5557696, 5555332, 5555398, 5559074, 5557698, 5555396, 5559076, 5557700, 5557898, 5555394,
        5559078, 5559080, 5559082, 5557398, 5555392, 5559084, 5559086, 5559088, 5557702, 5555390, 5559090,
        5559092, 5555388, 5559094, 5555386, 5559096, 5557882, 5555344, 5555342, 5555384, 5559098, 5559100,
        5555382, 5559102, 5559104, 5559154, 5559106, 5555380, 5559108, 5557400, 5557402, 5555378, 5559110,
        5559112, 5555376
    )
