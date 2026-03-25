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

    // Badge icon type constants  (setBadgeIconType)
    BADGE_ICON_NONE  = 0;
    BADGE_ICON_SMALL = 1;
    BADGE_ICON_LARGE = 2;

    // Visibility constants  (setVisibility)
    VISIBILITY_PRIVATE = -1;
    VISIBILITY_PUBLIC  =  1;
    VISIBILITY_SECRET  = -2;

    // Group alert behaviour constants  (setGroupAlertBehavior)
    GROUP_ALERT_ALL      = 0;
    GROUP_ALERT_SUMMARY  = 1;
    GROUP_ALERT_CHILDREN = 2;

    // Notification category constants  (setCategory)
    CATEGORY_ALARM          = 'alarm';
    CATEGORY_CALL           = 'call';
    CATEGORY_EMAIL          = 'email';
    CATEGORY_ERROR          = 'err';
    CATEGORY_EVENT          = 'event';
    CATEGORY_MESSAGE        = 'msg';
    CATEGORY_MISSED_CALL    = 'missed_call';
    CATEGORY_NAVIGATION     = 'navigation';
    CATEGORY_PROGRESS       = 'progress';
    CATEGORY_PROMO          = 'promo';
    CATEGORY_RECOMMENDATION = 'recommendation';
    CATEGORY_REMINDER       = 'reminder';
    CATEGORY_SERVICE        = 'service';
    CATEGORY_SOCIAL         = 'social';
    CATEGORY_STATUS         = 'status';
    CATEGORY_SYSTEM         = 'sys';
    CATEGORY_TRANSPORT      = 'transport';

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
     *   colorized               {boolean}   Use color as background (foreground services / media only)
     *   badgeIconType           {number}    Use BADGE_ICON_* constants
     *   subText                 {string}    Additional text shown in the notification header
     *   ticker                  {string}    Accessibility / pre-Lollipop status bar text
     *   number                  {number}    Badge count
     *   lights                  {object}    { color: string, onMs: number, offMs: number }
     *
     * Behaviour:
     *   autoCancel              {boolean}   Dismiss on tap (default: true)
     *   ongoing                 {boolean}   Prevent user from dismissing the notification
     *   onlyAlertOnce           {boolean}   Sound/vibrate only if not already showing
     *   silent                  {boolean}   Suppress sound & vibration for this instance
     *   localOnly               {boolean}   Do not bridge to wearables
     *   timeoutAfter            {number}    Auto-cancel after this many ms (Android 8+)
     *   vibrationPattern        {number[]}  e.g. [0, 250, 500, 250] (off/on/off/on ms)
     *   progress                {object}    { max: number, value: number, indeterminate: boolean }
     *
     * Timestamp:
     *   showWhen                {boolean}   Show the timestamp
     *   when                    {number}    Event timestamp in ms (defaults to now)
     *   usesChronometer         {boolean}   Show timestamp as a running stopwatch
     *   chronometerCountDown    {boolean}   Count down instead of up (Android 7+)
     *
     * Grouping / sorting:
     *   group                   {string}    Group key
     *   groupSummary            {boolean}   Mark as the group summary notification
     *   groupAlertBehavior      {number}    Use GROUP_ALERT_* constants
     *   sortKey                 {string}    Lexicographic sort key within the group
     *
     * Categorisation / visibility:
     *   category                {string}    Use CATEGORY_* constants
     *   visibility              {number}    Use VISIBILITY_* constants
     *   allowSystemGeneratedContextualActions {boolean}  Default: true
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
