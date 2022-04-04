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

package us.frollo.frollosdk.model.coredata.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import us.frollo.frollosdk.model.IAdapterModel
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType

@Entity(
    tableName = "contact",
    indices = [
        Index("contact_id"),
    ]
)

/** Data representation of a Contact */
data class Contact(

    /** Unique ID of the contact */
    @PrimaryKey
    @ColumnInfo(name = "contact_id") val contactId: Long,

    /** Contact created date */
    @ColumnInfo(name = "created_date") val createdDate: String,

    /** Contact modified date */
    @ColumnInfo(name = "modified_date") val modifiedDate: String,

    /** Contact verified status */
    @ColumnInfo(name = "verified") val verified: Boolean,

    /** Related provider account IDs of the contact */
    @ColumnInfo(name = "related_provider_account_ids") val relatedProviderAccountIds: List<Long>?,

    /** Name of the contact */
    @ColumnInfo(name = "name") var name: String,

    /** Nick name of the contact */
    @ColumnInfo(name = "nick_name") var nickName: String,

    /** Description */
    @ColumnInfo(name = "description") var description: String?,

    /** Specifies the aggregator with which this contact was synced from */
    @ColumnInfo(name = "aggregator") val aggregatorType: AggregatorType,

    /** ID of the Consent this contact was retrieved from */
    @ColumnInfo(name = "consent_id") val consentId: Long?,

    /** Indicates if edit APIs such as Update/Delete will work */
    @ColumnInfo(name = "editable") val editable: Boolean,

    /** Payment Method of the contact */
    @ColumnInfo(name = "payment_method") var paymentMethod: PaymentMethod,

    /** Payment Details of the contact */
    @ColumnInfo(name = "payment_details") var paymentDetails: PaymentDetails?

) : IAdapterModel
