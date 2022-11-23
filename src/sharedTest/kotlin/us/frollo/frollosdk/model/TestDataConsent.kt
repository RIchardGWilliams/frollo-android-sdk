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

package us.frollo.frollosdk.model

import us.frollo.frollosdk.model.api.cdr.CDRConfigurationResponse
import us.frollo.frollosdk.model.api.cdr.ConsentResponse
import us.frollo.frollosdk.model.api.cdr.DisclosureConsentResponse
import us.frollo.frollosdk.model.api.cdr.ExternalPartyResponse
import us.frollo.frollosdk.model.coredata.cdr.CDRModel
import us.frollo.frollosdk.model.coredata.cdr.CDRPermission
import us.frollo.frollosdk.model.coredata.cdr.CDRPermissionDetail
import us.frollo.frollosdk.model.coredata.cdr.ConsentCreateForm
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus
import us.frollo.frollosdk.model.coredata.cdr.ConsentUpdateForm
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyCompany
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyType
import us.frollo.frollosdk.model.coredata.cdr.SharingDuration
import us.frollo.frollosdk.model.coredata.cdr.TrustedAdvisorType
import us.frollo.frollosdk.testutils.randomNumber
import us.frollo.frollosdk.testutils.randomString
import us.frollo.frollosdk.testutils.randomUUID

internal fun testConsentResponseData(
    consentId: Long? = null,
    providerId: Long? = null,
    providerAccountId: Long? = null,
    status: ConsentStatus? = null
): ConsentResponse {

    return ConsentResponse(
        consentId = consentId ?: randomNumber().toLong(),
        providerId = providerId ?: randomNumber().toLong(),
        providerAccountId = providerAccountId ?: randomNumber().toLong(),
        status = status ?: ConsentStatus.ACTIVE,
        sharingDuration = randomNumber().toLong(),
        sharingStartedAt = "2020-01-03T12:02:12.505+10:00",
        sharingExpiresAt = "2020-06-03T12:02:12.505+10:00",
        sharingStoppedAt = "2020-05-03T12:02:12.505+10:00",
        authorisationRequestURL = "",
        confirmationPDFURL = "",
        withdrawalPDFURL = "",
        permissionIds = listOf("account_details", "transaction_details"),
        additionalPermissions = null,
        deleteRedundantData = true,
        cdrConfigExternalId = "frollo-default"
    )
}

internal fun testCDRPermissionData(): List<CDRPermission> {
    return listOf(
        CDRPermission(
            permissionId = "account_details",
            title = "Account balance and details",
            description = "We leverage...",
            required = true,
            details = listOf(
                CDRPermissionDetail(
                    detailId = "account_name",
                    description = "Name of account"
                )
            ),
            placement = 10
        ),
        CDRPermission(
            permissionId = "transaction_details",
            title = "Transaction and details",
            description = "We leverage...",
            required = false,
            details = listOf(
                CDRPermissionDetail(
                    detailId = "transaction_name",
                    description = "Name of transaction"
                )
            ),
            placement = 10
        )
    )
}

internal fun testConsentCreateFormData(providerId: Long? = null): ConsentCreateForm {
    return ConsentCreateForm(
        providerId = providerId ?: randomNumber().toLong(),
        sharingDuration = randomNumber().toLong(),
        permissions = listOf("account_details"),
        additionalPermissions = null,
        existingConsentId = null,
        cdrConfigExternalId = "frollo-default"
    )
}

internal fun testConsentUpdateFormData(sharingDuration: Long? = null): ConsentUpdateForm {
    return ConsentUpdateForm(
        sharingDuration = sharingDuration ?: randomNumber().toLong()
    )
}

internal fun testCDRConfigurationData(configId: Long? = null, externalId: String? = null): CDRConfigurationResponse {
    return CDRConfigurationResponse(
        configId = configId ?: randomNumber().toLong(),
        supportEmail = randomString(20),
        sharingDurations = listOf(testSharingDurationData(), testSharingDurationData(), testSharingDurationData()),
        permissions = testCDRPermissionData(),
        additionalPermissions = listOf(),
        externalId = externalId ?: randomUUID(),
        displayName = randomString(20),
        cdrPolicyUrl = randomString(20),
        model = CDRModel.AFFILIATE,
        relatedParties = listOf(),
        initialSyncWindowWeeks = 53,
        softwareId = "469811b0-90d8-eb11-a824-000d3a884a20",
        softwareName = "public",
        imageUrl = "https://frollo.com.au",
        summary = "TEST Summary",
        description = "TEST Description",
    )
}

internal fun testSharingDurationData(): SharingDuration {
    return SharingDuration(
        duration = randomNumber().toLong(),
        description = randomString(20),
        imageUrl = randomString(20),
        sharingUseDuration = randomNumber().toLong(),
        sharingUseDescription = randomString(20)
    )
}

internal fun testExternalPartyResponseData(
    partyId: Long? = null,
    status: ExternalPartyStatus? = null,
    type: ExternalPartyType? = null,
    trustedAdvisorType: TrustedAdvisorType? = null,
    externalId: String? = null
): ExternalPartyResponse {
    return ExternalPartyResponse(
        partyId = partyId ?: randomNumber().toLong(),
        externalId = externalId ?: randomString(20),
        key = randomString(6),
        name = randomString(20),
        company = ExternalPartyCompany(
            displayName = randomString(20),
            legalName = randomString(20)
        ),
        contact = randomString(20),
        description = randomString(20),
        status = status ?: ExternalPartyStatus.ENABLED,
        imageUrl = "https://frollo.com.au/image",
        smallImageUrl = "https://frollo.com.au/image_small",
        privacyUrl = "https://frollo.com.au/terms",
        type = type ?: ExternalPartyType.TRUSTED_ADVISOR,
        trustedAdvisorType = trustedAdvisorType ?: TrustedAdvisorType.ACCOUNTANT,
        summary = randomString(20),
        sharingDurations = listOf(
            testSharingDurationData()
        ),
        permissions = testCDRPermissionData()
    )
}

internal fun testDisclosureConsentResponseData(
    consentId: Long? = null,
    status: ConsentStatus? = null
): DisclosureConsentResponse {
    return DisclosureConsentResponse(
        consentId = consentId ?: randomNumber().toLong(),
        status = status ?: ConsentStatus.ACTIVE,
        linkedConsentIds = listOf(randomNumber().toLong()),
        permissionIds = listOf("account_details", "transaction_details"),
        disclosureDuration = randomNumber().toLong(),
        sharingStartedAt = "2022-02-04T05:10:19+00:00",
        sharingStoppedAt = "2022-12-04T05:10:19+00:00",
        sharingExpiresAt = "2020-06-03T12:02:12.505+10:00",
        externalParty = testExternalPartyResponseData(partyId = 12345L)
    )
}
