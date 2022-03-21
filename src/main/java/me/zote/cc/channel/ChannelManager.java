package me.zote.cc.channel;

import com.google.common.collect.Sets;
import me.zote.cc.ChatChannels;
import me.zote.cc.config.Cfgs;
import me.zote.cc.player.ChatPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;
import java.util.stream.Collectors;

public class ChannelManager {

    private static final Channel GLOBAL = new Channel("global", -1);
    private final Set<Channel> channels = Sets.newHashSet();
    private final ChatChannels plugin;

    public ChannelManager(ChatChannels plugin) {
        this.plugin = plugin;
    }

    public void loadChannels() {
        plugin.getLogger().info("Loading channels...");
        ConfigurationSection channelSection = Cfgs.of("channels.yml").get().getConfigurationSection("channels");
        for (String key : channelSection.getKeys(false)) {
            int radius = channelSection.getInt(key, -1);
            if (radius > 0)
                channels.add(new Channel(key, radius));
        }
        plugin.getLogger().info("Loaded " + channels.size() + " channels");
    }

    public String channelList() {
        if (channels.isEmpty())
            return "<green>global";
        return "<green>global<gray>, " + channels.stream()
                .map(channel -> "<green>" + channel.name() + " <gray>(" + channel.radius() + ")")
                .collect(Collectors.joining(", "));
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        saveAllChannels();
    }

    public void deleteChannel(String name) {
        channels.removeIf(channel -> channel.name().equalsIgnoreCase(name));
        Cfgs.of("channels.yml").set("channels." + name, null).save();
    }

    public void verifyChannel(ChatPlayer chatPlayer) {
        String currentChannel = chatPlayer.currentChannel();
        if (channel(currentChannel) == null)
            chatPlayer.currentChannel(null);
    }

    public Channel channel(String name) {
        if(name == null)
            return GLOBAL;
        return channels.stream().filter(ch -> ch.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private void saveAllChannels() {
        channels.forEach(channel -> Cfgs.of("channels.yml").set("channels." + channel.name(), channel.radius()).save());
    }

}
