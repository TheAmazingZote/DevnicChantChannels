package me.zote.cc;

import me.zote.cc.channel.ChannelManager;
import me.zote.cc.commands.BlacklistCmd;
import me.zote.cc.commands.ChannelCmd;
import me.zote.cc.database.Database;
import me.zote.cc.listeners.ChatListener;
import me.zote.cc.listeners.QuitListener;
import me.zote.cc.player.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatChannels extends JavaPlugin {

    private ChannelManager channelManager;
    private PlayerManager playerManager;
    private Database database;

    @Override
    public void onEnable() {
        channelManager = new ChannelManager(this);
        channelManager.loadChannels();

        database = new Database(this);
        database.createTableIfNeeded();

        playerManager = new PlayerManager(this);
        playerManager.loadPlayerData();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);

        getServer().getCommandMap().register("cc", new ChannelCmd(this));
        getServer().getCommandMap().register("cc", new BlacklistCmd(this));
    }

    @Override
    public void onDisable() {
        playerManager.close();
        database.close();
    }

    public Database database() {
        return database;
    }

    public ChannelManager channelManager() {
        return channelManager;
    }

    public PlayerManager playerManager() {
        return playerManager;
    }

}
