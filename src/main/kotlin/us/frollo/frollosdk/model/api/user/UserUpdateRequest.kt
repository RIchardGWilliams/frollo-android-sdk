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

package us.frollo.frollosdk.model.api.user

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.user.Attribution
import us.frollo.frollosdk.model.coredata.user.Gender
import us.frollo.frollosdk.model.coredata.user.HouseholdType
import us.frollo.frollosdk.model.coredata.user.Industry
import us.frollo.frollosdk.model.coredata.user.Occupation

/**
 * UserUpdateRequest
 *
 * Represents the request to update user details
 */
data class UserUpdateRequest(

    /** First name of the user (optional) */
    @SerializedName("given_name") val firstName: String? = null,

    /** Email address of the user (optional) */
    @SerializedName("email") val email: String? = null,

    /** Primary currency of the user */
    @SerializedName("primary_currency") val primaryCurrency: String? = null,

    /** Attribution of the user */
    @SerializedName("attribution") val attribution: Attribution? = null,

    /** Last name of the user (optional) */
    @SerializedName("family_name") val lastName: String? = null,

    /** Mobile phone number of the user (optional) */
    @SerializedName("mobile_number") val mobileNumber: String? = null,

    /** Gender of the user (optional) */
    @SerializedName("gender") val gender: Gender? = null,

    /** Current residential address of the user */
    @SerializedName("residential_address_id") val residentialAddressId: Long? = null,

    /** Mailing address of the user */
    @SerializedName("mailing_address_id") val mailingAddressId: Long? = null,

    /** Previous residential address of the user */
    @SerializedName("previous_address_id") val previousAddressId: Long? = null,

    /** Number of people in the household (optional) */
    @SerializedName("household_size") val householdSize: Int? = null,

    /** Household type of the user (optional) */
    @SerializedName("marital_status") val householdType: HouseholdType? = null,

    /** Occupation of the user (optional) */
    @SerializedName("occupation") val occupation: Occupation? = null,

    /** Industry the user works in (optional) */
    @SerializedName("industry") val industry: Industry? = null,

    /** Date of birth of the user (optional) (format pattern - yyyy-MM-dd) */
    @SerializedName("date_of_birth") val dateOfBirth: String? = null, // yyyy-MM-dd

    /** Drivers license of the user */
    @SerializedName("driver_license") val driverLicense: String? = null,

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

    /** Middle names of the user (optional) */
    @SerializedName("middle_names") val middleNames: String? = null,

    /** External Party ID the User is sharing data with (optional) */
    @SerializedName("external_party_id") val externalPartyId: Long? = null
)
