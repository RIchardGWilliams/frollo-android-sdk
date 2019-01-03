package us.frollo.frollosdk.extensions

import org.junit.Assert
import org.junit.Test
import us.frollo.frollosdk.model.getTestUserResponse

class ModelExtensionTest {

    @Test
    fun testUserResponseStripTokens() {
        val originalResponse = getTestUserResponse()
        val strippedResponse = originalResponse.stripTokens()
        Assert.assertNull(strippedResponse.refreshToken)
        Assert.assertNull(strippedResponse.accessToken)
        Assert.assertNull(strippedResponse.accessTokenExp)
    }

    @Test
    fun testUserResponseFetchTokens() {
        val originalResponse = getTestUserResponse()
        val tokenResponse = originalResponse.fetchTokens()
        Assert.assertEquals(originalResponse.refreshToken, tokenResponse.refreshToken)
        Assert.assertEquals(originalResponse.accessToken, tokenResponse.accessToken)
        Assert.assertEquals(originalResponse.accessTokenExp, tokenResponse.accessTokenExp)
    }
}