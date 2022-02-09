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

import us.frollo.frollosdk.model.api.managedproduct.ManagedProductCreateRequest
import us.frollo.frollosdk.model.coredata.managedproduct.TaxWithheldReason

internal fun testManagedProductRequestData(): ManagedProductCreateRequest {
    return ManagedProductCreateRequest(
        productId = 1L,
        acceptedTermsConditionsIds = listOf(1, 2),
        foreignTax = false,
        foreignTaxResidency = "US",
        taxResidency = "AU",
        tfn = "12345678",
        tin = "111",
        taxWithheldReason = TaxWithheldReason.CHOSE_TO_WITHHOLD
    )
}
