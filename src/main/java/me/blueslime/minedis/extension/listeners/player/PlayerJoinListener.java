package me.blueslime.minedis.extension.listeners.player;

import me.blueslime.minedis.extension.MStaffChat;
import me.blueslime.minedis.extension.cache.StaffCache;
import me.blueslime.minedis.extension.utils.StaffStatus;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener {
    private final MStaffChat main;

    public PlayerJoinListener(MStaffChat main) {
        this.main = main;
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        String permission = main.getConfiguration().getString("settings.command.permission", "minedis.staffchat.use");
        if (event.getPlayer().hasPermission(permission)) {
            main.getCache(StaffCache.class).set(
                    event.getPlayer().getUniqueId(),
                    StaffStatus.DISPLAY_CHAT
            );
        }
    }
}
