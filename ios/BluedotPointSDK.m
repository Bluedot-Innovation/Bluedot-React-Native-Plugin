#import "BluedotPointSDK.h"
@import BDPointSDK;

@implementation BluedotPointSDK {

    NSDateFormatter  *_dateFormatter;
}

RCT_EXPORT_MODULE()

- (instancetype)init {
    self = [super init];
    if (self) {

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
  dispatch_async(dispatch_get_main_queue(),^(void) {
    [[BDLocationManager instance] startGeoTriggeringWithAppRestartNotificationTitle:notificationTitle notificationButtonText:buttonText completion:^(NSError * error)
     {
        if (error != nil) {
            startGeoTriggeringTestFailedCallback(@[error.localizedDescription]);
        } else {
            startGeoTriggeringTestSuccessfulCallback(@[]);
        }
    }];
  });
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

RCT_REMAP_METHOD(getCustomEventMetaData,
                 getCustomEventMetaDataResolver: (RCTPromiseResolveBlock)resolve
                 getCustomEventMetaDataRejecter: (RCTPromiseRejectBlock)reject)
{
    resolve([BDLocationManager.instance customEventMetaData]);
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
    #warning Update to match new payload in SDK 16 ( + Android)
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

RCT_EXPORT_METHOD(backgroundLocationAccessForWhileUsing: (BOOL) enable)
{
    BDLocationManager.instance.backgroundLocationAccessForWhileUsing = enable;
}

// Brain AI

#define BRAIN_EVENT_TEXT_RESPONSE       @"brainEventTextResponse"
#define BRAIN_EVENT_CONTEXT_RESPONSE    @"brainEventContextResponse"
#define BRAIN_EVENT_ERROR               @"brainEventError"
#define BRAIN_EVENT_ERROR_CODE          @"brainEventErrorCode"
#define BRAIN_EVENT_RESPONSE_ID         @"brainEventResponseID"

#define BRAIN_ERROR_CHAT_NOT_FOUND      @"Chat not found"
#define BRAIN_ERROR_CREATE_CHAT         @"Failed to create chat"
#define BRAIN_ERROR_SUBMIT_FEEDBACK     @"Failed to submit feedback"

RCT_REMAP_METHOD(iOSCreateNewChat,
                 isNewChatResolved:(RCTPromiseResolveBlock)resolve
                 isNewChatRejected:(RCTPromiseRejectBlock)reject)
{
    RCTLogInfo(@"rzlv iOSCreateNewChat");
    Chat *chat = [[BDLocationManager.instance brainAI] createNewChat];

    if (chat == nil) {
        reject(BRAIN_ERROR_CREATE_CHAT, @"Chat is null", nil);
    } else {
        RCTLogInfo(@"rzlv iOSCreateNewChat.success: %@", chat.sessionID);
        resolve(chat.sessionID);
    }
}

RCT_EXPORT_METHOD(iOSCloseChatWithSessionID:(NSString *)sessionId)
{
    RCTLogInfo(@"rzlv iOSCloseChatWithSessionID: %@", sessionId);
    [[BDLocationManager.instance brainAI] closeChatWithSessionID:sessionId];
}

RCT_REMAP_METHOD(iOSGetChatSessionIds,
                 isGetChatResolved:(RCTPromiseResolveBlock)resolve
                 isGetChatRejected:(RCTPromiseRejectBlock)reject)
{
//    RCTLogInfo(@"rzlv: androidGetChatSessionIds");
//    NSMutableArray *sessionIds = [NSMutableArray new];
//    for (id chat in [[BDLocationManager.instance brainAI] chats]) {
//        [sessionIds addObject:chat.sessionID];
//    }
//    resolve(sessionIds);
}

RCT_EXPORT_METHOD(iOSSendMessage:(NSString *)sessionId message:(NSString *)message)
{
    RCTLogInfo(@"rzlv iOSSendMessage: %@: %@", sessionId, message);
    Chat *chat = [[BDLocationManager.instance brainAI] getChatWithSessionID:sessionId];

    if (chat == nil) {
        NSDictionary *map = @{BRAIN_EVENT_ERROR: BRAIN_ERROR_CHAT_NOT_FOUND};
        [self sendEventWithName:[NSString stringWithFormat:@"%@%@", BRAIN_EVENT_ERROR, sessionId] body:map];
    }

    [chat sendMessage:message onUpdate:^(StreamingResponseDto *res) {
        if (res.streamType == 1) { // CONTEXT
            RCTLogInfo(@"rzlv CONTEXT: %lu", res.contexts.count);
            if (res.contexts.count > 0) {
                NSMutableDictionary *map = [NSMutableDictionary new];
                map[BRAIN_EVENT_CONTEXT_RESPONSE] = res.contexts;
                map[BRAIN_EVENT_RESPONSE_ID] = res.responseID;
                [self sendEventWithName:[NSString stringWithFormat:@"%@%@", BRAIN_EVENT_CONTEXT_RESPONSE, sessionId] body:map];
            }
        }

        if (res.streamType == 2) { // RESPONSE_TEXT
            RCTLogInfo(@"rzlv RESPONSE_TEXT: %@", res.response);
            NSDictionary *map = @{BRAIN_EVENT_TEXT_RESPONSE: res.response};
            [self sendEventWithName:[NSString stringWithFormat:@"%@%@", BRAIN_EVENT_TEXT_RESPONSE, sessionId] body:map];
        }

        if (res.streamType == 3) { // RESPONSE_IDENTIFIER
            RCTLogInfo(@"rzlv RESPONSE_IDENTIFIER: %@", res.responseID);
            NSDictionary *map = @{BRAIN_EVENT_RESPONSE_ID: res.responseID};
            [self sendEventWithName:[NSString stringWithFormat:@"%@%@", BRAIN_EVENT_RESPONSE_ID, sessionId] body:map];
        }
    } onCompletion:^{
        RCTLogInfo(@"rzlv iOSSendMessage Completed");
    } onError:^(NSError *error) {
        RCTLogInfo(@"rzlv iOSSendMessage Error: %@", error ? error.localizedDescription : @"No data");
    }];
}

RCT_EXPORT_METHOD(iOSSubmitFeedback:(NSString *)sessionId responseId:(NSString *)responseId liked:(bool)liked)
{
    RCTLogInfo(@"rzlv iOSSubmitFeedback: %@: %@: %d", sessionId, responseId, liked);
    Chat *chat = [[BDLocationManager.instance brainAI] getChatWithSessionID:sessionId];
    Reaction feedback = liked ? ReactionLiked : ReactionDisliked;
    
    if (chat == nil) {
        NSDictionary *map = @{BRAIN_EVENT_ERROR: BRAIN_ERROR_CHAT_NOT_FOUND};
        [self sendEventWithName:[NSString stringWithFormat:@"%@%@", BRAIN_EVENT_ERROR, sessionId] body:map];
    } else {
        [chat submitFeedbackWithResponseID:responseId reaction:feedback completion:^(BOOL liked, NSError *error) {
            NSDictionary *map = @{BRAIN_EVENT_ERROR: BRAIN_ERROR_SUBMIT_FEEDBACK};
            [self sendEventWithName:[NSString stringWithFormat:@"%@%@", BRAIN_EVENT_ERROR, sessionId] body:map];
        }];
    }
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
        @"tempoTrackingDidUpdate",
        @"tempoTrackingStoppedWithError",
        @"lowPowerModeDidChange",
        @"locationAuthorizationDidChange",
        @"accuracyAuthorizationDidChange"
    ];
}

/*
 *  This method is passed the Zone information utilised by the Bluedot SDK.
 */
- (void)didUpdateZoneInfo {
    [self sendEventWithName:@"zoneInfoUpdate" body:nil];
}

- (void)didEnterZone:(nonnull GeoTriggerEvent *)enterEvent {
    NSDictionary *enterEventMap = @{};
    @try {
        NSData *data = [[enterEvent toJson:nil] dataUsingEncoding:NSUTF8StringEncoding];
        enterEventMap = [NSJSONSerialization JSONObjectWithData:data
                                                        options:0
                                                          error:nil];
    } @catch (NSException *exception) {

    } @finally {
        [self sendEventWithName:@"enterZone" body:enterEventMap];
    }
}

- (void)didExitZone: (nonnull GeoTriggerEvent *)exitEvent {
    NSDictionary *exitEventMap = @{};
    @try {
        NSData *data = [[exitEvent toJson:nil] dataUsingEncoding:NSUTF8StringEncoding];
        exitEventMap = [NSJSONSerialization JSONObjectWithData:data
                                                        options:0
                                                          error:nil];
    } @catch (NSException *exception) {

    } @finally {
        [self sendEventWithName:@"exitZone" body:exitEventMap];
    }
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

- (void)tempoTrackingDidUpdate:(TempoTrackingUpdate *)tempoUpdate {
    NSDictionary *tempoUpdateMap = @{};
    @try {
        NSData *data = [[tempoUpdate toJson:nil] dataUsingEncoding:NSUTF8StringEncoding];
        tempoUpdateMap = [NSJSONSerialization JSONObjectWithData:data
                                                       options:0
                                                         error:nil];
    } @catch (NSException *exception) {

    } @finally {
        [self sendEventWithName:@"tempoTrackingDidUpdate" body:tempoUpdateMap];
    }
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
