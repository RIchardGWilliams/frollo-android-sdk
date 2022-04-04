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
import us.frollo.frollosdk.extensions.toJsonObject
import java.io.Serializable

/** Represents the payment details of a contact */
/* NOTE: Any update to PaymentDetails ensure you update ContactResponseDeserializer & ContactRequestSerializer */
sealed class PaymentDetails : Serializable {

    /** Represents the payment details of a PayAnyone contact */
    data class PayAnyone(
        /** The name of the account holder */
        @SerializedName("account_holder") var accountHolder: String,

        /** The BSB of the account holder */
        @SerializedName("bsb") var bsb: String,

        /** The account number of the account holder */
        @SerializedName("account_number") var accountNumber: String
    ) : PaymentDetails()

    /** Represents the payment details of a BPay contact */
    data class Biller(
        /** The unique code of the biller */
        @SerializedName("biller_code") var billerCode: String,

        /** The customer reference number of the biller */
        @SerializedName("crn") var crn: String,

        /** The name of the biller */
        @SerializedName("biller_name") var billerName: String,

        /** The CRN type of the biller */
        @SerializedName("crn_type") var crnType: CRNType
    ) : PaymentDetails()

    /** Represents the payment details of a PayID contact */
    data class PayID(
        /** The payID of the contact */
        @SerializedName("payid") var payId: String,

        /** The name of the contact (Optional) */
        @SerializedName("name") var name: String?,

        /** The payId type of the contact */
        @SerializedName("type") var type: PayIDType
    ) : PaymentDetails()

    /** Represents the payment details of a International Payment contact */
    data class International(

        /** Beneficiary details of the contact */
        @SerializedName("beneficiary") val beneficiary: Beneficiary,

        /** Bank details of the contact */
        @SerializedName("bank_details") val bankDetails: BankDetails
    ) : PaymentDetails()

    /** Represents the payment details of a Digital Wallet Payment contact */
    data class DigitalWallet(

        /** Name of the wallet or wallet provider */
        @SerializedName("name") val name: String,

        /** Identifier of the digital wallet */
        @SerializedName("identifier") val identifier: String,

        /** Type of wallet identifier */
        @SerializedName("type") val type: DigitalWalletType,

        /** Provider of the wallet */
        @SerializedName("provider") val provider: DigitalWalletProvider,
    ) : PaymentDetails()

    /** Represents the payment details of a Card Payment contact */
    data class Card(

        /** Masked PAN to be paid */
        @SerializedName("card") val maskedCardPAN: String
    ) : PaymentDetails()

    companion object {
        internal fun jsonIsPayAnyone(json: String): Boolean {
            val jsonObject = json.toJsonObject()
            return jsonObject != null &&
                jsonObject.has("account_number") &&
                jsonObject.has("bsb")
        }
        internal fun jsonIsBiller(json: String): Boolean {
            val jsonObject = json.toJsonObject()
            return jsonObject != null &&
                jsonObject.has("biller_code") &&
                jsonObject.has("crn")
        }
        internal fun jsonIsPayID(json: String): Boolean {
            val jsonObject = json.toJsonObject()
            return jsonObject != null &&
                jsonObject.has("payid") &&
                jsonObject.has("type")
        }
        internal fun jsonIsInternational(json: String): Boolean {
            val jsonObject = json.toJsonObject()
            return jsonObject != null &&
                jsonObject.has("beneficiary") &&
                jsonObject.has("bank_details")
        }
        internal fun jsonIsDigitalWallet(json: String): Boolean {
            val jsonObject = json.toJsonObject()
            return jsonObject != null &&
                jsonObject.has("name") &&
                jsonObject.has("identifier") &&
                jsonObject.has("type") &&
                jsonObject.has("provider")
        }
        internal fun jsonIsCard(json: String): Boolean {
            val jsonObject = json.toJsonObject()
            return jsonObject != null &&
                jsonObject.has("card")
        }
    }
}
