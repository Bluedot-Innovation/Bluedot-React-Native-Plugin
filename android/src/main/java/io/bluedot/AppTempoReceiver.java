package io.bluedot;

import static io.bluedot.EventUtil.sendEvent;

import android.content.Context;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.TempoTrackingReceiver;
import au.com.bluedot.point.net.engine.event.TempoTrackingUpdate;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class AppTempoReceiver extends TempoTrackingReceiver {

    @Override
    public void onTempoTrackingUpdate(@NotNull TempoTrackingUpdate tempoTrackingUpdate,
                                      @NotNull Context context) {
        JSONObject jsonObject = null;
        WritableMap tempoUpdate = null;
        try {
            jsonObject = new JSONObject(tempoTrackingUpdate.toJson());
            Map<String, Object> mapEvent = MapUtil.toMap(jsonObject);
            tempoUpdate = MapUtil.toWritableMap(mapEvent);
            sendEvent(context, "tempoTrackingDidUpdate", tempoUpdate);
        } catch (JSONException exp) {
            System.out.println("Exception occurred during conversion of ExitEvent" + exp);
        }
    }

    /**
     * Called when there is an error that has caused Tempo to stop.
     *
     * @param bdError: can be a [TempoInvalidDestinationIdError][au.com.bluedot.point.TempoInvalidDestinationIdError]
     * or a [BDTempoError][au.com.bluedot.point.BDTempoError]
     * @param context: Android context
     * @since 15.3.0
     */
    @Override
    public void tempoStoppedWithError(@NotNull BDError bdError, @NotNull Context context) {
        WritableMap error = new WritableNativeMap();
        error.putString("error",bdError.getReason());
        sendEvent(context, "tempoStoppedWithError", error);
    }
}
