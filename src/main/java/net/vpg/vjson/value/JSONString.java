package net.vpg.vjson.value;

public final class JSONString extends JSONValue {
    private final String value;

    private JSONString(String value) {
        this.value = value;
    }

    public static JSONString of(String value) {
        if (value == null)
            throw new NullPointerException("value should not be null");
        return new JSONString(value);
    }

    public static String escape(String s) {
        if (s == null) return null;
        StringBuilder builder = new StringBuilder(s.length());
        for (int i = 0, len = s.length(); i < len; i++) {
            builder.append(escape(s.charAt(i)));
        }
        return builder.toString();
    }

    public static String escape(char c) {
        switch (c) {
            case '\\':
                return "\\\\";
            case '\b':
                return "\\b";
            case '\f':
                return "\\f";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            case '/':
                return "\\/";
            default:
                return Character.toString(c);
        }
    }

    public static String unescape(String s) {
        return s == null || !s.contains("\\") ? s : s.replaceAll("\\\\b", "\b")
            .replaceAll("\\\\\\\\", "\\")
            .replaceAll("\\\\/", "\\/")
            .replaceAll("\\\\\"", "\"")
            .replaceAll("\\\\f", "\f")
            .replaceAll("\\\\n", "\n")
            .replaceAll("\\\\r", "\r")
            .replaceAll("\\\\t", "\t");
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Object getRaw() {
        return value;
    }

    @Override
    public String deserialize() {
        return "\"" + escape(value) + "\"";
    }
}
