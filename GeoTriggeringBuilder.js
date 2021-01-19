import { NativeModules, Platform } from 'react-native'

class GeoTriggeringBuilder {
    constructor() {
        // Android Foreground notification parameters
        this.channelId = "";
        this.channelName = "";
        this.androidNotificationTitle = "";
        this.androidNotificationContent = "";
        this.androidNotificationId = -1;
        
        // iOS App Restart notification parameters
        this.iOSAppRestartNotificationTitle = null;
        this.iOSAppRestartNotificationButtonText = null;
    }

    androidNotification = (channelId = "", channelName = "", title = "", content= "", id= -1) => {
        this.channelId = channelId;
        this.channelName = channelName;
        this.androidNotificationTitle = title;
        this.androidNotificationContent = content;
        this.androidNotificationId = id;

        return this 
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
            NativeModules.BluedotPointSDK.androidStartGeoTriggering(
                this.channelId,
                this.channelName,
                this.androidNotificationTitle,
                this.androidNotificationContent, 
                this.androidNotificationId, 
                onSuccess, 
                onError)
        }
    }
}

module.exports = GeoTriggeringBuilder