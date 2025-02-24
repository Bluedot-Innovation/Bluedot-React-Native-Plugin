import { NativeModules, Platform } from 'react-native'

class BrainAi {
    constructor() {}

    BRAIN_EVENT_TEXT_RESPONSE = "brainEventTextResponse";
    BRAIN_EVENT_CONTEXT_RESPONSE = "brainEventContextResponse";
    BRAIN_EVENT_IDENTIFIER_RESPONSE = "brainEventIdentifierResponse";
    BRAIN_EVENT_ERROR = "brainEventError";

    testLog = (onSuccess, onError) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.testLog("hejka");
        }
    }

    createNewChat = () => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.testLog("BrainAiSdkModule: create new chat");
            return NativeModules.BrainAiSdk.androidCreateNewChat();
        }
    }

    sendMessage = (chatSessionId, message) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.BrainAiSdk.testLog("BrainAiSdkModule: sendMessage: "+message);
            return NativeModules.BrainAiSdk.androidSendMessage(chatSessionId, message);
        }
    }
}

module.exports = BrainAi
