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

import us.frollo.frollosdk.model.api.cdr.CDRConfigurationResponse
import us.frollo.frollosdk.model.api.cdr.ConsentCreateRequest
import us.frollo.frollosdk.model.api.cdr.ConsentResponse
import us.frollo.frollosdk.model.api.cdr.ConsentUpdateRequest
import us.frollo.frollosdk.model.api.cdr.DisclosureConsentResponse
import us.frollo.frollosdk.model.api.cdr.ExternalPartyResponse
import us.frollo.frollosdk.model.coredata.cdr.CDRConfiguration
import us.frollo.frollosdk.model.coredata.cdr.Consent
import us.frollo.frollosdk.model.coredata.cdr.ConsentCreateForm
import us.frollo.frollosdk.model.coredata.cdr.ConsentUpdateForm
import us.frollo.frollosdk.model.coredata.cdr.DisclosureConsent
import us.frollo.frollosdk.model.coredata.cdr.ExternalParty

internal fun ConsentResponse.toConsent(): Consent =
    Consent(
        consentId = consentId,
        providerId = providerId,
        providerAccountId = providerAccountId,
        permissionIds = permissionIds,
        additionalPermissions = additionalPermissions,
        authorisationRequestURL = authorisationRequestURL,
        confirmationPDFURL = confirmationPDFURL,
        withdrawalPDFURL = withdrawalPDFURL,
        deleteRedundantData = deleteRedundantData,
        sharingStartedAt = sharingStartedAt,
        sharingStoppedAt = sharingStoppedAt,
        sharingExpiresAt = sharingExpiresAt,
        sharingDuration = sharingDuration,
        status = status,
        cdrConfigExternalId = cdrConfigExternalId
    )

internal fun ConsentCreateForm.toConsentCreateRequest(): ConsentCreateRequest =
    ConsentCreateRequest(
        providerId = providerId,
        sharingDuration = sharingDuration,
        permissions = permissions,
        additionalPermissions = additionalPermissions,
        existingConsentId = existingConsentId,
        deleteRedundantData = true,
        cdrConfigExternalId = cdrConfigExternalId
    )

internal fun ConsentUpdateForm.toConsentUpdateRequest(): ConsentUpdateRequest =
    ConsentUpdateRequest(
        status = status,
        sharingDuration = sharingDuration,
        deleteRedundantData = deleteRedundantData
    )

internal fun CDRConfigurationResponse.toCDRConfiguration(): CDRConfiguration =
    CDRConfiguration(
        configId = configId,
        supportEmail = supportEmail,
        sharingDurations = sharingDurations,
        permissions = permissions,
        additionalPermissions = additionalPermissions,
        externalId = externalId,
        displayName = displayName,
        cdrPolicyUrl = cdrPolicyUrl,
        model = model,
        relatedParties = relatedParties,
        sharingUseDuration = sharingUseDuration,
        initialSyncWindowWeeks = initialSyncWindowWeeks,
        softwareId = softwareId,
        softwareName = softwareName,
        imageUrl = imageUrl,
        summary = summary,
        description = description
    )

internal fun ExternalPartyResponse.toExternalParty(): ExternalParty =
    ExternalParty(
        partyId = partyId,
        externalId = externalId,
        name = name,
        company = company,
        contact = contact,
        description = description,
        status = status,
        imageUrl = imageUrl,
        smallImageUrl = smallImageUrl,
        privacyUrl = privacyUrl,
        type = type,
        trustedAdvisorType = trustedAdvisorType,
        summary = summary,
        sharingDurations = sharingDurations,
        permissions = permissions
    )

internal fun DisclosureConsentResponse.toDisclosureConsent(): DisclosureConsent =
    DisclosureConsent(
        consentId = consentId,
        status = status,
        linkedConsentIds = linkedConsentIds,
        permissions = permissionIds,
        disclosureDuration = disclosureDuration,
        sharingStartedAt = sharingStartedAt,
        sharingStoppedAt = sharingStoppedAt,
        sharingExpiresAt = sharingExpiresAt,
        externalParty = externalParty?.toExternalParty()
    )
