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

package us.frollo.frollosdk.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import us.frollo.frollosdk.extensions.generateByteArray
import us.frollo.frollosdk.extensions.valueToByteArray
import us.frollo.frollosdk.extensions.valueToString
import us.frollo.frollosdk.preferences.Preferences
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class Keystore(private val pref: Preferences) {

    companion object {
        private const val TAG = "Keystore"

        private const val KEY_ALIAS = "FrolloSDKKey"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val TRANSFORMATION_AES = "AES/CBC/PKCS7Padding"
    }

    private val initialisationVector: ByteArray
        get() {
            return pref.initialisationVector?.valueToByteArray() ?: run {
                val newIV = generateByteArray(16)
                pref.initialisationVector = newIV.valueToString()
                newIV
            }
        }

    private val mKeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)

    internal var isSetup = false

    internal fun setup() {
        try {
            mKeyStore.load(null)
            generateKeys()
            isSetup = true
        } catch (e: Exception) {
            Log.e("FrolloSDKLogger", "$TAG.setup : Error - Failed to load KeyStore")
        }
    }

    private fun generateKeys() {
        if (!mKeyStore.containsAlias(KEY_ALIAS)) aesKey()
    }

    internal fun encrypt(plainText: String?) =
        if (plainText?.isNotEmpty() == true) aesEncrypt(plainText) else null

    internal fun decrypt(cipherText: String?) =
        if (cipherText?.isNotEmpty() == true) aesDecrypt(cipherText) else null

    private fun aesKey() {
        try {
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                // Turn off use of random IV every time
                .setRandomizedEncryptionRequired(false)
                .build()

            // Initialize a Key generator using the the AES algorithm and KeyStore.
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        } catch (e: Exception) {
            Log.e("FrolloSDKLogger", "$TAG.aesKey : Error - Keys creation failed")
        }
    }

    private fun aesEncrypt(inputStr: String?): String? {
        var encryptedStr: String? = null
        try {
            val secretKey = mKeyStore.getKey(KEY_ALIAS, null) as SecretKey

            val cipher = Cipher.getInstance(TRANSFORMATION_AES)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(initialisationVector))

            val inputBuff = inputStr?.toByteArray(charset("UTF-8"))
            val encryptedData = cipher.doFinal(inputBuff)
            encryptedStr = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("FrolloSDKLogger", "$TAG.aesEncrypt : Error - Data encryption failed")
        }
        return encryptedStr
    }

    private fun aesDecrypt(encryptedStr: String?): String? {
        var decryptedStr: String? = null
        try {
            val secretKey = mKeyStore?.getKey(KEY_ALIAS, null) as SecretKey

            val cipher = Cipher.getInstance(TRANSFORMATION_AES)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(initialisationVector))

            val encryptedBuff = Base64.decode(encryptedStr, Base64.DEFAULT)
            val decryptedData = cipher.doFinal(encryptedBuff)
            decryptedStr = String(decryptedData, 0, decryptedData.size, charset("UTF-8"))
        } catch (e: Exception) {
            Log.e("FrolloSDKLogger", "$TAG.aesDecrypt : Error - Data decryption failed")
        }
        return decryptedStr
    }

    internal fun reset() {
        try {
            mKeyStore.deleteEntry(KEY_ALIAS)
            isSetup = false
        } catch (e: Exception) {
            Log.e("FrolloSDKLogger", "$TAG.reset : Error - Delete key failed")
        }
    }
}
