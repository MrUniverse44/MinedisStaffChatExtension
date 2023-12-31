package me.blueslime.minedis.extension.staffchat;

import me.blueslime.minedis.api.extension.MinedisExtension;
import me.blueslime.minedis.extension.staffchat.commands.StaffChatCommand;
import me.blueslime.minedis.extension.staffchat.listeners.discord.DiscordChatListener;
import me.blueslime.minedis.extension.staffchat.listeners.player.PlayerChatListener;
import me.blueslime.minedis.extension.staffchat.listeners.player.PlayerJoinListener;
import me.blueslime.minedis.extension.staffchat.listeners.player.PlayerQuitListener;
import me.blueslime.minedis.extension.staffchat.utils.StaffStatus;
import me.blueslime.minedis.modules.cache.Cache;

import java.util.HashMap;
import java.util.UUID;

public final class MStaffChat extends MinedisExtension {
    private final Cache<UUID, StaffStatus> cache = new Cache<>(new HashMap<>());
    @Override
    public String getIdentifier() {
        return "MStaffChat";
    }

    @Override
    public String getName() {
        return "Minedis StaffChat";
    }

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
            getConfiguration().set("settings.formats.discord.with-embed.thumbnail", "https://cravatar.eu/avatar/%nick%.png");
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
            "msc-cache",
            cache
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
    }

    public boolean isEmbed() {
        return getConfiguration().getBoolean("settings.formats.discord.with-embed.enabled", false);
    }
}
