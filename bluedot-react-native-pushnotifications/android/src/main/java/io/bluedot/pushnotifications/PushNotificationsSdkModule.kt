package io.bluedot.pushnotifications

import android.app.NotificationManager
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
     * Forward an incoming FCM message to the Rezolve push module.
     * Call this from your @react-native-firebase/messaging onMessage and
     * setBackgroundMessageHandler callbacks.
     *
     * Expected message shape (matches @react-native-firebase/messaging payload):
     * {
     *   data: { notification_title, notification_body, com.rezolveai.push, campaignId, zoneId, notificationId }
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

    companion object {
        @JvmStatic
        var reactContextRef: ReactApplicationContext? = null
            private set
    }
}
