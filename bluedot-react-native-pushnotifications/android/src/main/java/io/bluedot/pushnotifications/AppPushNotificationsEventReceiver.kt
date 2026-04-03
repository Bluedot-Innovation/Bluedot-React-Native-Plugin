package io.bluedot.pushnotifications

import au.com.bluedot.point.api.push.model.RezolvePushData
import com.rezolve.pushnotifications.PushNotificationsEventReceiver
import com.facebook.react.bridge.WritableNativeMap

class AppPushNotificationsEventReceiver : PushNotificationsEventReceiver() {

    override fun onNotificationReceived(rezolvePushData: RezolvePushData) {
        PushEventUtil.sendEvent(PUSH_NOTIFICATION_RECEIVED, rezolvePushDataToWritableMap(rezolvePushData))
    }

    override fun onNotificationClicked(rezolvePushData: RezolvePushData) {
        PushEventUtil.sendEvent(PUSH_NOTIFICATION_CLICKED, rezolvePushDataToWritableMap(rezolvePushData))
    }

    private fun rezolvePushDataToWritableMap(data: RezolvePushData) = WritableNativeMap().apply {
        putString("title", data.title)
        putString("body", data.body)
        putString("pushVersion", data.pushVersion)
        putString("campaignId", data.campaignId)
        putString("zoneId", data.zoneId)
        putString("notificationId", data.notificationId)
        val customData = WritableNativeMap()
        data.data?.forEach { (k, v) -> customData.putString(k, v) }
        putMap("data", customData)
    }

    companion object {
        const val PUSH_NOTIFICATION_RECEIVED = "pushNotificationReceived"
        const val PUSH_NOTIFICATION_CLICKED = "pushNotificationClicked"
    }
}
