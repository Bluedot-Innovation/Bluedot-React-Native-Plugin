package io.bluedot;

import static io.bluedot.EventUtil.sendEvent;

import android.content.Context;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.BluedotServiceReceiver;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import org.jetbrains.annotations.NotNull;

public class BluedotErrorReceiver extends BluedotServiceReceiver {

    /**
     * Called when the Bluedot Point SDK encounters errors. If the error is fatal, the SDK services
     * may need to be restarted after the cause of the error has been addressed.
     *
     * @param[bdError] The error, please see [documentation](https://docs.bluedot.io/android-sdk/android-error-handling/)
     * for possible subtypes and appropriate corrective actions.
     * @param[context] Android context.
     * @since 15.3.0
     */
    @Override
    public void onBluedotServiceError(@NotNull BDError bdError, @NotNull Context context) {
        WritableMap error = new WritableNativeMap();
        error.putString("error",bdError.getReason());
        sendEvent(context, "onBluedotServiceError",error);
    }
}
