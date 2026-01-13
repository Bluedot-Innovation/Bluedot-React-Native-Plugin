package io.bluedot

import android.content.Context
import android.util.Log
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter

class EventUtil {

    companion object {
        @JvmStatic
        fun sendEvent(
            context: Context,
            eventName: String,
            params: WritableMap?
        ) {
            val reactApplication = context.applicationContext as ReactApplication
            val reactNativeHost: ReactNativeHost = reactApplication.reactNativeHost

            if (reactNativeHost == null) {
                Log.e("BluedotReactPlugin", "reactNativeHost is null")
                return
            }

            val reactInstanceManager = reactNativeHost.reactInstanceManager

            val reactContext: ReactContext? = reactInstanceManager.currentReactContext
            if (reactContext != null) {
                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
            } else {
                reactInstanceManager.addReactInstanceEventListener(
                    object : ReactInstanceEventListener {
                        override fun onReactContextInitialized(context: ReactContext) {
                            context.getJSModule(RCTDeviceEventEmitter::class.java)
                                .emit(eventName, params)
                            reactInstanceManager.removeReactInstanceEventListener(this)
                        }
                    })
            }
        }
    }
}
