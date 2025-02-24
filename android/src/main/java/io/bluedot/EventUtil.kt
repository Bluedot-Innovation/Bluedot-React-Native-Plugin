package io.bluedot

import android.content.Context
import android.util.Log
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceEventListener
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

            if (reactContext != null) {
                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
            } else {
                reactHost.addReactInstanceEventListener(
                    object : ReactInstanceEventListener {
                        override fun onReactContextInitialized(context: ReactContext) {
                            context.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
                            reactHost.removeReactInstanceEventListener(this)
                        }
                    })
            }
        }
    }
}
