import { NativeModules, Platform } from 'react-native'

class GeotriggeringBuilder {
    constructor() {
        // Android Foreground notification parameters
        this.androidNotificationChannelId = null;
        this.androidNotificationChannelName = null;
        this.androidNotificationTitle = null;
        this.androidNotificationContent = null;
        this.androidNotificationId = null;
        
        // iOS App Restart notification parameters
        this.iOSAppRestartNotificationTitle = null;
        this.iOSAppRestartNotificationButtonText = null;
    }

    androidNotification = (title, content, id) => { 
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
                NativeModules.BluedotPointSDK.iOSStartGeotriggeringWithAppRestartNotification(
                    this.iOSAppRestartNotificationTitle,
                    this.iOSAppRestartNotificationButtonText,
                    onSuccess,
                    onError
                )
                return 
            } 

            // With Completion
            NativeModules.BluedotPointSDK.iOSStartGeotriggering(onSuccess, onError);
        }

        if (Platform.OS === "android") {
            NativeModules.BluedotPointSDK.androidStartGeotriggering(
                this.androidNotificationChannelId,
                this.androidNotificationChannelName,
                this.androidNotificationTitle, 
                this.androidNotificationContent, 
                this.androidNotificationId, 
                onSuccess, 
                onError)
        }
    }
}

module.exports = GeotriggeringBuilder