package io.bluedot;

import android.util.Log;

import androidx.annotation.NonNull;
import au.com.bluedot.model.geo.Point;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.GeoTriggeringService;
import au.com.bluedot.point.net.engine.GeoTriggeringStatusListener;
import au.com.bluedot.point.net.engine.InitializationResultListener;
import au.com.bluedot.point.net.engine.ResetResultReceiver;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.net.engine.TempoService;
import au.com.bluedot.point.net.engine.TempoServiceStatusListener;
import au.com.bluedot.point.net.engine.ZoneInfo;
import au.com.bluedot.ruleEngine.model.rule.Destination;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.LifecycleEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluedotPointSdkModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    static ReactApplicationContext reactContext = null;
    ServiceManager serviceManager;
    private Callback logOutCallback;

    public BluedotPointSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addLifecycleEventListener(this);
        serviceManager = ServiceManager.getInstance(reactContext);
    }

    public static ReactApplicationContext getReactContextRef() {
        return reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "BluedotPointSDK";
    }

    @ReactMethod
    public void initialize(String projectId, Callback onSucessCallback, Callback onFailCallback) {

        InitializationResultListener resultListener = bdError -> {
            String text = "Initialization Result ";
            if (bdError != null) {
                text = text + bdError.getReason();
                onFailCallback.invoke(text);
            } else {
                text = text + "Success ";
                onSucessCallback.invoke(text);
            }
        };
        serviceManager.initialize(projectId, resultListener);
    }

    @ReactMethod
    public void isInitialized(Promise promise) {
        try {
            boolean isInitialized = serviceManager.isBluedotServiceInitialized();
            promise.resolve(isInitialized);
        } catch (Exception e) {
            promise.reject("Error getting the isInitialized");
        }
    }

    @ReactMethod
    public void reset(Callback onSucessCallback, Callback onFailCallback) {
        ResetResultReceiver resetResultReceiver = bdError -> {
            String text = "Reset Finished ";
            if (bdError != null) {
                text = text + bdError.getReason();
                onFailCallback.invoke(text);
            } else {
                text = text + "Success ";
                onSucessCallback.invoke(text);
            }
        };
        serviceManager.reset(resetResultReceiver);
    }

    @ReactMethod
    public void androidStartGeoTriggering(Callback onSuccess, Callback onError) {
        GeoTriggeringService.builder()
                .start(geoTriggerError -> {
                    if (geoTriggerError != null) {
                        onError.invoke("Error " + geoTriggerError.getReason());
                        return;
                    }
                    onSuccess.invoke();
                });
    }

    @ReactMethod
    public void isGeoTriggeringRunning(Promise promise) {
        try {
            boolean isRunning = GeoTriggeringService.isRunning();
            promise.resolve(isRunning);
        } catch (Exception e) {
            promise.reject("Error getting isGeoTriggeringRunning");
        }
    }

    @ReactMethod
    public void stopGeoTriggering(Callback onSuccessCallback, Callback onFailCallback) {
        // triggered variable used to avoid crash caused by double invocation of success
        // callback
        // due to GeoTriggeringStatusListener being invoked twice from SDK when stopping
        // in background mode.
        // TODO: Remove it when SDK fixes this issue
        AtomicBoolean triggered = new AtomicBoolean(false);

        GeoTriggeringStatusListener statusListener = error -> {
            if (triggered.get()) {
                return;
            }

            if (error == null) {
                onSuccessCallback.invoke();
                triggered.set(true);
                return;
            }
            onFailCallback.invoke(error.getReason());
        };
        GeoTriggeringService.stop(statusListener);
    }

    @ReactMethod
    public void androidStartTempoTracking(String destinationId, Callback onSuccess, Callback onError) {
        if (destinationId.isEmpty()) {
            onError.invoke("destinationId is null");
            return;
        }

        TempoServiceStatusListener tempoStatusListener = error -> {
            if (error == null) {
                onSuccess.invoke();
            } else {
                onError.invoke("Error -" + error.getReason());
            }
        };

        TempoService.builder()
                .destinationId(destinationId)
                .start(tempoStatusListener);
    }

    @ReactMethod
    public void isTempoRunning(Promise promise) {
        try {
            boolean isRunning = TempoService.isRunning();
            promise.resolve(isRunning);
        } catch (Exception e) {
            promise.reject("Error getting the isTempoRunning");
        }
    }

    @ReactMethod
    public void stopTempoTracking(Callback onSuccessCallback, Callback onFailCallback) {
        BDError error = TempoService.stop();
        if (error == null)
            onSuccessCallback.invoke();
        else
            onFailCallback.invoke("Error " + error.getReason());
    }

    /**
     * Links the foreground service that the host app has already started and foregrounded.
     *
     * The host app must call this from within its own foreground service's onStartCommand()
     * (or equivalent lifecycle point) — after startForeground() has succeeded — so that
     * the SDK can use the host process's foreground state for GeoTriggering (high-accuracy
     * mode) and Tempo (mandatory since SDK 19.0.0).
     *
     * This method is a thin JS-accessible wrapper over
     * ServiceManager.linkForegroundService(). It exists for app code that manages its
     * foreground service lifecycle via a React Native bridge (e.g. a JS-driven start
     * callback fired once the native service has actually foregrounded).
     */
    @ReactMethod
    public void linkForegroundService(Promise promise) {
        try {
            serviceManager.linkForegroundService();
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("linkForegroundService failed", e.getMessage());
        }
    }

    /**
     * Unlinks the foreground service previously linked via linkForegroundService().
     *
     * The host app must call this (or call unlinkForegroundService() natively from its
     * service's onDestroy()) before or when the foreground service stops. After unlinking,
     * any running Tempo session will stop and report a ForegroundServiceNotLinkedError via
     * the tempoStoppedWithError event.
     */
    @ReactMethod
    public void unlinkForegroundService(Promise promise) {
        try {
            serviceManager.unlinkForegroundService();
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("unlinkForegroundService failed", e.getMessage());
        }
    }

    @ReactMethod
    public void getSdkVersion(Promise promise) {
        try {
            String sdkVersion = serviceManager.getSdkVersion();
            promise.resolve(sdkVersion);
        } catch (Exception e) {
            promise.reject("Error getting the sdkVersion");
        }
    }

    @ReactMethod
    public void getZonesAndFences(Promise promise) {
        try {

            List<ZoneInfo> list = serviceManager.getZonesAndFences();

            WritableArray zoneList = new WritableNativeArray();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    ZoneInfo zoneInfo = list.get(i);
                    WritableMap zone = new WritableNativeMap();
                    if (zoneInfo.getZoneName() != null) {
                        zone.putString("zoneName", zoneInfo.getZoneName());
                    }

                    if (zoneInfo.getZoneId() != null) {
                        zone.putString("zoneId", zoneInfo.getZoneId());
                    }

                    if (zoneInfo.isCheckOut() != null) {
                        zone.putBoolean("isCheckOut", zoneInfo.isCheckOut());
                    }

                    if (zoneInfo.getDestination() != null) {
                        Destination destinationObj = zoneInfo.getDestination();

                        WritableMap destination = new WritableNativeMap();
                        destination.putString("name", destinationObj.getName());
                        destination.putString("destinationId", destinationObj.getDestinationId());
                        if (destinationObj.getAddress() != null) {
                            destination.putString("address", destinationObj.getAddress());
                        }

                        Point loc = destinationObj.getLocation();
                        WritableMap location = new WritableNativeMap();
                        location.putDouble("latitude", loc.getLatitude());
                        location.putDouble("longitude", loc.getLongitude());

                        destination.putMap("location", location);

                        if (destinationObj.getCustomData() != null) {
                            WritableMap customData = new WritableNativeMap();
                            Map<String, String> customDataMap = destinationObj.getCustomData();
                            for (Map.Entry<String, String> entry : customDataMap.entrySet()) {
                                customData.putString(entry.getKey(), entry.getValue());
                            }
                            destination.putMap("customData", customData);
                        }

                        zone.putMap("destination", destination);
                    }
                    zoneList.pushMap(zone);
                }
            }
            WritableMap map = new WritableNativeMap();
            map.putArray("zoneInfo", zoneList);
            promise.resolve(map);
        } catch (Exception e) {
            promise.reject("Error getting the ZoneInfo");
        }
    }

    @ReactMethod
    public void setZoneDisableByApplication(String zoneId, boolean disable) {
        serviceManager.setZoneDisableByApplication(zoneId, disable);
    }

    @ReactMethod
    public void setCustomEventMetaData(ReadableMap metaData) {
        if (metaData != null) {
            ReadableMapKeySetIterator mapKeySetIterator = metaData.keySetIterator();
            HashMap<String, String> metaDataMap = new HashMap<>();
            while (mapKeySetIterator.hasNextKey()) {
                String key = mapKeySetIterator.nextKey();
                metaDataMap.put(key, metaData.getString(key));
            }
            serviceManager.setCustomEventMetaData(metaDataMap);
        }
    }

    @ReactMethod
    public void getCustomEventMetaData(Promise promise) {
        try {
            Map<String, Object> metaDataMap = new HashMap<>(serviceManager.getCustomEventMetaData());
            WritableMap writableMap = MapUtil.toWritableMap(metaDataMap);
            promise.resolve(writableMap);
        } catch (Exception e) {
            promise.reject("Error getting the customEventMetaData");
        }
    }

    @ReactMethod
    public void backgroundLocationAccessForWhileUsing(boolean enable) {
        // the backgroundLocationAccessForWhileUsing method is added to keep consistency with the
        // iOS implementation
    }

    @ReactMethod
    public void getInstallRef(Promise promise) {
        try {
            String installRef = serviceManager.getInstallRef();
            promise.resolve(installRef);
        } catch (Exception e) {
            promise.reject("Error getting the Installation Reference");
        }
    }

    @ReactMethod
    public void addListener(String eventName) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    @Override
    public void onHostResume() {
       Log.d("Plugin", "onHostResume()");
       // Flush buffered events when ReactContext is ready
       if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
           EventUtil.flushBufferedEvents();
       }
    }

    @Override
    public void onHostPause() {
        Log.d("Plugin", "onHostPause()");
    }

    @Override
    public void onHostDestroy() {
        Log.d("Plugin", "onHostDestroy()");
    }
}
