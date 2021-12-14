package net.vplaygames.vjson;

import net.vplaygames.vjson.value.JSONArray;

public interface SerializableArray extends DeserializableValue {
    JSONArray toArray();

    @Override
    default String deserialize() {
        return toArray().deserialize();
    }
}
