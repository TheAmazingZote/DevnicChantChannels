package me.zote.cc.player;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class ChatPlayer {

    private final Set<String> blockedWords = Sets.newHashSet();
    private String currentChannel;
    private final UUID owner;

    public ChatPlayer(UUID owner) {
        this.owner = owner;
    }

    public ChatPlayer(UUID owner, String currentChannel, String[] blackListed) {
        this.owner = owner;
        this.currentChannel = currentChannel;
        this.blockedWords.addAll(Arrays.asList(blackListed));
    }

    public Set<String> blockedWords() {
        return blockedWords;
    }

    public String currentChannel() {
        return currentChannel;
    }

    public void currentChannel(String currentChannel) {
        this.currentChannel = currentChannel;
    }

    public UUID owner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPlayer that = (ChatPlayer) o;
        return Objects.equal(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(owner);
    }

}
