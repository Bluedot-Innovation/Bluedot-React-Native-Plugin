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

const setZoneDisableByApplication = (zoneId, disable) => {
    NativeModules.BluedotPointSDK.setZoneDisableByApplication(zoneId, disable)
}

const backgroundLocationAccessForWhileUsing = (enable) => {
    NativeModules.BluedotPointSDK.backgroundLocationAccessForWhileUsing(enable)
}

/**
 * Android only. Links the foreground service that the host app has already started.
 *
 * Call this after your app's own foreground service has successfully called
 * startForeground(). This is required before starting Tempo (SDK 19.0.0+) and
 * optional (but recommended for high-accuracy mode) before starting GeoTriggering.
 *
 * Returns a Promise that resolves when the link succeeds or rejects on error.
 */
const linkForegroundService = () => {
    return NativeModules.BluedotPointSDK.linkForegroundService()
}

/**
 * Android only. Unlinks the foreground service previously linked via linkForegroundService().
 *
 * Call this when your foreground service is stopping. If Tempo is running when this is
 * called, Tempo will stop and emit a tempoStoppedWithError event with
 * isForegroundServiceNotLinked: true.
 *
 * Returns a Promise.
 */
const unlinkForegroundService = () => {
    return NativeModules.BluedotPointSDK.unlinkForegroundService()
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
    linkForegroundService,
    unlinkForegroundService,
    getSdkVersion,
    getZonesAndFences,
    setZoneDisableByApplication,
    backgroundLocationAccessForWhileUsing
}

export default BluedotPointSDK
