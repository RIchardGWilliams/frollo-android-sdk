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

package us.frollo.frollosdk.database

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import us.frollo.frollosdk.core.testSDKConfig

class SDKDatabaseTest {

    private val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private lateinit var db: SDKDatabase

    @Before
    fun setUp() {
        AndroidThreeTen.init(app)
    }

    @Test
    fun testDBCreate() {
        db = SDKDatabase.getInstance(app, testSDKConfig())
        assertNotNull(db)
    }
}
