package com.baas.securities.enums;

public enum CustomStompCommand {
    CONNECT, SEND, SUBSCRIBE, UNSUBSCRIBE, DISCONNECT;

    public static boolean isCloseCommand(String command) {
        for (CustomStompCommand val : CustomStompCommand.values()) {
            if (val.name().equals(command)) {
                return true;
            }
        }
        return false;
    }
}
