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

package us.frollo.frollosdk.model.coredata.contacts

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/**
 * Indicates the payment method type for the contact
 */
enum class PaymentMethod {

    /** Pay Anyone type*/
    @SerializedName("pay_anyone") PAY_ANYONE,

    /** Pay ID type */
    @SerializedName("pay_id") PAY_ID,

    /** BPay type */
    @SerializedName("bpay") BPAY,

    /** International Payment type */
    @SerializedName("international") INTERNATIONAL,

    /** Digital Wallet Payment type */
    @SerializedName("digital_wallet") DIGITAL_WALLET,

    /** Card Payment type */
    @SerializedName("card") CARD;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
