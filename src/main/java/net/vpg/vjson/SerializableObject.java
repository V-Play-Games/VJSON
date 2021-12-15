package net.vpg.vjson;

import net.vpg.vjson.value.JSONObject;

public interface SerializableObject extends DeserializableValue {
    JSONObject toObject();

    @Override
    default String deserialize() {
        return toObject().deserialize();
    }
}
