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
        val title = notification?.getString("title") ?: ""
        val body  = notification?.getString("body")  ?: ""

        val rezolvePushData = RezolvePushData(
            title          = title,
            body           = body,
            pushVersion    = dataMap["pushVersion"]    ?: "",
            campaignId     = dataMap["campaignId"]     ?: "",
            zoneId         = dataMap["zoneId"]         ?: "",
            notificationId = dataMap["notificationId"] ?: "",
            data           = dataMap
        )

        ServiceManager.getInstance(reactApplicationContext)
            .pushNotificationsManager
            .onMessageReceived(rezolvePushData)
    }

    /**
     * Customise the notification appearance shown by the Bluedot push module.
     * Must be called before the first message arrives (e.g. in your app root component).
     *
     * Required:
     *   channelId    {string}  Notification channel ID
     *   channelName  {string}  User-visible channel name
     *
     * Appearance:
     *   importance              {number}   Channel importance: 1=MIN 2=LOW 3=DEFAULT 4=HIGH 5=MAX
     *   smallIconResourceName   {string}   Drawable resource name in the consumer app
     *   largeIconResourceName   {string}   Drawable resource name for the large icon
     *   color                   {string}   Accent color hex string e.g. "#FF0000"
     *   colorized               {boolean}  Use color as notification background
     *   badgeIconType           {number}   0=NONE 1=SMALL 2=LARGE
     *   subText                 {string}   Additional header text
     *   ticker                  {string}   Accessibility / pre-Lollipop status bar text
     *   number                  {number}   Badge count
     *   lights                  {object}   { color: string, onMs: number, offMs: number }
     *
     * Behaviour:
     *   autoCancel              {boolean}  Dismiss on tap (default: true)
     *   ongoing                 {boolean}  Prevent user dismissal
     *   onlyAlertOnce           {boolean}  Sound/vibrate only if not already showing
     *   silent                  {boolean}  Suppress sound and vibration for this instance
     *   localOnly               {boolean}  Do not bridge to wearables
     *   timeoutAfter            {number}   Auto-cancel after this many milliseconds (API 26+)
     *   vibrationPattern        {number[]} Vibration pattern e.g. [0, 250, 500, 250]
     *   progress                {object}   { max: number, value: number, indeterminate: boolean }
     *
     * Timestamp:
     *   showWhen                {boolean}  Show the timestamp
     *   when                    {number}   Event timestamp in ms (defaults to now)
     *   usesChronometer         {boolean}  Show timestamp as a running stopwatch
     *   chronometerCountDown    {boolean}  Count down instead of up (API 24+)
     *
     * Grouping / sorting:
     *   group                   {string}   Group key
     *   groupSummary            {boolean}  Mark as the group summary notification
     *   groupAlertBehavior      {number}   0=ALL 1=SUMMARY_ONLY 2=CHILDREN_ONLY
     *   sortKey                 {string}   Lexicographic sort key within the group
     *
     * Categorisation / visibility:
     *   category                {string}   CATEGORY_* constant e.g. "msg", "promo", "reminder"
     *   visibility              {number}   -1=PRIVATE (default) 1=PUBLIC -2=SECRET
     *   allowSystemGeneratedContextualActions {boolean} Default: true
     */
    @ReactMethod
    fun setCustomPushNotification(options: ReadableMap) {
        val channelId   = options.getString("channelId")   ?: "bluedot_push_channel"
        val channelName = options.getString("channelName") ?: "Push Notifications"
        val importance  = if (options.hasKey("importance")) options.getInt("importance")
                          else NotificationManager.IMPORTANCE_DEFAULT

        val notificationManager =
            reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(options.getBoolOr("autoCancel", true))
            .setContentIntent(contentIntent)

        // --- Appearance ---
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
                val bmp = BitmapFactory.decodeResource(
                    reactApplicationContext.resources, resId
                )
                if (bmp != null) builder.setLargeIcon(bmp)
            }
        }
        if (options.hasKey("color")) {
            runCatching { Color.parseColor(options.getString("color")) }
                .onSuccess { builder.setColor(it) }
        }
        if (options.hasKey("colorized"))    builder.setColorized(options.getBoolean("colorized"))
        if (options.hasKey("badgeIconType")) builder.setBadgeIconType(options.getInt("badgeIconType"))
        if (options.hasKey("subText"))      builder.setSubText(options.getString("subText"))
        if (options.hasKey("ticker"))       builder.setTicker(options.getString("ticker"))
        if (options.hasKey("number"))       builder.setNumber(options.getInt("number"))
        if (options.hasKey("lights")) {
            options.getMap("lights")?.let { l ->
                val lightColor = runCatching { Color.parseColor(l.getString("color")) }.getOrDefault(Color.WHITE)
                val onMs  = if (l.hasKey("onMs"))  l.getInt("onMs")  else 500
                val offMs = if (l.hasKey("offMs")) l.getInt("offMs") else 500
                builder.setLights(lightColor, onMs, offMs)
            }
        }

        // --- Behaviour ---
        if (options.hasKey("ongoing"))       builder.setOngoing(options.getBoolean("ongoing"))
        if (options.hasKey("onlyAlertOnce")) builder.setOnlyAlertOnce(options.getBoolean("onlyAlertOnce"))
        if (options.hasKey("silent"))        builder.setSilent(options.getBoolean("silent"))
        if (options.hasKey("localOnly"))     builder.setLocalOnly(options.getBoolean("localOnly"))
        if (options.hasKey("timeoutAfter"))  builder.setTimeoutAfter(options.getInt("timeoutAfter").toLong())
        if (options.hasKey("vibrationPattern")) {
            options.getArray("vibrationPattern")?.let { arr ->
                builder.setVibrate(LongArray(arr.size()) { arr.getInt(it).toLong() })
            }
        }
        if (options.hasKey("progress")) {
            options.getMap("progress")?.let { p ->
                builder.setProgress(
                    if (p.hasKey("max"))           p.getInt("max")                   else 100,
                    if (p.hasKey("value"))         p.getInt("value")                 else 0,
                    if (p.hasKey("indeterminate")) p.getBoolean("indeterminate")     else false
                )
            }
        }

        // --- Timestamp ---
        if (options.hasKey("showWhen"))           builder.setShowWhen(options.getBoolean("showWhen"))
        if (options.hasKey("when"))               builder.setWhen(options.getInt("when").toLong())
        if (options.hasKey("usesChronometer"))    builder.setUsesChronometer(options.getBoolean("usesChronometer"))
        if (options.hasKey("chronometerCountDown")) {
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                builder.setChronometerCountDown(options.getBoolean("chronometerCountDown"))
            }
        }

        // --- Grouping / sorting ---
        if (options.hasKey("group"))             builder.setGroup(options.getString("group"))
        if (options.hasKey("groupSummary"))      builder.setGroupSummary(options.getBoolean("groupSummary"))
        if (options.hasKey("groupAlertBehavior")) builder.setGroupAlertBehavior(options.getInt("groupAlertBehavior"))
        if (options.hasKey("sortKey"))           builder.setSortKey(options.getString("sortKey"))

        // --- Categorisation / visibility ---
        if (options.hasKey("category"))    builder.setCategory(options.getString("category"))
        if (options.hasKey("visibility"))  builder.setVisibility(options.getInt("visibility"))
        if (options.hasKey("allowSystemGeneratedContextualActions")) {
            builder.setAllowSystemGeneratedContextualActions(
                options.getBoolean("allowSystemGeneratedContextualActions")
            )
        }

        ServiceManager.getInstance(reactApplicationContext)
            .pushNotificationsManager
            .setCustomPushNotification(builder)
    }

    // Helper: read a boolean from ReadableMap with a fallback default
    private fun ReadableMap.getBoolOr(key: String, default: Boolean): Boolean =
        if (hasKey(key)) getBoolean(key) else default

    companion object {
        @JvmStatic
        var reactContextRef: ReactApplicationContext? = null
            private set

        @JvmStatic
        fun getReactContextRef(): ReactApplicationContext? = reactContextRef
    }
}
