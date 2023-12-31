package me.blueslime.minedis.extension.staffchat.listeners.player;

import me.blueslime.minedis.extension.staffchat.MStaffChat;
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
            "msc-cache"
        ).remove(
            event.getPlayer().getUniqueId()
        );
    }
}
