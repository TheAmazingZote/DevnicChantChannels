package me.zote.cc.config;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class Msgs {

    private final Map<String, String> placeholders = new HashMap<>();
    private final String message;

    public static Msgs of(String message) {
        return new Msgs(message);
    }

    private Msgs(String message) {
        this.message = Cfgs.of().get(message, message);
    }

    public Msgs vars(Map<String, String> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    public Msgs var(String name, Object value) {
        placeholders.put(name, value.toString());
        return this;
    }

    public String build() {
        return StrSubstitutor.replace(message, placeholders, "%", "%");
    }

    public Component asComp() {
        return MiniMessage.miniMessage().deserialize(build());
    }

    public String asPlain() {
        return LegacyComponentSerializer.builder()
                .character('§')
                .useUnusualXRepeatedCharacterHexFormat()
                .hexColors()
                .build()
                .serialize(asComp());
    }

    public void send(Audience audience) {
        if (audience != null) {
            audience.sendMessage(asComp());
        }
    }

}

