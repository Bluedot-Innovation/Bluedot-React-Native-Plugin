import { NativeModules, Platform } from 'react-native'

class BrainAi {
    constructor() {}

    BRAIN_EVENT_TEXT_RESPONSE = "brainEventTextResponse";
    BRAIN_EVENT_CONTEXT_RESPONSE = "brainEventContextResponse";
    BRAIN_EVENT_RESPONSE_ID = "brainEventResponseID";
    BRAIN_EVENT_ERROR = "brainEventError";

    createNewChat = () => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            return NativeModules.BrainAiSdk.androidCreateNewChat();
        }
    }

    closeChat = (chatSessionId) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.androidCloseChatWithSessionID(chatSessionId);
        }
    }

    getChatSessionIDs = () => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            return NativeModules.BrainAiSdk.androidGetChatSessionIds();
        }
    }

    sendMessage = (chatSessionId, message) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.androidSendMessage(chatSessionId, message);
        }
    }

    submitFeedback = (chatSessionId, responseId, liked) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.androidSubmitFeedback(chatSessionId, responseId, liked);
        }
    }
}

module.exports = BrainAi
