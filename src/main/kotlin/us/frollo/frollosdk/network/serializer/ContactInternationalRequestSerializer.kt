package us.frollo.frollosdk.network.serializer

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import us.frollo.frollosdk.model.api.contacts.ContactInternationalCreateUpdateRequest
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod
import java.lang.reflect.Type

internal object ContactInternationalRequestSerializer : JsonSerializer<ContactInternationalCreateUpdateRequest> {

    @Synchronized
    override fun serialize(src: ContactInternationalCreateUpdateRequest?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = Gson().toJsonTree(src, typeOfSrc).asJsonObject
        jsonObject.remove("payment_details") // Remove payment_details to ensure there is no duplicate when we add below
        val paymentDetailsElement = when (src?.paymentMethod) {
            PaymentMethod.INTERNATIONAL -> Gson().toJsonTree(src.paymentDetails, ContactInternationalCreateUpdateRequest.InternationalPaymentDetails::class.java)
            else -> null
        }
        paymentDetailsElement?.let {
            jsonObject.add("payment_details", it)
        }
        return jsonObject
    }
}
