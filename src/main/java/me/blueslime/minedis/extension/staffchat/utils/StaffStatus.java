package me.blueslime.minedis.extension.staffchat.utils;

public enum StaffStatus {
    DISPLAY_WRITE_CHAT,
    DISPLAY_CHAT,
    DISABLED;

    public static boolean isDisplay(String value) {
        switch (value) {
            case "disabled":
                return false;
            default:
            case "display-write-chat":
            case "display-chat":
                return true;
        }
    }
}
