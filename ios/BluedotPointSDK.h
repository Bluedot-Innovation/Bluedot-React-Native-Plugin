#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@import BDPointSDK;

@interface BluedotPointSDK : RCTEventEmitter <RCTBridgeModule, BDPLocationDelegate, BDPGeoTriggeringEventDelegate, BDPTempoTrackingDelegate, BDPBluedotServiceDelegate>

@end
