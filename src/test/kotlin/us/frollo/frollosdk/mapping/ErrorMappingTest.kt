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

import org.junit.Assert
import org.junit.Test
import us.frollo.frollosdk.error.APIErrorType
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.model.api.shared.APIErrorCode
import net.openid.appauth.AuthorizationException
import us.frollo.frollosdk.error.OAuthErrorType

class ErrorMappingTest {

    @Test
    fun testStringToAPIErrorResponseSuccess() {
        val jsonStr = "{\"error\":{\"error_code\":\"F0111\",\"error_message\":\"Invalid username or password\"}}"
        val response = jsonStr.toAPIErrorResponse()
        Assert.assertNotNull(response)
        Assert.assertEquals("F0111", response?.errorCode.toString())
        Assert.assertEquals("Invalid username or password", response?.errorMessage)
    }

    @Test
    fun testStringToAPIErrorResponseFail() {
        val jsonStr = "Unknown Error"
        val response = jsonStr.toAPIErrorResponse()
        Assert.assertNull(response)
    }

    @Test
    fun testIntToAPIErrorTypeSuccess() {
        val type = 401.toAPIErrorType(APIErrorCode.INVALID_USERNAME_PASSWORD)
        Assert.assertEquals(APIErrorType.INVALID_USERNAME_PASSWORD, type)
    }

    @Test
    fun testStringToDataError() {
        val jsonStr = "{\"type\":\"AUTHENTICATION\",\"sub_type\":\"MISSING_REFRESH_TOKEN\"}"
        val value = jsonStr.toDataError()
        Assert.assertNotNull(value)
        Assert.assertEquals(DataErrorType.AUTHENTICATION, value?.type)
        Assert.assertEquals(DataErrorSubType.MISSING_REFRESH_TOKEN, value?.subType)
    }

    @Test
    fun testAuthorizationExceptionToOAuthErrorType() {
        val exception = AuthorizationException.AuthorizationRequestErrors.ACCESS_DENIED
        val type = exception.toOAuthErrorType()
        Assert.assertEquals(OAuthErrorType.ACCESS_DENIED, type)
    }
}