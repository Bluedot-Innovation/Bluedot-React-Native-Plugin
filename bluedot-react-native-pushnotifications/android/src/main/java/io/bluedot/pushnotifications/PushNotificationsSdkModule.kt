package io.bluedot.pushnotifications

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class PushNotificationsSdkModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    init {
        reactContextRef = reactContext
    }

    override fun getName(): String = "BluedotPushNotificationsSDK"

    companion object {
        @JvmStatic
        var reactContextRef: ReactApplicationContext? = null
            private set

        @JvmStatic
        fun getReactContextRef(): ReactApplicationContext? = reactContextRef
    }
}
