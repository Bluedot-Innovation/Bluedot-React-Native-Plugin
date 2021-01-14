import { NativeModules, Platform } from 'react-native'

class TempoBuilder {
    constructor() {
        this.channelId = null;
        this.channelName = null;
        this.androidNotificationTitle = null;
        this.androidNotificationContent = null;
        this.androidNotificationId = null;
    }

    androidNotification = (channelId, channelName, title, content, id) => {
        this.channelId = channelId;
        this.channelName = channelName;
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
                this.channelId,
                this.channelName,
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