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

package us.frollo.frollosdk.model.coredata.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import us.frollo.frollosdk.model.IAdapterModel
import us.frollo.frollosdk.model.api.cards.DigitalWallet

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "card",
    indices = [
        Index("card_id"),
        Index("account_id")
    ]
)
/**
 * Data representation of Card
 */
data class Card(

    /** Unique ID of the card */
    @PrimaryKey @ColumnInfo(name = "card_id") val cardId: Long,

    /** ID of the account to which the card is associated with */
    @ColumnInfo(name = "account_id") val accountId: Long,

    /** Indicates the current status of the card */
    @ColumnInfo(name = "status") var status: CardStatus,

    /** The design type of the card */
    @ColumnInfo(name = "design_type") val designType: CardDesignType,

    /**
     * Date on which the card was created / ordered
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "created_at") val createdDate: String,

    /**
     * Date the card was cancelled (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "cancelled_at") val cancelledDate: String?,

    /** Name of the card (optional) */
    @ColumnInfo(name = "name") val name: String?,

    /** Nick name of the card (optional) */
    @ColumnInfo(name = "nick_name") var nickName: String?,

    /** Last 4 digits of the card's Primary Account Number (Optional) */
    @ColumnInfo(name = "pan_last_digits") val panLastDigits: String?,

    /** Date on which the card will expire (Optional). Date Format: MM/yy or MM/yyyy */
    @ColumnInfo(name = "expiry_date") val expiryDate: String?,

    /** Name of the card holder (Optional) */
    @ColumnInfo(name = "cardholder_name") val cardholderName: String?,

    /** The type of the card */
    @ColumnInfo(name = "type") val type: CardType?,

    /** Issuer of the card */
    @ColumnInfo(name = "issuer") val issuer: CardIssuer?,

    /** A List of [DigitalWallet] supported by the card. (Optional) */
    @ColumnInfo(name = "digital_wallets") val digitalWallets: List<DigitalWallet>?,

    /**
     * Date on which the pin was set (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "pin_set_at") val pinSetDate: String?

) : IAdapterModel
