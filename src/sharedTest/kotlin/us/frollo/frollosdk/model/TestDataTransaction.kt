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
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionBaseType
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionDescription
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionStatus
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdk.testutils.randomBoolean
import us.frollo.frollosdk.testutils.randomNumber
import us.frollo.frollosdk.testutils.randomUUID
import java.math.BigDecimal
import kotlin.random.Random

internal fun testTransactionResponseData(transactionId: Long? = null, accountId: Long? = null,
                                         categoryId: Long? = null, merchantId: Long? = null,
                                         transactionDate: String? = null, included: Boolean? = null) : TransactionResponse {
    return TransactionResponse(
            transactionId = transactionId ?: randomNumber().toLong(),
            accountId = accountId ?: randomNumber().toLong(),
            amount = Balance(amount = randomNumber().toBigDecimal(), currency = "AUD"),
            baseType = TransactionBaseType.values()[Random.nextInt(TransactionBaseType.values().size)],
            billId = randomNumber().toLong(),
            billPaymentId = randomNumber().toLong(),
            categoryId = categoryId ?: randomNumber().toLong(),
            merchantId = merchantId ?: randomNumber().toLong(),
            budgetCategory = BudgetCategory.values()[Random.nextInt(BudgetCategory.values().size)],
            description = TransactionDescription(original = randomUUID(), user = null, simple = null),
            included = included ?: randomBoolean(),
            memo = randomUUID(),
            postDate = "2019-01-01",
            status = TransactionStatus.values()[Random.nextInt(TransactionStatus.values().size)],
            transactionDate = transactionDate ?: "2019-01-01")
}

internal fun testTransactionsSummaryResponseData(count: Long? = null, sum: BigDecimal? = null) : TransactionsSummaryResponse {
    return TransactionsSummaryResponse(
            count = count ?: randomNumber().toLong(),
            sum = sum ?: randomNumber().toBigDecimal())
}