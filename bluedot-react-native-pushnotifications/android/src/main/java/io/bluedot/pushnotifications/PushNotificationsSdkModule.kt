package io.bluedot.pushnotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import au.com.bluedot.point.api.push.model.RezolvePushData
import au.com.bluedot.point.net.engine.ServiceManager
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class PushNotificationsSdkModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    init {
        reactContextRef = reactContext
    }

    override fun getName(): String = "BluedotPushNotificationsSDK"

    override fun initialize() {
        super.initialize()
        // Flush any events (e.g. notification clicked) that arrived before the
        // JS bridge was ready — most likely during a cold-start from a tap.
        PushEventUtil.flushBufferedEvents()
    }

    /**
     * Forward a new FCM token to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onTokenRefresh handler.
     */
    @ReactMethod
    fun onNewFcmToken(token: String) {
        ServiceManager.getInstance(reactApplicationContext)
            .pushNotificationsManager
            .onNewFcmToken(token)
    }

    /**
     * Forward an incoming FCM message to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onMessage and
     * setBackgroundMessageHandler callbacks.
     *
     * Expected message shape (matches @react-native-firebase/messaging payload):
     * {
     *   notification: { title: string, body: string },
     *   data: { campaignId, zoneId, notificationId, pushVersion, ...custom }
     * }
     */
    @ReactMethod
    fun onMessageReceived(message: ReadableMap) {
        val dataMap = mutableMapOf<String, String>()
        message.getMap("data")?.toHashMap()?.forEach { (k, v) ->
            dataMap[k] = v.toString()
        }

        val notification = message.getMap("notification")

        val rezolvePushData = RezolvePushData(
            title          = dataMap["notification_title"]  ?: notification?.getString("title") ?: "",
            body           = dataMap["notification_body"]   ?: notification?.getString("body")  ?: "",
            pushVersion    = dataMap["com.rezolveai.push"]  ?: "",
            campaignId     = dataMap["campaignId"]          ?: "",
            zoneId         = dataMap["zoneId"]              ?: "",
            notificationId = dataMap["notificationId"]      ?: "",
            data           = dataMap
        )

        ServiceManager.getInstance(reactApplicationContext)
            .pushNotificationsManager
            .onMessageReceived(rezolvePushData)
    }

    /**
     * Customise the notification appearance shown by the Bluedot push module.
     * Must be called before the first message arrives (e.g. in your app root component).
     * Pass null to revert to the SDK default appearance.
     *
     * Required:
     *   channelId    {string}  Notification channel ID
     *   channelName  {string}  User-visible channel name
     *
     * Appearance:
     *   importance              {number}   Channel importance: 1=MIN 2=LOW 3=DEFAULT 4=HIGH 5=MAX
     *   smallIconResourceName   {string}   Drawable resource name in the consumer app
     *   largeIconResourceName   {string}   Drawable resource name for the large icon. This must be a bitmap drawable, can't be a vector XML drawable
     *   color                   {string}   Accent color hex string e.g. "#FF0000"
     *
     * Behaviour:
     *   autoCancel              {boolean}  Dismiss on tap (default: true)
     *   ongoing                 {boolean}  Prevent user from dismissing the notification
     *   silent                  {boolean}  Suppress sound and vibration for this notification
     */
    @ReactMethod
    fun setCustomPushNotification(options: ReadableMap?) {
        if (options == null) {
            ServiceManager.getInstance(reactApplicationContext)
                .pushNotificationsManager
                .setCustomPushNotification(null)
            return
        }
        val channelId   = options.getString("channelId")   ?: "rezolve_push_channel"
        val channelName = options.getString("channelName") ?: "Push Notifications"
        val importance  = if (options.hasKey("importance")) options.getInt("importance")
                          else NotificationManager.IMPORTANCE_DEFAULT

        val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, channelName, importance)
        )

        // Tap always opens MainActivity — the JS layer decides where to navigate
        // via the PUSH_NOTIFICATION_CLICKED event.
        val launchIntent = reactApplicationContext.packageManager
            .getLaunchIntentForPackage(reactApplicationContext.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP }
        val contentIntent = PendingIntent.getActivity(
            reactApplicationContext,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(reactApplicationContext, channelId)
            // Map channel importance to legacy priority so pre-API-26 devices also honour
            // the requested urgency level (e.g. IMPORTANCE_HIGH → PRIORITY_HIGH for heads-up).
            .setPriority(importanceToPriority(importance))
            .setAutoCancel(options.getBoolOr("autoCancel", true))
            .setContentIntent(contentIntent)

        if (options.hasKey("smallIconResourceName")) {
            val resId = reactApplicationContext.resources.getIdentifier(
                options.getString("smallIconResourceName"), "drawable", reactApplicationContext.packageName
            )
            if (resId != 0) builder.setSmallIcon(resId)
        }
        if (options.hasKey("largeIconResourceName")) {
            val resId = reactApplicationContext.resources.getIdentifier(
                options.getString("largeIconResourceName"), "drawable", reactApplicationContext.packageName
            )
            if (resId != 0) {
                val bmp = BitmapFactory.decodeResource(reactApplicationContext.resources, resId)
                if (bmp != null) builder.setLargeIcon(bmp)
            }
        }
        if (options.hasKey("color")) {
            runCatching { Color.parseColor(options.getString("color")) }
                .onSuccess { builder.setColor(it) }
        }
        if (options.hasKey("ongoing")) builder.setOngoing(options.getBoolean("ongoing"))
        if (options.hasKey("silent"))  builder.setSilent(options.getBoolean("silent"))

        ServiceManager.getInstance(reactApplicationContext)
            .pushNotificationsManager
            .setCustomPushNotification(builder)
    }

    // Helper: read a boolean from ReadableMap with a fallback default
    private fun ReadableMap.getBoolOr(key: String, default: Boolean): Boolean =
        if (hasKey(key)) getBoolean(key) else default

    // Helper: map NotificationManager.IMPORTANCE_* to the equivalent NotificationCompat.PRIORITY_*
    // so that pre-API-26 devices honour the requested urgency level.
    private fun importanceToPriority(importance: Int): Int = when (importance) {
        NotificationManager.IMPORTANCE_MIN     -> NotificationCompat.PRIORITY_MIN
        NotificationManager.IMPORTANCE_LOW     -> NotificationCompat.PRIORITY_LOW
        NotificationManager.IMPORTANCE_HIGH    -> NotificationCompat.PRIORITY_HIGH
        NotificationManager.IMPORTANCE_MAX     -> NotificationCompat.PRIORITY_MAX
        else                                   -> NotificationCompat.PRIORITY_DEFAULT
    }

    companion object {
        @JvmStatic
        var reactContextRef: ReactApplicationContext? = null
            private set
    }
}
