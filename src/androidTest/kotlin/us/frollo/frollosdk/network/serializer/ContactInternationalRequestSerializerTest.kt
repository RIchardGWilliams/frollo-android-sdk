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
import us.frollo.frollosdk.model.api.contacts.ContactInternationalCreateUpdateRequest
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod
import us.frollo.frollosdk.testutils.toStringTrimmed

class ContactInternationalRequestSerializerTest {

    @Test
    fun testSerializeInternationalContactRequest() {
        val internationalRequest = ContactInternationalCreateUpdateRequest(
            nickName = "Johnny",
            description = null,
            paymentMethod = PaymentMethod.INTERNATIONAL,
            paymentDetails = ContactInternationalCreateUpdateRequest.InternationalPaymentDetails(
                name = "J GILBERT",
                country = "US",
                bankCountry = "US",
                accountNumber = "12345678"
            )
        )
        val jsonObject = ContactInternationalRequestSerializer.serialize(internationalRequest, ContactInternationalCreateUpdateRequest::class.java, null).asJsonObject
        assertEquals("Johnny", jsonObject["nick_name"].toStringTrimmed())
        assertEquals("international", jsonObject["payment_method"].toStringTrimmed())
        assertNull(jsonObject["description"])
        val paymentDetailsJsonObject = jsonObject["payment_details"].asJsonObject
        assertEquals("J GILBERT", paymentDetailsJsonObject["name"].toStringTrimmed())
        assertEquals("US", paymentDetailsJsonObject["country"].toStringTrimmed())
        assertEquals("US", paymentDetailsJsonObject["bank_country"].toStringTrimmed())
        assertEquals("12345678", paymentDetailsJsonObject["account_number"].toStringTrimmed())
    }
}
