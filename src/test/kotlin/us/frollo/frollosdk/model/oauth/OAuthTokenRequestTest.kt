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

package us.frollo.frollosdk.model.oauth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import us.frollo.frollosdk.model.testOAuthTokenRequestData
import us.frollo.frollosdk.testutils.randomString

class OAuthTokenRequestTest {

    @Test
    fun testValid() {
        var request = testOAuthTokenRequestData(grantType = OAuthGrantType.PASSWORD, password = randomString(8))
        assertTrue(request.isValid())

        request = testOAuthTokenRequestData(grantType = OAuthGrantType.PASSWORD, password = null, legacyToken = null)
        assertFalse(request.isValid())

        request = testOAuthTokenRequestData(grantType = OAuthGrantType.REFRESH_TOKEN, refreshToken = null)
        assertFalse(request.isValid())

        request = testOAuthTokenRequestData(grantType = OAuthGrantType.AUTHORIZATION_CODE, authorizationCode = null)
        assertFalse(request.isValid())
    }
}
