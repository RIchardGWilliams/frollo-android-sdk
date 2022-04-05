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

import us.frollo.frollosdk.model.api.contacts.ContactResponse
import us.frollo.frollosdk.model.coredata.contacts.Contact

internal fun ContactResponse.toContact(): Contact = Contact(
    contactId = contactId,
    createdDate = createdDate,
    modifiedDate = modifiedDate,
    verified = verified,
    relatedProviderAccountIds = relatedProviderAccountIds,
    name = name,
    nickName = nickName,
    description = description,
    aggregatorType = aggregatorType,
    consentId = consentId,
    editable = editable,
    paymentMethod = paymentMethod,
    paymentDetails = paymentDetails
)
