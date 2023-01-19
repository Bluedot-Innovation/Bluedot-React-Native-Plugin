package io.bluedot;

import android.content.Context;
import androidx.annotation.Nullable;
import au.com.bluedot.point.net.engine.FenceInfo;
import au.com.bluedot.point.net.engine.GeoTriggeringEventReceiver;
import au.com.bluedot.point.net.engine.LocationInfo;
import au.com.bluedot.point.net.engine.ZoneEntryEvent;
import au.com.bluedot.point.net.engine.ZoneExitEvent;
import au.com.bluedot.point.net.engine.ZoneInfo;
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

public class AppGeoTriggerReceiver extends GeoTriggeringEventReceiver {

    @Override
    public void onZoneInfoUpdate(@NotNull List<ZoneInfo> zones, @NotNull Context context) {
        WritableArray zoneList = new WritableNativeArray();
        for (ZoneInfo zoneInfo : zones) {
            WritableMap customDataZone = new WritableNativeMap();
            Map<String, String> customDataMap = zoneInfo.getCustomData();
            if (customDataMap != null) {
                for (String entry : customDataMap.keySet()) {
                    customDataZone.putString(entry, customDataMap.get(entry));
                }
            }

            WritableMap zone = new WritableNativeMap();
            zone.putString("id", zoneInfo.getZoneId());
            zone.putString("name", zoneInfo.getZoneName());
            zone.putMap("customData", customDataZone);
            zoneList.pushMap(zone);

        }

        WritableMap map = new WritableNativeMap();
        map.putArray("zoneInfo", zoneList);
        sendEvent(context, "zoneInfoUpdate", map);
    }

    @Override
    public void onZoneEntryEvent(@NotNull ZoneEntryEvent entryEvent, @NotNull Context context) {
        String entryDetails = "Entered zone " + entryEvent.getZoneInfo().getZoneName() + " via fence "
                + entryEvent.getFenceInfo().getName();
        String customData = "";
        if (entryEvent.getZoneInfo().getCustomData() != null)
            customData = entryEvent.getZoneInfo().getCustomData().toString();

        FenceInfo fenceInfo = entryEvent.getFenceInfo();
        WritableMap fenceDetails = new WritableNativeMap();
        fenceDetails.putString("fenceId", fenceInfo.getId());
        fenceDetails.putString("fenceName", fenceInfo.getName());

        ZoneInfo zoneInfo = entryEvent.getZoneInfo();
        WritableMap customDataZone = new WritableNativeMap();
        Map<String, String> customDataMap = zoneInfo.getCustomData();
        if (customDataMap != null) {
            for (String entry : customDataMap.keySet()) {
                customDataZone.putString(entry, customDataMap.get(entry));
            }
        }

        WritableMap zoneDetails = new WritableNativeMap();
        zoneDetails.putString("id", zoneInfo.getZoneId());
        zoneDetails.putString("name", zoneInfo.getZoneName());
        zoneDetails.putMap("customData", customDataZone);

        LocationInfo locationInfo = entryEvent.getLocationInfo();
        WritableMap locationDetails = new WritableNativeMap();
        locationDetails.putDouble("latitude", locationInfo.getLatitude());
        locationDetails.putDouble("longitude", locationInfo.getLongitude());
        locationDetails.putDouble("speed", locationInfo.getSpeed());
        locationDetails.putDouble("bearing", locationInfo.getBearing());
        locationDetails.putDouble("timeStamp", locationInfo.getTimeStamp());

        WritableMap writableMap = new WritableNativeMap();
        writableMap.putMap("fenceInfo", fenceDetails);
        writableMap.putMap("zoneInfo", zoneDetails);
        writableMap.putMap("locationInfo", locationDetails);
        writableMap.putBoolean("isExitEnabled", entryEvent.isExitEnabled());

        sendEvent(context, "enterZone", writableMap);
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
    public void onZoneExitEvent(@NotNull ZoneExitEvent exitEvent, @NotNull Context context) {
        String exitDetails = "Exited zone" + exitEvent.getZoneInfo().getZoneName();
        String dwellT = "Dwell time: " + exitEvent.getDwellTime() + " minutes";

        FenceInfo fenceInfo = exitEvent.getFenceInfo();
        WritableMap fenceDetails = new WritableNativeMap();
        fenceDetails.putString("id", fenceInfo.getId());
        fenceDetails.putString("name", fenceInfo.getName());

        ZoneInfo zoneInfo = exitEvent.getZoneInfo();
        WritableMap customDataZone = new WritableNativeMap();
        Map<String, String> customDataMap = zoneInfo.getCustomData();
        if (customDataMap != null) {
            for (String entry : customDataMap.keySet()) {
                customDataZone.putString(entry, customDataMap.get(entry));
            }
        }

        WritableMap zoneDetails = new WritableNativeMap();
        zoneDetails.putString("id", zoneInfo.getZoneId());
        zoneDetails.putString("name", zoneInfo.getZoneName());
        zoneDetails.putMap("customData", customDataZone);

        WritableMap writableMap = new WritableNativeMap();
        writableMap.putMap("fenceInfo", fenceDetails);
        writableMap.putMap("zoneInfo", zoneDetails);
        writableMap.putInt("dwellTime", exitEvent.getDwellTime());

        sendEvent(context, "exitZone", writableMap);
    }

}
