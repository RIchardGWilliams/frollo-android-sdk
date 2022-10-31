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

import org.junit.Assert.assertEquals
import org.junit.Test
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus
import us.frollo.frollosdk.model.testCDRConfigurationData
import us.frollo.frollosdk.model.testConsentCreateFormData
import us.frollo.frollosdk.model.testConsentResponseData
import us.frollo.frollosdk.model.testConsentUpdateFormData
import us.frollo.frollosdk.model.testDisclosureConsentResponseData
import us.frollo.frollosdk.model.testExternalPartyResponseData

class ConsentsMappingTest {

    @Test
    fun testConsentResponseToConsent() {
        val response = testConsentResponseData(consentId = 12345)
        val model = response.toConsent()
        assertEquals(12345L, model.consentId)
    }

    @Test
    fun testConsentCreateFormToConsentCreateRequest() {
        val response = testConsentCreateFormData(providerId = 12345)
        val model = response.toConsentCreateRequest()
        assertEquals(12345L, model.providerId)
    }

    @Test
    fun testConsentUpdateFormToConsentUpdateRequest() {
        val response = testConsentUpdateFormData(sharingDuration = 1234500)
        val model = response.toConsentUpdateRequest()
        assertEquals(1234500L, model.sharingDuration)
    }

    @Test
    fun testCDRConfigurationResponseToCDRConfiguration() {
        val response = testCDRConfigurationData(configId = 12345L)
        val model = response.toCDRConfiguration()
        assertEquals(12345L, model.configId)
    }

    @Test
    fun testExternalPartyResponseTotestExternalParty() {
        val response = testExternalPartyResponseData(partyId = 12345L)
        val model = response.toExternalParty()
        assertEquals(12345L, model.partyId)
    }

    @Test
    fun testDisclosureConsentResponseToDisclosureConsent() {
        val response = testDisclosureConsentResponseData(status = ConsentStatus.WITHDRAWN)
        val model = response.toDisclosureConsent()
        assertEquals(ConsentStatus.WITHDRAWN, model.status)
    }
}
