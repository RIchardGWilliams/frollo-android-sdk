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

import us.frollo.frollosdk.model.api.aggregation.accounts.AccountResponse
import us.frollo.frollosdk.model.api.aggregation.merchants.MerchantResponse
import us.frollo.frollosdk.model.api.aggregation.provideraccounts.ProviderAccountResponse
import us.frollo.frollosdk.model.api.aggregation.providers.ProviderResponse
import us.frollo.frollosdk.model.api.aggregation.providers.ProvidersResponse
import us.frollo.frollosdk.model.api.aggregation.tags.TransactionTagResponse
import us.frollo.frollosdk.model.api.aggregation.transactioncategories.TransactionCategoryResponse
import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionResponse
import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionsSummaryResponse
import us.frollo.frollosdk.model.api.cdr.CDRConfigurationResponse
import us.frollo.frollosdk.model.api.cdr.ConsentCreateRequest
import us.frollo.frollosdk.model.api.cdr.ConsentResponse
import us.frollo.frollosdk.model.api.cdr.ConsentUpdateRequest
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Balance
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccount
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.model.coredata.aggregation.providers.Provider
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderContainerName
import us.frollo.frollosdk.model.coredata.aggregation.tags.TransactionTag
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionsSummary
import us.frollo.frollosdk.model.coredata.cdr.CDRConfiguration
import us.frollo.frollosdk.model.coredata.cdr.Consent
import us.frollo.frollosdk.model.coredata.cdr.ConsentCreateForm
import us.frollo.frollosdk.model.coredata.cdr.ConsentUpdateForm

internal fun ProviderResponse.toProvider(): Provider =
    Provider(
        providerId = providerId,
        providerName = providerName,
        smallLogoUrl = smallLogoUrl ?: "", // Set empty string if null as the field in DB is non-nullable
        smallLogoRevision = smallLogoRevision ?: 0, // Set 0 if null as the field in DB is non-nullable
        providerStatus = providerStatus,
        popular = popular,
        containerNames = containerNames.map { ProviderContainerName.valueOf(it.toUpperCase()) }.toList(),
        loginUrl = loginUrl,
        largeLogoUrl = largeLogoUrl,
        largeLogoRevision = largeLogoRevision,
        baseUrl = baseUrl,
        forgetPasswordUrl = forgetPasswordUrl,
        oAuthSite = oAuthSite,
        authType = authType,
        mfaType = mfaType,
        helpMessage = helpMessage,
        loginHelpMessage = loginHelpMessage,
        loginForm = loginForm,
        encryption = encryption,
        aggregatorType = aggregatorType ?: AggregatorType.UNKNOWN, // This is a required field but sometimes backend sends null hence this workaround instead of making the column nullable in DB.
        permissionIds = permissionIds,
        productsAvailable = productsAvailable ?: false,
        jointAccountsAvailable = jointAccountsAvailable,
        associatedProviderIds = associatedProviderIds
    )

internal fun Provider.toProvidersResponse(): ProvidersResponse =
    ProvidersResponse(
        providerId = providerId,
        providerName = providerName,
        smallLogoUrl = smallLogoUrl,
        smallLogoRevision = smallLogoRevision,
        providerStatus = providerStatus,
        popular = popular,
        containerNames = containerNames,
        loginUrl = loginUrl,
        largeLogoUrl = largeLogoUrl,
        largeLogoRevision = largeLogoRevision,
        aggregatorType = aggregatorType,
        permissionIds = permissionIds,
        productsAvailable = productsAvailable
    )

internal fun ProviderAccountResponse.toProviderAccount(): ProviderAccount =
    ProviderAccount(
        providerAccountId = providerAccountId,
        providerId = providerId,
        editable = editable,
        refreshStatus = refreshStatus,
        loginForm = loginForm,
        externalId = externalId ?: ""
    )

internal fun AccountResponse.toAccount(): Account =
    Account(
        accountId = accountId,
        accountName = accountName,
        accountNumber = accountNumber,
        bsb = bsb,
        nickName = nickName,
        providerAccountId = providerAccountId,
        providerName = providerName,
        aggregatorType = aggregatorType ?: AggregatorType.UNKNOWN,
        aggregatorId = 0, // aggregatorId is removed from Accounts response from API version 2.5 hence defaulting it to 0 to avoid DB migration
        holderProfile = holderProfile,
        accountStatus = accountStatus,
        attributes = attributes,
        included = included,
        favourite = favourite,
        hidden = hidden,
        refreshStatus = refreshStatus,
        currentBalance = currentBalance?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        availableBalance = availableBalance?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        availableCash = availableCash?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        availableCredit = availableCredit?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        totalCashLimit = totalCashLimit?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        totalCreditLine = totalCreditLine?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        interestTotal = interestTotal?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        apr = apr,
        interestRate = interestRate,
        amountDue = amountDue?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        minimumAmountDue = minimumAmountDue?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        lastPaymentAmount = lastPaymentAmount?.let { Balance.getBalanceWithDefaultCurrencyIfMissing(it) },
        lastPaymentDate = lastPaymentDate,
        dueDate = dueDate,
        endDate = endDate,
        balanceDetails = balanceDetails,
        goalIds = goalIds,
        externalId = externalId ?: "",
        features = features,
        cdrProduct = cdrProduct,
        payIds = payIds
    )

internal fun TransactionResponse.toTransaction(): Transaction =
    Transaction(
        transactionId = transactionId,
        accountId = accountId,
        amount = Balance.getBalanceWithDefaultCurrencyIfMissing(amount),
        baseType = baseType,
        billId = billId,
        billPaymentId = billPaymentId,
        categoryId = category.id,
        merchant = merchant,
        budgetCategory = budgetCategory,
        description = description,
        included = included,
        memo = memo,
        postDate = postDate,
        status = status,
        transactionDate = transactionDate,
        userTags = userTags,
        externalId = externalId ?: "",
        goalId = goalId,
        reference = reference,
        reason = reason
    )

internal fun TransactionsSummaryResponse.toTransactionsSummary(): TransactionsSummary =
    TransactionsSummary(
        count = count,
        sum = sum
    )

internal fun TransactionCategoryResponse.toTransactionCategory(): TransactionCategory =
    TransactionCategory(
        transactionCategoryId = transactionCategoryId,
        userDefined = userDefined,
        name = name,
        categoryType = categoryType,
        defaultBudgetCategory = defaultBudgetCategory,
        iconUrl = iconUrl,
        placement = placement
    )

internal fun MerchantResponse.toMerchant(): Merchant =
    Merchant(
        merchantId = merchantId,
        name = name,
        merchantType = merchantType,
        smallLogoUrl = smallLogoUrl
    )

internal fun TransactionTagResponse.toTransactionTag(): TransactionTag =
    TransactionTag(name = name, count = count, lastUsedAt = lastUsedAt, createdAt = createdAt)

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
        sharingDuration = sharingDuration,
        status = status
    )

internal fun ConsentCreateForm.toConsentCreateRequest(): ConsentCreateRequest =
    ConsentCreateRequest(
        providerId = providerId,
        sharingDuration = sharingDuration,
        permissions = permissions,
        additionalPermissions = additionalPermissions,
        existingConsentId = existingConsentId,
        deleteRedundantData = true
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
        adrId = adrId,
        adrName = adrName,
        supportEmail = supportEmail,
        sharingDurations = sharingDurations,
        permissions = permissions,
        externalId = externalId,
        displayName = displayName,
        cdrPolicyUrl = cdrPolicyUrl,
        model = model,
        relatedParties = relatedParties,
        sharingUseDuration = sharingUseDuration
    )
