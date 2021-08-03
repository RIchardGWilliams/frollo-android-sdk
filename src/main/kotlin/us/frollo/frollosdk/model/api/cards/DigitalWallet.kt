package us.frollo.frollosdk.model.api.cards

import com.google.gson.annotations.SerializedName

enum class DigitalWallet {
    @SerializedName("apple_pay") APPLE_PAY,
    @SerializedName("google_pay") GOOGLE_PAY,
    @SerializedName("samsung_pay") SAMSUNG_PAY,
}
