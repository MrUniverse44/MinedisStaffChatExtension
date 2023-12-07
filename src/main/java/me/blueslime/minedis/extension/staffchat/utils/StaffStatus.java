package me.blueslime.minedis.extension.staffchat.utils;

public enum StaffStatus {
    DISPLAY_WRITE_CHAT,
    DISPLAY_CHAT,
    DISABLED;

    public static boolean isDisplay(StaffStatus value) {
        switch (value) {
            case DISABLED:
                return false;
            default:
            case DISPLAY_WRITE_CHAT:
            case DISPLAY_CHAT:
                return true;
        }
    }
}
