package me.blueslime.minedis.extension.staffchat.listeners.player;

import me.blueslime.minedis.Minedis;
import me.blueslime.minedis.extension.staffchat.MStaffChat;
import me.blueslime.minedis.extension.staffchat.cache.StaffCache;
import me.blueslime.minedis.extension.staffchat.utils.StaffStatus;
import me.blueslime.minedis.modules.discord.Controller;
import me.blueslime.minedis.utils.text.TextUtilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Map;
import java.util.UUID;

public class PlayerChatListener implements Listener {
    private final MStaffChat main;

    public PlayerChatListener(MStaffChat main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ChatEvent event) {
        if (event.isCancelled() || event.isCommand()) {
            return;
        }

        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        String guildID = main.getConfiguration().getString("settings.guild-id", "NOT_SET");
        String channelID = main.getConfiguration().getString("settings.channel-id", "NOT_SET");

        if (guildID.isEmpty() || guildID.equalsIgnoreCase("NOT_SET")) {
            return;
        }

        if (channelID.isEmpty() || channelID.equalsIgnoreCase("NOT_SET")) {
            return;
        }

        Guild guild = main.getPlugin().getModule(Controller.class).getBot().getClient().getGuildById(
                guildID
        );

        if (guild == null) {
            return;
        }

        TextChannel textChannel = guild.getTextChannelById(
                channelID
        );

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        StaffCache cache = main.getCache(StaffCache.class);

        if (cache.contains(player.getUniqueId()) && cache.get(player.getUniqueId()) == StaffStatus.DISPLAY_WRITE_CHAT) {

            event.setCancelled(true);

            Minedis plugin = main.getPlugin();

            ProxyServer server = plugin.getProxy();

            String message = event.getMessage();

            String name = main.getConfiguration().contains("server-name." + player.getServer().getInfo().getName()) ?
                    main.getConfiguration().getString(
                            "server-name." + player.getServer().getInfo().getName(), player.getServer().getInfo().getName()
                    ) : player.getServer().getInfo().getName();

            if (main.isEmbed()) {
                MessageEmbed embed = main.getEmbedConfiguration().build(
                        main,
                        event
                );

                if (textChannel != null && embed != null) {
                    textChannel.sendMessageEmbeds(embed).queue();
                }
            } else {
                String defFormat = main.getConfiguration().getString(
                    "settings.formats.discord.without-embed.message", "(**%location%** %nick%): %message%"
                ).replace(
                    "%location%",
                    name
                ).replace(
                    "%player%", player.getName()
                ).replace(
                    "%nick%", player.getName()
                ).replace(
                    "%displayname%", player.getDisplayName()
                ).replace(
                    "%display_name%", player.getDisplayName()
                ).replace(
                    "%server%", name
                ).replace(
                    "%location%", name
                ).replace(
                    "%chat%", TextUtilities.strip(message)
                ).replace(
                    "%message%", TextUtilities.strip(message)
                );

                if (textChannel != null) {
                    textChannel.sendMessage(defFormat).queue();
                }
            }

            String format = main.getConfiguration().getString(
                "settings.formats.minecraft", "&4[&cStaff&4]&c&l %location% &f%nick% &8» &7%message%"
            ).replace(
                "%location%",
                name
            ).replace(
                "%player%", player.getName()
            ).replace(
                "%nick%", player.getName()
            ).replace(
                "%displayname%", player.getDisplayName()
            ).replace(
                "%display_name%", player.getDisplayName()
            ).replace(
                "%server%", name
            ).replace(
                "%location%", name
            ).replace(
                "%chat%", message
            ).replace(
                "%message%", message
            );

            TextComponent component = TextUtilities.component(
                format
            );

            main.getPlugin().getProxy().getConsole().sendMessage(
                component
            );

            boolean silentBar = main.getConfiguration().getBoolean("settings.silent-bar.enabled", true);

            TextComponent silent = TextUtilities.component(
                main.getConfiguration().getString("settings.silent-bar.message", "&e+1 message in staff chat")
            );

            for (Map.Entry<UUID, StaffStatus> entry : main.getCache(StaffCache.class).entrySet()) {
                ProxiedPlayer proxied = server.getPlayer(entry.getKey());

                if (proxied != null && proxied.isConnected()) {
                    if (StaffStatus.isDisplay(entry.getValue())) {
                        proxied.sendMessage(component);
                    } else {
                        if (silentBar) {
                            proxied.sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    silent
                            );
                        }
                    }
                }
            }

            if (textChannel == null) {
                main.getPlugin().getLogger().info("Can't find staff-chat channel");
            }
        }
    }
}
