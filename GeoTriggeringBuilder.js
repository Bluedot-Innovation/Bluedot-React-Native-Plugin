import { NativeModules, Platform } from 'react-native'

class GeoTriggeringBuilder {
    constructor() {
        // iOS App Restart notification parameters
        this.iOSAppRestartNotificationTitle = null;
        this.iOSAppRestartNotificationButtonText = null;
    }

    iOSAppRestartNotification = (title, buttonText) => {
        this.iOSAppRestartNotificationTitle = title;
        this.iOSAppRestartNotificationButtonText = buttonText;

        return this
    }

    start = (onSuccess, onError) => {
        if (Platform.OS === "ios") {
            // With App Restart Notification
            if (this.iOSAppRestartNotificationTitle !== null && this.iOSAppRestartNotificationButtonText !== null) {
                NativeModules.BluedotPointSDK.iOSStartGeoTriggeringWithAppRestartNotification(
                    this.iOSAppRestartNotificationTitle,
                    this.iOSAppRestartNotificationButtonText,
                    onSuccess,
                    onError
                )
                return
            }

            // With Completion
            NativeModules.BluedotPointSDK.iOSStartGeoTriggering(onSuccess, onError);
        }

        if (Platform.OS === "android") {
            NativeModules.BluedotPointSDK.androidStartGeoTriggering(onSuccess, onError)
        }
    }
}

module.exports = GeoTriggeringBuilder
