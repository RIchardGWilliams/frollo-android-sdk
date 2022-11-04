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

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/** Fields for export transactions */
@Keep
enum class ExportTransactionField {

    /** ID of the Transaction */
    @SerializedName("id") ID,
    /** External ID of the Transaction */
    @SerializedName("external_id") EXTERNAL_ID,
    /** Original Description of the Transaction */
    @SerializedName("original_description") ORIGINAL_DESCRIPTION,
    /** Simple Description of the Transaction */
    @SerializedName("simple_description") SIMPLE_DESCRIPTION,
    /** User Description of the Transaction */
    @SerializedName("user_description") USER_DESCRIPTION,
    /** Amount of the Transaction */
    @SerializedName("amount") AMOUNT,
    /** Currency of the Transaction */
    @SerializedName("currency") CURRENCY,
    /** Date of the Transaction */
    @SerializedName("transaction_date") TRANSACTION_DATE,
    /** Posted Date of the Transaction */
    @SerializedName("posted_date") POSTED_DATE,
    /** Provider Account ID of the Transaction */
    @SerializedName("provider_account_id") PROVIDER_ACCOUNT_ID,
    /** Account ID of the Transaction */
    @SerializedName("account_id") ACCOUNT_ID,
    /** Account Number of the Transaction */
    @SerializedName("account_number") ACCOUNT_NUMBER,
    /** Account Name of the Transaction */
    @SerializedName("account_name") ACCOUNT_NAME,
    /** Base Type of the Transaction */
    @SerializedName("credit_debit") CREDIT_DEBIT,
    /** Type of the Transaction */
    @SerializedName("transaction_type") TRANSACTION_TYPE,
    /** Provider ID of the Transaction */
    @SerializedName("provider_id") PROVIDER_ID,
    /** Provider Name of the Transaction */
    @SerializedName("provider_name") PROVIDER_NAME,
    /** Merchant ID of the Transaction */
    @SerializedName("merchant_id") MERCHANT_ID,
    /** Merchant Name of the Transaction */
    @SerializedName("merchant_name") MERCHANT_NAME,
    /** Merchant Type of the Transaction */
    @SerializedName("merchant_type") MERCHANT_TYPE,
    /** Budget Category of the Transaction */
    @SerializedName("budget_category") BUDGET_CATEGORY,
    /** Category Name of the Transaction */
    @SerializedName("category_name") CATEGORY_NAME,
    /** Tags of the Transaction */
    @SerializedName("user_tags") USER_TAGS,
    /** Notes of the Transaction */
    @SerializedName("memo") MEMO,
    /** Included Column of the Transaction */
    @SerializedName("included") INCLUDED,
    /** Payee of the Transaction */
    @SerializedName("payee") PAYEE,
    /** Payer of the Transaction */
    @SerializedName("payer") PAYER,
    /** Reference of the Transaction */
    @SerializedName("reference") REFERENCE;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
