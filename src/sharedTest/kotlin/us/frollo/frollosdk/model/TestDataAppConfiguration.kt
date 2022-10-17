package us.frollo.frollosdk.model

import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig
import us.frollo.frollosdk.testutils.randomBoolean
import us.frollo.frollosdk.testutils.randomUUID

internal fun testCompanyConfigData(displayName: String? = null, supportEmail: String? = null): CompanyConfig {
    return CompanyConfig(
        displayName = displayName ?: "Frollo",
        legalName = "Frollo Australia Pty Ltd",
        abn = "12345678901",
        acn = "123456789",
        phone = "020000000000",
        address = "Level 33 100 Mount Street, North Sydney, NSW 2060",
        supportEmail = supportEmail ?: "support@frollo.us",
        supportPhone = "555 02 0000000"
    )
}

internal fun testFeatureConfigData(key: String? = null, enabled: Boolean? = null): FeatureConfig {
    return FeatureConfig(
        key = key ?: randomUUID(),
        name = "Budgeting",
        enabled = enabled ?: randomBoolean()
    )
}

internal fun testLinkConfigData(key: String? = null, url: String? = null): LinkConfig {
    return LinkConfig(
        key = key ?: randomUUID(),
        name = "Terms and Conditions",
        url = url ?: "https://frollo.us/terms"
    )
}
