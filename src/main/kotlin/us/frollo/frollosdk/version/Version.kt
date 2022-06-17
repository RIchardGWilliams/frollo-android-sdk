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

package us.frollo.frollosdk.version

import io.github.g00fy2.versioncompare.Version
import us.frollo.frollosdk.BuildConfig
import us.frollo.frollosdk.extensions.valueToString
import us.frollo.frollosdk.preferences.Preferences

internal class Version(private val pref: Preferences) {

    private var currentVersion = BuildConfig.SDK_VERSION_NAME
    private var previousVersion: String? = null
    private var versionHistory: MutableSet<String>

    init {
        previousVersion = pref.sdkVersion
        versionHistory = pref.sdkVersionHistory.toMutableSet()
    }

    fun migrationNeeded(): Boolean {
        previousVersion?.let { prev ->
            if (prev != currentVersion) {
                return true
            }
        } ?: run {
            // First install
            initialiseVersion()
        }
        return false
    }

    private fun initialiseVersion() {
        updatePreviousVersion(versionNumber = currentVersion)
    }

    fun migrateVersion() {
        if (previousVersion == null) return

        // Stubbed for future. Replace null check with let and iterate through versions

        updatePreviousVersion(currentVersion)
    }

    private fun updatePreviousVersion(versionNumber: String) {
        previousVersion = versionNumber
        versionHistory.add(versionNumber)

        pref.sdkVersion = previousVersion
        pref.sdkVersionHistory = versionHistory.toMutableList()
    }

    // NOTE: This method is an exception and is out of migrateVersion() method's
    // iteration because we need to do this before Keystore is initialized
    fun initializationVectorMigrationNeeded(): Boolean {
        return previousVersion?.let { lastVersion ->
            Version(lastVersion).isLowerThan("3.18.0")
        } ?: false
    }

    // NOTE: This method is an exception and is out of migrateVersion() method's
    // iteration because we need to do this before Keystore is initialized
    fun migrateInitializationVector() {
        val legacyIV = byteArrayOf(12, -18, 46, 125, -17, -120, -38, 79, 75, 93, -78, -31, -74, -35, -42, -70)
        pref.initialisationVector = legacyIV.valueToString()
    }
}
