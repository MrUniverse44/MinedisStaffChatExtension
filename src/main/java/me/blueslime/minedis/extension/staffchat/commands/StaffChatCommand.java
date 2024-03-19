package me.blueslime.minedis.extension.staffchat.commands;

import me.blueslime.minedis.api.command.MinecraftCommand;
import me.blueslime.minedis.api.command.sender.Sender;
import me.blueslime.minedis.extension.staffchat.MStaffChat;
import me.blueslime.minedis.extension.staffchat.utils.EmbedSection;
import me.blueslime.minedis.extension.staffchat.utils.StaffAuthenticator;
import me.blueslime.minedis.extension.staffchat.utils.StaffStatus;
import me.blueslime.minedis.modules.cache.Cache;
import me.blueslime.minedis.modules.discord.Controller;
import me.blueslime.minedis.modules.extensions.Extensions;
import me.blueslime.minedis.utils.text.TextReplacer;
import me.blueslime.minedis.utils.text.TextUtilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class StaffChatCommand extends MinecraftCommand {

    private final MStaffChat main;

    public StaffChatCommand(MStaffChat main, String name) {
        super(name);
        this.main = main;
    }


    @Override
    public void execute(Sender sender, String[] args) {
        String permission = main.getConfiguration().getString("settings.command.permission", "minedis.staffchat.use");
        if (sender.isPlayer() && sender.hasPermission(permission)) {
            ProxiedPlayer player = sender.toPlayer();

            boolean staffExtension = main.getModule(Extensions.class).isExtensionInstalled("MStaffAuthenticator");

            if (staffExtension) {
                if (StaffAuthenticator.contains(main, player)) {
                    return;
                }
            }

            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("toggle-chat") || args[0].equalsIgnoreCase("chat")) {
                    if (args.length >= 2) {
                        String check = args[1].toLowerCase(Locale.ENGLISH)
                                .replace("yes", "true")
                                .replace("si", "true")
                                .replace("no", "false");
                        boolean status = Boolean.parseBoolean(check);

                        if (!status) {
                            main.getCache("msc-cache").set(
                                    player.getUniqueId(),
                                    StaffStatus.DISABLED
                            );
                            sender.send(
                                    main.getConfiguration(),
                                    "messages.chat-status.disabled",
                                    "&aStaff Chat has been disabled"
                            );
                        } else {
                            main.getCache("msc-cache").set(
                                    player.getUniqueId(),
                                    StaffStatus.DISPLAY_WRITE_CHAT
                            );
                            sender.send(
                                    main.getConfiguration(),
                                    "messages.chat-status.enabled",
                                    "&aStaff Chat has been enabled"
                            );
                        }

                    }
                    return;
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i != args.length; i++) {
                    builder.append(args[i]).append(" ");
                }

                String server = main.getConfiguration().contains("server-name." + player.getServer().getInfo().getName()) ?
                        main.getConfiguration().getString(
                                "server-name." + player.getServer().getInfo().getName(),
                                player.getServer().getInfo().getName()
                        ) : player.getServer().getInfo().getName();

                String format = main.getConfiguration().getString(
                        "settings.formats.minecraft",
                        "&4[&cStaff&4]&c&l %location% &f%nick% &8» &7%message%"
                ).replace(
                        "%location%",
                        server
                ).replace(
                        "%server%",
                        server
                ).replace(
                        "%displayname%", player.getDisplayName()
                ).replace(
                        "%display_name%", player.getDisplayName()
                ).replace(
                        "%chat%",
                        builder.toString()
                ).replace(
                        "%message%",
                        builder.toString()
                ).replace(
                        "%player%",
                        player.getName()
                ).replace(
                        "%nick%",
                        player.getName()
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

                ProxyServer proxy = main.getPlugin().getProxy();

                Cache<UUID, StaffStatus> cache = main.getCache("msc-cache");

                for (Map.Entry<UUID, StaffStatus> entry : cache.entrySet()) {
                    ProxiedPlayer proxied = proxy.getPlayer(entry.getKey());

                    if (proxied != null && proxied.isConnected()) {
                        if (staffExtension) {
                            if (StaffAuthenticator.contains(main, proxied)) {
                                return;
                            }
                        }
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

                StandardGuildMessageChannel channel;

                TextChannel textChannel = guild.getTextChannelById(
                    channelID
                );

                if (textChannel == null) {
                    NewsChannel newsChannel = guild.getNewsChannelById(
                        channelID
                    );
                    if (newsChannel == null) {
                        return;
                    }
                    channel = newsChannel;
                } else {
                    channel = textChannel;
                }

                TextReplacer replacer = TextReplacer.builder().replace(
                    "%location%",
                    server
                ).replace(
                    "%player%", player.getName()
                ).replace(
                    "%nick%", player.getName()
                ).replace(
                    "%displayname%", player.getDisplayName()
                ).replace(
                    "%display_name%", player.getDisplayName()
                ).replace(
                    "%server%", server
                ).replace(
                    "%location%", server
                ).replace(
                    "%chat%", TextUtilities.strip(builder.toString())
                ).replace(
                    "%message%", TextUtilities.strip(builder.toString())
                );

                if (main.isEmbed()) {
                    MessageEmbed embed = new EmbedSection(
                            main.getConfiguration().getSection("settings.formats.discord.with-embed")
                    ).build(
                        replacer
                    );

                    if (embed != null) {
                        channel.sendMessageEmbeds(embed).queue();
                    }
                } else {
                    String defFormat = main.getConfiguration().getString(
                            "settings.formats.discord.without-embed.message", "(**%location%** %nick%): %message%"
                    );

                    channel.sendMessage(
                            replacer.apply(defFormat)
                    ).queue();
                }
            } else {
                Cache<UUID, StaffStatus> cache = main.getCache("msc-cache");

                boolean contains = cache.contains(player.getUniqueId()) && cache.get(player.getUniqueId()) == StaffStatus.DISPLAY_WRITE_CHAT;

                String path = contains ?
                        "messages.auto-chat.disabled" :
                        "messages.auto-chat.enabled";

                String def = contains ?
                        "&aNow your chat is not StaffChat, all messages will be sent to your default chat." :
                        "&aNow your chat was changed to StaffChat, all messages will be sent to the staff chat.";

                player.sendMessage(
                        TextUtilities.component(
                                main.getConfiguration().getString(
                                        path,
                                        def
                                )
                        )
                );

                if (contains) {
                    main.getCache("msc-cache").set(
                        player.getUniqueId(),
                        StaffStatus.DISPLAY_CHAT
                    );
                } else {
                    main.getCache("msc-cache").set(
                        player.getUniqueId(),
                        StaffStatus.DISPLAY_WRITE_CHAT
                    );
                }
            }
        }
    }
}
