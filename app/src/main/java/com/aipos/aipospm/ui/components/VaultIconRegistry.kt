package com.aipos.aipospm.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

enum class VaultIconCategory(@androidx.annotation.StringRes val displayNameRes: Int) {
    ALL(com.aipos.aipospm.R.string.ui_all_6a7208),
    DEVELOPER(com.aipos.aipospm.R.string.ui_developer_cloud_fe0f4f),
    SERVICES(com.aipos.aipospm.R.string.ui_services_web_b24e2a),
    MEDIA(com.aipos.aipospm.R.string.ui_media_entertainment_61ae7f),
    SECURITY(com.aipos.aipospm.R.string.ui_security_devices_aa7021),
    GENERAL(com.aipos.aipospm.R.string.ui_general_9239ee)
}

data class VaultIconItem(
    val id: String,
    @androidx.annotation.StringRes val labelRes: Int,
    val icon: ImageVector,
    val category: VaultIconCategory
)

object VaultIconRegistry {

    val ALL_ICONS: List<VaultIconItem> = listOf(
        // Developer & Cloud — Ideal for API Keys & developer services
        VaultIconItem("api", com.aipos.aipospm.R.string.ui_api_d93d10, Icons.Default.Api, VaultIconCategory.DEVELOPER),
        VaultIconItem("key", com.aipos.aipospm.R.string.ui_key_c67dd2, Icons.Default.VpnKey, VaultIconCategory.DEVELOPER),
        VaultIconItem("code", com.aipos.aipospm.R.string.ui_code_adac69, Icons.Default.Code, VaultIconCategory.DEVELOPER),
        VaultIconItem("terminal", com.aipos.aipospm.R.string.ui_terminal_a1f52c, Icons.Default.Terminal, VaultIconCategory.DEVELOPER),
        VaultIconItem("ai", com.aipos.aipospm.R.string.ui_ai_llm_9057fa, Icons.Default.AutoAwesome, VaultIconCategory.DEVELOPER),
        VaultIconItem("robot", com.aipos.aipospm.R.string.ui_bot_3a3192, Icons.Default.SmartToy, VaultIconCategory.DEVELOPER),
        VaultIconItem("cloud", com.aipos.aipospm.R.string.ui_cloud_b2efea, Icons.Default.Cloud, VaultIconCategory.DEVELOPER),
        VaultIconItem("database", com.aipos.aipospm.R.string.ui_database_61074f, Icons.Default.Storage, VaultIconCategory.DEVELOPER),
        VaultIconItem("server", com.aipos.aipospm.R.string.ui_server_cb0cb1, Icons.Default.Dns, VaultIconCategory.DEVELOPER),
        VaultIconItem("webhook", com.aipos.aipospm.R.string.ui_webhook_3ac282, Icons.Default.Webhook, VaultIconCategory.DEVELOPER),
        VaultIconItem("rocket", com.aipos.aipospm.R.string.ui_deploy_ci_521d1c, Icons.Default.RocketLaunch, VaultIconCategory.DEVELOPER),
        VaultIconItem("chip", com.aipos.aipospm.R.string.ui_hardware_b76ab6, Icons.Default.Memory, VaultIconCategory.DEVELOPER),
        VaultIconItem("bug", com.aipos.aipospm.R.string.ui_debug_bug_ac5e32, Icons.Default.BugReport, VaultIconCategory.DEVELOPER),
        VaultIconItem("data", com.aipos.aipospm.R.string.ui_data_json_ab565c, Icons.Default.DataObject, VaultIconCategory.DEVELOPER),
        VaultIconItem("analytics", com.aipos.aipospm.R.string.ui_analytics_25bc96, Icons.Default.Analytics, VaultIconCategory.DEVELOPER),
        VaultIconItem("brain", com.aipos.aipospm.R.string.ui_neural_ai_531cfd, Icons.Default.Psychology, VaultIconCategory.DEVELOPER),
        VaultIconItem("folder", com.aipos.aipospm.R.string.ui_folder_repo_03913c, Icons.Default.Folder, VaultIconCategory.DEVELOPER),
        VaultIconItem("network", com.aipos.aipospm.R.string.ui_network_hub_cb2110, Icons.Default.Hub, VaultIconCategory.DEVELOPER),
        VaultIconItem("router", com.aipos.aipospm.R.string.ui_router_6dcf16, Icons.Default.Router, VaultIconCategory.DEVELOPER),
        VaultIconItem("cable", com.aipos.aipospm.R.string.ui_cable_iot_0e0523, Icons.Default.Cable, VaultIconCategory.DEVELOPER),

        // Services & Web — Great for passwords & web credentials
        VaultIconItem("globe", com.aipos.aipospm.R.string.ui_website_2e8a57, Icons.Default.Language, VaultIconCategory.SERVICES),
        VaultIconItem("email", com.aipos.aipospm.R.string.ui_email_84add5, Icons.Default.Email, VaultIconCategory.SERVICES),
        VaultIconItem("bank", com.aipos.aipospm.R.string.ui_bank_finance_baea60, Icons.Default.AccountBalance, VaultIconCategory.SERVICES),
        VaultIconItem("card", com.aipos.aipospm.R.string.ui_credit_card_69ed5a, Icons.Default.CreditCard, VaultIconCategory.SERVICES),
        VaultIconItem("crypto", com.aipos.aipospm.R.string.ui_crypto_btc_7c478a, Icons.Default.CurrencyBitcoin, VaultIconCategory.SERVICES),
        VaultIconItem("wallet", com.aipos.aipospm.R.string.ui_wallet_b60862, Icons.Default.AccountBalanceWallet, VaultIconCategory.SERVICES),
        VaultIconItem("paid", com.aipos.aipospm.R.string.ui_payment_cash_4c9853, Icons.Default.Paid, VaultIconCategory.SERVICES),
        VaultIconItem("savings", com.aipos.aipospm.R.string.ui_savings_4390be, Icons.Default.Savings, VaultIconCategory.SERVICES),
        VaultIconItem("shopping", com.aipos.aipospm.R.string.ui_shopping_96a0dc, Icons.Default.ShoppingCart, VaultIconCategory.SERVICES),
        VaultIconItem("store", com.aipos.aipospm.R.string.ui_storefront_7c7a77, Icons.Default.Storefront, VaultIconCategory.SERVICES),
        VaultIconItem("receipt", com.aipos.aipospm.R.string.ui_bill_invoice_2ceb7c, Icons.AutoMirrored.Filled.ReceiptLong, VaultIconCategory.SERVICES),
        VaultIconItem("work", com.aipos.aipospm.R.string.ui_work_00040b, Icons.Default.Work, VaultIconCategory.SERVICES),
        VaultIconItem("school", com.aipos.aipospm.R.string.ui_education_aaf87f, Icons.Default.School, VaultIconCategory.SERVICES),
        VaultIconItem("chat", com.aipos.aipospm.R.string.ui_chat_social_2d7c0a, Icons.Default.Forum, VaultIconCategory.SERVICES),
        VaultIconItem("flight", com.aipos.aipospm.R.string.ui_travel_flight_52a332, Icons.Default.Flight, VaultIconCategory.SERVICES),
        VaultIconItem("hotel", com.aipos.aipospm.R.string.ui_hotel_stay_53a320, Icons.Default.Hotel, VaultIconCategory.SERVICES),
        VaultIconItem("car", com.aipos.aipospm.R.string.ui_transport_auto_1eea35, Icons.Default.DirectionsCar, VaultIconCategory.SERVICES),
        VaultIconItem("restaurant", com.aipos.aipospm.R.string.ui_food_dining_dba1e3, Icons.Default.Restaurant, VaultIconCategory.SERVICES),

        // Media & Entertainment
        VaultIconItem("gaming", com.aipos.aipospm.R.string.ui_gaming_c0ecc0, Icons.Default.SportsEsports, VaultIconCategory.MEDIA),
        VaultIconItem("movie", com.aipos.aipospm.R.string.ui_cinema_movies_940d22, Icons.Default.Movie, VaultIconCategory.MEDIA),
        VaultIconItem("tv", com.aipos.aipospm.R.string.ui_tv_streaming_255389, Icons.Default.Tv, VaultIconCategory.MEDIA),
        VaultIconItem("music", com.aipos.aipospm.R.string.ui_music_audio_aa99d7, Icons.Default.MusicNote, VaultIconCategory.MEDIA),
        VaultIconItem("camera", com.aipos.aipospm.R.string.ui_photos_camera_fbf87c, Icons.Default.PhotoCamera, VaultIconCategory.MEDIA),
        VaultIconItem("video", com.aipos.aipospm.R.string.ui_video_bc17c1, Icons.Default.Videocam, VaultIconCategory.MEDIA),
        VaultIconItem("headset", com.aipos.aipospm.R.string.ui_headphones_453cce, Icons.Default.Headphones, VaultIconCategory.MEDIA),
        VaultIconItem("book", com.aipos.aipospm.R.string.ui_books_reading_cb1270, Icons.AutoMirrored.Filled.MenuBook, VaultIconCategory.MEDIA),
        VaultIconItem("podcast", com.aipos.aipospm.R.string.ui_podcasts_fd52b4, Icons.Default.Podcasts, VaultIconCategory.MEDIA),

        // Security & Devices
        VaultIconItem("lock", com.aipos.aipospm.R.string.ui_lock_891ebc, Icons.Default.Lock, VaultIconCategory.SECURITY),
        VaultIconItem("shield", com.aipos.aipospm.R.string.ui_shield_082714, Icons.Default.Shield, VaultIconCategory.SECURITY),
        VaultIconItem("fingerprint", com.aipos.aipospm.R.string.ui_biometric_36c991, Icons.Default.Fingerprint, VaultIconCategory.SECURITY),
        VaultIconItem("vpn", com.aipos.aipospm.R.string.ui_vpn_proxy_9e1691, Icons.Default.VpnLock, VaultIconCategory.SECURITY),
        VaultIconItem("key_off", com.aipos.aipospm.R.string.ui_revoked_key_2f8616, Icons.Default.KeyOff, VaultIconCategory.SECURITY),
        VaultIconItem("privacy", com.aipos.aipospm.R.string.ui_privacy_cf0148, Icons.Default.VisibilityOff, VaultIconCategory.SECURITY),
        VaultIconItem("verified", com.aipos.aipospm.R.string.ui_verified_aed3b8, Icons.Default.VerifiedUser, VaultIconCategory.SECURITY),
        VaultIconItem("password", com.aipos.aipospm.R.string.ui_password_8be3c9, Icons.Default.Password, VaultIconCategory.SECURITY),
        VaultIconItem("pin", com.aipos.aipospm.R.string.ui_pin_code_7fa439, Icons.Default.Pin, VaultIconCategory.SECURITY),
        VaultIconItem("phone", com.aipos.aipospm.R.string.ui_mobile_b1d702, Icons.Default.Smartphone, VaultIconCategory.SECURITY),
        VaultIconItem("tablet", com.aipos.aipospm.R.string.ui_tablet_fabcac, Icons.Default.Tablet, VaultIconCategory.SECURITY),
        VaultIconItem("computer", com.aipos.aipospm.R.string.ui_computer_924645, Icons.Default.Computer, VaultIconCategory.SECURITY),
        VaultIconItem("laptop", com.aipos.aipospm.R.string.ui_laptop_ed91cd, Icons.Default.Laptop, VaultIconCategory.SECURITY),
        VaultIconItem("watch", com.aipos.aipospm.R.string.ui_smartwatch_8a6483, Icons.Default.Watch, VaultIconCategory.SECURITY),
        VaultIconItem("usb", com.aipos.aipospm.R.string.ui_hardware_key_039f3c, Icons.Default.Usb, VaultIconCategory.SECURITY),
        VaultIconItem("wifi", com.aipos.aipospm.R.string.ui_network_wi_fi_46e073, Icons.Default.Wifi, VaultIconCategory.SECURITY),

        // General & Lifestyle
        VaultIconItem("star", com.aipos.aipospm.R.string.ui_favorite_6b90b6, Icons.Default.Star, VaultIconCategory.GENERAL),
        VaultIconItem("person", com.aipos.aipospm.R.string.ui_personal_40f073, Icons.Default.Person, VaultIconCategory.GENERAL),
        VaultIconItem("home", com.aipos.aipospm.R.string.ui_home_70f8bb, Icons.Default.Home, VaultIconCategory.GENERAL),
        VaultIconItem("health", com.aipos.aipospm.R.string.ui_medical_health_12aef0, Icons.Default.MedicalServices, VaultIconCategory.GENERAL),
        VaultIconItem("fitness", com.aipos.aipospm.R.string.ui_fitness_gym_e84161, Icons.Default.FitnessCenter, VaultIconCategory.GENERAL),
        VaultIconItem("palette", com.aipos.aipospm.R.string.ui_creative_design_1a743f, Icons.Default.Palette, VaultIconCategory.GENERAL),
        VaultIconItem("favorite", com.aipos.aipospm.R.string.ui_heart_2a3733, Icons.Default.Favorite, VaultIconCategory.GENERAL),
        VaultIconItem("notifications", com.aipos.aipospm.R.string.ui_alert_news_0a44f4, Icons.Default.Notifications, VaultIconCategory.GENERAL),
        VaultIconItem("flag", com.aipos.aipospm.R.string.ui_flag_priority_4b7a3f, Icons.Default.Flag, VaultIconCategory.GENERAL),
        VaultIconItem("lightbulb", com.aipos.aipospm.R.string.ui_idea_notes_f3a15b, Icons.Default.Lightbulb, VaultIconCategory.GENERAL)
    )

    private val iconMap: Map<String, VaultIconItem> = ALL_ICONS.associateBy { it.id }

    fun getIcon(id: String?): ImageVector? {
        if (id == null) return null
        return iconMap[id]?.icon
    }

    fun getIconItem(id: String?): VaultIconItem? {
        if (id == null) return null
        return iconMap[id]
    }
}
