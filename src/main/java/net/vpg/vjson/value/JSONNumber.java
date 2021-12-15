package net.vpg.vjson.value;

public final class JSONNumber extends JSONValue {
    private final Number value;

    public JSONNumber(Number value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.NUMBER;
    }

    @Override
    public Number toNumber() {
        return value;
    }

    @Override
    public Object getRaw() {
        return value.doubleValue();
    }

    @Override
    public String deserialize() {
        return value.toString();
    }
}
