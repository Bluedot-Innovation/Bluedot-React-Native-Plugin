package io.bluedot;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import au.com.bluedot.point.net.engine.Chat;
import au.com.bluedot.point.net.engine.ServiceManager;

public class RezolveBrainAiSdkModule extends ReactContextBaseJavaModule {

    private final au.com.bluedot.point.net.engine.j brainAI;

    public RezolveBrainAiSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.brainAI = ServiceManager.getInstance(reactContext).getBrainAI();
    }

    @NonNull
    @Override
    public String getName() {
        return "RezolveBrainAiSdk";
    }

    @ReactMethod
    public void androidCreateNewChat(Callback onSuccess, Callback onError) {
        Log.d("rzlv", "androidCreateNewChat");
        Chat chat = brainAI.createNewChat();
        if (chat == null) {
            onError.invoke("Failed to create chat");
        } else {
            Log.d("rzlv", "androidCreateNewChat.success: " + chat.getSessionID());
            onSuccess.invoke(chat.getSessionID());
        }
    }

    // @ReactMethod
    // public Chat androidGetChatWithSessionID(String sessionId) {
    //     return brainAI.getChatWithSessionID(sessionId);
    // }

    // @ReactMethod
    // public void androidCloseChatWithSessionID(String sessionId) {
    //     brainAI.closeChatWithSessionID(sessionId);
    // }

    // @ReactMethod
    // public void androidAddChat(Chat chat) {
    //     brainAI.addChat(chat);
    // }

    // @ReactMethod
    // public List<Chat> androidGetChats() {
    //     return brainAI.getChats();
    // }

    @ReactMethod
    public void testLog(String text) {
        Log.d("rzlv", text);
    }
}
