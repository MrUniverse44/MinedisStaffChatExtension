package me.blueslime.minedis.extension.listeners.player;

import me.blueslime.minedis.extension.MStaffChat;
import me.blueslime.minedis.extension.cache.StaffCache;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {
    private final MStaffChat main;

    public PlayerQuitListener(MStaffChat main) {
        this.main = main;
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        main.getCache(
            StaffCache.class
        ).remove(
            event.getPlayer().getUniqueId()
        );
    }
}
