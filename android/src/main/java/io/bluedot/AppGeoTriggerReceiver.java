package io.bluedot;

import android.content.Context;
import androidx.annotation.Nullable;
import au.com.bluedot.point.net.engine.FenceInfo;
import au.com.bluedot.point.net.engine.GeoTriggeringEventReceiver;
import au.com.bluedot.point.net.engine.LocationInfo;
import au.com.bluedot.point.net.engine.ZoneInfo;
import au.com.bluedot.point.net.engine.AppState;
import au.com.bluedot.ruleEngine.model.rule.Destination;
import au.com.bluedot.point.net.engine.event.GeoTriggerEvent;
import au.com.bluedot.point.net.engine.event.NotificationZoneInfo;
import au.com.bluedot.point.net.engine.event.DeviceInfo;
import au.com.bluedot.point.net.engine.event.AppInfo;
import au.com.bluedot.point.net.engine.event.CrossedFence;
import au.com.bluedot.point.net.engine.event.TriggerEvent;
import au.com.bluedot.point.model.LocationDetails;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class AppGeoTriggerReceiver extends GeoTriggeringEventReceiver {

    @Override
    public void onZoneInfoUpdate(@NotNull Context context) {
        WritableMap successMsg = new WritableNativeMap();
        successMsg.putString("msg", "Zone Updated successfully");
        sendEvent(context, "zoneInfoUpdate", successMsg);
    }

    @Override
    public void onZoneEntryEvent(@NotNull GeoTriggerEvent entryEvent, @NotNull Context context) {

        WritableArray triggerEvents = new WritableNativeArray();
        triggerEvents.pushMap(getFenceEnteredMap(entryEvent.entryEvent()));

        WritableMap writableMap = new WritableNativeMap();
        writableMap.putMap("zoneInfo", getZoneInfoMap(entryEvent.getZoneInfo()));
        writableMap.putMap("deviceInfo", getDeviceInfoMap(entryEvent.getDeviceInfo()));
        writableMap.putMap("appInfo", getAppInfoMap(entryEvent.getAppInfo()));
        writableMap.putArray("triggerEvents", triggerEvents);
        writableMap.putString("triggerChainId", entryEvent.getTriggerChainId().toString());
        writableMap.putString("projectId", entryEvent.getProjectId().toString());
        writableMap.putString("installRef", entryEvent.getInstallRef().toString());
        writableMap.putString("notificationType", entryEvent.getNotificationType().toString());

        sendEvent(context, "enterZone", writableMap);
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

    @Override
    public void onZoneExitEvent(@NotNull GeoTriggerEvent exitEvent, @NotNull Context context) {

        WritableArray triggerEvents = new WritableNativeArray();
        triggerEvents.pushMap(getFenceEnteredMap(exitEvent.entryEvent()));
        triggerEvents.pushMap(getFenceExitedMap(exitEvent.exitEvent()));

        WritableMap writableMap = new WritableNativeMap();
        writableMap.putMap("zoneInfo", getZoneInfoMap(exitEvent.getZoneInfo()));
        writableMap.putMap("deviceInfo", getDeviceInfoMap(exitEvent.getDeviceInfo()));
        writableMap.putMap("appInfo", getAppInfoMap(exitEvent.getAppInfo()));
        writableMap.putArray("triggerEvents", triggerEvents);
        writableMap.putString("triggerChainId", exitEvent.getTriggerChainId().toString());
        writableMap.putString("projectId", exitEvent.getProjectId().toString());
        writableMap.putString("installRef", exitEvent.getInstallRef().toString());
        writableMap.putString("notificationType", exitEvent.getNotificationType().toString());

        sendEvent(context, "exitZone", writableMap);
    }

    public WritableMap getZoneInfoMap(NotificationZoneInfo zoneDetails) {

        WritableMap customDataZone = new WritableNativeMap();
        Map<String, String> customDataMap = zoneDetails.getCustomData();
        if (customDataMap != null) {
            for (String entry : customDataMap.keySet()) {
                customDataZone.putString(entry, customDataMap.get(entry));
            }
        }
        WritableMap zoneInfo = new WritableNativeMap();
        zoneInfo.putString("id", zoneDetails.getId().toString());
        zoneInfo.putString("name", zoneDetails.getName());
        zoneInfo.putMap("customData", customDataZone);
        return zoneInfo;
    }

    public WritableMap getDeviceInfoMap(DeviceInfo devInfo) {
        WritableMap deviceInfo = new WritableNativeMap();
        deviceInfo.putString("os", devInfo.getOs());
        deviceInfo.putString("osVersion", devInfo.getOsVersion());
        deviceInfo.putString("deviceType", devInfo.getDeviceType());
        deviceInfo.putString("advertisingId", devInfo.getAdvertisingId());
        return deviceInfo;
    }

    public WritableMap getAppInfoMap(AppInfo appDetails) {
        WritableMap customEventData = new WritableNativeMap();
        Map<String, String> customEventDataMap = appDetails.getCustomEventMetaData();
        if (customEventDataMap != null) {
            for (String entry : customEventDataMap.keySet()) {
                customEventData.putString(entry, customEventDataMap.get(entry));
            }
        }

        WritableMap appInfo = new WritableNativeMap();
        appInfo.putString("customerApplicationId", appDetails.getCustomerApplicationId());
        appInfo.putString("appBuildVersion", appDetails.getAppBuildVersion());
        appInfo.putString("minSdkVersion", appDetails.getMinSdkVersion());
        appInfo.putString("compileSdkVersion", appDetails.getCompileSdkVersion());
        appInfo.putString("targetSdkVersion", appDetails.getTargetSdkVersion());
        appInfo.putString("sdkVersion", appDetails.getSdkVersion());
        appInfo.putMap("customEventMetaData", customEventData);
        return appInfo;
    }

    public WritableMap getAppStateMap(AppState appState) {
        WritableMap applicationState = new WritableNativeMap();
        applicationState.putString("locationPermission",
                                   appState.getLocationPermission().toString());
        applicationState.putString("notificationPermission",
                                   appState.getNotificationPermission().toString());
        applicationState.putInt("batteryLevel", appState.getBatteryLevel());
        applicationState.putString("lastRuleUpdate", appState.getLastRuleUpdate().toString());
        applicationState.putString("viewState", appState.getViewState().toString());
        applicationState.putBoolean("foregroundServiceEnabled",
                                    appState.getForegroundServiceEnabled());
        return applicationState;
    }

    public WritableMap getLocationDetailMap(LocationDetails locationInfo) {

        WritableMap locationDetails = new WritableNativeMap();
        locationDetails.putDouble("latitude", locationInfo.getLatitude());
        locationDetails.putDouble("longitude", locationInfo.getLongitude());
        locationDetails.putDouble("horizontalAccuracy", locationInfo.getHorizontalAccuracy());
        locationDetails.putString("time", locationInfo.getTime().toString());
        if (locationInfo.getAltitude() != null) {
            locationDetails.putDouble("altitude", locationInfo.getAltitude());
        }
        if (locationInfo.getVerticalAccuracy() != null) {
            locationDetails.putDouble("verticalAccuracy", locationInfo.getVerticalAccuracy());
        }
        if (locationInfo.getSpeed() != null) {
            locationDetails.putDouble("speed", locationInfo.getSpeed());
        }
        if (locationInfo.getBearing() != null) {
            locationDetails.putDouble("bearing", locationInfo.getBearing());
        }
        return locationDetails;
    }

    public WritableArray getLocationsArray(TriggerEvent triggerEvent) {
        WritableArray locations = new WritableNativeArray();
        List<LocationDetails> locs = new ArrayList<>();
        if (triggerEvent.getEventType().getJsonName().equals("fenceEntered")) {
            TriggerEvent.FenceEnteredEvent fenceEnteredEvent =
                    (TriggerEvent.FenceEnteredEvent) triggerEvent;
            locs = fenceEnteredEvent.getLocations();
        }

        if (triggerEvent.getEventType().getJsonName().equals("fenceExited")) {
            TriggerEvent.FenceExitedEvent fenceExitedEvent =
                    (TriggerEvent.FenceExitedEvent) triggerEvent;
            locs = fenceExitedEvent.getLocations();
        }

        for (int i = 0; i < locs.size(); i++) {
            locations.pushMap(getLocationDetailMap(locs.get(i)));
        }
        return locations;
    }

    public WritableMap getCrossedFenceMap(CrossedFence fence) {
        WritableMap crossedFence = new WritableNativeMap();
        crossedFence.putString("fenceId", fence.getFenceId());
        crossedFence.putString("fenceName", fence.getFenceName());
        crossedFence.putString("crossTime", fence.getCrossTime().toString());
        crossedFence.putMap("location", getLocationDetailMap(fence.getLocation()));
        return crossedFence;
    }

    public WritableArray getCrossedFencesArray(List<CrossedFence> crossedFenceList) {
        WritableArray crossedFences = new WritableNativeArray();

        for (int i = 0; i < crossedFenceList.size(); i++) {
            crossedFences.pushMap(getCrossedFenceMap(crossedFenceList.get(i)));
        }
        return crossedFences;
    }

    public WritableMap getFenceEnteredMap(TriggerEvent.FenceEnteredEvent fenceEnteredEvent) {

        WritableMap fenceEnteredEventMap = new WritableNativeMap();
        fenceEnteredEventMap.putString("fenceId", fenceEnteredEvent.getFenceId().toString());
        fenceEnteredEventMap.putString("fenceName", fenceEnteredEvent.getFenceName());
        fenceEnteredEventMap.putArray("locations", getLocationsArray(fenceEnteredEvent));
        fenceEnteredEventMap.putArray("crossedFences",
                                      getCrossedFencesArray(fenceEnteredEvent.getCrossedFences()));
        fenceEnteredEventMap.putMap("applicationState",
                                    getAppStateMap(fenceEnteredEvent.getApplicationState()));
        fenceEnteredEventMap.putString("eventTime", fenceEnteredEvent.getEventTime().toString());
        fenceEnteredEventMap.putString("localEventTime", fenceEnteredEvent.getLocalEventTime());
        fenceEnteredEventMap.putString("eventType", fenceEnteredEvent.getEventType().toString());
        return fenceEnteredEventMap;
    }

    public WritableMap getFenceExitedMap(TriggerEvent.FenceExitedEvent fenceExitedEvent) {

        WritableMap fenceExitedEventMap = new WritableNativeMap();
        fenceExitedEventMap.putString("fenceId", fenceExitedEvent.getFenceId().toString());
        fenceExitedEventMap.putString("fenceName", fenceExitedEvent.getFenceName());
        fenceExitedEventMap.putArray("locations", getLocationsArray(fenceExitedEvent));
        fenceExitedEventMap.putMap("applicationState",
                                   getAppStateMap(fenceExitedEvent.getApplicationState()));
        fenceExitedEventMap.putString("eventTime", fenceExitedEvent.getEventTime().toString());
        fenceExitedEventMap.putString("localEventTime", fenceExitedEvent.getLocalEventTime());
        fenceExitedEventMap.putDouble("distance", fenceExitedEvent.getDistance());
        fenceExitedEventMap.putDouble("distanceRequired", fenceExitedEvent.getDistanceRequired());
        fenceExitedEventMap.putInt("dwellTime", Math.toIntExact(fenceExitedEvent.getDwellTime()));
        fenceExitedEventMap.putString("eventType", fenceExitedEvent.getEventType().toString());
        return fenceExitedEventMap;
    }
}
