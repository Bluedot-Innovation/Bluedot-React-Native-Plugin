# bluedot-react-native-pushnotifications

An optional React Native wrapper for the Bluedot Push Notifications SDK. Delivers push notification callbacks (`onReceived`, `onClicked`) from Bluedot campaigns to your React Native app via Firebase Cloud Messaging.

> **Android only.** All methods are no-ops on iOS.

## Requirements

- Android API 29+ (Android 10)
- `bluedot-react-native` ≥ 3.4.1 (core SDK must be installed and initialised first)
- Firebase Cloud Messaging configured in your app (`@react-native-firebase/messaging`)

---

## Installation

```bash
npm install bluedot-react-native-pushnotifications
```

### Android — register the package

In your app's `MainApplication`:

```java
import io.bluedot.pushnotifications.PushNotificationsSdkPackage;

// inside getPackages():
packages.add(new PushNotificationsSdkPackage());
```

### Android — add the google-services plugin

In `android/build.gradle` (project-level):
```groovy
dependencies {
    classpath 'com.google.gms:google-services:4.4.2'
}
```

In `android/app/build.gradle` (app-level), at the bottom:
```groovy
apply plugin: 'com.google.gms.google-services'
```

Place `google-services.json` (downloaded from the Firebase console) at `android/app/google-services.json`.

---

## Usage

### 1. Wire up Firebase Messaging

Forward FCM token and message events to the Bluedot SDK. Do this once, early in your app lifecycle (e.g. in your root `App.js`):

```js
import messaging from '@react-native-firebase/messaging';
import PushNotifications from 'bluedot-react-native-pushnotifications';

// Forward FCM token updates
messaging().onTokenRefresh(token => {
    PushNotifications.onNewFcmToken(token);
});

// Forward foreground messages
messaging().onMessage(async remoteMessage => {
    PushNotifications.onMessageReceived(remoteMessage);
});

// Forward background / quit-state messages
messaging().setBackgroundMessageHandler(async remoteMessage => {
    PushNotifications.onMessageReceived(remoteMessage);
});
```

### 2. Listen for notification events

```js
import PushNotifications from 'bluedot-react-native-pushnotifications';

// Subscribe — returns a subscription with a .remove() method
const receivedSub = PushNotifications.on(
    PushNotifications.PUSH_NOTIFICATION_RECEIVED,
    (data) => {
        console.log('Notification received:', data.title, data.campaignId);
        // data fields: title, body, pushVersion, campaignId, zoneId, notificationId, data
    }
);

const clickedSub = PushNotifications.on(
    PushNotifications.PUSH_NOTIFICATION_CLICKED,
    (data) => {
        // Navigate to the relevant screen using your navigation library
        navigationRef.navigate('CampaignScreen', {
            campaignId: data.campaignId,
            zoneId:     data.zoneId,
        });
    }
);

// Unsubscribe when no longer needed
receivedSub.remove();
clickedSub.remove();
```

> **Cold-start taps:** If the user taps a notification while the app is fully closed, the `PUSH_NOTIFICATION_CLICKED` event is buffered and delivered automatically once the JS bridge is ready.

### 3. Customise notification appearance (optional)

Call this before the first message arrives — for example inside a `useEffect` at the root of your app:

```js
import PushNotifications from 'bluedot-react-native-pushnotifications';

PushNotifications.setCustomPushNotification({
    // Required
    channelId:   'my_push_channel',
    channelName: 'My App Notifications',

    // Appearance
    importance:            PushNotifications.IMPORTANCE_HIGH,
    smallIconResourceName: 'ic_notification',      // drawable resource name in your Android project
    largeIconResourceName: 'ic_large_notification', // drawable resource name in your Android project
    color:                 '#FF6200',               // accent color
    badgeIconType:         PushNotifications.BADGE_ICON_SMALL,
    subText:               'Bluedot',

    // Behaviour
    autoCancel:       true,
    silent:           false,
    onlyAlertOnce:    true,
    vibrationPattern: [0, 250, 500, 250],  // off/on/off/on in ms
    timeoutAfter:     30000,               // auto-dismiss after 30s (Android 8+)
    lights:           { color: '#FF6200', onMs: 500, offMs: 1000 },

    // Grouping
    group:              'bluedot_notifications',
    groupAlertBehavior: PushNotifications.GROUP_ALERT_SUMMARY,

    // Categorisation
    category:   PushNotifications.CATEGORY_PROMO,
    visibility: PushNotifications.VISIBILITY_PUBLIC,
});
```

---

## Available constants

### `IMPORTANCE_*` — notification channel importance
| Constant | Value |
|---|---|
| `IMPORTANCE_MIN` | 1 |
| `IMPORTANCE_LOW` | 2 |
| `IMPORTANCE_DEFAULT` | 3 |
| `IMPORTANCE_HIGH` | 4 |
| `IMPORTANCE_MAX` | 5 |

### `BADGE_ICON_*` — badge icon type
| Constant | Value |
|---|---|
| `BADGE_ICON_NONE` | 0 |
| `BADGE_ICON_SMALL` | 1 |
| `BADGE_ICON_LARGE` | 2 |

### `VISIBILITY_*` — lockscreen visibility
| Constant | Value |
|---|---|
| `VISIBILITY_PRIVATE` | -1 (default) |
| `VISIBILITY_PUBLIC` | 1 |
| `VISIBILITY_SECRET` | -2 |

### `GROUP_ALERT_*` — group alert behaviour
| Constant | Value |
|---|---|
| `GROUP_ALERT_ALL` | 0 (default) |
| `GROUP_ALERT_SUMMARY` | 1 |
| `GROUP_ALERT_CHILDREN` | 2 |

### `CATEGORY_*` — notification category
`CATEGORY_ALARM`, `CATEGORY_CALL`, `CATEGORY_EMAIL`, `CATEGORY_ERROR`, `CATEGORY_EVENT`, `CATEGORY_MESSAGE`, `CATEGORY_MISSED_CALL`, `CATEGORY_NAVIGATION`, `CATEGORY_PROGRESS`, `CATEGORY_PROMO`, `CATEGORY_RECOMMENDATION`, `CATEGORY_REMINDER`, `CATEGORY_SERVICE`, `CATEGORY_SOCIAL`, `CATEGORY_STATUS`, `CATEGORY_SYSTEM`, `CATEGORY_TRANSPORT`

---

## Notification payload fields

Delivered to both `PUSH_NOTIFICATION_RECEIVED` and `PUSH_NOTIFICATION_CLICKED` callbacks:

| Field | Type | Description |
|---|---|---|
| `title` | `string` | Notification title |
| `body` | `string` | Notification body |
| `pushVersion` | `string` | Push schema version |
| `campaignId` | `string` | Campaign UUID |
| `zoneId` | `string` | Zone UUID |
| `notificationId` | `string` | Notification UUID |
| `data` | `object` | Custom key-value pairs from the payload |

---

## API reference

| Method | Description |
|---|---|
| `onNewFcmToken(token)` | Forward an FCM token update to the Bluedot SDK |
| `onMessageReceived(remoteMessage)` | Forward an incoming FCM message to the Bluedot SDK |
| `setCustomPushNotification(options)` | Customise notification appearance and behaviour |
| `on(eventName, callback)` | Subscribe to a notification event, returns subscription |
| `removeAllListeners(eventName)` | Remove all listeners for a given event name |
</content>
</invoke>