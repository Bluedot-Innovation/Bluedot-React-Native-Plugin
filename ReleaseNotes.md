# Bluedot React Native Plugin release notes

## 4.0.0

- Integrated with Bluedot Android SDK v19.0.0 and iOS SDK 18.0.0.
- **Breaking:** Removed the Android foreground-notification parameters from `GeoTriggeringBuilder.androidNotification()` and `TempoBuilder.androidNotification()`, and from the underlying native `androidStartGeoTriggering` / `androidStartTempoTracking` calls. Android SDK 19.0.0 no longer builds its own foreground notification; apps must run and manage their own foreground service instead.
- **Breaking:** Removed `setNotificationIdResourceId` / native `setNotificationIDResourceID` — no longer needed now that the plugin does not create notifications on the app's behalf.
- Added `BluedotPointSDK.linkForegroundService()` and `BluedotPointSDK.unlinkForegroundService()` (Android only, Promise-based). Call `linkForegroundService()` after your app's foreground service has successfully called `startForeground()`. This is now **required** before starting Tempo tracking (SDK 19.0.0+) and recommended before starting GeoTriggering in high-accuracy mode. Call `unlinkForegroundService()` when your foreground service stops.
- Tempo errors now emit `tempoTrackingStoppedWithError` (renamed from `tempoStoppedWithError`) on both Android and iOS. The Android payload now includes `isForegroundServiceNotLinked: true` when Tempo stops because the linked foreground service was killed/stopped (new `ForegroundServiceNotLinkedError`).
- `bluedot-react-native-pushnotifications`: removed the JitPack repository dependency; the push SDK artifact is now resolved from the same Maven source as the core Point SDK.
