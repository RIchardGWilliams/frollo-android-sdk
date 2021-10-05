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

package us.frollo.frollosdk.model.coredata.address

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import us.frollo.frollosdk.model.IAdapterModel
import java.io.Serializable

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "addresses",
    indices = [
        Index("address_id")
    ]
)
/**
 * Data representation of Address
 */
data class Address(

    /** Unique Identifier of the Address */
    @PrimaryKey @ColumnInfo(name = "address_id") var addressId: Long,

    /** Address DPID - Delivery Point Identifier (Optional) */
    @ColumnInfo(name = "dpid") var dpId: String? = null,

    /** Address line 1 (Optional) */
    @ColumnInfo(name = "line_1") var line1: String? = null,

    /** Address line 2 (Optional) */
    @ColumnInfo(name = "line_2") var line2: String? = null,

    /** Address suburb name. (Optional) */
    @ColumnInfo(name = "suburb") var suburb: String? = null,

    /** Address town name. (Optional) */
    @ColumnInfo(name = "town") var town: String? = null,

    /** Address region. (Optional) */
    @ColumnInfo(name = "region") var region: String? = null,

    /** Address state. (Optional) */
    @ColumnInfo(name = "state") var state: String? = null,

    /** Address country. (Optional) */
    @ColumnInfo(name = "country") var country: String? = null,

    /** Address postcode. (Optional) */
    @ColumnInfo(name = "postal_code") var postcode: String? = null,

    /** Full address in formatted form. (Optional) */
    @ColumnInfo(name = "long_form") var longForm: String? = null

) : IAdapterModel, Serializable
