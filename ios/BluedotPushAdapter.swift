//
//  BluedotPushAdapter.swift
//
//  Created by Natalia Klymenko on 26/2/2026.
//  Copyright © 2026 Bluedot Innovation. All rights reserved.
//

import Foundation
import BDPointSDK

@objc(BluedotPushAdapter)
public final class BluedotPushAdapter: NSObject {

    @objc(notificationEventMapFromPayload:)
    public class func notificationEventMap(from payload: PushPayload) -> NSDictionary {
        return [
            "title": payload.title,
            "body": "",
            "campaignId": payload.campaignId,
            "zoneId": payload.zoneId,
            "notificationId": payload.notificationId
        ] as NSDictionary
    }
}

