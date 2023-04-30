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

package us.frollo.frollosdk.aggregation

import us.frollo.frollosdk.base.PaginationInfoDatedCursor
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.extensions.fetchTransactions
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.mapping.toTransaction
import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionResponse
import us.frollo.frollosdk.model.api.shared.PaginatedDatedCursorResponse
import us.frollo.frollosdk.model.api.shared.PaginatedResponse
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionFilter
import us.frollo.frollosdk.network.makeApiCall

// Transaction
/**
 * This is an experimental addition in order to simplify how we do our API access
 * using inline code, but it really runs asynchronously.
 * This could be simplified but we are doing an object conversion which is really not
 * necessary
 */
suspend fun Aggregation.fetchTransactionSuspended(transactionId: Long): Resource<Transaction> {
    val response = makeApiCall {
        aggregationAPI.fetchTransactionSuspended(transactionId)
    }
    return when (response.status) {
        Resource.Status.SUCCESS -> {
            Resource.success(data = response.data?.toTransaction())
        }
        Resource.Status.ERROR -> {
            Log.e("${Aggregation.TAG}#fetchTransaction", response.error?.localizedDescription)
            Resource.error(response.error)
        }
    }
}

suspend fun Aggregation.fetchTransactionsSuspended(transactionFilter: TransactionFilter): Resource<PaginatedDatedCursorResponse<Transaction>> {
    val response = makeApiCall {
        // Filter cannot be null, at least we need size to be supplied
        aggregationAPI.fetchTransactionsSuspended(transactionFilter.getQueryMap())
    }
    return when (response.status) {
        Resource.Status.SUCCESS -> {
            handlePaginatedResponseSuspended(response.data)
        }
        Resource.Status.ERROR -> {
            Log.e("${Aggregation.TAG}#fetchTransactionsByIds", response.error?.localizedDescription)
            Resource.error(response.error)
        }
    }
}

internal fun Aggregation.handlePaginatedResponseSuspended(paginatedResponse: PaginatedResponse<TransactionResponse>?): Resource<PaginatedDatedCursorResponse<Transaction>> {
    val merchantIds = paginatedResponse?.data?.map { tx -> tx.merchant.id }
    if (merchantIds != null) {
        // TODO: Have to check the implications of running this async inside coroutine
        fetchMissingMerchants(merchantIds.toSet())
    }

    paginatedResponse?.data?.let { transactions ->
        val firstTransaction = transactions.firstOrNull()
        val lastTransaction = transactions.lastOrNull()

        val paginationInfo = PaginationInfoDatedCursor(
            before = paginatedResponse.paging.cursors?.before,
            after = paginatedResponse.paging.cursors?.after,
            total = paginatedResponse.paging.total,
            beforeDate = firstTransaction?.transactionDate,
            beforeId = firstTransaction?.transactionId,
            afterDate = lastTransaction?.transactionDate,
            afterId = lastTransaction?.transactionId
        )
        Resource.success(
            PaginatedDatedCursorResponse(
                data = transactions.map { it.toTransaction() },
                paginationInfo = paginationInfo
            )
        )
    } ?: Resource.success(
        PaginatedDatedCursorResponse(data = emptyList(), paginationInfo = PaginationInfoDatedCursor())
    )
}
