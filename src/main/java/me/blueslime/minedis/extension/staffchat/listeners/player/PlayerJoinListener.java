package me.blueslime.minedis.extension.staffchat.listeners.player;

import me.blueslime.minedis.extension.staffchat.MStaffChat;
import me.blueslime.minedis.extension.staffchat.utils.StaffStatus;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerJoinListener implements Listener {
    private final MStaffChat main;

    public PlayerJoinListener(MStaffChat main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PostLoginEvent event) {
        String permission = main.getConfiguration().getString("settings.command.permission", "minedis.staffchat.use");
        if (event.getPlayer().hasPermission(permission)) {
            main.getCache("msc-cache").set(
                event.getPlayer().getUniqueId(),
                StaffStatus.DISPLAY_CHAT
            );
        }
    }
}
