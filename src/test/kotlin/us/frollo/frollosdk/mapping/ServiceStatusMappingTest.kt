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
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutageType
import us.frollo.frollosdk.model.testServiceOutageResponseData

class ServiceStatusMappingTest {

    @Test
    fun testServiceOutageResponseToServiceOutage() {
        val response = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE)
        val model = response.toServiceOutage()
        assertEquals(ServiceOutageType.OUTAGE, model.type)
    }

    @Test
    fun testServiceOutageUpdate() {
        val outage1 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE).toServiceOutage().apply {
            outageId = 123
        }
        val outage2 = testServiceOutageResponseData(type = ServiceOutageType.INFO).toServiceOutage().apply {
            outageId = 123
        }
        outage1.update(outage2)
        assertEquals(ServiceOutageType.INFO, outage1.type)
        assertEquals(123L, outage1.outageId)
        assertEquals(outage2.startDate, outage1.startDate)
        assertEquals(outage2.endDate, outage1.endDate)
        assertEquals(outage2.duration, outage1.duration)
        assertEquals(outage2.message.description, outage1.message.description)
        assertEquals(outage2.message.title, outage1.message.title)
        assertEquals(outage2.message.summary, outage1.message.summary)
        assertEquals(outage2.message.actionName, outage1.message.actionName)
        assertEquals(outage2.message.actionUrl, outage1.message.actionUrl)
    }
}
