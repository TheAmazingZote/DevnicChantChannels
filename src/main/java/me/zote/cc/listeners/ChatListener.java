package me.zote.cc.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zote.cc.ChatChannels;
import me.zote.cc.channel.Channel;
import me.zote.cc.channel.ChannelManager;
import me.zote.cc.player.ChatPlayer;
import me.zote.cc.player.PlayerManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.function.Predicate;

public class ChatListener implements Listener {

    private final ChannelManager channelManager;
    private final PlayerManager playerManager;

    public ChatListener(ChatChannels plugin) {
        this.channelManager = plugin.channelManager();
        this.playerManager = plugin.playerManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {

        Player sender = event.getPlayer();
        ChatPlayer chatPlayer = playerManager.getOrCreate(sender);
        Channel channel = channelManager.channel(chatPlayer.currentChannel());

        if (!channel.isGlobal()) {
            event.viewers().removeIf(filter(sender, channel).negate());
        }

        event.viewers().removeIf(audience -> audience instanceof Player player && isBlocked(player, event.message()));
    }

    private Predicate<Audience> filter(Player sender, Channel channel) {

        World world = sender.getWorld();
        int sqrRadius = channel.radius() * channel.radius();

        return audience -> {
            if (audience instanceof Player player) {
                if (!player.getWorld().equals(world))
                    return false;
                return sender.getLocation().distanceSquared(player.getLocation()) <= sqrRadius;
            }
            return true;
        };

    }

    private boolean isBlocked(Player player, Component component) {
        ChatPlayer chatPlayer = playerManager.getOrCreate(player);
        String msgString = PlainTextComponentSerializer.plainText().serialize(component).toLowerCase();
        String[] arr = msgString.split(" ");
        return Arrays.stream(arr).anyMatch(chatPlayer.blockedWords()::contains);
    }

}
