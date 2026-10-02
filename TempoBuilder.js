import { NativeModules, Platform } from 'react-native'

class TempoBuilder {
    constructor() {}

    start = (destinationId = "", onSuccess, onError) => {
        if (Platform.OS === "ios") {
            NativeModules.BluedotPointSDK.iOSStartTempoTracking(destinationId, onSuccess, onError)
        }

        if (Platform.OS === "android") {
            NativeModules.BluedotPointSDK.androidStartTempoTracking(destinationId, onSuccess, onError)
        }
    }
}

module.exports = TempoBuilder
