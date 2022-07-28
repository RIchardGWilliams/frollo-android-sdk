package us.frollo.frollosdk.model.api.aggregation.tags

import com.google.gson.annotations.SerializedName

internal data class TransactionTagsBulkDeleteRequest(

    @SerializedName("name") val name: String,
    @SerializedName("transaction_ids") val transactionIds: List<Long>,
    @SerializedName("remove_tag_rule_id") val removeTagRuleId: Long?
)
