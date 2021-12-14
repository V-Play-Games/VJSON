package net.vplaygames.vjson;

import net.vplaygames.vjson.value.JSONObject;

public interface SerializableObject extends DeserializableValue {
    JSONObject toObject();

    @Override
    default String deserialize() {
        return toObject().deserialize();
    }
}
