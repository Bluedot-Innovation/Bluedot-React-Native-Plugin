package io.bluedot

import android.util.Log
import au.com.bluedot.point.net.engine.BDStreamingResponseDtoContext
import au.com.bluedot.point.net.engine.Chat
import au.com.bluedot.point.net.engine.ServiceManager
import au.com.bluedot.point.net.engine.StreamType
import au.com.bluedot.point.net.engine.j
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import io.bluedot.EventUtil.Companion.sendEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrainAiSdkModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val brainAi: j? = ServiceManager.getInstance(reactContext).brainAI

    override fun getName(): String {
        return "BrainAiSdk"
    }

    @ReactMethod
    fun androidCreateNewChat(promise: Promise) {
        Log.d("rzlv", "androidCreateNewChat")
        validateBrainAi()
        val chat = brainAi?.createNewChat()
        if (chat == null) {
            promise.reject(BrainError.FAILED_TO_CREATE_CHAT.value, "Chat is null")
        } else {
            Log.d("rzlv", "androidCreateNewChat.success: " + chat.sessionID)
            promise.resolve(chat.sessionID)
        }
    }

    @ReactMethod
    fun androidCloseChatWithSessionID(sessionId: String) {
        Log.d("rzlv", "androidCloseChatWithSessionID: $sessionId")
        validateBrainAi()
        brainAi?.closeChatWithSessionID(sessionId)
    }

    @ReactMethod
    fun androidGetChatSessionIds(promise: Promise) {
        Log.d("rzlv", "androidGetChatSessionIds")
        validateBrainAi()
        val sessionIds: WritableArray = WritableNativeArray()
        brainAi?.chats?.forEach {
            sessionIds.pushString(it.sessionID)
        }
        promise.resolve(sessionIds)
    }

    @ReactMethod
    fun androidSendMessage(sessionId: String, message: String) {
        Log.d("rzlv", "androidSendMessage: $sessionId: $message")
        CoroutineScope(Dispatchers.IO).launch {
            validateBrainAi()
            val chat = brainAi?.getChatWithSessionID(sessionId)
            if (chat == null) {
                val map = WritableNativeMap().apply {
                    putString(BRAIN_EVENT_ERROR, BrainError.CHAT_NOT_FOUND.value)
                    putInt(BRAIN_EVENT_ERROR_CODE, BrainError.CHAT_NOT_FOUND.code)
                }
                sendEvent(reactContext, "$BRAIN_EVENT_ERROR$sessionId", map)
            } else {
                chat.sendMessage(message).forEach { res ->
                    if (res.stream_type == StreamType.RESPONSE_TEXT) {
                        Log.d("rzlv", "Response RESPONSE_TEXT: ${res.response}")
                        val map = WritableNativeMap().apply {
                            putString(BRAIN_EVENT_TEXT_RESPONSE, res.response)
                            putString(BRAIN_EVENT_RESPONSE_ID, res.response_id)
                        }
                        sendEvent(reactContext, "$BRAIN_EVENT_TEXT_RESPONSE$sessionId", map)
                    }

                    if (res.stream_type == StreamType.CONTEXT) {
                        Log.d("rzlv", "Response CONTEXT: ${res.contexts.size}")
                        if (res.contexts.isNotEmpty()) {
                            val array = WritableNativeArray().apply {
                                for (item in res.contexts) {
                                    pushMap(item.toWritableMap())
                                }
                            }
                            val map = WritableNativeMap().apply {
                                putArray(BRAIN_EVENT_CONTEXT_RESPONSE, array)
                                putString(BRAIN_EVENT_RESPONSE_ID, res.response_id)
                            }
                            sendEvent(reactContext, "$BRAIN_EVENT_CONTEXT_RESPONSE$sessionId", map)
                        }
                    }
                    if (res.stream_type == StreamType.RESPONSE_IDENTIFIER) {
                        Log.d("rzlv", "RESPONSE_IDENTIFIER: ${res.response_id}")
                        val map = WritableNativeMap().apply {
                            putString(BRAIN_EVENT_RESPONSE_ID, res.response_id)
                        }
                        sendEvent(reactContext, "$BRAIN_EVENT_RESPONSE_ID$sessionId", map)
                        return@forEach
                    }
                }
            }
        }
    }

    @ReactMethod
    fun androidSubmitFeedback(sessionId: String, responseId: String, liked: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            validateBrainAi()
            val chat = brainAi?.getChatWithSessionID(sessionId)
            val feedback = if (liked) Chat.ChatFeedback.LIKED else Chat.ChatFeedback.DISLIKED
            if (chat == null) {
                val map = WritableNativeMap().apply {
                    putString(BRAIN_EVENT_ERROR, BrainError.CHAT_NOT_FOUND.value)
                    putInt(BRAIN_EVENT_ERROR_CODE, BrainError.CHAT_NOT_FOUND.code)
                }
                sendEvent(reactContext, "$BRAIN_EVENT_ERROR$sessionId", map)
            } else {
                chat.submitFeedback(responseId, feedback)?.let { error ->
                    val map = WritableNativeMap().apply {
                        putString(BRAIN_EVENT_ERROR, error.reason)
                        putInt(BRAIN_EVENT_ERROR_CODE, BrainError.FAILED_TO_SUBMIT_FEEDBACK.code)
                    }
                    sendEvent(reactContext, "$BRAIN_EVENT_ERROR$sessionId", map)
                }
            }
        }
    }

    private fun validateBrainAi() {
        if (brainAi == null) {
            throw IllegalStateException("SDK not initialized.")
        }
    }

    private fun BDStreamingResponseDtoContext.toWritableMap() : WritableMap {
        val map = WritableNativeMap().apply {
            title?.let { putString("title", it) }
            price?.let { putDouble("price", it) }
            description?.let { putString("description", it) }
            merchant_id?.let { putInt("merchant_id", it) }
            category_id?.let { putInt("category_id", it) }
            product_id?.let { putInt("product_id", it) }
            image_links?.let {
                val imageArray = WritableNativeArray()
                for (item in it) {
                    imageArray.pushString(item)
                }
                putArray("image_links", imageArray)
            }
        }

        return map
    }

    enum class BrainError(val value: String, val code: Int) {
        CHAT_NOT_FOUND("Chat not found", 10001),
        FAILED_TO_CREATE_CHAT("Failed to create chat", 10002),
        FAILED_TO_SUBMIT_FEEDBACK("Failed to submit feedback", 10003)
    }

    companion object {
        // Events are also defined in the plugin for easier integration on the client app.
        const val BRAIN_EVENT_TEXT_RESPONSE = "brainEventTextResponse"
        const val BRAIN_EVENT_CONTEXT_RESPONSE = "brainEventContextResponse"
        const val BRAIN_EVENT_ERROR = "brainEventError"
        const val BRAIN_EVENT_ERROR_CODE = "brainEventErrorCode"
        const val BRAIN_EVENT_RESPONSE_ID = "brainEventResponseID"
    }
}
