package io.bluedot;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import au.com.bluedot.point.net.engine.GeoTriggeringEventReceiver;
import au.com.bluedot.point.net.engine.event.GeoTriggerEvent;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactHost;
import com.facebook.react.ReactInstanceEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

public class AppGeoTriggerReceiver extends GeoTriggeringEventReceiver {

    private static boolean firstTrigger = true;
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

            //Added a wait for 1 sec for 1st trigger to give App listeners time to register for callback
            if (firstTrigger) {
                Log.d("Plugin", "Wait for 1 secs for first Entry");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Log.i("Plugin", "Exception"+e.getLocalizedMessage());
                    e.printStackTrace();
                }
                Log.d("Plugin", "Wait is Over");
                firstTrigger = false;
            }

            sendEvent(context, "enterZone", writableMap);
        } catch (JSONException exp) {
            System.out.println("Exception occurred during conversion of EntryEvent" + exp);
        }
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
                        public void onReactContextInitialized(@NonNull ReactContext context) {
                            context.getJSModule(
                                    DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(eventName, params);
                            reactHost.removeReactInstanceEventListener(this);
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
