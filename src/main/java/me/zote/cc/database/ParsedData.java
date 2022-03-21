package me.zote.cc.database;

import com.google.common.collect.Maps;

import java.util.Map;

public class ParsedData {

    private final Map<String, Object> values = Maps.newHashMap();

    protected void put(String value, Object object) {
        values.put(value, object);
    }

    public <T> T get(String key) {
        return (T) values.get(key);
    }

}
