import { NativeModules, NativeEventEmitter } from 'react-native';
import GeoTriggeringBuilder from './GeoTriggeringBuilder'
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
    GeoTriggeringBuilder,
    TempoBuilder,
    isGeoTriggeringRunning,
    stopGeoTriggering,
    isTempoRunning,
    stopTempoTracking,
    getSdkVersion,
    getZonesAndFences,
    setZoneDisableByApplication
}

export default BluedotPointSDK