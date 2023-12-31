package me.blueslime.minedis.extension.staffchat.listeners.discord;

import me.blueslime.minedis.extension.staffchat.MStaffChat;
import me.blueslime.minedis.extension.staffchat.utils.StaffStatus;
import me.blueslime.minedis.modules.cache.Cache;
import me.blueslime.minedis.utils.text.TextUtilities;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.Map;
import java.util.UUID;

public class DiscordChatListener extends ListenerAdapter {
    private final MStaffChat main;

    public DiscordChatListener(MStaffChat main) {
        this.main = main;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getChannel() instanceof TextChannel) {

            TextChannel channel = event.getChannel().asTextChannel();

            Configuration settings = main.getConfiguration();

            String selectedChannel = settings.getString("settings.channel-id", "NOT_SET");

            if (event.getMember() == null) {
                return;
            }

            Member member = event.getMember();

            if (event.getAuthor().isBot()) {
                return;
            }

            if (channel.getId().equals(selectedChannel)) {
                String format = settings.getString(
                    "settings.formats.minecraft",
                    "&4[&cStaff&4]&c&l %location% &f%nick% &8» &7%message%"
                ).replace(
                    "%location%",
                    "Discord"
                ).replace(
                    "%message%",
                    event.getMessage().getContentRaw()
                ).replace(
                    "%player%",
                    member.getEffectiveName()
                ).replace(
                    "%nick%",
                    member.getEffectiveName()
                ).replace(
                    "%name%",
                    member.getEffectiveName()
                ).replace(
                    "%server%",
                    "Discord"
                ).replace(
                    "%chat%",
                    event.getMessage().getContentRaw()
                );

                boolean silentBar = main.getConfiguration().getBoolean("settings.silent-bar.enabled", true);

                TextComponent silent = TextUtilities.component(
                        main.getConfiguration().getString("settings.silent-bar.message", "&e+1 message in staff chat")
                );

                TextComponent component = TextUtilities.component(format);

                main.getPlugin().getProxy().getConsole().sendMessage(
                    component
                );

                ProxyServer server = main.getPlugin().getProxy();

                Cache<UUID, StaffStatus> cache = main.getCache("msc-cache");

                for (Map.Entry<UUID, StaffStatus> entry : cache.entrySet()) {
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
            }
            return;
        }
    }
}
