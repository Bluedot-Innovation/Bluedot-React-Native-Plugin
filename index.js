import { NativeModules, NativeEventEmitter } from 'react-native';
import GeoTriggeringBuilder from './GeoTriggeringBuilder'
import TempoBuilder from './TempoBuilder'

const eventEmitter = new NativeEventEmitter(NativeModules.BluedotPointSDK)
const subscriptionsList = new Set(); // Set of strings - eventNames

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

const getCustomEventMetaData = () => {
    return NativeModules.BluedotPointSDK.getCustomEventMetaData()
}

const setNotificationIdResourceId = (resourceId) => {
    NativeModules.BluedotPointSDK.setNotificationIDResourceID(resourceId)
}

const setZoneDisableByApplication = (zoneId, disable) => {
    NativeModules.BluedotPointSDK.setZoneDisableByApplication(zoneId, disable)
}

const backgroundLocationAccessForWhileUsing = (enable) => {
    NativeModules.BluedotPointSDK.backgroundLocationAccessForWhileUsing(enable)
}

const on = (eventName, callback) => {
    eventEmitter.addListener(eventName, callback)
    subscriptionsList.add(eventName)
}

const unsubscribe = (eventName) => {
    eventEmitter.removeAllListeners(eventName)
    subscriptionsList.delete(eventName)
}

const unsubscribeAll = () => {
    subscriptionsList.forEach(eventName => eventEmitter.removeAllListeners(eventName))
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
    getCustomEventMetaData,
    setNotificationIdResourceId,
    getInstallRef,
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
    backgroundLocationAccessForWhileUsing
}

export default BluedotPointSDK