package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountSubType

/** Liabilities Bucket(grouping) indicating which account.account_attributes.account_type
 *  are included in manual liabilities accounts
 */
data class LiabilitiesBucket(

    /** Account sub-types */
    @SerializedName("account_types") val accountSubTypes: List<AccountSubType>
)
