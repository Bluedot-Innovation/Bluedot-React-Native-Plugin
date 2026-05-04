import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const pushModule = NativeModules.BluedotPushNotificationsSDK;
const pushEventEmitter = pushModule ? new NativeEventEmitter(pushModule) : null;

class PushNotifications {

    // Event names — use these with on() / removeAllListeners()
    PUSH_NOTIFICATION_RECEIVED = "pushNotificationReceived";
    PUSH_NOTIFICATION_CLICKED  = "pushNotificationClicked";

    /**
     * Forward a new FCM token to the Bluedot push module.
     * Call this from your @react-native-firebase/messaging onTokenRefresh handler.
     *
     * @param {string} token - the FCM registration token
     */
    onNewFcmToken = (token) => {
        if (Platform.OS !== 'android') return;
        if (!pushModule) {
            console.error('BluedotPushNotifications: push support is not enabled. Set bluedotPushEnabled=true in android/gradle.properties and rebuild.');
            return;
        }
        pushModule.onNewFcmToken(token);
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
        if (!pushModule) {
            console.error('BluedotPushNotifications: push support is not enabled. Set bluedotPushEnabled=true in android/gradle.properties and rebuild.');
            return;
        }
        pushModule.onMessageReceived(remoteMessage);
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
        if (!pushEventEmitter) {
            console.error('BluedotPushNotifications: push support is not enabled. Set bluedotPushEnabled=true in android/gradle.properties and rebuild.');
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
        if (!pushEventEmitter) return;
        pushEventEmitter.removeAllListeners(eventName);
    }
}

export default new PushNotifications();
