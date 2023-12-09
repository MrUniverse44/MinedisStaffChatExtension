package me.blueslime.minedis.extension.staffchat.utils;

import me.blueslime.minedis.extension.authenticator.cache.CodeCache;
import me.blueslime.minedis.extension.staffchat.MStaffChat;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffAuthenticator {

    public static boolean contains(MStaffChat main, ProxiedPlayer player) {
        return main.getCache(CodeCache.class).contains(player.getUniqueId());
    }
}
