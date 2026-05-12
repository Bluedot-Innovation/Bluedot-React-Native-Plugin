import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const getActiveEmitter = () => {
    if (Platform.OS === 'android') {
        const nativeModule = NativeModules.BluedotPushNotificationsSDK;
        return nativeModule ? new NativeEventEmitter(nativeModule) : null;
    }
    if (Platform.OS === 'ios') {
        const nativeModule = NativeModules.BluedotPointSDK;
        return nativeModule ? new NativeEventEmitter(nativeModule) : null;
    }
    return null;
};

class PushNotifications {

    // Event names — use these with on() / removeAllListeners()
    PUSH_NOTIFICATION_RECEIVED = "pushNotificationReceived";
    PUSH_NOTIFICATION_CLICKED  = "pushNotificationClicked";

    /**
     * Android only
     * Forward a new FCM token to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onTokenRefresh handler.
     *
     * @param {string} token - the FCM registration token
     */
    onNewFcmToken = (token) => {
        if (Platform.OS !== 'android') return;
        const pushModule = NativeModules.BluedotPushNotificationsSDK;
        if (!pushModule) return;
        pushModule.onNewFcmToken(token);
    }

    /**
     * Android only
     * Forward an incoming FCM message to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onMessage and
     * setBackgroundMessageHandler callbacks.
     *
     * @param {object} remoteMessage - the message object from @react-native-firebase/messaging
     */
    onMessageReceived = (remoteMessage) => {
        if (Platform.OS !== 'android') return;
        const pushModule = NativeModules.BluedotPushNotificationsSDK;
        if (!pushModule) return;
        pushModule.onMessageReceived(remoteMessage);
    }

    /**
     * Subscribe to a push notification event.
     *
     * Payload delivered to the callback contains:
     *   title        {string}              Notification title
     *   body         {string}              Notification body // Android only
     *   pushVersion  {string}              Push schema version // Android only
     *   campaignId   {string}              Campaign UUID
     *   zoneId       {string}              Zone UUID
     *   notificationId {string}            Notification UUID
     *   data         {object}              Custom key-value pairs from the payload // Android only
     *
     * @param {string}   eventName  One of the PUSH_NOTIFICATION_* constants above
     * @param {function} callback   Invoked with the push data payload
     * @returns {EmitterSubscription}  Call .remove() to unsubscribe
     */
     on = (eventName, callback) => {
        const emitter = getActiveEmitter();
        if (!emitter) {
            console.warn('Native push emitter is not available on this platform.');
            return { remove: () => {} };
        }
        return emitter.addListener(eventName, callback);
    }

    /**
     * Remove all listeners for a given event name.
     *
     * @param {string} eventName
     */
    removeAllListeners = (eventName) => {
        const emitter = getActiveEmitter();
        if (!emitter) return;
        emitter.removeAllListeners(eventName);
   }
}

export default new PushNotifications();
