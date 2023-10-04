package net.vpg.vjson.value;

public final class JSONNull extends JSONValue {
    private static final JSONNull instance = new JSONNull();

    private JSONNull() {
    }

    public static JSONNull getInstance() {
        return instance;
    }

    @Override
    public Type getType() {
        return Type.NULL;
    }

    @Override
    public Object getRaw() {
        return null;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String deserialize() {
        return "null";
    }
}
