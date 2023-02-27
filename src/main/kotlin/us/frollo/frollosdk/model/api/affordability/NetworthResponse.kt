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

package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName

/**
Net worth Model
Holds information about the user's Networth
 */
data class NetworthResponse(
    /** Summary of User's networth */
    @SerializedName("summary") val summary: NetworthSummary,

    /** An object to represent list if IDs of user's assets */
    @SerializedName("assets") val assets: NetworthAssetsLiabilities,

    /** An object to represent list if IDs of user's liabilities */
    @SerializedName("liabilities") val liabilities: NetworthAssetsLiabilities

)
