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

package us.frollo.frollosdk.model.api.managedproduct

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.managedproduct.ManagedProduct
import us.frollo.frollosdk.model.coredata.managedproduct.TaxWithheldReason
import us.frollo.frollosdk.model.coredata.managedproduct.TermsCondition

/**
 * ManagedProductCreateRequest
 *
 * Represents the request to create a managed product
 */
data class ManagedProductCreateRequest(

    /** ID of the [ManagedProduct] to create */
    @SerializedName("product_id") val productId: Long,

    /** Array of IDs of [TermsCondition] for [ManagedProduct] to create */
    @SerializedName("accepted_terms_conditions_ids") val acceptedTermsConditionsIds: List<Long>,

    /** Foreign tax user (optional) */
    @SerializedName("foreign_tax") val foreignTax: Boolean? = null,

    /** Tax residency (optional) */
    @SerializedName("tax_residency") val taxResidency: String? = null,

    /** Foreign Tax residency (optional) */
    @SerializedName("foreign_tax_residency") val foreignTaxResidency: String? = null,

    /** TFN of the user */
    @SerializedName("tfn") val tfn: String? = null,

    /** TIN of the user */
    @SerializedName("tin") val tin: String? = null,

    /** Reason for withholding tax */
    @SerializedName("tax_withheld_reason") val taxWithheldReason: TaxWithheldReason? = null,
)
