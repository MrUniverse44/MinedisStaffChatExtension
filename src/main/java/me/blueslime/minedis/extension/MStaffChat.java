package me.blueslime.minedis.extension;

import me.blueslime.minedis.api.extension.MinedisExtension;
import me.blueslime.minedis.extension.cache.StaffCache;
import me.blueslime.minedis.extension.commands.StaffChatCommand;
import me.blueslime.minedis.extension.listeners.discord.DiscordChatListener;
import me.blueslime.minedis.extension.listeners.player.PlayerChatListener;
import me.blueslime.minedis.extension.listeners.player.PlayerJoinListener;
import me.blueslime.minedis.extension.listeners.player.PlayerQuitListener;
import me.blueslime.minedis.extension.utils.ColorUtils;
import me.blueslime.minedis.utils.text.TextUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;

import java.awt.*;
import java.util.HashMap;

public final class MStaffChat extends MinedisExtension {
    private final StaffCache cache = new StaffCache(new HashMap<>());
    @Override
    public String getIdentifier() {
        return "MStaffChat";
    }

    @Override
    public String getName() {
        return "Minedis StaffChat";
    }
    private EmbedTemplate template;

    @Override
    public void onEnabled() {
        getPlugin().getLogger().info("Loading StaffChat Extension");
        if (!getConfiguration().contains("settings.channel-id")) {
            getConfiguration().set("settings.channel-id", "NOT_SET");
        }
        if (!getConfiguration().contains("settings.guild-id")) {
            getConfiguration().set("settings.guild-id", "NOT_SET");
        }
        if (!getConfiguration().contains("settings.command.permission")) {
            getConfiguration().set("settings.command.permission", "minedis.staffchat.use");
        }
        if (!getConfiguration().contains("settings.command.value")) {
            getConfiguration().set("settings.command.value", "sc");
        }
        if (!getConfiguration().contains("settings.formats.minecraft")) {
            getConfiguration().set("settings.formats.minecraft", "&4[&cStaff&4]&c&l %location% &f%nick% &8» &7%message%");
        }
        if (!getConfiguration().contains("settings.silent-bar.enabled")) {
            getConfiguration().set("settings.silent-bar.enabled", true);
        }
        if (!getConfiguration().contains("settings.silent-bar.message")) {
            getConfiguration().set("settings.silent-bar.message", "&e+1 message in staff chat");
        }
        if (!getConfiguration().contains("messages.auto-chat.enabled")) {
            getConfiguration().set("messages.auto-chat.enabled", "&aNow your chat was changed to StaffChat, all messages will be sent to the staff chat.");
        }
        if (!getConfiguration().contains("messages.auto-chat.disabled")) {
            getConfiguration().set("messages.auto-chat.disabled", "&aNow your chat is not StaffChat, all messages will be sent to your default chat.");
        }
        if (!getConfiguration().contains("messages.chat-status.disabled")) {
            getConfiguration().set("messages.chat-status.disabled", "&aStaff Chat has been disabled");
        }
        if (!getConfiguration().contains("messages.chat-status.enabled")) {
            getConfiguration().set("messages.chat-status.enabled", "&aStaff Chat has been enabled");
        }
        if (!getConfiguration().contains("messages.chat-status.disabled")) {
            getConfiguration().set("messages.chat-status.disabled", "&aStaff Chat has been disabled");
        }
        if (!getConfiguration().contains("settings.formats.discord.without-embed.message")) {
            getConfiguration().set("settings.formats.discord.without-embed.message", "(**%location%** %nick%): %message%");
        }
        if (!getConfiguration().contains("settings.formats.discord.with-embed.enabled")) {
            getConfiguration().set("settings.formats.discord.with-embed.enabled", true);
            getConfiguration().set("settings.formats.discord.with-embed.title", "SpigotMC");
            getConfiguration().set("settings.formats.discord.with-embed.description", "(%location% %nick%): %message%");
            getConfiguration().set("settings.formats.discord.with-embed.color", "YELLOW");
            getConfiguration().set("settings.formats.discord.with-embed.footer", "mc.spigotmc.org");
        }

        if (!getConfiguration().contains("settings.hooks.MStaffAuthenticator")) {
            getConfiguration().set("settings.hooks.MStaffAuthenticator", false);
        }

        saveConfiguration();

        registerMinecraftListeners(
            new PlayerQuitListener(this),
            new PlayerJoinListener(this),
            new PlayerChatListener(this)
        );

        registerEventListeners(
            new DiscordChatListener(this)
        );

        registerCache(
            cache
        );

        template = new EmbedTemplate(
            getConfiguration().getString("settings.formats.discord.with-embed.title", "SpigotMC"),
            getConfiguration().getString("settings.formats.discord.with-embed.description", "(%location% %nick%): %message%"),
            getConfiguration().getString("settings.formats.discord.with-embed.footer", "mc.spigotmc.org"),
            getConfiguration().getString("settings.formats.discord.with-embed.color", "YELLOW")
        );

        registerMinecraftCommand(
            new StaffChatCommand(
                this,
                getConfiguration().getString("settings.command.value", "sc")
            )
        );

        getLogger().info("All listeners are loaded from MStaffChat");
    }

    @Override
    public void onDisable() {
        getLogger().info("All listeners are unloaded from MStaffChat");

        template = null;
    }

    public boolean isEmbed() {
        return getConfiguration().getBoolean("settings.formats.discord.with-embed.enabled", false);
    }

    public EmbedTemplate getEmbedConfiguration() {
        return template;
    }

    public static class EmbedTemplate {

        private final String description;
        private final String footer;
        private final String title;
        private final Color color;

        public EmbedTemplate(String title, String description, String footer, Color color) {
            this.description = description;
            this.footer = footer;
            this.title = title;
            this.color = color;
        }

        public EmbedTemplate(String title, String description, String footer, String color) {
            this(
                title,
                description,
                footer,
                ColorUtils.getColor(color)
            );
        }

        public String getDescription() {
            return description;
        }

        public String getFooter() {
            return footer;
        }

        public String getTitle() {
            return title;
        }

        public Color getColor() {
            return color;
        }

        public MessageEmbed build(MStaffChat chat, ProxiedPlayer player, String message) {
            String server = chat.getConfiguration().contains("server-name." + player.getServer().getInfo().getName())
                ? chat.getConfiguration().getString(
                    "server-name." + player.getServer().getInfo().getName(),
                    player.getServer().getInfo().getName())
                : player.getServer().getInfo().getName();

            return new EmbedBuilder().setTitle(
                getTitle().replace(
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
                        "%chat%", TextUtilities.strip(message)
                ).replace(
                        "%message%", TextUtilities.strip(message)
                )
            ).setColor(
                getColor()
            ).setFooter(
                getFooter().replace(
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
                        "%chat%", message
                ).replace(
                        "%message%", message
                )
            ).setDescription(
                getDescription().replace(
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
                        "%chat%", message
                ).replace(
                        "%message%", message
                )
            ).build();
        }

        public MessageEmbed build(MStaffChat chat, ChatEvent event) {
            if (!(event.getSender() instanceof ProxiedPlayer)) {
                return null;
            }
            return build(
                    chat,
                    (ProxiedPlayer)event.getSender(),
                    event.getMessage()
            );
        }
    }
}
