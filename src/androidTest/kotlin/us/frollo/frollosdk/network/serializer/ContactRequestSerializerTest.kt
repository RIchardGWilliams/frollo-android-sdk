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

package us.frollo.frollosdk.network.serializer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import us.frollo.frollosdk.model.api.contacts.ContactCreateUpdateRequest
import us.frollo.frollosdk.model.coredata.contacts.CRNType
import us.frollo.frollosdk.model.coredata.contacts.DigitalWalletProvider
import us.frollo.frollosdk.model.coredata.contacts.DigitalWalletType
import us.frollo.frollosdk.model.coredata.contacts.PayIDType
import us.frollo.frollosdk.model.coredata.contacts.PaymentDetails
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod
import us.frollo.frollosdk.testutils.toStringTrimmed

class ContactRequestSerializerTest {

    @Test
    fun testSerializePayAnyoneContactRequest() {
        val payAnyoneRequest = ContactCreateUpdateRequest(
            nickName = "Johnny Boy",
            description = null,
            paymentMethod = PaymentMethod.PAY_ANYONE,
            paymentDetails = PaymentDetails.PayAnyone(
                accountHolder = "Mr Johnathan Smith",
                accountNumber = "12345678",
                bsb = "100-123"
            )
        )
        val jsonObject = ContactRequestSerializer.serialize(payAnyoneRequest, ContactCreateUpdateRequest::class.java, null).asJsonObject
        assertEquals("Johnny Boy", jsonObject["nick_name"].toStringTrimmed())
        assertEquals("pay_anyone", jsonObject["payment_method"].toStringTrimmed())
        assertNull(jsonObject["description"])
        val paymentDetailsJsonObject = jsonObject["payment_details"].asJsonObject
        assertEquals("Mr Johnathan Smith", paymentDetailsJsonObject["account_holder"].toStringTrimmed())
        assertEquals("12345678", paymentDetailsJsonObject["account_number"].toStringTrimmed())
        assertEquals("100-123", paymentDetailsJsonObject["bsb"].toStringTrimmed())
    }

    @Test
    fun testSerializeBPayContactRequest() {
        val bPayRequest = ContactCreateUpdateRequest(
            nickName = "Tenstra",
            description = null,
            paymentMethod = PaymentMethod.BPAY,
            paymentDetails = PaymentDetails.Biller(
                billerCode = "2275362",
                crn = "723647803",
                billerName = "Tenstra Inc",
                crnType = CRNType.FIXED
            )
        )
        val jsonObject = ContactRequestSerializer.serialize(bPayRequest, ContactCreateUpdateRequest::class.java, null).asJsonObject
        assertEquals("Tenstra", jsonObject["nick_name"].toStringTrimmed())
        assertEquals("bpay", jsonObject["payment_method"].toStringTrimmed())
        assertNull(jsonObject["description"])
        val paymentDetailsJsonObject = jsonObject["payment_details"].asJsonObject
        assertEquals("2275362", paymentDetailsJsonObject["biller_code"].toStringTrimmed())
        assertEquals("723647803", paymentDetailsJsonObject["crn"].toStringTrimmed())
        assertEquals("Tenstra Inc", paymentDetailsJsonObject["biller_name"].toStringTrimmed())
        assertEquals("fixed_crn", paymentDetailsJsonObject["crn_type"].toStringTrimmed())
    }

    @Test
    fun testSerializePayIDContactRequest() {
        val payIDRequest = ContactCreateUpdateRequest(
            nickName = "Johnny",
            description = null,
            paymentMethod = PaymentMethod.PAY_ID,
            paymentDetails = PaymentDetails.PayID(
                name = "J GILBERT",
                payId = "j.gilbert@frollo.com",
                type = PayIDType.EMAIL
            )
        )
        val jsonObject = ContactRequestSerializer.serialize(payIDRequest, ContactCreateUpdateRequest::class.java, null).asJsonObject
        assertEquals("Johnny", jsonObject["nick_name"].toStringTrimmed())
        assertEquals("pay_id", jsonObject["payment_method"].toStringTrimmed())
        assertNull(jsonObject["description"])
        val paymentDetailsJsonObject = jsonObject["payment_details"].asJsonObject
        assertEquals("j.gilbert@frollo.com", paymentDetailsJsonObject["payid"].toStringTrimmed())
        assertEquals("J GILBERT", paymentDetailsJsonObject["name"].toStringTrimmed())
        assertEquals("email", paymentDetailsJsonObject["type"].toStringTrimmed())
    }

    @Test
    fun testSerializeDigitalWalletContactRequest() {
        val request = ContactCreateUpdateRequest(
            nickName = "Johnny",
            description = null,
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            paymentDetails = PaymentDetails.DigitalWallet(
                name = "J GILBERT",
                identifier = "j.gilbert@frollo.com",
                type = DigitalWalletType.EMAIL,
                provider = DigitalWalletProvider.PAYPAL_AU
            )
        )
        val jsonObject = ContactRequestSerializer.serialize(request, ContactCreateUpdateRequest::class.java, null).asJsonObject
        assertEquals("Johnny", jsonObject["nick_name"].toStringTrimmed())
        assertEquals("digital_wallet", jsonObject["payment_method"].toStringTrimmed())
        assertNull(jsonObject["description"])
        val paymentDetailsJsonObject = jsonObject["payment_details"].asJsonObject
        assertEquals("J GILBERT", paymentDetailsJsonObject["name"].toStringTrimmed())
        assertEquals("j.gilbert@frollo.com", paymentDetailsJsonObject["identifier"].toStringTrimmed())
        assertEquals("EMAIL", paymentDetailsJsonObject["type"].toStringTrimmed())
        assertEquals("PAYPAL_AU", paymentDetailsJsonObject["provider"].toStringTrimmed())
    }

    @Test
    fun testSerializeCardDContactRequest() {
        val request = ContactCreateUpdateRequest(
            nickName = "Johnny",
            description = null,
            paymentMethod = PaymentMethod.CARD,
            paymentDetails = PaymentDetails.Card(
                maskedCardPAN = "xxxxxxxxxxxx1234"
            )
        )
        val jsonObject = ContactRequestSerializer.serialize(request, ContactCreateUpdateRequest::class.java, null).asJsonObject
        assertEquals("Johnny", jsonObject["nick_name"].toStringTrimmed())
        assertEquals("card", jsonObject["payment_method"].toStringTrimmed())
        assertNull(jsonObject["description"])
        val paymentDetailsJsonObject = jsonObject["payment_details"].asJsonObject
        assertEquals("xxxxxxxxxxxx1234", paymentDetailsJsonObject["card"].toStringTrimmed())
    }
}
