package me.zote.cc.player;

import com.google.common.collect.Sets;
import me.zote.cc.ChatChannels;
import me.zote.cc.channel.ChannelManager;
import me.zote.cc.database.Database;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final Set<ChatPlayer> chatPlayers = Sets.newHashSet();
    private final ChannelManager channelManager;
    private final ChatChannels plugin;
    private final Database database;

    public PlayerManager(ChatChannels plugin) {
        this.plugin = plugin;
        this.database = plugin.database();
        this.channelManager = plugin.channelManager();
    }

    public void loadPlayerData() {
        plugin.getLogger().info("Loading player data...");
        String query = "SELECT * FROM user_channels;";
        database.query(query).forEach(pd -> {
            String uuidString = pd.get("uuid");
            String channel = pd.get("channel");
            String blackListString = pd.get("blacklist");

            UUID owner = UUID.fromString(uuidString);
            String[] blackList = blackListString.split("::");

            ChatPlayer chatPlayer = new ChatPlayer(owner, channel, blackList);
            channelManager.verifyChannel(chatPlayer);
            chatPlayers.add(chatPlayer);
        });

        plugin.getLogger().info("Loaded " + chatPlayers.size() + " players");
    }

    public void close() {
        plugin.getLogger().info("Saving player data...");
        chatPlayers.forEach(this::savePlayer);
    }

    public void cleanupAllChannels() {
        chatPlayers.forEach(channelManager::verifyChannel);
    }

    public void savePlayer(Player player) {
        savePlayer(getOrCreate(player));
    }

    private void savePlayer(ChatPlayer chatPlayer) {
        String query = "UPDATE user_channels SET channel = ?, blacklist = ? WHERE uuid = ?;";
        database.update(query, chatPlayer.currentChannel(), String.join("::", chatPlayer.blockedWords()), chatPlayer.owner().toString());
    }

    public ChatPlayer getOrCreate(Player player) {
        return chatPlayers.stream()
                .filter(cp -> player.getUniqueId().equals(cp.owner()))
                .findFirst()
                .orElseGet(() -> {
                    ChatPlayer chatPlayer = new ChatPlayer(player.getUniqueId());
                    chatPlayers.add(chatPlayer);
                    String query = "INSERT INTO user_channels (uuid, channel, blacklist) VALUES (?, ?, ?);";
                    database.update(query, chatPlayer.owner().toString(), null, "");
                    return chatPlayer;
                });
    }

}
