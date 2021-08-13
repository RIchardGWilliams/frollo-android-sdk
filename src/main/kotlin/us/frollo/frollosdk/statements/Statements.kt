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

package us.frollo.frollosdk.statements

import okhttp3.ResponseBody
import us.frollo.frollosdk.base.PaginatedResultWithData
import us.frollo.frollosdk.base.PaginationInfo
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.model.api.statements.Statement
import us.frollo.frollosdk.model.api.statements.StatementResponse
import us.frollo.frollosdk.model.api.statements.StatementSortBy
import us.frollo.frollosdk.model.api.statements.StatementType
import us.frollo.frollosdk.model.coredata.shared.OrderType
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.StatementsAPI

/** Manages all aspects of Addresses */
class Statements(network: NetworkService) {

    companion object {
        private const val TAG = "Statements"
    }

    private val statementsAPI: StatementsAPI = network.create(StatementsAPI::class.java)

    /**
     * Refresh statements list from host
     *
     * @param accountIds:List<Long> list of account ids to get statement for
     * @param statementType: [StatementType],
     * @param fromDate:String,//2021-01-01
     * @param toDate:String,//2021-01-01
     * @param before:Int,
     * @param after:Int,
     * @param size:Int,
     * @param sortBy: [StatementSortBy]
     * @param orderType: [OrderType]
     * @param completion: OnFrolloSDKCompletionListener<PaginatedResultWithData<PaginationInfo, List<Statement>>>? = null
     */
    fun fetchStatements(
        accountIds: List<Long>,
        statementType: StatementType? = null,
        fromDate: String? = null, // 2021-01-01
        toDate: String? = null, // 2021-01-01
        before: Int? = null,
        after: Int? = null,
        size: Int? = null,
        sortBy: StatementSortBy? = null,
        orderType: OrderType? = null,
        completion: OnFrolloSDKCompletionListener<PaginatedResultWithData<PaginationInfo, List<Statement>>>? = null
    ) {
        statementsAPI.fetchStatements(
            accountIds.joinToString(","), statementType, fromDate, toDate,
            before, after, size, sortBy, orderType
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    handleStatementsResponse(resource.data, completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchManagedProducts", resource.error?.localizedDescription)
                    completion?.invoke(PaginatedResultWithData.Error(resource.error))
                }
            }
        }
    }

    private fun handleStatementsResponse(
        response: StatementResponse?,
        completion: OnFrolloSDKCompletionListener<PaginatedResultWithData<PaginationInfo, List<Statement>>>?
    ) {
        completion?.invoke(
            PaginatedResultWithData.Success(
                paginationInfo = PaginationInfo(
                    after = response?.paging?.cursors?.after?.toLong(),
                    before = response?.paging?.cursors?.before?.toLong(),
                    total = response?.paging?.total
                ),
                data = response?.statements
            )
        )
    }

    /**
     * Download a specific statement from the host
     *
     * @param referenceId of statement to download
     *
     * @return Single<InputStream> inputStream of file
     */
    fun fetchStatement(referenceId: String, completion: OnFrolloSDKCompletionListener<Resource<ResponseBody>?>? = null) {
        statementsAPI.fetchStatement(referenceId).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    completion?.invoke(resource)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#downloadStatement", resource.error?.localizedDescription)
                    completion?.invoke(resource)
                }
            }
        }
    }
}
