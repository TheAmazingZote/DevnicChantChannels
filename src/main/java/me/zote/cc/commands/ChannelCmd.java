package me.zote.cc.commands;

import me.zote.cc.ChatChannels;
import me.zote.cc.channel.Channel;
import me.zote.cc.channel.ChannelManager;
import me.zote.cc.config.Msgs;
import me.zote.cc.player.ChatPlayer;
import me.zote.cc.player.PlayerManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChannelCmd extends Command {

    private final ChannelManager channelManager;
    private final PlayerManager playerManager;

    public ChannelCmd(ChatChannels plugin) {
        super("channel", "Change or view your channel", "/channel <channel>", List.of("ch"));
        this.playerManager = plugin.playerManager();
        this.channelManager = plugin.channelManager();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (args.length == 0) {
            Msgs.of("<red>Usage: /channel <channel>").send(sender);
            return false;
        }

        String arg = args[0].toLowerCase();

        if (sender.hasPermission("cc.admin")) {

            switch (arg) {
                case "reload" -> {
                    reload(sender);
                    return true;
                }
                case "create" -> {
                    create(sender, args);
                    return true;
                }
                case "delete" -> {
                    delete(sender, args);
                    return true;
                }
            }

        }

        if (arg.equals("list")) {
            Msgs.of("<green>Channels: " + channelManager.channelList()).send(sender);
            return true;
        }

        if (!(sender instanceof Player player))
            return false;

        ChatPlayer chatPlayer = playerManager.getOrCreate(player);

        if (arg.equals("info")) {
            Channel currentChannel = channelManager.channel(chatPlayer.currentChannel());
            if (currentChannel.isGlobal()) {
                Msgs.of("<gray>Channel: <green>global").send(player);
            } else {
                Msgs.of("<gray>Channel: <green>" + currentChannel.name()).send(player);
                Msgs.of("<gray>Radius: <green>" + currentChannel.radius()).send(player);
            }
            return true;
        }

        Channel channel = channelManager.channel(arg);

        if (channel == null) {
            Msgs.of("<red>Invalid channel.").send(player);
            return true;
        }

        chatPlayer.currentChannel(arg);
        Msgs.of("<green>You have joined to the channel: " + arg).send(player);
        return true;
    }

    private void reload(CommandSender sender) {
        channelManager.loadChannels();
        playerManager.cleanupAllChannels();
        Msgs.of("<green>Reloaded!").send(sender);
    }

    // /channel create[0] <name>[1] <radius>[2]
    private void create(CommandSender sender, String[] args) {
        if (args.length != 3) {
            Msgs.of("<gray>Usage: <gold>/channel create <name> <radius>").send(sender);
            return;
        }

        String name = args[1];

        if (name.equalsIgnoreCase("global")) {
            Msgs.of("<red>Invalid channel name").send(sender);
            return;
        }

        String radiusString = args[2];
        if (!NumberUtils.isNumber(radiusString)) {
            Msgs.of("<red>Invalid channel radius").send(sender);
            return;
        }
        int radius = Integer.parseInt(radiusString);

        if (radius < 1) {
            Msgs.of("<red>Radius must be a positive number").send(sender);
            return;
        }

        Channel channel = new Channel(name, radius);
        channelManager.addChannel(channel);
        Msgs.of("<green>A new channel was created!").send(sender);

    }

    // /channel delete[0] <name>[1]
    private void delete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Msgs.of("<gray>Usage: <gold>/channel delete <name>").send(sender);
            return;
        }

        String name = args[1];

        if (name.equalsIgnoreCase("global")) {
            Msgs.of("<red>Invalid channel name").send(sender);
            return;
        }

        if (channelManager.channel(name) == null) {
            Msgs.of("<red>That channel doesn't exists").send(sender);
            return;
        }

        channelManager.deleteChannel(name);
        Msgs.of("<green>The channel was deleted!").send(sender);
        playerManager.cleanupAllChannels();

    }

}
