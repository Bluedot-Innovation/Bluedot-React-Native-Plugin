package io.bluedot

import android.util.Log
import au.com.bluedot.point.net.engine.BDStreamingResponseDtoContext
import au.com.bluedot.point.net.engine.ServiceManager
import au.com.bluedot.point.net.engine.StreamType
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableNativeArray
import io.bluedot.EventUtil.Companion.sendEvent
import org.json.simple.JSONObject

class BrainAiSdkModule(val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val brainAI = ServiceManager.getInstance(reactContext).brainAI

    override fun getName(): String {
        return "BrainAiSdk"
    }

    @ReactMethod
    fun androidCreateNewChat(promise: Promise) {
        Log.d("rzlv", "androidCreateNewChat")
        val chat = brainAI.createNewChat()
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
        brainAI.closeChatWithSessionID(sessionId)
    }

    @ReactMethod
    fun androidGetChatSessionIds(promise: Promise) {
        val sessionIds: WritableArray = WritableNativeArray()
        brainAI.chats.forEach {
            sessionIds.pushString(it.sessionID)
        }
        promise.resolve(sessionIds)
    }

    @ReactMethod
    fun androidSendMessage(sessionId: String, message: String) {

        val chat = brainAI.getChatWithSessionID(sessionId)
        if (chat == null) {
            val mapEvent: Map<String, String> = mapOf(
                Pair(BRAIN_EVENT_ERROR, BrainError.CHAT_NOT_FOUND.value)
            )
            sendEvent(reactContext, BRAIN_EVENT_ERROR, MapUtil.toWritableMap(mapEvent))
        } else {
            chat.sendMessage(message).forEach { res ->
                if (res.stream_type == StreamType.RESPONSE_TEXT) {
                    Log.d("rzlv", "Response RESPONSE_TEXT: ${res.response}")
                    val mapEvent: Map<String, String> = mapOf(
                        Pair(BRAIN_EVENT_TEXT_RESPONSE, res.response)
                    )
                    sendEvent(reactContext, BRAIN_EVENT_TEXT_RESPONSE, MapUtil.toWritableMap(mapEvent))
                }

                if (res.stream_type == StreamType.CONTEXT) {
                    Log.d("rzlv", "Response CONTEXT: ${res.contexts.size}")
                    if (res.contexts.isNotEmpty()) {

                        // TODO
//                        sendEvent(reactContext, BRAIN_EVENT_CONTEXT_RESPONSE, MapUtil.toWritableMap(mapEvent))
                    }
                }
                if (res.stream_type == StreamType.RESPONSE_IDENTIFIER) {
                    val responseId = res.response_id
                    // TODO
                    Log.d("rzlv", "RESPONSE_IDENTIFIER: ${responseId}")
                    return@forEach
                }
            }
        }
    }

    @ReactMethod
    fun testLog(text: String) {
        Log.d("rzlv", text)
    }

    private fun BDStreamingResponseDtoContext.toMap() : Map<String, Any> {
        val mappedContext = mutableMapOf<String, Any>()
        title?.let { mappedContext["title"] = it }
        price?.let { mappedContext["price"] = it }
        description?.let { mappedContext["description"] = it }
        merchant_id?.let { mappedContext["merchantId"] = it }
        category_id?.let { mappedContext["categoryId"] = it }
        product_id?.let { mappedContext["productId"] = it }
        image_links?.let { mappedContext["imageLinks"] = it }
        return mappedContext
    }

    enum class BrainError(val value: String) {
        FAILED_TO_CREATE_CHAT("Failed to create chat"),
        CHAT_NOT_FOUND("Chat not found"),
    }

    companion object {
        // Events are also defined in the plugin for easier integration on the client app.
        const val BRAIN_EVENT_TEXT_RESPONSE = "brainEventTextResponse"
        const val BRAIN_EVENT_CONTEXT_RESPONSE = "brainEventContextResponse"
        const val BRAIN_EVENT_IDENTIFIER_RESPONSE = "brainEventIdentifierResponse"
        const val BRAIN_EVENT_ERROR = "brainEventError"

        const val BRAIN_EVENT_CONTEXT_IMAGE = "brainEventContextImage"
    }
}
