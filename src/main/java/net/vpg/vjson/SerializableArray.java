package net.vpg.vjson;

import net.vpg.vjson.value.JSONArray;

public interface SerializableArray extends DeserializableValue {
    JSONArray toArray();

    @Override
    default String deserialize() {
        return toArray().deserialize();
    }
}
