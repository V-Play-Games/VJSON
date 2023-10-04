package net.vpg.vjson.value;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;

public interface JSONContainer<T> {
    JSONValue get(T t);

    default boolean isNull(T index) {
        return get(index).isNull();
    }

    default boolean isType(T index, JSONValue.Type type) {
        return get(index).getType() == type;
    }

    default Optional<JSONValue> opt(T t) {
        return Optional.of(get(t)).filter(o -> !o.isNull());
    }

    default <V> V get(T t, V def, Function<JSONValue, V> convertor) {
        JSONValue val = get(t);
        return val.isNull() ? def : convertor.apply(val);
    }

    default boolean getBoolean(T t) {
        return get(t).toBoolean();
    }

    default boolean getBoolean(T t, boolean def) {
        return get(t, def, JSONValue::toBoolean);
    }

    default Optional<Boolean> optBoolean(T t) {
        return opt(t).map(JSONValue::toBoolean);
    }

    default Number getNumber(T t) {
        return get(t).toNumber();
    }

    default Number getNumber(T t, Number def) {
        return get(t, def, JSONValue::toNumber);
    }

    default Optional<Number> optNumber(T t) {
        return opt(t).map(JSONValue::toNumber);
    }

    default int getInt(T t) {
        return get(t).toInt();
    }

    default int getInt(T t, int def) {
        return getNumber(t, def).intValue();
    }

    default OptionalInt optInt(T t) {
        return opt(t).stream().mapToInt(JSONValue::toInt).findFirst();
    }

    default long getLong(T t) {
        return get(t).toLong();
    }

    default long getLong(T t, long def) {
        return getNumber(t, def).longValue();
    }

    default OptionalLong optLong(T t) {
        return opt(t).stream().mapToLong(JSONValue::toLong).findFirst();
    }

    default double getDouble(T t) {
        return get(t).toLong();
    }

    default double getDouble(T t, long def) {
        return getNumber(t, def).doubleValue();
    }

    default OptionalDouble optDouble(T t) {
        return opt(t).stream().mapToDouble(JSONValue::toDouble).findFirst();
    }

    default String getString(T t) {
        return get(t).toString();
    }

    default String getString(T t, String def) {
        return get(t, def, JSONValue::toString);
    }

    default Optional<String> optString(T t) {
        return opt(t).map(JSONValue::toString);
    }

    default JSONObject getObject(T t) {
        return get(t).toObject();
    }

    default Optional<JSONObject> optObject(T t) {
        return opt(t).map(JSONValue::toObject);
    }

    default JSONArray getArray(T t) {
        return get(t).toArray();
    }

    default Optional<JSONArray> optArray(T t) {
        return opt(t).map(JSONValue::toArray);
    }
}
