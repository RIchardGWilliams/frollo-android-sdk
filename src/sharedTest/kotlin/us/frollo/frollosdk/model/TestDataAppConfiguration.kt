package us.frollo.frollosdk.model

import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig

internal fun testCompanyConfigData(): CompanyConfig {
    return CompanyConfig(
        displayName = "Frollo",
        legalName = "Frollo Australia Pty Ltd",
        abn = "12345678901",
        acn = "123456789",
        phone = "020000000000",
        address = "Level 33 100 Mount Street, North Sydney, NSW 2060",
        supportEmail = "support@frollo.us",
        supportPhone = "555 02 0000000"
    )
}

internal fun testFeatureConfigData(): List<FeatureConfig> {
    return listOf(
        FeatureConfig(
            key = "budgets",
            name = "Budgeting",
            enabled = true
        ),
        FeatureConfig(
            key = "assets and liabilities",
            name = "manual_asset_liability",
            enabled = false
        ),
        FeatureConfig(
            key = "financial passport",
            name = "financial_passport",
            enabled = true
        )
    )
}

internal fun testLinkConfigData(): List<LinkConfig> {
    return listOf(
        LinkConfig(
            key = "terms",
            name = "Terms and Conditions",
            url = "https://frollo.us/terms"
        ),
        LinkConfig(
            key = "privacy policy",
            name = "Privacy Policy",
            url = "https://frollo.us/api/pages/privacy"
        ),
        LinkConfig(
            key = "contact us",
            name = "Contact Us",
            url = "https://frollo.us/api/pages/explainer_landing_page_android"
        )
    )
}
