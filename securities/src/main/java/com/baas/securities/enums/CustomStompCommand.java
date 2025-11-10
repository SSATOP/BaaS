package com.baas.securities.enums;

public enum CustomStompCommand {
    CONNECT, SEND, SUBSCRIBE, UNSUBSCRIBE, DISCONNECT;

    public static boolean isCloseCommand(String command) {
        return command.equals(UNSUBSCRIBE.name()) || command.equals(DISCONNECT.name());
    }

    public static boolean isConnectCommand(String command) {
        return command.equals(CONNECT.name());
    }

    public static boolean isSendOrSubscribe(String command) {
        return command.equals(SUBSCRIBE.name()) || command.equals(SEND.name());
    }
}