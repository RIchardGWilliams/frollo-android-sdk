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

package us.frollo.frollosdk.model.coredata.cdr

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/** Defines all the possible cases of the type of a Trusted Advisor */
enum class TrustedAdvisorType {

    /** Qualified accountant */
    @SerializedName("accountant") ACCOUNTANT,

    /** Mortgage broker */
    @SerializedName("broker") BROKER,

    /** Persons who are admitted to the legal profession */
    @SerializedName("lawyer") LAWYER,

    /** Registered tax agents, BAS agents and tax (financial) advisors */
    @SerializedName("tax_agent") TAX_AGENT,

    /** Financial advisers or financial planners */
    @SerializedName("advisor") ADVISOR,

    /** Financial counselling agencies */
    @SerializedName("counsellor") COUNSELLOR,

    /** Other trusted advisers not included in above classes */
    @SerializedName("other") OTHER;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
