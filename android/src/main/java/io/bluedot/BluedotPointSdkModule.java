package io.bluedot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import au.com.bluedot.application.model.Proximity;
import au.com.bluedot.point.ApplicationNotificationListener;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.BeaconInfo;
import au.com.bluedot.point.net.engine.FenceInfo;
import au.com.bluedot.point.net.engine.GeoTriggeringService;
import au.com.bluedot.point.net.engine.GeoTriggeringStatusListener;
import au.com.bluedot.point.net.engine.InitializationResultListener;
import au.com.bluedot.point.net.engine.LocationInfo;
import au.com.bluedot.point.net.engine.ResetResultReceiver;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.net.engine.TempoService;
import au.com.bluedot.point.net.engine.TempoServiceStatusListener;
import au.com.bluedot.point.net.engine.ZoneInfo;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Notification.PRIORITY_MAX;

public class BluedotPointSdkModule extends ReactContextBaseJavaModule
        implements ApplicationNotificationListener {

    private final ReactApplicationContext reactContext;
    ServiceManager serviceManager;
    private Callback logOutCallback;

    public BluedotPointSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        serviceManager = ServiceManager.getInstance(reactContext);
    }

    @Override
    public String getName() {
        return "BluedotPointSDK";
    }

    private void sendEvent(ReactContext reactContext,
            String eventName,
            @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
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
    public void isInitialized(Promise promise){
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
            serviceManager.unsubscribeForApplicationNotification(this);
        };
        serviceManager.reset(resetResultReceiver);
    }

    @ReactMethod
    public void androidStartGeoTriggering(String channelId,
                                          String channelName,
                                          String androidNotificationTitle,
                                          String androidNotificationContent,
                                          Integer androidNotificationId,
                                          Callback onSuccess,
                                          Callback onError) {
        //Start as With FG Service
        if (!androidNotificationTitle.isEmpty() && !androidNotificationContent.isEmpty()) {

            if ((channelId.isEmpty()) || (channelName.isEmpty())) {
                onError.invoke("Missing channelId and channelName for Notification");
                return;
            }

            Notification fgNotification =
                    createNotification(channelId, channelName, androidNotificationTitle,
                                       androidNotificationContent);
            if (androidNotificationId != -1) {
                //Set notificationId for GeoTriggerService
                GeoTriggeringService.builder()
                        .notification(fgNotification)
                        .notificationId(androidNotificationId)
                        .start(reactContext, geoTriggerError -> {
                            if (geoTriggerError != null) {
                                onError.invoke("Error " + geoTriggerError.getReason());
                                return;
                            }
                            serviceManager.subscribeForApplicationNotification(this);
                            onSuccess.invoke();
                        });
            } else {
                //Use default notificationId set by PointSDK
                GeoTriggeringService.builder()
                        .notification(fgNotification)
                        .start(reactContext, geoTriggerError -> {
                            if (geoTriggerError != null) {
                                onError.invoke("Error " + geoTriggerError.getReason());
                                return;
                            }
                            serviceManager.subscribeForApplicationNotification(this);
                            onSuccess.invoke();
                        });
            }
        } else {
            //Start as No FG Service
            GeoTriggeringService.builder()
                    .start(reactContext, geoTriggerError -> {
                        if (geoTriggerError != null) {
                            onError.invoke("Error " + geoTriggerError.getReason());
                            return;
                        }
                        serviceManager.subscribeForApplicationNotification(this);
                        onSuccess.invoke();
                    });
        }
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
    public void stopGeoTriggering(Callback onSuccessCallback, Callback onFailCallback){
        GeoTriggeringStatusListener statusListener = error -> {
            if (error == null) {
                serviceManager.unsubscribeForApplicationNotification(this);
                onSuccessCallback.invoke();
                return;
            }
            serviceManager.unsubscribeForApplicationNotification(this);
            onFailCallback.invoke(error.getReason());
        };
        GeoTriggeringService.stop(reactContext, statusListener);
    }

    @ReactMethod
    public void androidStartTempoTracking(String destinationId,
                                          String channelId,
                                          String channelName,
                                          String androidNotificationTitle,
                                          String androidNotificationContent,
                                          Integer androidNotificationId,
                                          Callback onSuccess,
                                          Callback onError) {

        if (destinationId.isEmpty()) {
            onError.invoke("destinationId is null");
            return;
        }

        if ((channelId.isEmpty()) || (channelName.isEmpty()) || (androidNotificationTitle.isEmpty()) || (androidNotificationContent.isEmpty())) {
            onError.invoke("Missing param from channelId/channelName/androidNotificationTitle/androidNotificationContent");
            return;
        }

        Notification fgNotification =
                createNotification(channelId, channelName, androidNotificationTitle,
                                   androidNotificationContent);
        TempoServiceStatusListener tempoStatusListener = error -> {
            if (error == null) {
                onSuccess.invoke();
            } else {
                onError.invoke("Error -" + error.getReason());
            }
        };

        if (androidNotificationId != -1) {
            TempoService.builder()
                    .notificationId(androidNotificationId)
                    .notification(fgNotification)
                    .destinationId(destinationId)
                    .start(reactContext, tempoStatusListener);
        } else {
            TempoService.builder()
                    .notification(fgNotification)
                    .destinationId(destinationId)
                    .start(reactContext, tempoStatusListener);
        }
    }

    @ReactMethod
    public void isTempoRunning(Promise promise){
        try {
            boolean isRunning = TempoService.isRunning(reactContext);
            promise.resolve(isRunning);
        } catch (Exception e) {
            promise.reject("Error getting the isTempoRunning");
        }
    }

    @ReactMethod
    public void stopTempoTracking(Callback onSuccessCallback, Callback onFailCallback) {
        BDError error = TempoService.stop(reactContext);
        if(error == null)
            onSuccessCallback.invoke();
        else
            onFailCallback.invoke("Error "+error.getReason());

    }

    @ReactMethod
    public void getSdkVersion(Promise promise){
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
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                WritableMap zone = new WritableNativeMap();
                zone.putString("name",list.get(i).getZoneName());
                zone.putString("id",list.get(i).getZoneId());
                zoneList.pushMap(zone);
            }
        }
        WritableMap map = new WritableNativeMap();
        map.putArray("zoneInfo",zoneList);
            promise.resolve(map);
        } catch (Exception e) {
            promise.reject("Error getting the ZoneInfo");
        }
    }

    @ReactMethod
    public void setZoneDisableByApplication(String zoneId, boolean disable) {
        serviceManager.setZoneDisableByApplication(zoneId, disable);
    }

    private Notification createNotification(String channelId,String channelName,String title, String content) {

        Intent activityIntent = new Intent(this.getCurrentActivity().getIntent());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(reactContext, 0,
                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(channelId, channelName,
                                NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            Notification.Builder notification = new Notification.Builder(reactContext, channelId)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new Notification.BigTextStyle().bigText(content))
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher);
            return notification.build();
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(reactContext)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setPriority(PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher);
            return notification.build();
        }
    }

    @Override
    public void onCheckIntoFence(FenceInfo fenceInfo, ZoneInfo zoneInfo, LocationInfo locationInfo,
                                 Map<String, String> map, boolean isCheckout) {
        //Do nothing for fence callback as its handled from onZoneEntryEvent
    }

    @Override
    public void onCheckedOutFromFence(FenceInfo fenceInfo, ZoneInfo zoneInfo, int dwellTime,
                                                Map<String, String> map) {
        //Do nothing for fence callback as its handled from onZoneExitEvent
    }

    @Override public void onCheckIntoBeacon(BeaconInfo beaconInfo, ZoneInfo zoneInfo,
                                            LocationInfo locationInfo, Proximity proximity, Map<String, String> map, boolean isCheckout) {
        WritableMap beaconDetails = new WritableNativeMap();
        beaconDetails.putString("ID",beaconInfo.getId());
        beaconDetails.putString("name",beaconInfo.getName());
        beaconDetails.putString("macAddress",beaconInfo.getMacAddress());
        beaconDetails.putDouble("latitude",beaconInfo.getLocation().getLatitude());
        beaconDetails.putDouble("longitude",beaconInfo.getLocation().getLongitude());

        WritableMap zoneDetails = new WritableNativeMap();
        zoneDetails.putString("ID",zoneInfo.getZoneId());
        zoneDetails.putString("name",zoneInfo.getZoneName());

        WritableMap locationDetails = new WritableNativeMap();
        locationDetails.putDouble("unixDate", locationInfo.getTimeStamp());
        locationDetails.putDouble("latitude", locationInfo.getLatitude());
        locationDetails.putDouble("longitude", locationInfo.getLongitude());
        locationDetails.putDouble("bearing", locationInfo.getBearing());
        locationDetails.putDouble("speed", locationInfo.getSpeed());

        WritableMap customData = new WritableNativeMap();
        if(map != null) {
            for (String entry : map.keySet()) {
                customData.putString(entry, map.get(entry));
            }
        }

        WritableMap writableMap = new WritableNativeMap();
        writableMap.putMap("beaconInfo",beaconDetails);
        writableMap.putMap("zoneInfo",zoneDetails);
        writableMap.putMap("locationInfo", locationDetails);
        writableMap.putMap("customData",customData);
        writableMap.putInt("proximity",getIntForProximity(proximity));
        writableMap.putBoolean("willCheckout",isCheckout);

        sendEvent(reactContext, "checkedIntoBeacon",writableMap);
    }

    @Override public void onCheckedOutFromBeacon(BeaconInfo beaconInfo, ZoneInfo zoneInfo, int dwellTime,
                                                 Map<String, String> map) {

        WritableMap beaconDetails = new WritableNativeMap();
        beaconDetails.putString("ID",beaconInfo.getId());
        beaconDetails.putString("name",beaconInfo.getName());
        beaconDetails.putString("macAddress",beaconInfo.getMacAddress());
        beaconDetails.putDouble("latitude",beaconInfo.getLocation().getLatitude());
        beaconDetails.putDouble("longitude",beaconInfo.getLocation().getLongitude());

        WritableMap zoneDetails = new WritableNativeMap();
        zoneDetails.putString("ID",zoneInfo.getZoneId());
        zoneDetails.putString("name",zoneInfo.getZoneName());

        WritableMap customData = new WritableNativeMap();
        if(map != null) {
            for (String entry : map.keySet()) {
                customData.putString(entry, map.get(entry));
            }
        }

        WritableMap writableMap = new WritableNativeMap();
        writableMap.putMap("beaconInfo",beaconDetails);
        writableMap.putMap("zoneInfo",zoneDetails);
        writableMap.putMap("customData",customData);
        writableMap.putInt("dwellTime",dwellTime);
        sendEvent(reactContext, "checkedOutFromBeacon",writableMap);
    }

    @ReactMethod
    public void setCustomEventMetaData(ReadableMap metaData){
        if(metaData != null) {
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
    public void setNotificationIDResourceID(String resourceID){
        // the setNotificationIDResourceID method is added to keep consistency with the iOS implementation
    }


    @ReactMethod
    public void getInstallRef(Promise promise){
        try {
            String installRef = serviceManager.getInstallRef();
            promise.resolve(installRef);
        } catch (Exception e) {
            promise.reject("Error getting the Installation Reference");
        }
    }

    private int getIntForProximity(Proximity value) {
        int result = 0;
        switch (value) {
            case Unknown:
                result = 0;
                break;
            case Immediate:
                result = 1;
                break;
            case Near:
                result = 2;
                break;
            case Far:
                result = 3;
                break;
        }
        return result;
    }
}
