import { NativeModules, Platform } from 'react-native'

class BrainAi {
    constructor() {}

    testLog = (onSuccess, onError) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.RezolveBrainAiSdk.testLog("hejka");
        }
    }

    createNewChat = (onSuccess, onError) => {
        if (Platform.OS === "ios") {
            
        }

        if (Platform.OS === "android") {
            NativeModules.RezolveBrainAiSdk.testLog("create new chat");
            NativeModules.RezolveBrainAiSdk.androidCreateNewChat(onSuccess, onError);
        }
    }
}

module.exports = BrainAi