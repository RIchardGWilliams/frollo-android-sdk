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

package us.frollo.frollosdk.model.coredata.aggregation.accounts

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/** Balance model with amount and currency */
data class Balance(

    /** Amount */
    @ColumnInfo(name = "amount") @SerializedName("amount") var amount: BigDecimal,

    /** Currency */
    @ColumnInfo(name = "currency") @SerializedName("currency") var currency: String
) {
    companion object {
        fun getBalanceWithDefaultCurrencyIfMissing(balance: Balance): Balance {
            return if (balance.currency == null) { // Note: Failsafe check if API doesn't send the currency
                Balance(balance.amount, "AUD")
            } else {
                balance
            }
        }
    }
}
