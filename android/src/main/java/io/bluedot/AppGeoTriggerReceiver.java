package io.bluedot;

import android.content.Context;
import androidx.annotation.Nullable;
import au.com.bluedot.point.net.engine.GeoTriggeringEventReceiver;
import au.com.bluedot.point.net.engine.event.GeoTriggerEvent;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class AppGeoTriggerReceiver extends GeoTriggeringEventReceiver {

    @Override
    public void onZoneInfoUpdate(@NotNull Context context) {
        sendEvent(context, "zoneInfoUpdate", null);
    }

    @Override
    public void onZoneEntryEvent(@NotNull GeoTriggerEvent entryEvent, @NotNull Context context) {
        JSONObject jsonObject = null;
        WritableMap writableMap = null;
        try {

            jsonObject = new JSONObject(entryEvent.toJson());
            Map<String, Object> mapEvent = MapUtil.toMap(jsonObject);
            writableMap = MapUtil.toWritableMap(mapEvent);
            sendEvent(context, "enterZone", writableMap);
        } catch (JSONException exp) {
            System.out.println("Exception occurred during conversion of EntryEvent" + exp);
        }
    }

    public void sendEvent(Context context,
            String eventName,
            @Nullable WritableMap params) {

        ReactApplication reactApplication = (ReactApplication) context.getApplicationContext();
        ReactContext reactContext = reactApplication.getReactNativeHost().getReactInstanceManager()
                .getCurrentReactContext();
        ReactInstanceManager reactInstanceManager = reactApplication.getReactNativeHost().getReactInstanceManager();

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

    @Override
    public void onZoneExitEvent(@NotNull GeoTriggerEvent exitEvent, @NotNull Context context) {

        JSONObject jsonObject = null;
        WritableMap writableMap = null;
        try {

            jsonObject = new JSONObject(exitEvent.toJson());
            Map<String, Object> mapEvent = MapUtil.toMap(jsonObject);
            writableMap = MapUtil.toWritableMap(mapEvent);
            sendEvent(context, "exitZone", writableMap);
        } catch (JSONException exp) {
            System.out.println("Exception occurred during conversion of ExitEvent" + exp);
        }
    }

}
