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
import us.frollo.frollosdk.model.coredata.user.FeatureFlag
import us.frollo.frollosdk.model.coredata.user.Gender
import us.frollo.frollosdk.model.coredata.user.HouseholdType
import us.frollo.frollosdk.model.coredata.user.Industry
import us.frollo.frollosdk.model.coredata.user.Occupation
import us.frollo.frollosdk.model.coredata.user.RegisterStep
import us.frollo.frollosdk.model.coredata.user.TFNStatus
import us.frollo.frollosdk.model.coredata.user.UserAddress
import us.frollo.frollosdk.model.coredata.user.UserStatus

internal data class UserResponse(
    @SerializedName("id") val userId: Long,
    @SerializedName("given_name") val firstName: String?,
    @SerializedName("email") val email: String,
    @SerializedName("email_verified") val emailVerified: Boolean,
    @SerializedName("status") val status: UserStatus,
    @SerializedName("primary_currency") val primaryCurrency: String,
    @SerializedName("valid_password") val validPassword: Boolean,
    @SerializedName("register_steps") val registerSteps: List<RegisterStep>?,
    @SerializedName("registration_date") val registrationDate: String,
    @SerializedName("facebook_id") val facebookId: String?,
    @SerializedName("attribution") val attribution: Attribution?,
    @SerializedName("family_name") val lastName: String?,
    @SerializedName("mobile_number") val mobileNumber: String?,
    @SerializedName("gender") val gender: Gender?,
    @SerializedName("residential_address") val residentialAddress: UserAddress?,
    @SerializedName("mailing_address") val mailingAddress: UserAddress?,
    @SerializedName("previous_address") val previousAddress: UserAddress?,
    @SerializedName("household_size") val householdSize: Int?,
    @SerializedName("marital_status") val householdType: HouseholdType?,
    @SerializedName("occupation") val occupation: Occupation?,
    @SerializedName("industry") val industry: Industry?,
    @SerializedName("date_of_birth") val dateOfBirth: String?, // yyyy-MM-dd
    @SerializedName("driver_license") val driverLicense: String?,
    @SerializedName("features") val features: List<FeatureFlag>?,
    @SerializedName("tfn_status") val tfnStatus: TFNStatus?,
    @SerializedName("external_id") val externalId: String?,
    @SerializedName("middle_names") val middleNames: String?,
    @SerializedName("external_party_id") val externalPartyId: Long?
)
