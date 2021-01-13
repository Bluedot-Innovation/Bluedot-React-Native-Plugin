import { NativeModules, Platform } from 'react-native'

class TempoBuilder {
    constructor() {
        this.androidNotificationTitle = null;
        this.androidNotificationContent = null;
        this.androidNotificationId = null;
    }

    androidNotification = (title, content, id) => { 
        this.androidNotificationTitle = title;
        this.androidNotificationContent = content;
        this.androidNotificationId = id;

        return this 
    }

    start = (destinationId, onSuccess, onError) => {
        if (Platform.OS === "ios") {
            NativeModules.BluedotPointSDK.iOSStartTempoTracking(destinationId, onSuccess, onError)
        }

        if (Platform.OS === "android") {
            NativeModules.BluedotPointSDK.androidStartTempoTracking(
                destinationId,
                this.androidNotificationTitle,
                this.androidNotificationContent,
                this.androidNotificationId,
                onSuccess,
                onError
            )
        }
    }
}

module.exports = TempoBuilder