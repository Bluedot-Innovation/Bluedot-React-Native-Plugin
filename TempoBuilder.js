import { NativeModules, Platform } from 'react-native'

class TempoBuilder {
    constructor() {
        this.channelId = "";
        this.channelName = "";
        this.androidNotificationTitle = "";
        this.androidNotificationContent = "";
        this.androidNotificationId = -1;
    }

    androidNotification = (channelId = "", channelName = "", title = "", content = "", id = -1) => {
        this.channelId = channelId;
        this.channelName = channelName;
        this.androidNotificationTitle = title;
        this.androidNotificationContent = content;
        this.androidNotificationId = id;

        return this 
    }

    start = (destinationId = "", onSuccess, onError) => {
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