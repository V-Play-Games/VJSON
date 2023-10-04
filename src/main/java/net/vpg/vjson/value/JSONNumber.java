package net.vpg.vjson.value;

public final class JSONNumber extends JSONValue {
    private final Number value;

    private JSONNumber(Number value) {
        this.value = value;
    }

    public static JSONNumber of(Number value) {
        return new JSONNumber(value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof JSONNumber && ((JSONNumber) o).value.doubleValue() == this.value.doubleValue();
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
        return value;
    }

    @Override
    public String deserialize() {
        return value.toString();
    }
}
