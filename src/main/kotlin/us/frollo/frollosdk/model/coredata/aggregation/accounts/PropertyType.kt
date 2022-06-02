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

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/** Type of Property */
@Keep
enum class PropertyType {

    /** NON_SPECIALISED_INDUSTRIAL */
    @SerializedName("non_specialised_industrial") NON_SPECIALISED_INDUSTRIAL,

    /** LIGHT_INDUSTRIAL */
    @SerializedName("light_industrial") LIGHT_INDUSTRIAL,

    /** COMPANY_TITLE_UNIT */
    @SerializedName("company_title_unit") COMPANY_TITLE_UNIT,

    /** CONVERTED_MOTEL_UNITS */
    @SerializedName("converted_motel_units") CONVERTED_MOTEL_UNITS,

    /** DUPLEX */
    @SerializedName("duplex") DUPLEX,

    /** DETACHED */
    @SerializedName("detached") DETACHED,

    /** GOVERNMENT_RENTAL_GUARANTEE */
    @SerializedName("government_rental_guarantee") GOVERNMENT_RENTAL_GUARANTEE,

    /** HOLIDAY_HOME */
    @SerializedName("holiday_home") HOLIDAY_HOME,

    /** HOLIDAY_RENTAL */
    @SerializedName("holiday_rental") HOLIDAY_RENTAL,

    /** NEW_STRATA_TITLE_UNIT */
    @SerializedName("new_strata_title_unit") NEW_STRATA_TITLE_UNIT,

    /** RENTAL_GUARANTEE */
    @SerializedName("rental_guarantee") RENTAL_GUARANTEE,

    /** RESORT_UNIT */
    @SerializedName("resort_unit") RESORT_UNIT,

    /** SEMI_DETACHED */
    @SerializedName("semi_detached") SEMI_DETACHED,

    /** SERVICED_APARTMENT */
    @SerializedName("serviced_apartment") SERVICED_APARTMENT,

    /** STRATA_TITLE_UNIT */
    @SerializedName("strata_title_unit") STRATA_TITLE_UNIT,

    /** TERRACE */
    @SerializedName("terrace") TERRACE,

    /** TIMESHARE */
    @SerializedName("timeshare") TIMESHARE,

    /** TOWNHOUSE */
    @SerializedName("townhouse") TOWNHOUSE,

    /** VACANT_LAND */
    @SerializedName("vacant_land") VACANT_LAND,

    /** VILLA */
    @SerializedName("villa") VILLA,

    /** PROF_CHAMBERS */
    @SerializedName("prof_chambers") PROF_CHAMBERS,

    /** OFFICES */
    @SerializedName("offices") OFFICES,

    /** FACTORY */
    @SerializedName("factory") FACTORY,

    /** WAREHOUSE */
    @SerializedName("warehouse") WAREHOUSE,

    /** RETIREMENT_VILLAGE */
    @SerializedName("retirement_village") RETIREMENT_VILLAGE,

    /** NON_SPECIALISED_COMMERCIAL */
    @SerializedName("non_specialised_commercial") NON_SPECIALISED_COMMERCIAL,

    /** RESIDENTIAL_COMMERCIAL */
    @SerializedName("residential_commercial") RESIDENTIAL_COMMERCIAL,

    /** OTHER */
    @SerializedName("other") OTHER,

    /** LESS_THAN_8_HECTARES */
    @SerializedName("less_than_8") LESS_THAN_8_HECTARES,

    /** HECTARES_8_TO_50 */
    @SerializedName("8_50_hectares") HECTARES_8_TO_50,

    /** OVER_50_HECTARES */
    @SerializedName("over_50_hectares") OVER_50_HECTARES;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
