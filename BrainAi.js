import { NativeModules, Platform } from 'react-native'

class BrainAi {
    constructor() {}

    BRAIN_EVENT_TEXT_RESPONSE = "brainEventTextResponse";
    BRAIN_EVENT_CONTEXT_RESPONSE = "brainEventContextResponse";
    BRAIN_EVENT_RESPONSE_ID = "brainEventResponseID";
    BRAIN_EVENT_ERROR = "brainEventError";

    errorMessages = {
        10001: "CHAT_NOT_FOUND",
        10002: "FAILED_TO_CREATE_CHAT",
        10003: "FAILED_TO_SUBMIT_FEEDBACK",
    };

    createNewChat = () => {
        if (Platform.OS === "ios") {
            return NativeModules.BluedotPointSDK.iOSCreateNewChat();
        }

        if (Platform.OS === "android") {
            return NativeModules.BrainAiSdk.androidCreateNewChat();
        }
    }

    closeChat = (chatSessionId) => {
        if (Platform.OS === "ios") {
            NativeModules.BluedotPointSDK.iOSCloseChatWithSessionID(chatSessionId);
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.androidCloseChatWithSessionID(chatSessionId);
        }
    }

    getChatSessionIDs = () => {
        if (Platform.OS === "ios") {
            return NativeModules.BluedotPointSDK.iOSGetChatSessionIds();
        }

        if (Platform.OS === "android") {
            return NativeModules.BrainAiSdk.androidGetChatSessionIds();
        }
    }

    sendMessage = (chatSessionId, message) => {
        if (Platform.OS === "ios") {
            NativeModules.BluedotPointSDK.iOSSendMessage(chatSessionId, message);
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.androidSendMessage(chatSessionId, message);
        }
    }

    submitFeedback = (chatSessionId, responseId, liked) => {
        if (Platform.OS === "ios") {
            NativeModules.BluedotPointSDK.iOSSubmitFeedback(chatSessionId, responseId, liked);
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.androidSubmitFeedback(chatSessionId, responseId, liked);
        }
    }
}

module.exports = BrainAi
