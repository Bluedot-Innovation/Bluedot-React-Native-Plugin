import { NativeModules, NativeEventEmitter } from 'react-native';

const eventEmitter = new NativeEventEmitter(NativeModules.BluedotPointSDK)

const initialize = (projectId, onSucessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.initialize(projectId, onSucessCallback, onFailCallback)
}

const isInitialized = () => {
    return NativeModules.BluedotPointSDK.isInitialized()
}

const reset = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.reset(onSuccessCallback, onFailCallback)
}

const startGeotriggering = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.startGeotriggering(onSuccessCallback, onFailCallback)
}

const isGeotriggeringRunning = () => {
    return NativeModules.BluedotPointSDK.isGeotriggeringRunning()
}

const stopGeotriggering = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.stopGeotriggering(onSuccessCallback, onFailCallback)
}

const authenticate = (projectId, authorizationLevel, onSucessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.authenticate(projectId, authorizationLevel, onSucessCallback, onFailCallback)
}

const logOut = (onSucessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.logOut(onSucessCallback, onFailCallback)
}

const setCustomEventMetaData = (eventMetaData) => {
    NativeModules.BluedotPointSDK.setCustomEventMetaData(eventMetaData)
}

const setNotificationIdResourceId = (resourceId) => {
    NativeModules.BluedotPointSDK.setNotificationIDResourceID(resourceId)
}

const setForegroundNotification = (channelId, channelName, title, content, targetAllApis) => {
    NativeModules.BluedotPointSDK.setForegroundNotification(channelId, channelName, title, content, targetAllApis)
}

const on = (eventName, callback) => {
    eventEmitter.addListener(eventName, callback)
}

const unsubscribe = (eventName, callback) => {
    eventEmitter.removeListener(eventName, callback)
}

const startTempoTracking = (destinationId, callback) => {
    NativeModules.BluedotPointSDK.startTempoTracking(destinationId, callback)
}

const stopTempoTracking = () => {
    NativeModules.BluedotPointSDK.stopTempoTracking()
}

const getInstallRef = () => {
    return NativeModules.BluedotPointSDK.getInstallRef()
}

const isBlueDotPointServiceRunning = () => {
    return NativeModules.BluedotPointSDK.isBlueDotPointServiceRunning()
}

const BluedotPointSDK = { 
    authenticate, 
    logOut, 
    on, 
    unsubscribe, 
    setForegroundNotification, 
    setCustomEventMetaData,
    setNotificationIdResourceId,
    startTempoTracking,
    stopTempoTracking,
    getInstallRef,
    isBlueDotPointServiceRunning,
    // New API
    initialize,
    isInitialized,
    reset,
    startGeotriggering,
    isGeotriggeringRunning,
    stopGeotriggering
}

export default BluedotPointSDK