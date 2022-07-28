package us.frollo.frollosdk.model.api.aggregation.tags

import com.google.gson.annotations.SerializedName

internal data class TransactionTagBulkCreateRequest(

    @SerializedName("name") val name: String,
    @SerializedName("transaction_ids") val transactionIds: List<Long>,
    @SerializedName("create_tag_rule_id") val createTagRuleId: Long?
)
