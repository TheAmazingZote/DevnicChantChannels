package me.zote.cc.commands;

import me.zote.cc.ChatChannels;
import me.zote.cc.config.Msgs;
import me.zote.cc.player.ChatPlayer;
import me.zote.cc.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlacklistCmd extends Command {

    private final PlayerManager playerManager;

    public BlacklistCmd(ChatChannels plugin) {
        super("blacklist");
        this.playerManager = plugin.playerManager();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (args.length != 2) {
            Msgs.of("<red>Usage: /blacklist <add/remove> <word>").send(sender);
            return false;
        }

        if (!(sender instanceof Player player))
            return false;

        String opt = args[0];
        String word = args[1];

        ChatPlayer chatPlayer = playerManager.getOrCreate(player);
        boolean add = opt.equalsIgnoreCase("add");

        if (add) {
            if (chatPlayer.blockedWords().add(word.toLowerCase())) {
                Msgs.of("<green>Word added to your blacklist").send(player);
            } else {
                Msgs.of("<green>Word already was in your blacklist").send(player);
            }
        } else {
            if (chatPlayer.blockedWords().remove(word.toLowerCase())) {
                Msgs.of("<green>Word removed from your blacklist").send(player);
            } else {
                Msgs.of("<green>Word didn't exists in your blacklist").send(player);
            }
        }

        return false;
    }

}
