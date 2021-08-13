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

package us.frollo.frollosdk.network.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import us.frollo.frollosdk.model.api.statements.StatementResponse
import us.frollo.frollosdk.model.api.statements.StatementSortBy
import us.frollo.frollosdk.model.api.statements.StatementType
import us.frollo.frollosdk.model.coredata.shared.OrderType

internal interface StatementsAPI {
    companion object {
        const val URL_STATEMENTS = "statements"
        const val URL_STATEMENT_DOWNLOAD = "$URL_STATEMENTS/{reference_id}"
    }

    @GET(URL_STATEMENTS)
    fun fetchStatements(
        @Query("account_ids")accountIds: String,
        @Query("type")statementType: StatementType? = null,
        @Query("from_date")fromDate: String? = null, // 2021-01-01
        @Query("to_date")toDate: String? = null, // 2021-01-01
        @Query("before")before: Int? = null,
        @Query("after")after: Int? = null,
        @Query("size")size: Int? = null,
        @Query("sort")statementSortBy: StatementSortBy? = null,
        @Query("order")orderType: OrderType? = null
    ): Call<StatementResponse>

    @GET(URL_STATEMENTS)
    fun fetchStatementsRx(
        @Query("account_ids")accountIds: String,
        @Query("type")statementType: StatementType? = null,
        @Query("from_date")fromDate: String? = null, // 2021-01-01
        @Query("to_date")toDate: String? = null, // 2021-01-01
        @Query("before")before: Int? = null,
        @Query("after")after: Int? = null,
        @Query("size")size: Int? = null,
        @Query("sort")statementSortBy: StatementSortBy? = null,
        @Query("order")orderType: OrderType? = null
    ): Single<StatementResponse>

    @GET(URL_STATEMENT_DOWNLOAD)
    fun fetchStatement(@Path("reference_id")referenceId: String): Call<ResponseBody>

    @GET(URL_STATEMENT_DOWNLOAD)
    fun downloadStatementRx(@Path("reference_id")referenceId: String): Single<ResponseBody>
}
