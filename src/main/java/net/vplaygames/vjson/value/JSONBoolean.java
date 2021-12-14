package net.vplaygames.vjson.value;

public final class JSONBoolean extends JSONValue {
    private static final JSONBoolean TRUE = new JSONBoolean(true);
    private static final JSONBoolean FALSE = new JSONBoolean(false);
    private final boolean value;

    private JSONBoolean(boolean value) {
        this.value = value;
    }

    public static JSONBoolean ofTrue() {
        return TRUE;
    }

    public static JSONBoolean ofFalse() {
        return FALSE;
    }

    public static JSONBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    @Override
    public boolean toBoolean() {
        return value;
    }

    @Override
    public Object getRaw() {
        return value;
    }

    @Override
    public String deserialize() {
        return Boolean.toString(value);
    }
}
