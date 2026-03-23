package io.bluedot.pushnotifications

import android.util.Log
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import java.util.concurrent.CopyOnWriteArrayList

class PushEventUtil {

    data class BufferedEvent(val eventName: String, val params: WritableMap?)

    companion object {
        private val eventBuffer = CopyOnWriteArrayList<BufferedEvent>()

        @JvmStatic
        fun sendEvent(eventName: String, params: WritableMap?) {
            val reactContext = PushNotificationsSdkModule.getReactContextRef()
            if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
                Log.i("BluedotPushRNPlugin", "emit event $eventName")
                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
            } else {
                Log.w("BluedotPushRNPlugin", "ReactContext not ready, buffering event: $eventName")
                eventBuffer.add(BufferedEvent(eventName, params))
            }
        }

        @JvmStatic
        fun flushBufferedEvents() {
            val reactContext = PushNotificationsSdkModule.getReactContextRef()
            if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
                for (event in eventBuffer) {
                    Log.i("BluedotPushRNPlugin", "Flushing buffered event: ${event.eventName}")
                    reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(event.eventName, event.params)
                }
                eventBuffer.clear()
            }
        }
    }
}
