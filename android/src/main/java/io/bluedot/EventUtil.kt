package io.bluedot

import android.util.Log
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import java.util.concurrent.CopyOnWriteArrayList

data class BufferedEvent(val eventName: String, val params: WritableMap?)

object EventUtil {

    private val eventBuffer = CopyOnWriteArrayList<BufferedEvent>()

    @JvmStatic
    fun sendEvent(
        eventName: String,
        params: WritableMap?
    ) {
        val reactContext = BluedotPointSdkModule.getReactContextRef()
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            Log.i("BluedotReactPlugin", "emit event $eventName")
            reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
        } else {
            Log.w("BluedotReactPlugin", "ReactContext not ready, buffering event: $eventName")
            eventBuffer.add(BufferedEvent(eventName, params))
        }
    }

    @JvmStatic
    fun flushBufferedEvents() {
        val reactContext = BluedotPointSdkModule.getReactContextRef()
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            for (event in eventBuffer) {
                Log.i("BluedotReactPlugin", "Flushing buffered event: ${event.eventName}")
                reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(event.eventName, event.params)
            }
            eventBuffer.clear()
        }
    }
}
