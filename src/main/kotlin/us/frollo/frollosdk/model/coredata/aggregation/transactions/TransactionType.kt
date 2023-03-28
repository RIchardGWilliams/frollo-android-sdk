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

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/** Transaction type for manual transactions */
enum class TransactionType {

    /** Fee */
    @SerializedName("fee") FEE,

    /** Interest charged */
    @SerializedName("interest_charged") INTEREST_CHARGED,

    /** Interest paid */
    @SerializedName("interest_paid") INTEREST_PAID,

    /** Transfer outgoing */
    @SerializedName("transfer_outgoing") TRANSFER_OUTGOING,

    /** Transfer incoming */
    @SerializedName("transfer_incoming") TRANSFER_INCOMING,

    /** Payment */
    @SerializedName("payment") PAYMENT,

    /** Direct debit */
    @SerializedName("direct_debit") DIRECT_DEBIT,

    /** Other */
    @SerializedName("other") OTHER;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
