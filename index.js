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

const startTempoTrackingWithCallbacks = (destinationId, onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.startTempoTrackingWithCallbacks(destinationId, onSuccessCallback, onFailCallback)
}

const isTempoRunning = () => {
    return NativeModules.BluedotPointSDK.isTempoRunning()
}

const stopTempoTrackingWithCallbacks = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.stopTempoTrackingWithCallbacks(onSuccessCallback, onFailCallback)
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

const getInstallRef = () => {
    return NativeModules.BluedotPointSDK.getInstallRef()
}

const getSdkVersion = () => {
    return NativeModules.BluedotPointSDK.getSdkVersion()
}

const getZonesAndFences = () => {
    return NativeModules.BluedotPointSDK.getZonesAndFences()
}

const startGeotriggeringWithAppRestartNotification = (onSuccessCallback, onFailCallback, notificationTitle, notificationButtonText) => {
    NativeModules.BluedotPointSDK.startGeotriggeringWithAppRestartNotification(onSuccessCallback, onFailCallback, notificationTitle, notificationButtonText)
}

// DEPRECATED METHODS
const authenticate = (projectId, authorizationLevel, onSucessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.authenticate(projectId, authorizationLevel, onSucessCallback, onFailCallback)
}

const logOut = (onSucessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.logOut(onSucessCallback, onFailCallback)
}

const startTempoTracking = (destinationId, onFailCallback) => {
    NativeModules.BluedotPointSDK.startTempoTracking(destinationId, onFailCallback)
}

const stopTempoTracking = () => {
   NativeModules.BluedotPointSDK.stopTempoTracking()
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
    // New APIs
    initialize,
    isInitialized,
    reset,
    startGeotriggering,
    isGeotriggeringRunning,
    stopGeotriggering,
    startTempoTrackingWithCallbacks,
    isTempoRunning,
    stopTempoTrackingWithCallbacks,
    getSdkVersion,
    getZonesAndFences,
    startGeotriggeringWithAppRestartNotification
}

export default BluedotPointSDK