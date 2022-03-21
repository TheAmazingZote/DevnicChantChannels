package me.zote.cc.channel;

public record Channel(String name, int radius) {

    public boolean isGlobal() {
        return name.equalsIgnoreCase("global");
    }

}
