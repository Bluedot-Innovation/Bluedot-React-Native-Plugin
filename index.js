import { NativeModules, NativeEventEmitter } from 'react-native';
import GeotriggeringBuilder from './GeoTriggeringBuilder'
import TempoBuilder from './TempoBuilder'

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

const isGeotriggeringRunning = () => {
    return NativeModules.BluedotPointSDK.isGeotriggeringRunning()
}

const stopGeotriggering = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.stopGeotriggering(onSuccessCallback, onFailCallback)
}

const isTempoRunning = () => {
    return NativeModules.BluedotPointSDK.isTempoRunning()
}

const stopTempoTracking = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.stopTempoTrackingWithCallbacks(onSuccessCallback, onFailCallback)
}

const setCustomEventMetaData = (eventMetaData) => {
    NativeModules.BluedotPointSDK.setCustomEventMetaData(eventMetaData)
}

const setNotificationIdResourceId = (resourceId) => {
    NativeModules.BluedotPointSDK.setNotificationIDResourceID(resourceId)
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

const setZoneDisableByApplication = (zoneId, disable) => {
    NativeModules.BluedotPointSDK.setZoneDisableByApplication(zoneId, disable)
}

const BluedotPointSDK = { 
    on, 
    unsubscribe, 
    setCustomEventMetaData,
    setNotificationIdResourceId,
    getInstallRef,
    // New APIs
    initialize,
    isInitialized,
    reset,
    GeotriggeringBuilder,
    TempoBuilder,
    isGeotriggeringRunning,
    stopGeotriggering,
    isTempoRunning,
    stopTempoTracking,
    getSdkVersion,
    getZonesAndFences,
    setZoneDisableByApplication
}

export default BluedotPointSDK