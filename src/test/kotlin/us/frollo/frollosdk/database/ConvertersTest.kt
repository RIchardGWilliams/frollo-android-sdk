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

import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import us.frollo.frollosdk.model.api.cards.DigitalWallet
import us.frollo.frollosdk.model.api.user.payid.UserPayIdAccountResponse
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountClassification
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountFeatureSubType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountFeatureType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountGroup
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountOwnerType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountRelationship
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountStatus
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountSubType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.BalanceTier
import us.frollo.frollosdk.model.coredata.aggregation.accounts.CDRProductInformation
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyPurpose
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyZoning
import us.frollo.frollosdk.model.coredata.aggregation.accounts.RelatedAccount
import us.frollo.frollosdk.model.coredata.aggregation.accounts.StatementOrPaymentFrequency
import us.frollo.frollosdk.model.coredata.aggregation.accounts.VehicleType
import us.frollo.frollosdk.model.coredata.aggregation.merchants.MerchantLocation
import us.frollo.frollosdk.model.coredata.aggregation.merchants.MerchantType
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.AccountRefreshAdditionalStatus
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.AccountRefreshStatus
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.AccountRefreshSubStatus
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderAuthType
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderContainerName
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderEncryptionType
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFormType
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderLoginForm
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderMFAType
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderStatus
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategoryType
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionBaseType
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionStatus
import us.frollo.frollosdk.model.coredata.bills.BillFrequency
import us.frollo.frollosdk.model.coredata.bills.BillPaymentStatus
import us.frollo.frollosdk.model.coredata.bills.BillStatus
import us.frollo.frollosdk.model.coredata.bills.BillType
import us.frollo.frollosdk.model.coredata.budgets.BudgetFrequency
import us.frollo.frollosdk.model.coredata.budgets.BudgetStatus
import us.frollo.frollosdk.model.coredata.budgets.BudgetTrackingStatus
import us.frollo.frollosdk.model.coredata.budgets.BudgetType
import us.frollo.frollosdk.model.coredata.cards.CardDesignType
import us.frollo.frollosdk.model.coredata.cards.CardIssuer
import us.frollo.frollosdk.model.coredata.cards.CardStatus
import us.frollo.frollosdk.model.coredata.cards.CardType
import us.frollo.frollosdk.model.coredata.cdr.CDRModel
import us.frollo.frollosdk.model.coredata.cdr.CDRParty
import us.frollo.frollosdk.model.coredata.cdr.CDRPartyType
import us.frollo.frollosdk.model.coredata.cdr.CDRPermissionDetail
import us.frollo.frollosdk.model.coredata.cdr.CDRPolicy
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalParty
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyCompany
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyType
import us.frollo.frollosdk.model.coredata.cdr.SharingDuration
import us.frollo.frollosdk.model.coredata.cdr.TrustedAdvisorType
import us.frollo.frollosdk.model.coredata.contacts.BankAddress
import us.frollo.frollosdk.model.coredata.contacts.BankDetails
import us.frollo.frollosdk.model.coredata.contacts.Beneficiary
import us.frollo.frollosdk.model.coredata.contacts.CRNType
import us.frollo.frollosdk.model.coredata.contacts.DigitalWalletProvider
import us.frollo.frollosdk.model.coredata.contacts.DigitalWalletType
import us.frollo.frollosdk.model.coredata.contacts.PayIDType
import us.frollo.frollosdk.model.coredata.contacts.PaymentDetails
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod
import us.frollo.frollosdk.model.coredata.goals.GoalFrequency
import us.frollo.frollosdk.model.coredata.goals.GoalStatus
import us.frollo.frollosdk.model.coredata.goals.GoalTarget
import us.frollo.frollosdk.model.coredata.goals.GoalTrackingStatus
import us.frollo.frollosdk.model.coredata.goals.GoalTrackingType
import us.frollo.frollosdk.model.coredata.messages.ContentType
import us.frollo.frollosdk.model.coredata.messages.OpenMode
import us.frollo.frollosdk.model.coredata.payday.PaydayFrequency
import us.frollo.frollosdk.model.coredata.payday.PaydayStatus
import us.frollo.frollosdk.model.coredata.payments.NPPServiceIdType
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.ReportPeriod
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutageType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdk.model.coredata.user.Attribution
import us.frollo.frollosdk.model.coredata.user.FeatureFlag
import us.frollo.frollosdk.model.coredata.user.Gender
import us.frollo.frollosdk.model.coredata.user.HouseholdType
import us.frollo.frollosdk.model.coredata.user.Industry
import us.frollo.frollosdk.model.coredata.user.Occupation
import us.frollo.frollosdk.model.coredata.user.RegisterStep
import us.frollo.frollosdk.model.coredata.user.TFNStatus
import us.frollo.frollosdk.model.coredata.user.UserStatus
import us.frollo.frollosdk.model.coredata.user.payid.UserPayIdAccountStatus
import us.frollo.frollosdk.model.testAccountFeatureDetailsData
import us.frollo.frollosdk.model.testAccountFeaturesData
import us.frollo.frollosdk.model.testCDRPermissionData
import java.math.BigDecimal

class ConvertersTest {

    @Test
    fun testStringToListOfString() {
        val string = "|welcome|welcome_event|event|"
        val list = Converters.instance.stringToListOfString(string)
        assertNotNull(list)
        assertTrue(list?.size == 3)
        assertEquals("welcome", list?.get(0))
        assertEquals("welcome_event", list?.get(1))
        assertEquals("event", list?.get(2))

        assertNull(Converters.instance.stringToListOfString(null))
    }

    @Test
    fun testStringFromListOfString() {
        val list = mutableListOf("welcome", "welcome_event", "event")
        val string = Converters.instance.stringFromListOfString(list)
        assertEquals("|welcome|welcome_event|event|", string)
    }

    @Test
    fun testStringToListOfFeatureFlag() {
        val json = "[{\"feature\":\"demo\",\"enabled\":true},{\"feature\":\"aggregation\",\"enabled\":false}]"
        val features = Converters.instance.stringToListOfFeatureFlag(json)
        assertNotNull(features)
        assertTrue(features?.size == 2)
        assertEquals("demo", features?.get(0)?.feature)
        assertEquals(true, features?.get(0)?.enabled)
        assertEquals("aggregation", features?.get(1)?.feature)
        assertEquals(false, features?.get(1)?.enabled)

        assertNull(Converters.instance.stringToListOfFeatureFlag(null))
    }

    @Test
    fun testStringFromListOfFeatureFlag() {
        val features = mutableListOf(FeatureFlag(feature = "demo", enabled = true), FeatureFlag(feature = "aggregation", enabled = false))
        val json = Converters.instance.stringFromListOfFeatureFlag(features)
        assertEquals("[{\"feature\":\"demo\",\"enabled\":true},{\"feature\":\"aggregation\",\"enabled\":false}]", json)
    }

    @Test
    fun testStringToUserStatus() {
        val status = Converters.instance.stringToUserStatus("BUDGET_READY")
        assertEquals(UserStatus.BUDGET_READY, status)

        assertNull(Converters.instance.stringToUserStatus(null))
    }

    @Test
    fun testStringFromUserStatus() {
        val str = Converters.instance.stringFromUserStatus(UserStatus.BUDGET_READY)
        assertEquals("BUDGET_READY", str)

        assertNull(Converters.instance.stringFromUserStatus(null))
    }

    @Test
    fun testStringToGender() {
        val status = Converters.instance.stringToGender("MALE")
        assertEquals(Gender.MALE, status)

        assertNull(Converters.instance.stringToGender(null))
    }

    @Test
    fun testStringFromGender() {
        val str = Converters.instance.stringFromGender(Gender.MALE)
        assertEquals("MALE", str)

        assertNull(Converters.instance.stringFromGender(null))
    }

    @Test
    fun testStringToHouseholdType() {
        val status = Converters.instance.stringToHouseholdType("COUPLE_WITH_KIDS")
        assertEquals(HouseholdType.COUPLE_WITH_KIDS, status)

        assertNull(Converters.instance.stringToHouseholdType(null))
    }

    @Test
    fun testStringFromHouseholdType() {
        val str = Converters.instance.stringFromHouseholdType(HouseholdType.COUPLE_WITH_KIDS)
        assertEquals("COUPLE_WITH_KIDS", str)

        assertNull(Converters.instance.stringFromHouseholdType(null))
    }

    @Test
    fun testStringToOccupation() {
        val status = Converters.instance.stringToOccupation("PROFESSIONALS")
        assertEquals(Occupation.PROFESSIONALS, status)

        assertNull(Converters.instance.stringToOccupation(null))
    }

    @Test
    fun testStringFromOccupation() {
        val str = Converters.instance.stringFromOccupation(Occupation.PROFESSIONALS)
        assertEquals("PROFESSIONALS", str)

        assertNull(Converters.instance.stringFromOccupation(null))
    }

    @Test
    fun testStringToIndustry() {
        val status = Converters.instance.stringToIndustry("EDUCATION_AND_TRAINING")
        assertEquals(Industry.EDUCATION_AND_TRAINING, status)

        assertNull(Converters.instance.stringToIndustry(null))
    }

    @Test
    fun testStringFromIndustry() {
        val str = Converters.instance.stringFromIndustry(Industry.EDUCATION_AND_TRAINING)
        assertEquals("EDUCATION_AND_TRAINING", str)

        assertNull(Converters.instance.stringFromIndustry(null))
    }

    @Test
    fun testStringToAttribution() {
        val json = "{\"network\":\"organic\",\"campaign\":\"frollo\"}"
        val attr = Converters.instance.stringToAttribution(json)
        assertNotNull(attr)
        assertEquals("organic", attr?.network)
        assertEquals("frollo", attr?.campaign)

        assertNull(Converters.instance.stringToAttribution(null))
    }

    @Test
    fun testStringFromAttribution() {
        val attr = Attribution(network = "organic", campaign = "frollo")
        val json = Converters.instance.stringFromAttribution(attr)
        assertEquals("{\"network\":\"organic\",\"campaign\":\"frollo\"}", json)
    }

    @Test
    fun testStringToListOfRegisterStep() {
        val json = "[{\"key\":\"kyc\",\"index\":0,\"required\":true,\"completed\":true},{\"key\":\"survey\",\"index\":1,\"required\":true,\"completed\":false}]"
        val steps = Converters.instance.stringToListOfRegisterStep(json)
        assertNotNull(steps)
        assertEquals(2, steps?.size)
        assertEquals("kyc", steps?.get(0)?.key)
        assertEquals(0, steps?.get(0)?.index)
        assertEquals(true, steps?.get(0)?.required)
        assertEquals(true, steps?.get(0)?.completed)
        assertEquals("survey", steps?.get(1)?.key)
        assertEquals(1, steps?.get(1)?.index)
        assertEquals(true, steps?.get(1)?.required)
        assertEquals(false, steps?.get(1)?.completed)
        assertNull(Converters.instance.stringToListOfRegisterStep(null))
    }

    @Test
    fun testStringFromListOfRegisterStep() {
        val steps = listOf(
            RegisterStep(
                key = "kyc",
                index = 0,
                required = true,
                completed = true
            ),
            RegisterStep(
                key = "survey",
                index = 1,
                required = true,
                completed = false
            )
        )
        val json = Converters.instance.stringFromListOfRegisterStep(steps)
        assertEquals("[{\"key\":\"kyc\",\"index\":0,\"required\":true,\"completed\":true},{\"key\":\"survey\",\"index\":1,\"required\":true,\"completed\":false}]", json)
    }

    @Test
    fun testStringToTFNStatus() {
        val status = Converters.instance.stringToTFNStatus("RECEIVED")
        assertEquals(TFNStatus.RECEIVED, status)

        assertNull(Converters.instance.stringToTFNStatus(null))
    }

    @Test
    fun testStringFromTFNStatus() {
        val str = Converters.instance.stringFromTFNStatus(TFNStatus.RECEIVED)
        assertEquals("RECEIVED", str)

        assertNull(Converters.instance.stringFromTFNStatus(null))
    }

    @Test
    fun testStringFromDigitalWallet() {
        val str = Converters.instance.stringFromListOfDigitalWallet(listOf(DigitalWallet.GOOGLE_PAY, DigitalWallet.SAMSUNG_PAY))
        assertEquals("[\"google_pay\",\"samsung_pay\"]", str)

        assertEquals(null, Converters.instance.stringFromListOfDigitalWallet(null))
    }
    @Test
    fun testStringToDigitalWallets() {
        val list = Converters.instance.stringToListOfDigitalWallet("[\"google_pay\",\"samsung_pay\"]")
        assertEquals(listOf(DigitalWallet.GOOGLE_PAY, DigitalWallet.SAMSUNG_PAY), list)

        assertEquals(null, Converters.instance.stringToListOfDigitalWallet(null))
    }

    @Test
    fun testStringToContentType() {
        val status = Converters.instance.stringToContentType("IMAGE")
        assertEquals(ContentType.IMAGE, status)

        assertEquals(ContentType.TEXT, Converters.instance.stringToContentType(null))
    }

    @Test
    fun testStringFromContentType() {
        val str = Converters.instance.stringFromContentType(ContentType.IMAGE)
        assertEquals("IMAGE", str)

        assertEquals("TEXT", Converters.instance.stringFromContentType(null))
    }

    @Test
    fun testStringToOpenMode() {
        val status = Converters.instance.stringToOpenMode("INTERNAL")
        assertEquals(OpenMode.INTERNAL, status)
        assertEquals(OpenMode.INTERNAL, Converters.instance.stringToOpenMode(null))
    }

    @Test
    fun testStringFromOpenMode() {
        val str = Converters.instance.stringFromOpenMode(OpenMode.INTERNAL)
        assertEquals("INTERNAL", str)
        assertEquals("INTERNAL", Converters.instance.stringFromOpenMode(null))
    }

    @Test
    fun testStringToProviderStatus() {
        val status = Converters.instance.stringToProviderStatus("SUPPORTED")
        assertEquals(ProviderStatus.SUPPORTED, status)

        assertNull(Converters.instance.stringToProviderStatus(null))
    }

    @Test
    fun testStringFromProviderStatus() {
        val str = Converters.instance.stringFromProviderStatus(ProviderStatus.SUPPORTED)
        assertEquals("SUPPORTED", str)

        assertNull(Converters.instance.stringFromProviderStatus(null))
    }

    @Test
    fun testStringToProviderAuthType() {
        val status = Converters.instance.stringToProviderAuthType("CREDENTIALS")
        assertEquals(ProviderAuthType.CREDENTIALS, status)

        assertEquals(ProviderAuthType.UNKNOWN, Converters.instance.stringToProviderAuthType(null))
    }

    @Test
    fun testStringFromProviderAuthType() {
        val str = Converters.instance.stringFromProviderAuthType(ProviderAuthType.CREDENTIALS)
        assertEquals("CREDENTIALS", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromProviderAuthType(null))
    }

    @Test
    fun testStringToProviderMFAType() {
        val status = Converters.instance.stringToProviderMFAType("QUESTION")
        assertEquals(ProviderMFAType.QUESTION, status)

        assertEquals(ProviderMFAType.UNKNOWN, Converters.instance.stringToProviderMFAType(null))
    }

    @Test
    fun testStringFromProviderMFAType() {
        val str = Converters.instance.stringFromProviderMFAType(ProviderMFAType.QUESTION)
        assertEquals("QUESTION", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromProviderMFAType(null))
    }

    @Test
    fun testStringToProviderLoginForm() {
        val json = "{\"id\":\"13039\",\"forgetPasswordURL\":\"https://ib.mebank.com.au/auth/ib/login.html\",\"formType\":\"login\",\"row\":[]}"
        val form = Converters.instance.stringToProviderLoginForm(json)
        assertNotNull(form)
        assertEquals("13039", form?.formId)
        assertEquals("https://ib.mebank.com.au/auth/ib/login.html", form?.forgetPasswordUrl)
        assertEquals(ProviderFormType.LOGIN, form?.formType)
        assertTrue(form?.rows?.isEmpty() == true)

        assertNull(Converters.instance.stringToProviderLoginForm(null))
    }

    @Test
    fun testStringFromProviderLoginForm() {
        val form = ProviderLoginForm(
            formId = "13039",
            forgetPasswordUrl = "https://ib.mebank.com.au/auth/ib/login.html",
            formType = ProviderFormType.LOGIN,
            rows = listOf(),
            help = null,
            mfaInfoText = null,
            mfaInfoTitle = null,
            mfaTimeout = null
        )
        val json = Converters.instance.stringFromProviderLoginForm(form)
        assertEquals("{\"id\":\"13039\",\"forgetPasswordURL\":\"https://ib.mebank.com.au/auth/ib/login.html\",\"formType\":\"login\",\"row\":[]}", json)
    }

    @Test
    fun testStringToProviderEncryptionType() {
        val status = Converters.instance.stringToProviderEncryptionType("ENCRYPT_VALUES")
        assertEquals(ProviderEncryptionType.ENCRYPT_VALUES, status)

        assertNull(Converters.instance.stringToProviderEncryptionType(null))
    }

    @Test
    fun testStringFromProviderEncryptionType() {
        val str = Converters.instance.stringFromProviderEncryptionType(ProviderEncryptionType.ENCRYPT_VALUES)
        assertEquals("ENCRYPT_VALUES", str)

        assertNull(Converters.instance.stringFromProviderEncryptionType(null))
    }

    @Test
    fun testStringToListOfProviderContainerName() {
        val string = "|bank|credit_card|bill|"
        val list = Converters.instance.stringToListOfProviderContainerName(string)
        assertNotNull(list)
        assertTrue(list?.size == 3)
        assertEquals(ProviderContainerName.BANK, list?.get(0))
        assertEquals(ProviderContainerName.CREDIT_CARD, list?.get(1))
        assertEquals(ProviderContainerName.BILL, list?.get(2))

        assertNull(Converters.instance.stringToListOfProviderContainerName(null))
    }

    @Test
    fun testStringFromListOfProviderContainerName() {
        val list = mutableListOf(ProviderContainerName.BANK, ProviderContainerName.CREDIT_CARD, ProviderContainerName.BILL)
        val string = Converters.instance.stringFromListOfProviderContainerName(list)
        assertEquals("|bank|credit_card|bill|", string)
    }

    @Test
    fun testStringToAggregatorType() {
        val status = Converters.instance.stringToAggregatorType("CDR")
        assertEquals(AggregatorType.CDR, status)

        assertEquals(AggregatorType.UNKNOWN, Converters.instance.stringToAggregatorType(null))
    }

    @Test
    fun testStringFromAggregatorType() {
        val str = Converters.instance.stringFromAggregatorType(AggregatorType.CDR)
        assertEquals("CDR", str)

        assertEquals("YODLEE", Converters.instance.stringFromAggregatorType(AggregatorType.YODLEE))
        assertEquals("UNKNOWN", Converters.instance.stringFromAggregatorType(null))
    }

    @Test
    fun testStringToListOfCDRPermission() {
        val json = "[{\"id\":\"account_details\",\"title\":\"Account balance and details\",\"description\":\"We leverage...\",\"required\":\"true\",\"details\":[{\"id\":\"account_name\",\"description\":\"Name of account\"}],\"placement\":10},{\"id\":\"transaction_details\",\"title\":\"Transaction and details\",\"description\":\"We leverage...\",\"required\":\"false\",\"details\":[{\"id\":\"transaction_name\",\"description\":\"Name of transaction\"}],\"placement\":10}]"
        val permissions = Converters.instance.stringToListOfCDRPermission(json)
        assertNotNull(permissions)
        assertEquals(2, permissions?.size)
        assertEquals("account_details", permissions?.get(0)?.permissionId)
        assertEquals("Account balance and details", permissions?.get(0)?.title)
        assertEquals("We leverage...", permissions?.get(0)?.description)
        assertEquals(true, permissions?.get(0)?.required)
        assertEquals(1, permissions?.get(0)?.details?.size)
        assertEquals(10, permissions?.get(0)?.placement)
        assertEquals("transaction_details", permissions?.get(1)?.permissionId)
        assertEquals("Transaction and details", permissions?.get(1)?.title)
        assertEquals("We leverage...", permissions?.get(1)?.description)
        assertEquals(false, permissions?.get(1)?.required)
        assertEquals(1, permissions?.get(1)?.details?.size)
        assertEquals(10, permissions?.get(1)?.placement)

        assertNull(Converters.instance.stringToListOfCDRPermission(null))
    }

    @Test
    fun testStringFromListOfCDRPermission() {
        val permissions = testCDRPermissionData()
        val json = Converters.instance.stringFromListOfCDRPermission(permissions)
        assertEquals("[{\"id\":\"account_details\",\"title\":\"Account balance and details\",\"description\":\"We leverage...\",\"required\":true,\"details\":[{\"id\":\"account_name\",\"description\":\"Name of account\"}],\"placement\":10},{\"id\":\"transaction_details\",\"title\":\"Transaction and details\",\"description\":\"We leverage...\",\"required\":false,\"details\":[{\"id\":\"transaction_name\",\"description\":\"Name of transaction\"}],\"placement\":10}]", json)
    }

    @Test
    fun testStringToListOfCDRPermissionDetail() {
        val json = "[{\"id\":\"account_name\",\"description\":\"Name of account\"},{\"id\":\"transaction_name\",\"description\":\"Name of transaction\"}]"
        val details = Converters.instance.stringToListOfCDRPermissionDetail(json)
        assertNotNull(details)
        assertEquals(2, details?.size)
        assertEquals("account_name", details?.get(0)?.detailId)
        assertEquals("Name of account", details?.get(0)?.description)
        assertEquals("transaction_name", details?.get(1)?.detailId)
        assertEquals("Name of transaction", details?.get(1)?.description)

        assertNull(Converters.instance.stringToListOfCDRPermissionDetail(null))
    }

    @Test
    fun testStringFromListOfCDRPermissionDetail() {
        val details = listOf(
            CDRPermissionDetail(
                detailId = "account_name",
                description = "Name of account"
            ),
            CDRPermissionDetail(
                detailId = "transaction_name",
                description = "Name of transaction"
            )
        )
        val json = Converters.instance.stringFromListOfCDRPermissionDetail(details)
        assertEquals("[{\"id\":\"account_name\",\"description\":\"Name of account\"},{\"id\":\"transaction_name\",\"description\":\"Name of transaction\"}]", json)
    }

    @Test
    fun testStringToAccountRefreshStatus() {
        val status = Converters.instance.stringToAccountRefreshStatus("SUCCESS")
        assertEquals(AccountRefreshStatus.SUCCESS, status)

        assertEquals(AccountRefreshStatus.UPDATING, Converters.instance.stringToAccountRefreshStatus(null))
    }

    @Test
    fun testStringFromAccountRefreshStatus() {
        val str = Converters.instance.stringFromAccountRefreshStatus(AccountRefreshStatus.SUCCESS)
        assertEquals("SUCCESS", str)

        assertEquals("UPDATING", Converters.instance.stringFromAccountRefreshStatus(null))
    }

    @Test
    fun testStringToAccountRefreshSubStatus() {
        val status = Converters.instance.stringToAccountRefreshSubStatus("SUCCESS")
        assertEquals(AccountRefreshSubStatus.SUCCESS, status)

        assertNull(Converters.instance.stringToAccountRefreshSubStatus(null))
    }

    @Test
    fun testStringFromAccountRefreshSubStatus() {
        val str = Converters.instance.stringFromAccountRefreshSubStatus(AccountRefreshSubStatus.SUCCESS)
        assertEquals("SUCCESS", str)

        assertNull(Converters.instance.stringFromAccountRefreshSubStatus(null))
    }

    @Test
    fun testStringToAccountRefreshAdditionalStatus() {
        val status = Converters.instance.stringToAccountRefreshAdditionalStatus("ACCEPT_SPLASH")
        assertEquals(AccountRefreshAdditionalStatus.ACCEPT_SPLASH, status)

        assertNull(Converters.instance.stringToAccountRefreshAdditionalStatus(null))
    }

    @Test
    fun testStringFromAccountRefreshAdditionalStatus() {
        val str = Converters.instance.stringFromAccountRefreshAdditionalStatus(AccountRefreshAdditionalStatus.ACCEPT_SPLASH)
        assertEquals("ACCEPT_SPLASH", str)

        assertNull(Converters.instance.stringFromAccountRefreshAdditionalStatus(null))
    }

    @Test
    fun testStringToBigDecimal() {
        val value1 = (123.00001).toBigDecimal()
        val value2 = Converters.instance.stringToBigDecimal("123.00001")
        assertEquals(value1, value2)
    }

    @Test
    fun testStringFromBigDecimal() {
        val value = (123.00001).toBigDecimal()
        val valueStr = Converters.instance.stringFromBigDecimal(value)
        assertEquals("123.00001", valueStr)
    }

    @Test
    fun testStringToAccountStatus() {
        val status = Converters.instance.stringToAccountStatus("ACTIVE")
        assertEquals(AccountStatus.ACTIVE, status)

        assertNull(Converters.instance.stringToAccountStatus(null))
    }

    @Test
    fun testStringFromAccountStatus() {
        val str = Converters.instance.stringFromAccountStatus(AccountStatus.ACTIVE)
        assertEquals("ACTIVE", str)

        assertNull(Converters.instance.stringFromAccountStatus(null))
    }

    @Test
    fun testStringToAccountType() {
        val status = Converters.instance.stringToAccountType("BANK")
        assertEquals(AccountType.BANK, status)

        assertEquals(AccountType.UNKNOWN, Converters.instance.stringToAccountType(null))
    }

    @Test
    fun testStringFromAccountType() {
        val str = Converters.instance.stringFromAccountType(AccountType.BANK)
        assertEquals("BANK", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromAccountType(null))
    }

    @Test
    fun testStringToAccountClassification() {
        val status = Converters.instance.stringToAccountClassification("ADD_ON_CARD")
        assertEquals(AccountClassification.ADD_ON_CARD, status)

        assertEquals(AccountClassification.OTHER, Converters.instance.stringToAccountClassification(null))
    }

    @Test
    fun testStringFromAccountClassification() {
        val str = Converters.instance.stringFromAccountClassification(AccountClassification.ADD_ON_CARD)
        assertEquals("ADD_ON_CARD", str)

        assertEquals("OTHER", Converters.instance.stringFromAccountClassification(null))
    }

    @Test
    fun testStringToAccountSubType() {
        val status = Converters.instance.stringToAccountSubType("AUTO_INSURANCE")
        assertEquals(AccountSubType.AUTO_INSURANCE, status)

        assertEquals(AccountSubType.OTHER, Converters.instance.stringToAccountSubType(null))
    }

    @Test
    fun testStringFromAccountSubType() {
        val str = Converters.instance.stringFromAccountSubType(AccountSubType.AUTO_INSURANCE)
        assertEquals("AUTO_INSURANCE", str)

        assertEquals("OTHER", Converters.instance.stringFromAccountSubType(null))
    }

    @Test
    fun testStringToAccountGroup() {
        val status = Converters.instance.stringToAccountGroup("INVESTMENT")
        assertEquals(AccountGroup.INVESTMENT, status)

        assertEquals(AccountGroup.OTHER, Converters.instance.stringToAccountGroup(null))
    }

    @Test
    fun testStringFromAccountGroup() {
        val str = Converters.instance.stringFromAccountGroup(AccountGroup.INVESTMENT)
        assertEquals("INVESTMENT", str)

        assertEquals("OTHER", Converters.instance.stringFromAccountGroup(null))
    }

    @Test
    fun testStringToListOfBalanceTier() {
        val json = "[{\"description\":\"Below average\",\"min\":0,\"max\":549},{\"description\":\"Above average\",\"min\":550,\"max\":700}]"
        val tiers = Converters.instance.stringToListOfBalanceTier(json)
        assertNotNull(tiers)
        assertTrue(tiers?.size == 2)
        assertEquals("Below average", tiers?.get(0)?.description)
        assertEquals(549, tiers?.get(0)?.max)
        assertEquals(0, tiers?.get(0)?.min)
        assertEquals("Above average", tiers?.get(1)?.description)
        assertEquals(700, tiers?.get(1)?.max)
        assertEquals(550, tiers?.get(1)?.min)

        assertNull(Converters.instance.stringToListOfBalanceTier(null))
    }

    @Test
    fun testStringFromListOfBalanceTier() {
        val tiers = mutableListOf(BalanceTier(description = "Below average", max = 549, min = 0), BalanceTier(description = "Above average", max = 700, min = 550))
        val json = Converters.instance.stringFromListOfBalanceTier(tiers)
        assertEquals("[{\"description\":\"Below average\",\"min\":0,\"max\":549},{\"description\":\"Above average\",\"min\":550,\"max\":700}]", json)
    }

    @Test
    fun testStringToListOfAccountFeature() {
        val json = "[{\"id\":\"payments\",\"name\":\"Payments\",\"image_url\":\"https://image.png\",\"details\":[{\"id\":\"bpay\",\"name\":\"BPAY\",\"image_url\":\"https://image-detail.png\"},{\"id\":\"npp\",\"name\":\"PayID\"}]},{\"id\":\"transfers\",\"name\":\"Transfers\",\"details\":[{\"id\":\"internal\",\"name\":\"Transfer\"}]},{\"id\":\"statements\",\"name\":\"Statements\"}]"
        val features = Converters.instance.stringToListOfAccountFeature(json)
        assertNotNull(features)
        assertTrue(features?.size == 3)
        assertEquals(AccountFeatureType.PAYMENTS, features?.get(0)?.featureId)
        assertEquals("Payments", features?.get(0)?.name)
        assertEquals("https://image.png", features?.get(0)?.imageUrl)
        assertEquals(2, features?.get(0)?.details?.size)
        assertEquals(AccountFeatureSubType.BPAY, features?.get(0)?.details?.get(0)?.detailId)
        assertEquals("BPAY", features?.get(0)?.details?.get(0)?.name)
        assertEquals("https://image-detail.png", features?.get(0)?.details?.get(0)?.imageUrl)
        assertEquals(AccountFeatureType.TRANSFERS, features?.get(1)?.featureId)
        assertNull(features?.get(1)?.imageUrl)
        assertEquals(1, features?.get(1)?.details?.size)
        assertEquals(AccountFeatureType.STATEMENTS, features?.get(2)?.featureId)
        assertNull(features?.get(2)?.imageUrl)
        assertNull(features?.get(2)?.details)

        assertNull(Converters.instance.stringToListOfAccountFeature(null))
    }

    @Test
    fun testStringFromListOfAccountFeature() {
        val features = testAccountFeaturesData()
        val json = Converters.instance.stringFromListOfAccountFeature(features)
        assertEquals("[{\"id\":\"payments\",\"name\":\"Payments\",\"image_url\":\"https://image.png\",\"details\":[{\"id\":\"bpay\",\"name\":\"BPAY\",\"image_url\":\"https://image-detail.png\"},{\"id\":\"npp\",\"name\":\"PayID\"}]},{\"id\":\"transfers\",\"name\":\"Transfers\",\"details\":[{\"id\":\"internal\",\"name\":\"Transfer\"}]},{\"id\":\"statements\",\"name\":\"Statements\"}]", json)
    }

    @Test
    fun testStringToListOfAccountFeatureDetail() {
        val json = "[{\"id\":\"bpay\",\"name\":\"BPAY\",\"image_url\":\"https://image-detail.png\"},{\"id\":\"npp\",\"name\":\"PayID\"}]"
        val details = Converters.instance.stringToListOfAccountFeatureDetail(json)
        assertNotNull(details)
        assertTrue(details?.size == 2)
        assertEquals(AccountFeatureSubType.BPAY, details?.get(0)?.detailId)
        assertEquals("BPAY", details?.get(0)?.name)
        assertEquals("https://image-detail.png", details?.get(0)?.imageUrl)
        assertEquals(AccountFeatureSubType.NPP, details?.get(1)?.detailId)
        assertEquals("PayID", details?.get(1)?.name)
        assertNull(details?.get(1)?.imageUrl)

        assertNull(Converters.instance.stringToListOfAccountFeatureDetail(null))
    }

    @Test
    fun testStringFromListOfAccountFeatureDetail() {
        val details = testAccountFeatureDetailsData()
        val json = Converters.instance.stringFromListOfAccountFeatureDetail(details)
        assertEquals("[{\"id\":\"bpay\",\"name\":\"BPAY\",\"image_url\":\"https://image-detail.png\"},{\"id\":\"npp\",\"name\":\"PayID\"}]", json)
    }

    @Test
    fun testStringToAccountFeatureType() {
        val status = Converters.instance.stringToAccountFeatureType("PAYMENTS")
        assertEquals(AccountFeatureType.PAYMENTS, status)

        assertEquals(AccountFeatureType.UNKNOWN, Converters.instance.stringToAccountFeatureType(null))
    }

    @Test
    fun testStringFromAccountFeatureType() {
        val str = Converters.instance.stringFromAccountFeatureType(AccountFeatureType.PAYMENTS)
        assertEquals("PAYMENTS", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromAccountFeatureType(null))
    }

    @Test
    fun testStringToAccountFeatureSubType() {
        val status = Converters.instance.stringToAccountFeatureSubType("BPAY")
        assertEquals(AccountFeatureSubType.BPAY, status)

        assertEquals(AccountFeatureSubType.UNKNOWN, Converters.instance.stringToAccountFeatureSubType(null))
    }

    @Test
    fun testStringFromAccountFeatureSubType() {
        val str = Converters.instance.stringFromAccountFeatureSubType(AccountFeatureSubType.BPAY)
        assertEquals("BPAY", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromAccountFeatureSubType(null))
    }

    @Test
    fun testStringToListOfUserPayIdAccountResponse() {
        val json = "[{\"type\": \"mobile\",\"payid\": \"+61411111111\",\"payid_name\": \"Frollo\",\"payid_status\": \"ACTIVE\",\"created_at\": \"2021-01-28T05:24:40.597Z\",\"updated_at\": \"2021-01-28T05:24:40.597Z\"},{\"type\": \"email\",\"payid\": \"user@example.com\",\"payid_name\": \"Frollo\",\"payid_status\": \"DEREGISTERED\",\"created_at\": \"2021-02-28T05:24:40.597Z\",\"updated_at\": \"2021-02-28T05:24:40.597Z\"}]"
        val info = Converters.instance.stringToListOfUserPayIdAccountResponse(json)
        assertNotNull(info)
        assertTrue(info?.size == 2)

        assertEquals(PayIDType.MOBILE, info?.get(0)?.type)
        assertEquals(UserPayIdAccountStatus.ACTIVE, info?.get(0)?.status)
        assertEquals("+61411111111", info?.get(0)?.payId)
        assertEquals("Frollo", info?.get(0)?.name)
        assertEquals("2021-01-28T05:24:40.597Z", info?.get(0)?.createdAt)
        assertEquals("2021-01-28T05:24:40.597Z", info?.get(0)?.updatedAt)

        assertEquals(PayIDType.EMAIL, info?.get(1)?.type)
        assertEquals(UserPayIdAccountStatus.DEREGISTERED, info?.get(1)?.status)
        assertEquals("user@example.com", info?.get(1)?.payId)
        assertEquals("Frollo", info?.get(1)?.name)
        assertEquals("2021-02-28T05:24:40.597Z", info?.get(1)?.createdAt)
        assertEquals("2021-02-28T05:24:40.597Z", info?.get(1)?.updatedAt)

        assertNull(Converters.instance.stringToListOfUserPayIdAccountResponse(null))
    }

    @Test
    fun testStringFromListOfUserPayIdAccountResponse() {
        val info = listOf(
            UserPayIdAccountResponse("+61411111111", UserPayIdAccountStatus.ACTIVE, PayIDType.MOBILE, "Frollo", "2021-01-28T05:24:40.597Z", "2021-01-28T05:24:40.597Z", null),
            UserPayIdAccountResponse("user@example.com", UserPayIdAccountStatus.DEREGISTERED, PayIDType.EMAIL, "Frollo", "2021-02-28T05:24:40.597Z", "2021-02-28T05:24:40.597Z", null)
        )
        val json = Converters.instance.stringFromListOfUserPayIdAccountResponse(info)
        assertEquals("[{\"payid\":\"+61411111111\",\"payid_status\":\"ACTIVE\",\"type\":\"mobile\",\"payid_name\":\"Frollo\",\"created_at\":\"2021-01-28T05:24:40.597Z\",\"updated_at\":\"2021-01-28T05:24:40.597Z\"},{\"payid\":\"user@example.com\",\"payid_status\":\"DEREGISTERED\",\"type\":\"email\",\"payid_name\":\"Frollo\",\"created_at\":\"2021-02-28T05:24:40.597Z\",\"updated_at\":\"2021-02-28T05:24:40.597Z\"}]", json)
    }

    @Test
    fun testStringToListOfCDRProductInformation() {
        val json = "[{\"name\":\"Benefits\",\"value\":\"Free ATMs\"},{\"name\":\"Addons\",\"value\":\"0% Interest\"}]"
        val info = Converters.instance.stringToListOfCDRProductInformation(json)
        assertNotNull(info)
        assertTrue(info?.size == 2)
        assertEquals("Benefits", info?.get(0)?.name)
        assertEquals("Free ATMs", info?.get(0)?.value)
        assertEquals("Addons", info?.get(1)?.name)
        assertEquals("0% Interest", info?.get(1)?.value)

        assertNull(Converters.instance.stringToListOfCDRProductInformation(null))
    }

    @Test
    fun testStringFromListOfCDRProductInformation() {
        val info = listOf(
            CDRProductInformation("Benefits", "Free ATMs"),
            CDRProductInformation("Addons", "0% Interest")
        )
        val json = Converters.instance.stringFromListOfCDRProductInformation(info)
        assertEquals("[{\"name\":\"Benefits\",\"value\":\"Free ATMs\"},{\"name\":\"Addons\",\"value\":\"0% Interest\"}]", json)
    }

    @Test
    fun testStringToListOfRelatedAccount() {
        val json = "[{\"account_id\":123,\"relationship\":\"offset\"},{\"account_id\":456,\"relationship\":\"loan\"},{\"account_id\":789,\"relationship\":\"loan\"}]"
        val accounts = Converters.instance.stringToListOfRelatedAccount(json)
        assertNotNull(accounts)
        assertTrue(accounts?.size == 3)
        assertEquals(123L, accounts?.get(0)?.accountId)
        assertEquals(AccountRelationship.OFFSET, accounts?.get(0)?.relationship)
        assertEquals(456L, accounts?.get(1)?.accountId)
        assertEquals(AccountRelationship.LOAN, accounts?.get(1)?.relationship)
        assertEquals(789L, accounts?.get(2)?.accountId)
        assertEquals(AccountRelationship.LOAN, accounts?.get(2)?.relationship)

        assertNull(Converters.instance.stringToListOfRelatedAccount(null))
    }

    @Test
    fun testStringFromListOfRelatedAccount() {
        val accounts = listOf(
            RelatedAccount(123L, AccountRelationship.OFFSET),
            RelatedAccount(456L, AccountRelationship.LOAN),
            RelatedAccount(789L, AccountRelationship.LOAN)
        )
        val json = Converters.instance.stringFromListOfRelatedAccount(accounts)
        assertEquals("[{\"account_id\":123,\"relationship\":\"offset\"},{\"account_id\":456,\"relationship\":\"loan\"},{\"account_id\":789,\"relationship\":\"loan\"}]", json)
    }

    @Test
    fun testStringToStatementOrPaymentFrequency() {
        val status = Converters.instance.stringToStatementOrPaymentFrequency("MONTHLY")
        assertEquals(StatementOrPaymentFrequency.MONTHLY, status)

        assertEquals(StatementOrPaymentFrequency.IRREGULAR, Converters.instance.stringToStatementOrPaymentFrequency(null))
    }

    @Test
    fun testStringFromStatementOrPaymentFrequency() {
        val str = Converters.instance.stringFromStatementOrPaymentFrequency(StatementOrPaymentFrequency.MONTHLY)
        assertEquals("MONTHLY", str)

        assertEquals("IRREGULAR", Converters.instance.stringFromStatementOrPaymentFrequency(null))
    }

    @Test
    fun testStringToAccountOwnerType() {
        val status = Converters.instance.stringToAccountOwnerType("BUSINESS")
        assertEquals(AccountOwnerType.BUSINESS, status)

        assertEquals(AccountOwnerType.UNKNOWN, Converters.instance.stringToAccountOwnerType(null))
    }

    @Test
    fun testStringFromAccountOwnerType() {
        val str = Converters.instance.stringFromAccountOwnerType(AccountOwnerType.BUSINESS)
        assertEquals("BUSINESS", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromAccountOwnerType(null))
    }

    @Test
    fun testStringToVehicleType() {
        val status = Converters.instance.stringToVehicleType("BIKE")
        assertEquals(VehicleType.BIKE, status)

        assertNull(Converters.instance.stringToVehicleType(null))
    }

    @Test
    fun testStringFromVehicleType() {
        val str = Converters.instance.stringFromVehicleType(VehicleType.BIKE)
        assertEquals("BIKE", str)

        assertNull(Converters.instance.stringFromVehicleType(null))
    }

    @Test
    fun testStringToPropertyType() {
        val status = Converters.instance.stringToPropertyType("COMPANY_TITLE_UNIT")
        assertEquals(PropertyType.COMPANY_TITLE_UNIT, status)

        assertEquals(PropertyType.OTHER, Converters.instance.stringToPropertyType(null))
    }

    @Test
    fun testStringFromPropertyType() {
        val str = Converters.instance.stringFromPropertyType(PropertyType.COMPANY_TITLE_UNIT)
        assertEquals("COMPANY_TITLE_UNIT", str)

        assertEquals("OTHER", Converters.instance.stringFromPropertyType(null))
    }

    @Test
    fun testStringToPropertyZoning() {
        val status = Converters.instance.stringToPropertyZoning("COMMERCIAL")
        assertEquals(PropertyZoning.COMMERCIAL, status)

        assertNull(Converters.instance.stringToPropertyZoning(null))
    }

    @Test
    fun testStringFromPropertyZoning() {
        val str = Converters.instance.stringFromPropertyZoning(PropertyZoning.COMMERCIAL)
        assertEquals("COMMERCIAL", str)

        assertNull(Converters.instance.stringFromPropertyZoning(null))
    }

    @Test
    fun testStringToPropertyPurpose() {
        val status = Converters.instance.stringToPropertyPurpose("INVESTMENT")
        assertEquals(PropertyPurpose.INVESTMENT, status)

        assertNull(Converters.instance.stringToPropertyPurpose(null))
    }

    @Test
    fun testStringFromPropertyPurpose() {
        val str = Converters.instance.stringFromPropertyPurpose(PropertyPurpose.INVESTMENT)
        assertEquals("INVESTMENT", str)

        assertNull(Converters.instance.stringFromPropertyPurpose(null))
    }

    @Test
    fun testStringToTransactionBaseType() {
        val status = Converters.instance.stringToTransactionBaseType("CREDIT")
        assertEquals(TransactionBaseType.CREDIT, status)

        assertEquals(TransactionBaseType.UNKNOWN, Converters.instance.stringToTransactionBaseType(null))
    }

    @Test
    fun testStringFromTransactionBaseType() {
        val str = Converters.instance.stringFromTransactionBaseType(TransactionBaseType.CREDIT)
        assertEquals("CREDIT", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromTransactionBaseType(null))
    }

    @Test
    fun testStringToTransactionStatus() {
        val status = Converters.instance.stringToTransactionStatus("POSTED")
        assertEquals(TransactionStatus.POSTED, status)

        assertNull(Converters.instance.stringToTransactionStatus(null))
    }

    @Test
    fun testStringFromTransactionStatus() {
        val str = Converters.instance.stringFromTransactionStatus(TransactionStatus.POSTED)
        assertEquals("POSTED", str)

        assertNull(Converters.instance.stringFromTransactionStatus(null))
    }

    @Test
    fun testStringToMerchantLocation() {
        val json = "{\"formatted_address\":\"41 McLaren St, North Sydney, NSW 2120 Australia\",\"line_1\":\"41 McLaren St\",\"suburb\":\"North Sydney\",\"state\":\"NSW\",\"postcode\":\"2120\",\"country\":\"Australia\",\"latitude\":-33.83517030000001,\"longitude\":151.2086038}"
        val location = Converters.instance.stringToMerchantLocation(json)
        assertNotNull(location)
        assertEquals("41 McLaren St, North Sydney, NSW 2120 Australia", location?.formattedAddress)
        assertEquals("41 McLaren St", location?.line1)
        assertNull(location?.line2)
        assertNull(location?.line3)
        assertEquals("North Sydney", location?.suburb)
        assertEquals("NSW", location?.state)
        assertEquals("2120", location?.postcode)
        assertEquals("Australia", location?.country)
        assertEquals(BigDecimal("-33.83517030000001"), location?.latitude)
        assertEquals(BigDecimal("151.2086038"), location?.longitude)

        assertNull(Converters.instance.stringToMerchantLocation(null))
    }

    @Test
    fun testStringFromMerchantLocation() {
        val location = MerchantLocation(
            formattedAddress = "41 McLaren St, North Sydney, NSW 2120 Australia",
            line1 = "41 McLaren St",
            line2 = null,
            line3 = null,
            suburb = "North Sydney",
            state = "NSW",
            postcode = "2120",
            country = "Australia",
            latitude = BigDecimal("-33.83517030000001"),
            longitude = BigDecimal("151.2086038")
        )
        val json = Converters.instance.stringFromMerchantLocation(location)
        assertEquals("{\"formatted_address\":\"41 McLaren St, North Sydney, NSW 2120 Australia\",\"line_1\":\"41 McLaren St\",\"suburb\":\"North Sydney\",\"state\":\"NSW\",\"postcode\":\"2120\",\"country\":\"Australia\",\"latitude\":-33.83517030000001,\"longitude\":151.2086038}", json)
    }

    @Test
    fun testStringToBudgetCategory() {
        val status = Converters.instance.stringToBudgetCategory("INCOME")
        assertEquals(BudgetCategory.INCOME, status)

        assertNull(Converters.instance.stringToBudgetCategory(null))
    }

    @Test
    fun testStringFromBudgetCategory() {
        val str = Converters.instance.stringFromBudgetCategory(BudgetCategory.INCOME)
        assertEquals("INCOME", str)

        assertNull(Converters.instance.stringFromBudgetCategory(null))
    }

    @Test
    fun testStringToTransactionCategoryType() {
        val status = Converters.instance.stringToTransactionCategoryType("INCOME")
        assertEquals(TransactionCategoryType.INCOME, status)

        assertEquals(TransactionCategoryType.UNCATEGORIZED, Converters.instance.stringToTransactionCategoryType(null))
    }

    @Test
    fun testStringFromTransactionCategoryType() {
        val str = Converters.instance.stringFromTransactionCategoryType(TransactionCategoryType.INCOME)
        assertEquals("INCOME", str)

        assertEquals("UNCATEGORIZED", Converters.instance.stringFromTransactionCategoryType(null))
    }

    @Test
    fun testStringToMerchantType() {
        val status = Converters.instance.stringToMerchantType("RETAILER")
        assertEquals(MerchantType.RETAILER, status)

        assertEquals(MerchantType.UNKNOWN, Converters.instance.stringToMerchantType(null))
    }

    @Test
    fun testStringFromMerchantType() {
        val str = Converters.instance.stringFromMerchantType(MerchantType.RETAILER)
        assertEquals("RETAILER", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromMerchantType(null))
    }

    @Test
    fun testStringToReportGrouping() {
        val status = Converters.instance.stringToReportGrouping("BUDGET_CATEGORY")
        assertEquals(ReportGrouping.BUDGET_CATEGORY, status)

        assertNull(Converters.instance.stringToReportGrouping(null))
    }

    @Test
    fun testStringFromReportGrouping() {
        val str = Converters.instance.stringFromReportGrouping(ReportGrouping.BUDGET_CATEGORY)
        assertEquals("BUDGET_CATEGORY", str)

        assertNull(Converters.instance.stringFromReportGrouping(null))
    }

    @Test
    fun testStringToReportPeriod() {
        val status = Converters.instance.stringToReportPeriod("MONTH")
        assertEquals(ReportPeriod.MONTH, status)

        assertNull(Converters.instance.stringToReportPeriod(null))
    }

    @Test
    fun testStringFromReportPeriod() {
        val str = Converters.instance.stringFromReportPeriod(ReportPeriod.MONTH)
        assertEquals("MONTH", str)

        assertNull(Converters.instance.stringFromReportPeriod(null))
    }

    @Test
    fun testStringToListOfLong() {
        val string = "|1024790|1024791|1024792|"
        val list = Converters.instance.stringToListOfLong(string)
        assertNotNull(list)
        assertTrue(list?.size == 3)
        assertEquals(1024790L, list?.get(0))
        assertEquals(1024791L, list?.get(1))
        assertEquals(1024792L, list?.get(2))

        assertNull(Converters.instance.stringToListOfString(null))
    }

    @Test
    fun testStringFromListOfLong() {
        val list = mutableListOf<Long>(1024790, 1024791, 1024792)
        val string = Converters.instance.stringFromListOfLong(list)
        assertEquals("|1024790|1024791|1024792|", string)
    }

    @Test
    fun testStringToBillType() {
        val status = Converters.instance.stringToBillType("MANUAL")
        assertEquals(BillType.MANUAL, status)

        assertEquals(BillType.BILL, Converters.instance.stringToBillType(null))
    }

    @Test
    fun testStringFromBillType() {
        val str = Converters.instance.stringFromBillType(BillType.MANUAL)
        assertEquals("MANUAL", str)

        assertEquals("BILL", Converters.instance.stringFromBillType(null))
    }

    @Test
    fun testStringToBillStatus() {
        val status = Converters.instance.stringToBillStatus("CONFIRMED")
        assertEquals(BillStatus.CONFIRMED, status)

        assertEquals(BillStatus.ESTIMATED, Converters.instance.stringToBillStatus(null))
    }

    @Test
    fun testStringFromBillStatus() {
        val str = Converters.instance.stringFromBillStatus(BillStatus.CONFIRMED)
        assertEquals("CONFIRMED", str)

        assertEquals("ESTIMATED", Converters.instance.stringFromBillStatus(null))
    }

    @Test
    fun testStringToBillFrequency() {
        val status = Converters.instance.stringToBillFrequency("ANNUALLY")
        assertEquals(BillFrequency.ANNUALLY, status)

        assertEquals(BillFrequency.UNKNOWN, Converters.instance.stringToBillFrequency(null))
    }

    @Test
    fun testStringFromBillFrequency() {
        val str = Converters.instance.stringFromBillFrequency(BillFrequency.ANNUALLY)
        assertEquals("ANNUALLY", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromBillFrequency(null))
    }

    @Test
    fun testStringToBillPaymentStatus() {
        val status = Converters.instance.stringToBillPaymentStatus("FUTURE")
        assertEquals(BillPaymentStatus.FUTURE, status)

        assertEquals(BillPaymentStatus.DUE, Converters.instance.stringToBillPaymentStatus(null))
    }

    @Test
    fun testStringFromBillPaymentStatus() {
        val str = Converters.instance.stringFromBillPaymentStatus(BillPaymentStatus.FUTURE)
        assertEquals("FUTURE", str)

        assertEquals("DUE", Converters.instance.stringFromBillPaymentStatus(null))
    }

    @Test
    fun testStringToGoalTrackingType() {
        val status = Converters.instance.stringToGoalTrackingType("CREDIT")
        assertEquals(GoalTrackingType.CREDIT, status)

        assertEquals(GoalTrackingType.DEBIT_CREDIT, Converters.instance.stringToGoalTrackingType(null))
    }

    @Test
    fun testStringFromGoalTrackingType() {
        val str = Converters.instance.stringFromGoalTrackingType(GoalTrackingType.CREDIT)
        assertEquals("CREDIT", str)

        assertEquals("DEBIT_CREDIT", Converters.instance.stringFromGoalTrackingType(null))
    }

    @Test
    fun testStringToGoalTrackingStatus() {
        val status = Converters.instance.stringToGoalTrackingStatus("ABOVE")
        assertEquals(GoalTrackingStatus.ABOVE, status)

        assertEquals(GoalTrackingStatus.EQUAL, Converters.instance.stringToGoalTrackingStatus(null))
    }

    @Test
    fun testStringFromGoalTrackingStatus() {
        val str = Converters.instance.stringFromGoalTrackingStatus(GoalTrackingStatus.ABOVE)
        assertEquals("ABOVE", str)

        assertEquals("EQUAL", Converters.instance.stringFromGoalTrackingStatus(null))
    }

    @Test
    fun testStringToGoalTarget() {
        val status = Converters.instance.stringToGoalTarget("AMOUNT")
        assertEquals(GoalTarget.AMOUNT, status)

        assertEquals(GoalTarget.OPEN_ENDED, Converters.instance.stringToGoalTarget(null))
    }

    @Test
    fun testStringFromGoalTarget() {
        val str = Converters.instance.stringFromGoalTarget(GoalTarget.AMOUNT)
        assertEquals("AMOUNT", str)

        assertEquals("OPEN_ENDED", Converters.instance.stringFromGoalTarget(null))
    }

    @Test
    fun testStringToGoalStatus() {
        val status = Converters.instance.stringToGoalStatus("ACTIVE")
        assertEquals(GoalStatus.ACTIVE, status)

        assertEquals(GoalStatus.UNSTARTED, Converters.instance.stringToGoalStatus(null))
    }

    @Test
    fun testStringFromGoalStatus() {
        val str = Converters.instance.stringFromGoalStatus(GoalStatus.ACTIVE)
        assertEquals("ACTIVE", str)

        assertEquals("UNSTARTED", Converters.instance.stringFromGoalStatus(null))
    }

    @Test
    fun testStringToGoalFrequency() {
        val status = Converters.instance.stringToGoalFrequency("ANNUALLY")
        assertEquals(GoalFrequency.ANNUALLY, status)

        assertEquals(GoalFrequency.SINGULAR, Converters.instance.stringToGoalFrequency(null))
    }

    @Test
    fun testStringFromGoalFrequency() {
        val str = Converters.instance.stringFromGoalFrequency(GoalFrequency.ANNUALLY)
        assertEquals("ANNUALLY", str)

        assertEquals("SINGULAR", Converters.instance.stringFromGoalFrequency(null))
    }

    @Test
    fun testStringToBudgetTrackingStatus() {
        val status = Converters.instance.stringToBudgetTrackingStatus("EQUAL")
        assertEquals(BudgetTrackingStatus.EQUAL, status)

        assertEquals(BudgetTrackingStatus.EQUAL, Converters.instance.stringToBudgetTrackingStatus(null))
    }

    @Test
    fun testStringFromBudgetTrackingStatus() {
        val str = Converters.instance.stringFromBudgetTrackingStatus(BudgetTrackingStatus.EQUAL)
        assertEquals("EQUAL", str)
        assertEquals("EQUAL", Converters.instance.stringFromBudgetTrackingStatus(null))
    }

    @Test
    fun testStringToBudgetStatus() {
        val status = Converters.instance.stringToBudgetStatus("UNSTARTED")
        assertEquals(BudgetStatus.UNSTARTED, status)
        assertEquals(BudgetStatus.UNSTARTED, Converters.instance.stringToBudgetStatus(null))
    }

    @Test
    fun testStringFromBudgetStatus() {
        val str = Converters.instance.stringFromBudgetStatus(BudgetStatus.UNSTARTED)
        assertEquals("UNSTARTED", str)
        assertEquals("UNSTARTED", Converters.instance.stringFromBudgetStatus(null))
    }

    @Test
    fun testStringToBudgetFrequency() {
        val status = Converters.instance.stringToBudgetFrequency("MONTHLY")
        assertEquals(BudgetFrequency.MONTHLY, status)
        assertEquals(BudgetFrequency.MONTHLY, Converters.instance.stringToBudgetFrequency(null))
    }

    @Test
    fun testStringFromBudgetFrequency() {
        val str = Converters.instance.stringFromBudgetFrequency(BudgetFrequency.MONTHLY)
        assertEquals("MONTHLY", str)

        assertEquals("MONTHLY", Converters.instance.stringFromBudgetFrequency(null))
    }

    @Test
    fun testStringToBudgetType() {
        val status = Converters.instance.stringToBudgetType("BUDGET_CATEGORY")
        assertEquals(BudgetType.BUDGET_CATEGORY, status)

        assertEquals(BudgetType.BUDGET_CATEGORY, Converters.instance.stringToBudgetType(null))
    }

    @Test
    fun testStringFromBudgetType() {
        val str = Converters.instance.stringFromBudgetType(BudgetType.BUDGET_CATEGORY)
        assertEquals("BUDGET_CATEGORY", str)

        assertEquals("BUDGET_CATEGORY", Converters.instance.stringFromBudgetType(null))
    }

    @Test
    fun testStringToMetadata() {
        val json = "{\"seen\":true}"
        val metadata = Converters.instance.stringToMetadata(json)
        assertNotNull(metadata)
        assertEquals(true, metadata?.get("seen")?.asBoolean)

        assertNull(Converters.instance.stringToMetadata(null))
    }

    @Test
    fun testStringFromMetadata() {
        val metadata = JsonObject().apply {
            addProperty("seen", true)
        }
        val json = Converters.instance.stringFromMetadata(metadata)
        assertEquals("{\"seen\":true}", json)
    }

    @Test
    fun testStringToConsentStatus() {
        val status = Converters.instance.stringToConsentStatus("ACTIVE")
        assertEquals(ConsentStatus.ACTIVE, status)

        assertEquals(ConsentStatus.UNKNOWN, Converters.instance.stringToConsentStatus(null))
    }

    @Test
    fun testStringFromConsentStatus() {
        val str = Converters.instance.stringFromConsentStatus(ConsentStatus.ACTIVE)
        assertEquals("ACTIVE", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromConsentStatus(null))
    }

    @Test
    fun testStringToListOfSharingDuration() {
        val json = "[{\"duration\":1234500,\"description\":\"Account Details\",\"image_url\":\"http://app.image.png\"},{\"duration\":2345678,\"description\":\"Transaction Details\",\"image_url\":\"http://app.image.png\"}]"
        val info = Converters.instance.stringToListOfSharingDuration(json)
        assertNotNull(info)
        assertTrue(info?.size == 2)
        assertEquals(1234500L, info?.first()?.duration)
        assertEquals("Account Details", info?.first()?.description)
        assertEquals("http://app.image.png", info?.first()?.imageUrl)
        assertEquals(2345678L, info?.get(1)?.duration)
        assertEquals("Transaction Details", info?.get(1)?.description)
        assertEquals("http://app.image.png", info?.get(1)?.imageUrl)

        assertNull(Converters.instance.stringToListOfSharingDuration(null))
    }

    @Test
    fun testStringFromListOfSharingDuration() {
        val info = listOf(
            SharingDuration(1234500, "Account Details", "http://app.image.png"),
            SharingDuration(2345678, "Transaction Details", "http://app.image.png")
        )
        val json = Converters.instance.stringFromListOfSharingDuration(info)
        assertEquals("[{\"duration\":1234500,\"description\":\"Account Details\",\"image_url\":\"http://app.image.png\"},{\"duration\":2345678,\"description\":\"Transaction Details\",\"image_url\":\"http://app.image.png\"}]", json)
    }

    @Test
    fun testStringToCDRModel() {
        val status = Converters.instance.stringToCDRModel("AFFILIATE")
        assertEquals(CDRModel.AFFILIATE, status)

        assertEquals(CDRModel.PRINCIPAL, Converters.instance.stringToCDRModel(null))
    }

    @Test
    fun testStringFromCDRModel() {
        val str = Converters.instance.stringFromCDRModel(CDRModel.AFFILIATE)
        assertEquals("AFFILIATE", str)

        assertEquals("PRINCIPAL", Converters.instance.stringFromCDRModel(null))
    }

    @Test
    fun testStringToListOfCDRParty() {
        val json = "[{\"id\":12345,\"name\":\"ACME Inc\",\"description\":\"Enhance stuff\",\"type\":\"osp\",\"adr_id\":\"AFF0001\",\"policy\":{\"name\":\"CDR Policy\",\"url\":\"https://example.com\"}}]"
        val info = Converters.instance.stringToListOfCDRParty(json)
        assertNotNull(info)
        assertTrue(info?.size == 1)
        assertEquals(12345L, info?.first()?.partyId)
        assertEquals("ACME Inc", info?.first()?.name)
        assertEquals("Enhance stuff", info?.first()?.description)
        assertEquals(CDRPartyType.OSP, info?.first()?.type)
        assertEquals("AFF0001", info?.first()?.adrId)
        assertEquals("CDR Policy", info?.first()?.policy?.name)
        assertEquals("https://example.com", info?.first()?.policy?.url)

        assertNull(Converters.instance.stringToListOfCDRParty(null))
    }

    @Test
    fun testStringFromListOfCDRParty() {
        val info = listOf(
            CDRParty(
                partyId = 12345,
                name = "ACME Inc",
                displayName = null,
                summary = "Test Summary",
                description = "Enhance stuff",
                imageUrl = "https://frollo.com.au",
                type = CDRPartyType.OSP,
                adrId = "AFF0001",
                registrationNumber = "12 345 678 901",
                policy = CDRPolicy(
                    "CDR Policy",
                    "https://example.com"
                ),
                phone = null,
                address = null,
                supportEmail = null,
                supportPhone = null,
                websiteUrl = null
            )
        )
        val json = Converters.instance.stringFromListOfCDRParty(info)
        assertEquals("[{\"id\":12345,\"name\":\"ACME Inc\",\"summary\":\"Test Summary\",\"description\":\"Enhance stuff\",\"image_url\":\"https://frollo.com.au\",\"type\":\"osp\",\"adr_id\":\"AFF0001\",\"registration_number\":\"12 345 678 901\",\"policy\":{\"name\":\"CDR Policy\",\"url\":\"https://example.com\"}}]", json)
    }

    @Test
    fun testStringToCDRPartyType() {
        val status = Converters.instance.stringToCDRPartyType("REPRESENTATIVE")
        assertEquals(CDRPartyType.REPRESENTATIVE, status)

        assertEquals(CDRPartyType.AFFILIATE, Converters.instance.stringToCDRPartyType(null))
    }

    @Test
    fun testStringFromCDRPartyType() {
        val str = Converters.instance.stringFromCDRPartyType(CDRPartyType.REPRESENTATIVE)
        assertEquals("REPRESENTATIVE", str)

        assertEquals("AFFILIATE", Converters.instance.stringFromCDRPartyType(null))
    }

    @Test
    fun testStringToCDRPolicy() {
        val json = "{\"name\":\"CDR Policy\",\"url\":\"https://example.com\"}"
        val info = Converters.instance.stringToCDRPolicy(json)
        assertNotNull(info)
        assertEquals("CDR Policy", info?.name)
        assertEquals("https://example.com", info?.url)

        assertNull(Converters.instance.stringToCDRPolicy(null))
    }

    @Test
    fun testStringFromCDRPolicy() {
        val info = CDRPolicy("CDR Policy", "https://example.com")
        val json = Converters.instance.stringFromCDRPolicy(info)
        assertEquals("{\"name\":\"CDR Policy\",\"url\":\"https://example.com\"}", json)
    }

    @Test
    fun testStringToExternalPartyStatus() {
        val status = Converters.instance.stringToExternalPartyStatus("ENABLED")
        assertEquals(ExternalPartyStatus.ENABLED, status)

        assertEquals(ExternalPartyStatus.DISABLED, Converters.instance.stringToExternalPartyStatus(null))
    }

    @Test
    fun testStringFromExternalPartyStatus() {
        val str = Converters.instance.stringFromExternalPartyStatus(ExternalPartyStatus.ENABLED)
        assertEquals("ENABLED", str)

        assertEquals("DISABLED", Converters.instance.stringFromExternalPartyStatus(null))
    }

    @Test
    fun testStringToExternalPartyType() {
        val status = Converters.instance.stringToExternalPartyType("TRUSTED_ADVISOR")
        assertEquals(ExternalPartyType.TRUSTED_ADVISOR, status)

        assertEquals(ExternalPartyType.CDR_INSIGHT, Converters.instance.stringToExternalPartyType(null))
    }

    @Test
    fun testStringFromExternalPartyType() {
        val str = Converters.instance.stringFromExternalPartyType(ExternalPartyType.TRUSTED_ADVISOR)
        assertEquals("TRUSTED_ADVISOR", str)

        assertEquals("CDR_INSIGHT", Converters.instance.stringFromExternalPartyType(null))
    }

    @Test
    fun testStringToTrustedAdvisorType() {
        val status = Converters.instance.stringToTrustedAdvisorType("ADVISOR")
        assertEquals(TrustedAdvisorType.ADVISOR, status)

        assertNull(Converters.instance.stringToTrustedAdvisorType(null))
    }

    @Test
    fun testStringFromTrustedAdvisorType() {
        val str = Converters.instance.stringFromTrustedAdvisorType(TrustedAdvisorType.ADVISOR)
        assertEquals("ADVISOR", str)

        assertNull(Converters.instance.stringFromTrustedAdvisorType(null))
    }

    @Test
    fun testStringToExternalParty() {
        val json = "{\"id\":1,\"external_id\":\"6hsf735\",\"key\":\"ACME001\",\"name\":\"CheckFinance1\",\"contact\":\"support@frollo.us\",\"description\":\"They broker things...\",\"status\":\"enabled\",\"type\":\"ta\",\"ta_type\":\"accountant\",\"privacy_url\":\"http://frollo.us/privacy\"}"
        val externalParty = Converters.instance.stringToExternalParty(json)
        assertNotNull(externalParty)
        assertEquals(1L, externalParty?.partyId)
        assertEquals("6hsf735", externalParty?.externalId)
        assertEquals("ACME001", externalParty?.key)
        assertEquals("CheckFinance1", externalParty?.name)
        assertEquals("support@frollo.us", externalParty?.contact)
        assertEquals("They broker things...", externalParty?.description)
        assertEquals(ExternalPartyStatus.ENABLED, externalParty?.status)
        assertEquals("http://frollo.us/privacy", externalParty?.privacyUrl)
        assertEquals(ExternalPartyType.TRUSTED_ADVISOR, externalParty?.type)
        assertEquals(TrustedAdvisorType.ACCOUNTANT, externalParty?.trustedAdvisorType)

        assertNull(Converters.instance.stringToExternalParty(null))
    }

    @Test
    fun testStringFromExternalParty() {
        val externalParty = ExternalParty(
            partyId = 1,
            externalId = "6hsf735",
            name = "CheckFinance1",
            company = ExternalPartyCompany(
                displayName = "Frollo",
                legalName = "Frollo",
                abn = "12345678901",
                acn = "123456789"
            ),
            contact = "support@frollo.us",
            description = "Test123",
            status = ExternalPartyStatus.ENABLED,
            imageUrl = "https://frollo.com.au/image",
            smallImageUrl = "https://frollo.com.au/image_small",
            privacyUrl = "http://frollo.us/privacy",
            type = ExternalPartyType.TRUSTED_ADVISOR,
            trustedAdvisorType = TrustedAdvisorType.ACCOUNTANT,
            summary = "Test summary",
            sharingDurations = listOf(
                SharingDuration(
                    duration = 86400,
                    description = "Sharing duration",
                    imageUrl = "https://frollo.com.au/image"
                )
            ),
            permissions = testCDRPermissionData(),
            key = "ACME001",
            phone = "020000000000",
            address = "Level 33 100 Mount Street, North Sydney, NSW 2060",
            supportEmail = "support@frollo.us",
            supportPhone = "555 02 0000000",
            websiteUrl = "https://frollo.com.au",
            displayName = "CheckFinance1"
        )
        val json = Converters.instance.stringFromExternalParty(externalParty)
        assertEquals("{\"id\":1,\"external_id\":\"6hsf735\",\"key\":\"ACME001\",\"name\":\"CheckFinance1\",\"display_name\":\"CheckFinance1\",\"company\":{\"display_name\":\"Frollo\",\"legal_name\":\"Frollo\",\"abn\":\"12345678901\",\"acn\":\"123456789\"},\"contact\":\"support@frollo.us\",\"description\":\"Test123\",\"status\":\"enabled\",\"image_url\":\"https://frollo.com.au/image\",\"small_image_url\":\"https://frollo.com.au/image_small\",\"privacy_url\":\"http://frollo.us/privacy\",\"type\":\"ta\",\"ta_type\":\"accountant\",\"summary\":\"Test summary\",\"sharing_durations\":[{\"duration\":86400,\"description\":\"Sharing duration\",\"image_url\":\"https://frollo.com.au/image\"}],\"permissions\":[{\"id\":\"account_details\",\"title\":\"Account balance and details\",\"description\":\"We leverage...\",\"required\":true,\"details\":[{\"id\":\"account_name\",\"description\":\"Name of account\"}],\"placement\":10},{\"id\":\"transaction_details\",\"title\":\"Transaction and details\",\"description\":\"We leverage...\",\"required\":false,\"details\":[{\"id\":\"transaction_name\",\"description\":\"Name of transaction\"}],\"placement\":10}],\"phone\":\"020000000000\",\"address\":\"Level 33 100 Mount Street, North Sydney, NSW 2060\",\"support_email\":\"support@frollo.us\",\"support_phone\":\"555 02 0000000\",\"website_url\":\"https://frollo.com.au\"}", json)
    }

    @Test
    fun testStringToNPPServiceIdType() {
        val status = Converters.instance.stringToNPPServiceIdType("X2P1")
        assertEquals(NPPServiceIdType.X2P1, status)

        assertNull(Converters.instance.stringToNPPServiceIdType(null))
    }

    @Test
    fun testStringFromNPPServiceIdType() {
        val str = Converters.instance.stringFromNPPServiceIdType(NPPServiceIdType.X2P1)
        assertEquals("X2P1", str)

        assertNull(Converters.instance.stringFromNPPServiceIdType(null))
    }

    @Test
    fun testStringToPaymentMethod() {
        val status = Converters.instance.stringToPaymentMethod("BPAY")
        assertEquals(PaymentMethod.BPAY, status)

        assertEquals(PaymentMethod.PAY_ANYONE, Converters.instance.stringToPaymentMethod(null))
    }

    @Test
    fun testStringFromPaymentMethod() {
        val str = Converters.instance.stringFromPaymentMethod(PaymentMethod.BPAY)
        assertEquals("BPAY", str)

        assertEquals("PAY_ANYONE", Converters.instance.stringFromPaymentMethod(null))
    }

    @Test
    fun testStringToPaymentDetails() {
        val payAnyoneJson = "{\"account_holder\":\"Mr Johnathan Smith\",\"bsb\":\"100-123\",\"account_number\":\"12345678\"}"
        val payAnyoneDetails = Converters.instance.stringToPaymentDetails(payAnyoneJson) as? PaymentDetails.PayAnyone
        assertNotNull(payAnyoneDetails)
        assertEquals("Mr Johnathan Smith", payAnyoneDetails?.accountHolder)
        assertEquals("12345678", payAnyoneDetails?.accountNumber)
        assertEquals("100-123", payAnyoneDetails?.bsb)

        val billerJson = "{\"biller_code\":\"2275362\",\"crn\":\"723647803\",\"biller_name\":\"Tenstra Inc\",\"crn_type\":\"fixed_crn\"}"
        val billerDetails = Converters.instance.stringToPaymentDetails(billerJson) as? PaymentDetails.Biller
        assertNotNull(billerDetails)
        assertEquals("2275362", billerDetails?.billerCode)
        assertEquals("723647803", billerDetails?.crn)
        assertEquals("Tenstra Inc", billerDetails?.billerName)
        assertEquals(CRNType.FIXED, billerDetails?.crnType)

        val payIDJson = "{\"name\":\"J GILBERT\",\"payid\":\"j.gilbert@frollo.com\",\"type\":\"email\"}"
        val payIDDetails = Converters.instance.stringToPaymentDetails(payIDJson) as? PaymentDetails.PayID
        assertNotNull(payIDDetails)
        assertEquals("j.gilbert@frollo.com", payIDDetails?.payId)
        assertEquals("J GILBERT", payIDDetails?.name)
        assertEquals(PayIDType.EMAIL, payIDDetails?.type)

        val internationalJson = "{\"beneficiary\":{\"name\":\"Anne Maria\",\"country\":\"New Zeland\",\"message\":\"Test message new\"},\"bank_details\":{\"country\":\"New Zeland\",\"account_number\":\"12345666\",\"bank_address\":{\"address\":\"ABC 666\"},\"bic\":\"777\",\"fed_wire_number\":\"1234566\",\"sort_code\":\"666\",\"chip_number\":\"555\",\"routing_number\":\"444\",\"legal_entity_identifier\":\"123666\"}}"
        val internationalDetails = Converters.instance.stringToPaymentDetails(internationalJson) as? PaymentDetails.International
        assertNotNull(internationalDetails)
        assertEquals("Anne Maria", internationalDetails?.beneficiary?.name)
        assertEquals("New Zeland", internationalDetails?.beneficiary?.country)
        assertEquals("Test message new", internationalDetails?.beneficiary?.message)
        assertEquals("New Zeland", internationalDetails?.bankDetails?.country)
        assertEquals("12345666", internationalDetails?.bankDetails?.accountNumber)
        assertEquals("ABC 666", internationalDetails?.bankDetails?.bankAddress?.address)
        assertEquals("777", internationalDetails?.bankDetails?.bic)
        assertEquals("1234566", internationalDetails?.bankDetails?.fedWireNumber)
        assertEquals("666", internationalDetails?.bankDetails?.sortCode)
        assertEquals("555", internationalDetails?.bankDetails?.chipNumber)
        assertEquals("444", internationalDetails?.bankDetails?.routingNumber)
        assertEquals("123666", internationalDetails?.bankDetails?.legalEntityIdentifier)

        assertNull(Converters.instance.stringToPaymentDetails(null))
    }

    @Test
    fun testStringFromPaymentDetails() {
        val payAnyoneDetails = PaymentDetails.PayAnyone(
            accountHolder = "Mr Johnathan Smith",
            accountNumber = "12345678",
            bsb = "100-123"
        )
        val payAnyoneJson = Converters.instance.stringFromPaymentDetails(payAnyoneDetails)
        assertEquals("{\"account_holder\":\"Mr Johnathan Smith\",\"bsb\":\"100-123\",\"account_number\":\"12345678\"}", payAnyoneJson)

        val billerDetails = PaymentDetails.Biller(
            billerCode = "2275362",
            crn = "723647803",
            billerName = "Tenstra Inc",
            crnType = CRNType.FIXED
        )
        val billerJson = Converters.instance.stringFromPaymentDetails(billerDetails)
        assertEquals("{\"biller_code\":\"2275362\",\"crn\":\"723647803\",\"biller_name\":\"Tenstra Inc\",\"crn_type\":\"fixed_crn\"}", billerJson)

        val payIDDetails = PaymentDetails.PayID(
            name = "J GILBERT",
            payId = "j.gilbert@frollo.com",
            type = PayIDType.EMAIL
        )
        val payIDJson = Converters.instance.stringFromPaymentDetails(payIDDetails)
        assertEquals("{\"payid\":\"j.gilbert@frollo.com\",\"name\":\"J GILBERT\",\"type\":\"email\"}", payIDJson)

        val internationalDetails = PaymentDetails.International(
            beneficiary = Beneficiary(
                name = "Anne Maria",
                country = "New Zeland",
                message = "Test message new"
            ),
            bankDetails = BankDetails(
                country = "New Zeland",
                accountNumber = "12345666",
                bankAddress = BankAddress(
                    address = "ABC 666"
                ),
                bic = "777",
                fedWireNumber = "1234566",
                sortCode = "666",
                chipNumber = "555",
                routingNumber = "444",
                legalEntityIdentifier = "123666"
            )
        )
        val internationalJson = Converters.instance.stringFromPaymentDetails(internationalDetails)
        assertEquals("{\"beneficiary\":{\"name\":\"Anne Maria\",\"country\":\"New Zeland\",\"message\":\"Test message new\"},\"bank_details\":{\"country\":\"New Zeland\",\"account_number\":\"12345666\",\"bank_address\":{\"address\":\"ABC 666\"},\"bic\":\"777\",\"fed_wire_number\":\"1234566\",\"sort_code\":\"666\",\"chip_number\":\"555\",\"routing_number\":\"444\",\"legal_entity_identifier\":\"123666\"}}", internationalJson)
    }

    @Test
    fun testStringToDigitalWalletType() {
        val status = Converters.instance.stringToDigitalWalletType("CONTACT_NAME")
        assertEquals(DigitalWalletType.CONTACT_NAME, status)

        assertNull(Converters.instance.stringToDigitalWalletType(null))
    }

    @Test
    fun testStringFromDigitalWalletType() {
        val str = Converters.instance.stringFromDigitalWalletType(DigitalWalletType.CONTACT_NAME)
        assertEquals("CONTACT_NAME", str)

        assertNull(Converters.instance.stringFromDigitalWalletType(null))
    }

    @Test
    fun testStringToDigitalWalletProvider() {
        val status = Converters.instance.stringToDigitalWalletProvider("PAYPAL_AU")
        assertEquals(DigitalWalletProvider.PAYPAL_AU, status)

        assertEquals(DigitalWalletProvider.OTHER, Converters.instance.stringToDigitalWalletProvider(null))
    }

    @Test
    fun testStringFromDigitalWalletProvider() {
        val str = Converters.instance.stringFromDigitalWalletProvider(DigitalWalletProvider.PAYPAL_AU)
        assertEquals("PAYPAL_AU", str)

        assertEquals("OTHER", Converters.instance.stringFromDigitalWalletProvider(null))
    }

    @Test
    fun testStringToCardStatus() {
        val status = Converters.instance.stringToCardStatus("ACTIVE")
        assertEquals(CardStatus.ACTIVE, status)

        assertEquals(CardStatus.PENDING, Converters.instance.stringToCardStatus(null))
    }

    @Test
    fun testStringFromCardStatus() {
        val str = Converters.instance.stringFromCardStatus(CardStatus.ACTIVE)
        assertEquals("ACTIVE", str)

        assertEquals("PENDING", Converters.instance.stringFromCardStatus(null))
    }

    @Test
    fun testStringToCardDesignType() {
        val status = Converters.instance.stringToCardDesignType("DEFAULT")
        assertEquals(CardDesignType.DEFAULT, status)

        assertEquals(CardDesignType.DEFAULT, Converters.instance.stringToCardDesignType(null))
    }

    @Test
    fun testStringFromCardDesignType() {
        val str = Converters.instance.stringFromCardDesignType(CardDesignType.DEFAULT)
        assertEquals("DEFAULT", str)

        assertEquals("DEFAULT", Converters.instance.stringFromCardDesignType(null))
    }

    @Test
    fun testStringToCardType() {
        val status = Converters.instance.stringToCardType("CREDIT")
        assertEquals(CardType.CREDIT, status)

        assertNull(Converters.instance.stringToCardType(null))
    }

    @Test
    fun testStringFromCardType() {
        val str = Converters.instance.stringFromCardType(CardType.CREDIT)
        assertEquals("CREDIT", str)

        assertNull(Converters.instance.stringFromCardType(null))
    }

    @Test
    fun testStringToCardIssuer() {
        val status = Converters.instance.stringToCardIssuer("VISA")
        assertEquals(CardIssuer.VISA, status)

        assertNull(Converters.instance.stringToCardIssuer(null))
    }

    @Test
    fun testStringFromCardIssuer() {
        val str = Converters.instance.stringFromCardIssuer(CardIssuer.VISA)
        assertEquals("VISA", str)

        assertNull(Converters.instance.stringFromCardIssuer(null))
    }

    @Test
    fun testStringToPaydayStatus() {
        val status = Converters.instance.stringToPaydayStatus("CONFIRMED")
        assertEquals(PaydayStatus.CONFIRMED, status)

        assertEquals(PaydayStatus.UNKNOWN, Converters.instance.stringToPaydayStatus(null))
    }

    @Test
    fun testStringFromPaydayStatus() {
        val str = Converters.instance.stringFromPaydayStatus(PaydayStatus.CONFIRMED)
        assertEquals("CONFIRMED", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromPaydayStatus(null))
    }

    @Test
    fun testStringToPaydayFrequency() {
        val frequency = Converters.instance.stringToPaydayFrequency("MONTHLY")
        assertEquals(PaydayFrequency.MONTHLY, frequency)

        assertEquals(PaydayFrequency.UNKNOWN, Converters.instance.stringToPaydayFrequency(null))
    }

    @Test
    fun testStringFromPaydayFrequency() {
        val str = Converters.instance.stringFromPaydayFrequency(PaydayFrequency.MONTHLY)
        assertEquals("MONTHLY", str)

        assertEquals("UNKNOWN", Converters.instance.stringFromPaydayFrequency(null))
    }

    @Test
    fun testStringToServiceOutageType() {
        val frequency = Converters.instance.stringToServiceOutageType("OUTAGE")
        assertEquals(ServiceOutageType.OUTAGE, frequency)

        assertEquals(ServiceOutageType.INFO, Converters.instance.stringToServiceOutageType(null))
    }

    @Test
    fun testStringFromServiceOutageType() {
        val str = Converters.instance.stringFromServiceOutageType(ServiceOutageType.OUTAGE)
        assertEquals("OUTAGE", str)

        assertEquals("INFO", Converters.instance.stringFromServiceOutageType(null))
    }
}
