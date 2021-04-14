package io.bluedot;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.BluedotServiceReceiver;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import org.jetbrains.annotations.NotNull;


public class BluedotErrorReceiver extends BluedotServiceReceiver {

    /**
     * Called when the Bluedot Point SDK encounters errors. If the error is fatal, the SDK services
     * may need to be restarted after the cause of the error has been addressed.
     *
     * @param[bdError] The error, please see [documentation](https://docs.bluedot.io/android-sdk/android-error-handling/)
     * for possible subtypes and appropriate corrective actions.
     * @param[context] Android context.
     * @since 15.3.0
     */
    @Override
    public void onBluedotServiceError(@NotNull BDError bdError, @NotNull Context context) {
        WritableMap error = new WritableNativeMap();
        error.putString("error",bdError.getReason());
        sendEvent(context, "onBluedotServiceError",error);
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