import { NativeModules, NativeEventEmitter } from 'react-native';
import GeoTriggeringBuilder from './GeoTriggeringBuilder'
import TempoBuilder from './TempoBuilder'

const eventEmitter = new NativeEventEmitter(NativeModules.BluedotPointSDK)
const subscriptionsList = new Set();

const initialize = (projectId, onSucessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.initialize(projectId, onSucessCallback, onFailCallback)
}

const isInitialized = () => {
    return NativeModules.BluedotPointSDK.isInitialized()
}

const reset = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.reset(onSuccessCallback, onFailCallback)
}

const isGeoTriggeringRunning = () => {
    return NativeModules.BluedotPointSDK.isGeoTriggeringRunning()
}

const stopGeoTriggering = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.stopGeoTriggering(onSuccessCallback, onFailCallback)
}

const isTempoRunning = () => {
    return NativeModules.BluedotPointSDK.isTempoRunning()
}

const stopTempoTracking = (onSuccessCallback, onFailCallback) => {
    NativeModules.BluedotPointSDK.stopTempoTracking(onSuccessCallback, onFailCallback)
}

const setCustomEventMetaData = (eventMetaData) => {
    NativeModules.BluedotPointSDK.setCustomEventMetaData(eventMetaData)
}

const setNotificationIdResourceId = (resourceId) => {
    NativeModules.BluedotPointSDK.setNotificationIDResourceID(resourceId)
}

const setZoneDisableByApplication = (zoneId, disable) => {
    NativeModules.BluedotPointSDK.setZoneDisableByApplication(zoneId, disable)
}

const allowBackgroundLocationUpdates = (enable) => {
    NativeModules.BluedotPointSDK.allowBackgroundLocationUpdates(enable)
}

const on = (eventName, callback) => {
    eventEmitter.addListener(eventName, callback)
}

const unsubscribe = (eventName) => {
    eventEmitter.removeAllListeners(eventName)
    subscriptionsList.delete(eventName)
}

const unsubscribeAll = () => {
    subscriptionsList.forEach(event => eventEmitter.removeAllListeners(event))
    subscriptionsList.clear()
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

const BluedotPointSDK = { 
    on, 
    unsubscribe,
    unsubscribeAll,
    setCustomEventMetaData,
    setNotificationIdResourceId,
    getInstallRef,
    // New APIs
    initialize,
    isInitialized,
    reset,
    GeoTriggeringBuilder,
    TempoBuilder,
    isGeoTriggeringRunning,
    stopGeoTriggering,
    isTempoRunning,
    stopTempoTracking,
    getSdkVersion,
    getZonesAndFences,
    setZoneDisableByApplication,
    allowBackgroundLocationUpdates
}

export default BluedotPointSDK