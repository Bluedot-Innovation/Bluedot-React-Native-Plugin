#import "BluedotPointSDK.h"
@import BDPointSDK;

@implementation BluedotPointSDK {

    NSDateFormatter  *_dateFormatter;
}

RCT_EXPORT_MODULE()

- (instancetype)init {
    self = [super init];
    if (self) {
        
        // Deprecated
        BDLocationManager.instance.locationDelegate = self;
        
        // New APIs
        BDLocationManager.instance.tempoTrackingDelegate = self;
        BDLocationManager.instance.geoTriggeringEventDelegate = self;

        //  Setup a generic date formatter
        _dateFormatter = [ NSDateFormatter new ];
        [ _dateFormatter setDateFormat: @"dd-MMM-yyyy HH:mm" ];
    }
    return self;
}

RCT_EXPORT_METHOD(initialize:(NSString *)projectId
    initializationSuccessful:(RCTResponseSenderBlock)initializationSuccessfulCallback
    initializationFailed: (RCTResponseSenderBlock)initializationFailedCallback)
{
    [[BDLocationManager instance] initializeWithProjectId: projectId completion:^(NSError * error)
     {
         if (error != nil) {
             initializationFailedCallback(@[error.localizedDescription]);
         } else {
             initializationSuccessfulCallback(@[]);
         }
     }];
}

RCT_REMAP_METHOD(isInitialized,
                 isInitializedResolver: (RCTPromiseResolveBlock)resolve
                 isInitializedRejecter: (RCTPromiseRejectBlock)reject)
{
    BOOL isInitialized = [ BDLocationManager.instance isInitialized ];
    NSNumber *output = [NSNumber numberWithBool: isInitialized ];

    resolve(output);
}

RCT_EXPORT_METHOD(reset:(RCTResponseSenderBlock)resetSuccessfulCallback
    resetFailed:(RCTResponseSenderBlock)resetFailedCallback)
{
    [[BDLocationManager instance] resetWithCompletion:^(NSError * error)
    {
        if (error != nil) {
            resetFailedCallback(@[error.localizedDescription]);
        } else {
            resetSuccessfulCallback(@[]);
        }
    }];
}

RCT_EXPORT_METHOD(iOSStartGeoTriggering:(RCTResponseSenderBlock)startGeoTriggeringSuccessfulCallback
                  startGeoTriggeringFailed:(RCTResponseSenderBlock)startGeoTriggeringFailedCallback)
{
    
    [[BDLocationManager instance] startGeoTriggeringWithCompletion:^(NSError * error)
    {
        if (error != nil) {
            startGeoTriggeringFailedCallback(@[error.localizedDescription]);
        } else {
            startGeoTriggeringSuccessfulCallback(@[]);
        }
    }];
}

RCT_EXPORT_METHOD(iOSStartGeoTriggeringWithAppRestartNotification: (NSString *) notificationTitle
                  notificationButtonText: (NSString *)buttonText
                  startGeoTriggeringTestSuccess: (RCTResponseSenderBlock)startGeoTriggeringTestSuccessfulCallback
                  startGeoTriggeringTestFailed: (RCTResponseSenderBlock)startGeoTriggeringTestFailedCallback)
{
    
    [[BDLocationManager instance] startGeoTriggeringWithAppRestartNotificationTitle:notificationTitle notificationButtonText:buttonText completion:^(NSError * error)
    {
        if (error != nil) {
            startGeoTriggeringTestFailedCallback(@[error.localizedDescription]);
        } else {
            startGeoTriggeringTestSuccessfulCallback(@[]);
        }
    }];
}

RCT_REMAP_METHOD(isGeoTriggeringRunning,
                 isGeoTriggeringRunningWithResolver: (RCTPromiseResolveBlock)resolve
                 isGeoTriggeringRunningRejecter: (RCTPromiseRejectBlock)reject)
{
    BOOL isGeoTriggeringRunning = [ BDLocationManager.instance isGeoTriggeringRunning ];
    NSNumber *output = [NSNumber numberWithBool: isGeoTriggeringRunning ];

    resolve(output);
}

RCT_EXPORT_METHOD(stopGeoTriggering:(RCTResponseSenderBlock)stopGeoTriggeringSuccessfulCallback
    stopGeoTriggeringFailed:(RCTResponseSenderBlock)stopGeoTriggeringFailedCallback)
{
    [[BDLocationManager instance] stopGeoTriggeringWithCompletion:^(NSError * error)
    {
        if (error != nil) {
            stopGeoTriggeringFailedCallback(@[error.localizedDescription]);
        } else {
            stopGeoTriggeringSuccessfulCallback(@[]);
        }
    }];
}

RCT_EXPORT_METHOD(iOSStartTempoTracking: (NSString *) destinationId
                  startTempoSuccess: (RCTResponseSenderBlock) startTempoSuccessCallback
                  startTempoFailed: (RCTResponseSenderBlock) startTempoFailedCallback)
{
    [ BDLocationManager.instance startTempoTrackingWithDestinationId: destinationId completion:^(NSError * error) {
        if (error != nil) {
            startTempoFailedCallback(@[error.localizedDescription]);
        } else {
            startTempoSuccessCallback(@[]);
        }
    }];
}

RCT_REMAP_METHOD(isTempoRunning,
                 isTempoRunningResolver: (RCTPromiseResolveBlock)resolve
                 isTempoRunningRejecter: (RCTPromiseRejectBlock)reject)
{
    BOOL isTempoRunning = [ BDLocationManager.instance isTempoRunning ];
    NSNumber *output = [NSNumber numberWithBool: isTempoRunning ];

    resolve(output);
}

RCT_EXPORT_METHOD(stopTempoTracking: (RCTResponseSenderBlock)stopTempoSuccessCallback
                  stopTempoFailed: (RCTResponseSenderBlock)stopTempoFailedCallback)
{
    NSLog( @"Stop Tempo Tracking");
    [ BDLocationManager.instance stopTempoTrackingWithCompletion:^(NSError * error) {
        if (error != nil) {
            stopTempoFailedCallback(@[error.localizedDescription]);
        } else {
            stopTempoSuccessCallback(@[]);
        }
    }];
}

RCT_EXPORT_METHOD(setCustomEventMetaData: (NSDictionary *) eventMetaData)
{
    [ BDLocationManager.instance setCustomEventMetaData: eventMetaData ];
}

RCT_EXPORT_METHOD(setForegroundNotification: (NSString *) channelId
                  channelName: (NSString *) channelName
                  title: (NSString *) title
                  content: (NSString *) content
                  targetAllAPis: (BOOL) targetAllAPis ) {
    NSLog( @"Note: setForegroundNotification is applicable to Android only");
}

RCT_EXPORT_METHOD(setNotificationIDResourceID: (NSString *) resourceID){
    NSLog( @"Note: setNotificationIDResourceID is applicable to Android only");
}

RCT_EXPORT_METHOD(notifyPushUpdateWithData: (NSDictionary *) data) {
    [[ BDLocationManager instance] notifyPushUpdateWithData:data];
}

/*
*  This method returns a JavaScript Promise. Resolves the installRef from the BDLocationManager and Rejects and error.
*/
RCT_REMAP_METHOD(getInstallRef,
                 getInstallRefWithResolver: (RCTPromiseResolveBlock)resolve
                 rejecter: (RCTPromiseRejectBlock)reject)
{
    NSString *installRef = [ BDLocationManager.instance installRef ];

    if (installRef) {
        resolve(installRef);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_REMAP_METHOD(getSdkVersion,
                 getSdkVersionResolver: (RCTPromiseResolveBlock)resolve
                 getSdkVersionRejecter: (RCTPromiseRejectBlock)reject)
{
    NSString *sdkVersion = [ BDLocationManager.instance sdkVersion ];
    resolve(sdkVersion);
}

RCT_REMAP_METHOD(getZonesAndFences,
                  getZonesAndFencesResolver: (RCTPromiseResolveBlock)resolve
                  getZonesAndFencesRejecter:(RCTPromiseRejectBlock)reject)
{
    NSSet *zoneInfos = [ BDLocationManager.instance zoneInfos ];
    NSMutableArray  *returnZones = [ NSMutableArray new ];
    
    for( BDZoneInfo *zone in zoneInfos )
    {
        [ returnZones addObject: [ self zoneToDict: zone ] ];
    }
    
    resolve(returnZones);
}

RCT_EXPORT_METHOD(setZoneDisableByApplication: (NSString *) zoneId
                   disable:(BOOL)disable)
{
    [[BDLocationManager instance] setZone:zoneId disableByApplication:disable ];
}

RCT_EXPORT_METHOD(allowsBackgroundLocationUpdates: (BOOL) enable)
{
    BDLocationManager.instance.allowsBackgroundLocationUpdates = enable;
}

/*
    ANDROID METHODS
 
    The following list of methods are no-op
    to keep consistency with the Android implementation
 */

RCT_EXPORT_METHOD(androidStartGeoTriggering) {
    // NO-OP Method
}

RCT_EXPORT_METHOD(androidStartTempoTracking) {
    // NO-OP Method
}

/* END of Android Methods */

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"zoneInfoUpdate",
        @"enterZone",
        @"exitZone",
        @"tempoTrackingDidExpire",
        @"tempoTrackingStoppedWithError",
        @"lowPowerModeDidChange",
        @"locationAuthorizationDidChange",
        @"accuracyAuthorizationDidChange"
    ];
}

- (void)didEnterZone: (nonnull BDZoneEntryEvent *) enterEvent {
    NSDictionary *returnFence = [ self fenceToDict: enterEvent.fence ];
    NSDictionary *returnZone = [ self zoneToDict: enterEvent.zone ];
    NSDictionary *returnLocation = [ self locationToDict: enterEvent.location ];

    [self sendEventWithName:@"enterZone" body:@{
        @"fenceInfo" : returnFence,
        @"zoneInfo" : returnZone,
        @"locationInfo" : returnLocation,
        @"isExitEnabled" : [NSNumber numberWithBool: enterEvent.isExitEnabled],
        @"customData" : enterEvent.zone.customData != nil ? enterEvent.zone.customData : [NSNull null]
    }];
}

- (void)didExitZone: (nonnull BDZoneExitEvent *) exitEvent {
    NSDictionary *returnFence = [ self fenceToDict: exitEvent.fence ];
    NSDictionary *returnZone = [ self zoneToDict: exitEvent.zone ];
    NSTimeInterval  unixDate = [ exitEvent.date timeIntervalSince1970 ];
    
    [self sendEventWithName:@"exitZone" body:@{
        @"fenceInfo" : returnFence,
        @"zoneInfo" : returnZone,
        @"date" : @(unixDate),
        @"duration" : @(exitEvent.duration)
    }];
}

- (void)tempoTrackingDidExpire {
    [self sendEventWithName:@"tempoTrackingStoppedWithError" body:@{
        @"error" : @"Tempo tracking has expired"
    }];
}

- (void)didStopTrackingWithError:(NSError *)error {
    [self sendEventWithName:@"tempoTrackingStoppedWithError" body:@{
        @"error" : error.localizedDescription
    }];
}

- (void)lowPowerModeDidChange:(bool)isLowPowerMode {
    [self sendEventWithName:@"lowPowerModeDidChange" body:@{
        @"isLowPowerMode": [NSNumber numberWithBool: isLowPowerMode]
    }];
}

- (void)locationAuthorizationDidChangeFromPreviousStatus:(CLAuthorizationStatus)previousAuthorizationStatus toNewStatus:(CLAuthorizationStatus)newAuthorizationStatus {
    [self sendEventWithName:@"locationAuthorizationDidChange" body:@{
        @"previousAuthorizationStatus": @(previousAuthorizationStatus),
        @"newAuthorizationStatus": @(newAuthorizationStatus)
    }];
}

- (void)accuracyAuthorizationDidChangeFromPreviousAuthorization:(CLAccuracyAuthorization)previousAccuracyAuthorization toNewAuthorization:(CLAccuracyAuthorization)newAccuracyAuthorization {
    [self sendEventWithName:@"accuracyAuthorizationDidChange" body:@{
        @"previousAccuracyAuthorization": @(previousAccuracyAuthorization),
        @"newAccuracyAuthorization": @(newAccuracyAuthorization)
    }];
}

/*
*  This method is passed the Zone information utilised by the Bluedot SDK.
*/
- (void)onZoneInfoUpdate:(NSSet<BDZoneInfo *> *)zoneInfos {
    NSLog( @"Point sdk updated with %lu zones", (unsigned long)zoneInfos.count );
        
    NSMutableArray  *returnZones = [ NSMutableArray new ];

    for( BDZoneInfo *zone in zoneInfos )
    {
        [ returnZones addObject: [ self zoneToDict: zone ] ];
    }
    
    [self sendEventWithName:@"zoneInfoUpdate" body:@{
        @"zoneInfos" : returnZones
    }];
}

/*
 *  Return an NSDictionary with extrapolated zone details
 */
- (NSDictionary *)zoneToDict: (BDZoneInfo *)zone
{
    NSMutableDictionary  *dict = [ NSMutableDictionary new ];

    [ dict setObject:zone.name forKey:@"name"];
    [ dict setObject:zone.ID forKey:@"ID"];
    [ dict setObject:zone.customData forKey:@"customData"];

    return dict;
}

/*
 *  Return a NSDictionary with extrapolated fence details into
 */
- (NSDictionary *)fenceToDict: (BDFenceInfo *)fence
{
    NSMutableDictionary  *dict = [ NSMutableDictionary new ];

    [ dict setObject:fence.name forKey:@"name"];
    [ dict setObject:fence.ID forKey:@"ID"];

    return dict;
}

/*
 *  Return an NSDictionary with extrapolated location details into
 */
- (NSDictionary *)locationToDict: (BDLocationInfo *)location
{
    NSMutableDictionary  *dict = [ NSMutableDictionary new ];
    NSTimeInterval  unixDate = [ location.timestamp timeIntervalSince1970 ];
    
    [ dict setObject:@( unixDate ) forKey:@"unixDate"];
    [ dict setObject:@( location.latitude ) forKey:@"latitude"];
    [ dict setObject:@( location.longitude ) forKey:@"longitude"];
    [ dict setObject:@( location.bearing ) forKey:@"bearing"];
    [ dict setObject:@( location.speed ) forKey:@"speed"];

    return dict;
}

@end
