package io.bluedot

import android.content.Context
import android.util.Log
import com.facebook.react.ReactApplication
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter

class EventUtil {

    companion object {
        @JvmStatic
        fun sendEvent(
            eventName: String,
            params: WritableMap?
        ) {
            val reactContext = BluedotPointSdkModule.reactContext
            if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
                Log.i("BluedotReactPlugin", "emit event $eventName")
                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
            } else {
                Log.e("BluedotReactPlugin", "ReactContext not ready, event not sent: $eventName")
                // Optionally buffer or drop the event
            }
        }
    }
}
