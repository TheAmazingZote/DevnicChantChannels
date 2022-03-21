package me.zote.cc.listeners;

import me.zote.cc.ChatChannels;
import me.zote.cc.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private final PlayerManager playerManager;

    public QuitListener(ChatChannels plugin) {
        this.playerManager = plugin.playerManager();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerManager.savePlayer(event.getPlayer());
    }

}
