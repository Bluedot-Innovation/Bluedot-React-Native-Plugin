package io.bluedot.push

import android.util.Log
import com.facebook.react.bridge.WritableMap
import java.util.concurrent.CopyOnWriteArrayList

class PushEventUtil {

    data class BufferedEvent(val eventName: String, val params: WritableMap?)

    companion object {
        private val eventBuffer = CopyOnWriteArrayList<BufferedEvent>()

        @JvmStatic
        fun sendEvent(eventName: String, params: WritableMap?) {
            val reactContext = PushNotificationsSdkModule.reactContextRef
            if (reactContext != null && reactContext.hasActiveReactInstance()) {
                Log.i("BluedotPushRNPlugin", "emit event $eventName")
                reactContext.emitDeviceEvent(eventName, params)
            } else {
                Log.w("BluedotPushRNPlugin", "ReactContext not ready, buffering event: $eventName")
                eventBuffer.add(BufferedEvent(eventName, params))
            }
        }

        @JvmStatic
        fun flushBufferedEvents() {
            val reactContext = PushNotificationsSdkModule.reactContextRef
            if (reactContext != null && reactContext.hasActiveReactInstance()) {
                for (event in eventBuffer) {
                    Log.i("BluedotPushRNPlugin", "Flushing buffered event: ${event.eventName}")
                    reactContext.emitDeviceEvent(event.eventName, event.params)
                }
                eventBuffer.clear()
            }
        }
    }
}
