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

package us.frollo.frollosdk

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import us.frollo.frollosdk.address.AddressManagement
import us.frollo.frollosdk.affordability.Affordability
import us.frollo.frollosdk.aggregation.Aggregation
import us.frollo.frollosdk.appconfiguration.AppConfiguration
import us.frollo.frollosdk.authentication.OAuth2Authentication
import us.frollo.frollosdk.authentication.OAuth2Helper
import us.frollo.frollosdk.bills.Bills
import us.frollo.frollosdk.budgets.Budgets
import us.frollo.frollosdk.cards.Cards
import us.frollo.frollosdk.contacts.Contacts
import us.frollo.frollosdk.core.AppInfo
import us.frollo.frollosdk.core.DeviceInfo
import us.frollo.frollosdk.core.testSDKConfig
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.events.Events
import us.frollo.frollosdk.goals.Goals
import us.frollo.frollosdk.images.Images
import us.frollo.frollosdk.keystore.Keystore
import us.frollo.frollosdk.kyc.KYC
import us.frollo.frollosdk.managedproducts.ManagedProducts
import us.frollo.frollosdk.messages.Messages
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.DATokenAPI
import us.frollo.frollosdk.network.api.TokenAPI
import us.frollo.frollosdk.notifications.Notifications
import us.frollo.frollosdk.paydays.Paydays
import us.frollo.frollosdk.payments.Payments
import us.frollo.frollosdk.preferences.Preferences
import us.frollo.frollosdk.reports.Reports
import us.frollo.frollosdk.servicestatus.ServiceStatusManagement
import us.frollo.frollosdk.statements.Statements
import us.frollo.frollosdk.surveys.Surveys
import us.frollo.frollosdk.user.UserManagement

abstract class BaseAndroidTest {

    companion object {
        const val TOKEN_URL = "token/"
        const val REVOKE_TOKEN_URL = "revoke/"
    }

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    lateinit var mockServer: MockWebServer
    lateinit var mockTokenServer: MockWebServer
    lateinit var mockRevokeTokenServer: MockWebServer
    lateinit var network: NetworkService
    lateinit var preferences: Preferences
    lateinit var keystore: Keystore
    lateinit var database: SDKDatabase
    lateinit var oAuth2Authentication: OAuth2Authentication
    lateinit var aggregation: Aggregation
    lateinit var userManagement: UserManagement
    lateinit var bills: Bills
    lateinit var events: Events
    lateinit var messages: Messages
    lateinit var notifications: Notifications
    lateinit var reports: Reports
    lateinit var surveys: Surveys
    lateinit var goals: Goals
    lateinit var budgets: Budgets
    lateinit var images: Images
    lateinit var payments: Payments
    lateinit var contacts: Contacts
    lateinit var kyc: KYC
    lateinit var managedProducts: ManagedProducts
    lateinit var cards: Cards
    lateinit var paydays: Paydays
    lateinit var addressManagement: AddressManagement
    lateinit var statements: Statements
    lateinit var serviceStatusManagement: ServiceStatusManagement
    lateinit var affordability: Affordability
    lateinit var appInfo: AppInfo
    lateinit var appConfiguration: AppConfiguration

    val scopes = listOf("offline_access", "openid", "email")

    protected open fun initSetup(daOAuth2Login: Boolean = false) {
        mockServer = MockWebServer()
        mockServer.start()
        val baseUrl = mockServer.url("/")

        mockTokenServer = MockWebServer()
        mockTokenServer.start()
        val baseTokenUrl = mockTokenServer.url("/$TOKEN_URL")

        mockRevokeTokenServer = MockWebServer()
        mockRevokeTokenServer.start()
        val baseRevokeTokenUrl = mockRevokeTokenServer.url("/$REVOKE_TOKEN_URL")

        val config = testSDKConfig(
            serverUrl = baseUrl.toString(),
            tokenUrl = baseTokenUrl.toString(),
            revokeTokenURL = baseRevokeTokenUrl.toString(),
            daOAuth2Login = daOAuth2Login
        )
        if (!FrolloSDK.isSetup) {
            FrolloSDK.context = app
            FrolloSDK.setup(config) {}
        }

        appInfo = AppInfo(app)
        preferences = Preferences(app)
        keystore = Keystore(preferences)
        keystore.setup()
        database = SDKDatabase.getInstance(app, config)
        val oAuth = OAuth2Helper(config = config)
        network = NetworkService(oAuth2Helper = oAuth, keystore = keystore, pref = preferences, appInfo = appInfo)

        oAuth2Authentication = OAuth2Authentication(oAuth, preferences).apply {
            tokenAPI = network.createAuth(TokenAPI::class.java)
            revokeTokenAPI = network.createRevoke(TokenAPI::class.java)
            if (config.isDAOAuth2LoginEnabled()) {
                daTokenAPI = network.createDATokenAuth(DATokenAPI::class.java)
            }
            authToken = network.authToken
        }
        network.accessTokenProvider = oAuth2Authentication
        network.authenticationCallback = oAuth2Authentication

        userManagement = UserManagement(DeviceInfo(app), network, config.clientId, database, preferences)
        aggregation = Aggregation(network, database, LocalBroadcastManager.getInstance(app))
        bills = Bills(network, database, aggregation)
        events = Events(network)
        messages = Messages(network, database)
        notifications = Notifications(userManagement, events, messages)
        reports = Reports(network, database, aggregation)
        surveys = Surveys(network)
        goals = Goals(network, database)
        budgets = Budgets(network, database)
        images = Images(network, database)
        payments = Payments(network)
        contacts = Contacts(network, database)
        kyc = KYC(network)
        managedProducts = ManagedProducts(network)
        cards = Cards(network, database)
        paydays = Paydays(network, database)
        addressManagement = AddressManagement(network, database)
        statements = Statements(network)
        affordability = Affordability(network)
        serviceStatusManagement = ServiceStatusManagement(network, database)
        appConfiguration = AppConfiguration(network, database)

        AndroidThreeTen.init(app)
    }

    protected open fun tearDown() {
        mockServer.shutdown()
        mockTokenServer.shutdown()
        mockRevokeTokenServer.shutdown()
        network.reset()
        oAuth2Authentication.reset()
        preferences.resetAll()
        database.clearAllTables()
    }

    fun clearLoggedInPreferences() {
        preferences.resetLoggedIn()
        preferences.resetEncryptedAccessToken()
        preferences.resetEncryptedRefreshToken()
        preferences.resetAccessTokenExpiry()
    }
}
