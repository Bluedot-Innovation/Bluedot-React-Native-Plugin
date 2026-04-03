import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const pushEventEmitter = new NativeEventEmitter(NativeModules.BluedotPushNotificationsSDK);

class PushNotifications {

    // Event names — use these with on() / removeAllListeners()
    PUSH_NOTIFICATION_RECEIVED = "pushNotificationReceived";
    PUSH_NOTIFICATION_CLICKED  = "pushNotificationClicked";

    // Android NotificationManager importance constants
    IMPORTANCE_MIN     = 1;
    IMPORTANCE_LOW     = 2;
    IMPORTANCE_DEFAULT = 3;
    IMPORTANCE_HIGH    = 4;
    IMPORTANCE_MAX     = 5;

    /**
     * Forward a new FCM token to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onTokenRefresh handler.
     *
     * @param {string} token - the FCM registration token
     */
    onNewFcmToken = (token) => {
        if (Platform.OS !== 'android') return;
        NativeModules.BluedotPushNotificationsSDK.onNewFcmToken(token);
    }

    /**
     * Forward an incoming FCM message to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onMessage and
     * setBackgroundMessageHandler callbacks.
     *
     * @param {object} remoteMessage - the message object from @react-native-firebase/messaging
     */
    onMessageReceived = (remoteMessage) => {
        if (Platform.OS !== 'android') return;
        NativeModules.BluedotPushNotificationsSDK.onMessageReceived(remoteMessage);
    }

    /**
     * Customise the notification appearance shown by the Bluedot push module.
     * Must be called before the first message arrives — e.g. in your root component's useEffect.
     * Pass null to revert to the SDK default appearance.
     *
     * Required:
     *   channelId    {string}  Notification channel ID
     *   channelName  {string}  User-visible channel name
     *
     * Appearance:
     *   importance              {number}    Channel importance — use IMPORTANCE_* constants (default: IMPORTANCE_DEFAULT)
     *   smallIconResourceName   {string}    Drawable resource name in your Android app
     *   largeIconResourceName   {string}    Drawable resource name for the large icon
     *   color                   {string}    Accent color hex e.g. "#FF0000"
     *
     * Behaviour:
     *   autoCancel              {boolean}   Dismiss on tap (default: true)
     *   ongoing                 {boolean}   Prevent user from dismissing the notification
     *   silent                  {boolean}   Suppress sound & vibration for this notification
     */
    setCustomPushNotification = (options) => {
        if (Platform.OS !== 'android') return;
        NativeModules.BluedotPushNotificationsSDK.setCustomPushNotification(options);
    }

    /**
     * Subscribe to a push notification event.
     *
     * Payload delivered to the callback contains:
     *   title        {string}              Notification title
     *   body         {string}              Notification body
     *   pushVersion  {string}              Push schema version
     *   campaignId   {string}              Campaign UUID
     *   zoneId       {string}              Zone UUID
     *   notificationId {string}            Notification UUID
     *   data         {object}              Custom key-value pairs from the payload
     *
     * @param {string}   eventName  One of the PUSH_NOTIFICATION_* constants above
     * @param {function} callback   Invoked with the push data payload
     * @returns {EmitterSubscription}  Call .remove() to unsubscribe
     */
    on = (eventName, callback) => {
        if (Platform.OS !== 'android') {
            console.warn('BluedotPushNotifications: push notifications are only supported on Android.');
            return { remove: () => {} };
        }
        return pushEventEmitter.addListener(eventName, callback);
    }

    /**
     * Remove all listeners for a given event name.
     *
     * @param {string} eventName
     */
    removeAllListeners = (eventName) => {
        pushEventEmitter.removeAllListeners(eventName);
    }
}

export default new PushNotifications();
