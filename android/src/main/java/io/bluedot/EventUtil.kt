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
                Log.i("BluedotReactPlugin", "reactContext is not null emit event " + eventName)
                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
            } else {
                Log.i("BluedotReactPlugin", "reactContext is null use addReactInstanceEventListener " + eventName)
                reactInstanceManager.addReactInstanceEventListener(
                    object : ReactInstanceEventListener {
                        override fun onReactContextInitialized(context: ReactContext) {
                            Log.i("BluedotReactPlugin", "onReactContextInitialized emit event " + eventName)
                            context.getJSModule(RCTDeviceEventEmitter::class.java)
                                .emit(eventName, params)
                          //  reactInstanceManager.removeReactInstanceEventListener(this)
                        }
                    })
            }
        }
    }
}
