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
            val reactHost = reactApplication.reactHost
            if (reactHost == null) {
                Log.e("BluedotReactPlugin", "ReactHost is null")
                return
            }

            val reactContext = reactHost.currentReactContext

//            if (reactContext != null) {
//                Log.d("BluedotReactPlugin", "Sending event: $eventName")
//                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
//            } else {
//                Log.d("BluedotReactPlugin", "addReactInstanceEventListener: $eventName")
//                reactHost.addReactInstanceEventListener(
//                    object : ReactInstanceEventListener {
//                        override fun onReactContextInitialized(context: ReactContext) {
//                            Log.d("BluedotReactPlugin", "addReactInstanceEventListener: onReactContextInitialized $eventName")
//                            context.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
//                            reactHost.removeReactInstanceEventListener(this)
//                        }
//                    })
//            }

            val reactNativeHost: ReactNativeHost  = reactApplication.getReactNativeHost()
            val mReactInstanceManager = reactNativeHost.reactInstanceManager

            // 1. Check if the ReactContext is already initialized
            val currentContext: ReactContext? = mReactInstanceManager.getCurrentReactContext()
            if (currentContext != null) {
                // Context is already ready, use it directly
                Log.d("BluedotReactPlugin", "ReactContext already initialized.")
                currentContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
            } else {
                // Context is not ready, add a listener to wait for it
                mReactInstanceManager.addReactInstanceEventListener(object :
                    ReactInstanceEventListener {
                    override fun onReactContextInitialized(validContext: ReactContext) {
                        Log.d("BluedotReactPlugin", "onReactContextInitialized called. validContext", validContext)
                        validContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
                        // Optional: Remove the listener once it's used to prevent memory leaks,
                        // especially if the Activity lifecycle means it might be added multiple times.
                         mReactInstanceManager.removeReactInstanceEventListener(this)
                    }
                })
            }


        }
    }
}
