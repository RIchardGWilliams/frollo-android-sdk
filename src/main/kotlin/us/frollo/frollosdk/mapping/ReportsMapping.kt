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

package us.frollo.frollosdk.mapping

import us.frollo.frollosdk.model.api.reports.AccountBalanceReportResponse
import us.frollo.frollosdk.model.api.reports.CashflowReportResponse
import us.frollo.frollosdk.model.api.reports.ReportsResponse
import us.frollo.frollosdk.model.api.reports.ReportsResponse.ReportResponse
import us.frollo.frollosdk.model.api.reports.ReportsResponse.ReportResponse.GroupReportResponse
import us.frollo.frollosdk.model.coredata.reports.CashflowReport
import us.frollo.frollosdk.model.coredata.reports.GroupReport
import us.frollo.frollosdk.model.coredata.reports.Report
import us.frollo.frollosdk.model.coredata.reports.ReportAccountBalance
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.ReportPeriod
import us.frollo.frollosdk.model.coredata.reports.TransactionReportPeriod

internal fun AccountBalanceReportResponse.Report.BalanceReport.toReportAccountBalance(date: String, period: ReportPeriod) =
    ReportAccountBalance(
        date = date,
        value = value,
        period = period,
        currency = currency,
        accountId = id
    )

internal fun ReportsResponse.toReports(grouping: ReportGrouping, period: TransactionReportPeriod): List<Report> {
    val reports = mutableListOf<Report>()
    this.data.forEach { reportResponse ->
        reports.add(reportResponse.toReport(grouping, period))
    }
    return reports
}

internal fun ReportResponse.toReport(grouping: ReportGrouping, period: TransactionReportPeriod): Report {
    val groups = mutableListOf<GroupReport>()
    this.groups.forEach { groupResponse ->
        groups.add(groupResponse.toGroupReport(grouping, period, this.date))
    }
    return Report(
        date = date,
        isIncome = income,
        value = value,
        groups = groups,
        grouping = grouping,
        period = period
    )
}

internal fun GroupReportResponse.toGroupReport(grouping: ReportGrouping, period: TransactionReportPeriod, date: String): GroupReport =
    GroupReport(
        linkedId = id,
        name = name,
        isIncome = income,
        value = value,
        transactionIds = transactionIds,
        grouping = grouping,
        period = period,
        date = date
    )

internal fun CashflowReportResponse.toCashflowReports(): List<CashflowReport> {
    val cashflowReports = mutableListOf<CashflowReport>()
    this.data.forEach {
        cashflowReports.add(it)
    }
    return cashflowReports
}
