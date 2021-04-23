package io.bluedot;

import android.content.Context;

import androidx.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.jetbrains.annotations.NotNull;

import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.TempoTrackingReceiver;

public class AppTempoReceiver extends TempoTrackingReceiver {
    /**
     * Called when there is an error that has caused Tempo to stop.
     *
     * @param bdError: can be a [TempoInvalidDestinationIdError][au.com.bluedot.point.TempoInvalidDestinationIdError]
     * or a [BDTempoError][au.com.bluedot.point.BDTempoError]
     * @param context: Android context
     * @since 15.3.0
     */
    @Override
    public void tempoStoppedWithError(@NotNull BDError bdError, @NotNull Context context) {
        WritableMap error = new WritableNativeMap();
        error.putString("error",bdError.getReason());
        sendEvent(context, "tempoStoppedWithError", error);
    }

    public void sendEvent(Context context,
                          String eventName,
                          @Nullable WritableMap params) {

        ReactApplication reactApplication = (ReactApplication) context.getApplicationContext();
        ReactContext reactContext = reactApplication.getReactNativeHost().getReactInstanceManager()
                .getCurrentReactContext();
        ReactInstanceManager reactInstanceManager =
                reactApplication.getReactNativeHost().getReactInstanceManager();

        if (reactContext != null) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } else {
            reactInstanceManager.addReactInstanceEventListener(
                    new ReactInstanceManager.ReactInstanceEventListener() {
                        @Override
                        public void onReactContextInitialized(ReactContext context) {
                            context.getJSModule(
                                    DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(eventName, params);
                            reactInstanceManager.removeReactInstanceEventListener(this);
                        }
                    });
        }
    }
}