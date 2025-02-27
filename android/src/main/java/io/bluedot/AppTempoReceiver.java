package io.bluedot;

import android.content.Context;
import androidx.annotation.Nullable;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.TempoTrackingReceiver;
import au.com.bluedot.point.net.engine.event.TempoTrackingUpdate;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactHost;
import com.facebook.react.ReactInstanceEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class AppTempoReceiver extends TempoTrackingReceiver {

    @Override
    public void onTempoTrackingUpdate(@NotNull TempoTrackingUpdate tempoTrackingUpdate,
                                      @NotNull Context context) {
        JSONObject jsonObject = null;
        WritableMap tempoUpdate = null;
        try {

            jsonObject = new JSONObject(tempoTrackingUpdate.toJson());
            Map<String, Object> mapEvent = MapUtil.toMap(jsonObject);
            tempoUpdate = MapUtil.toWritableMap(mapEvent);
            sendEvent(context, "tempoTrackingDidUpdate", tempoUpdate);
        } catch (JSONException exp) {
            System.out.println("Exception occurred during conversion of ExitEvent" + exp);
        }
    }

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
        ReactHost reactHost = reactApplication.getReactHost();
        ReactContext reactContext = reactHost.getCurrentReactContext();

        if (reactContext != null) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } else {
            reactHost.addReactInstanceEventListener(
                    new ReactInstanceEventListener() {
                        @Override
                        public void onReactContextInitialized(ReactContext context) {
                            context.getJSModule(
                                    DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(eventName, params);
                            reactHost.removeReactInstanceEventListener(this);
                        }
                    });
        }
    }
}