#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTLog.h>

@import BDPointSDK;

@interface BluedotPointSDK : RCTEventEmitter <RCTBridgeModule, BDPGeoTriggeringEventDelegate, BDPTempoTrackingDelegate, BDPBluedotServiceDelegate>

@end
