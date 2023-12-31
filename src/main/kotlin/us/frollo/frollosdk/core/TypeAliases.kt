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

package us.frollo.frollosdk.core

import okhttp3.ResponseBody
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.error.LoginFormError
import us.frollo.frollosdk.model.api.affordability.FinancialPassportResponse

/**
 * Frollo SDK Completion Handler with success state or error state if an issue occurs
 */
typealias OnFrolloSDKCompletionListener<T> = (T) -> Unit

/**
 * Frollo SDK Validation Completion Handler with valid result and optional error if validation fails
 */
typealias FormValidationCompletionListener = (valid: Boolean, error: LoginFormError?) -> Unit

/**
 * Frollo SDK Completion Handler with tuple of bool that indicates whether the API is waiting for the data to be ready,
 * exported filename and the exported data if success and optional error if an issue occurs
 */
typealias OnFrolloSDKExportDataCompletionListener<Boolean, T> = (isEmptyResponseCode: Boolean, Resource<ResponseBody>?) -> Unit

/**
 *
 */
typealias OnFrolloSDKFPCompletionListener<Boolean, T> = (isEmptyResponseCode: Boolean, Resource<FinancialPassportResponse>?) -> Unit
