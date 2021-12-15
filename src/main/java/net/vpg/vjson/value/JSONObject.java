/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.vjson.value;

import net.vpg.vjson.SerializableArray;
import net.vpg.vjson.SerializableObject;
import net.vpg.vjson.parser.ParseException;
import net.vpg.vjson.reader.JSONReader;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class JSONObject extends JSONValue implements SerializableObject {
    Map<String, JSONValue> map;

    public JSONObject() {
        map = new HashMap<>();
    }

    /**
     * Creates a new JSONObject from a Map and copies key-value pairs
     *
     * @param map the map to copy key-value pairs from
     */
    public JSONObject(Map<?, ?> map) {
        this();
        putAll(map);
    }

    public static JSONObject parse(Reader in) throws ParseException {
        return getParser().parse(in).toObject();
    }

    public static JSONObject parse(URL url) throws ParseException, IOException {
        return getParser().parse(url).toObject();
    }

    public static JSONObject parse(InputStream in) throws ParseException {
        return getParser().parse(in).toObject();
    }

    public static JSONObject parse(String s) throws ParseException {
        return getParser().parse(s).toObject();
    }

    public static JSONObject parse(File f) throws ParseException, FileNotFoundException {
        return getParser().parse(f).toObject();
    }

    public static JSONObject parse(JSONReader s) throws ParseException {
        return getParser().parse(s).toObject();
    }

    public static JSONObject parseFile(String path) throws ParseException, FileNotFoundException {
        return parse(new File(path));
    }

    public boolean isNull(String key) {
        return get(key) == null;
    }

    public boolean isType(String key, Type type) {
        return get(key).getType() == type;
    }

    public int length() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public JSONValue get(String key) {
        return JSONValue.of(map.get(key));
    }

    public Optional<JSONValue> opt(String key) {
        JSONValue o = map.get(key);
        return Optional.ofNullable(o);
    }

    public boolean getBoolean(String key) {
        return get(key).toBoolean();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        JSONValue value = get(key);
        return value == null ? defaultValue : value.toBoolean();
    }

    public Optional<Boolean> optBoolean(String key) {
        return opt(key).map(JSONValue::toBoolean);
    }

    public int getInt(String key) {
        return get(key).toInt();
    }

    public int getInt(String key, int defaultValue) {
        JSONValue value = get(key);
        return value == null ? defaultValue : value.toInt();
    }

    public OptionalInt optInt(String key) {
        JSONValue value = get(key);
        if (value == null) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(value.toInt());
        }
    }

    public long getLong(String key) {
        return get(key).toLong();
    }

    public long getLong(String key, long defaultValue) {
        JSONValue value = get(key);
        return value == null ? defaultValue : value.toLong();
    }

    public OptionalLong optLong(String key) {
        JSONValue value = get(key);
        if (value == null) {
            return OptionalLong.empty();
        } else {
            return OptionalLong.of(value.toLong());
        }
    }

    public double getDouble(String key) {
        return get(key).toLong();
    }

    public double getDouble(String key, long defaultValue) {
        JSONValue value = get(key);
        return value == null ? defaultValue : value.toLong();
    }

    public OptionalDouble optDouble(String key) {
        JSONValue value = get(key);
        if (value == null) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(value.toDouble());
        }
    }

    public String getString(String key) {
        return get(key).toString();
    }

    public String getString(String key, String defaultValue) {
        JSONValue value = get(key);
        return value == null ? defaultValue : value.toString();
    }

    public Optional<String> optString(String key) {
        return opt(key).map(JSONValue::toString);
    }

    public JSONObject getObject(String key) {
        return get(key).toObject();
    }

    public Optional<JSONObject> optObject(String key) {
        return opt(key).map(JSONValue::toObject);
    }

    public JSONArray getArray(String key) {
        return get(key).toArray();
    }

    public Optional<JSONArray> optArray(String key) {
        return opt(key).map(JSONValue::toArray);
    }

    public JSONObject put(String key, Object value) {
        map.put(key, getValue(value));
        return this;
    }

    protected JSONValue getValue(Object value) {
        if (value instanceof SerializableArray)
            return ((SerializableArray) value).toArray();
        else if (value instanceof SerializableObject)
            return ((SerializableObject) value).toObject();
        else
            return JSONValue.of(value);
    }

    public JSONObject putAll(Map<?, ?> map) {
        map.forEach((k, v) -> put(String.valueOf(k), JSONValue.of(v)));
        return this;
    }

    public JSONObject putAll(JSONObject object) {
        return putAll(object.map);
    }

    public JSONObject remove(String key) {
        map.remove(key);
        return this;
    }

    public Map<String, JSONValue> toMap() {
        return map;
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

    public Collection<JSONValue> getValues() {
        return map.values();
    }

    public Set<Map.Entry<String, JSONValue>> getEntrySet() {
        return map.entrySet();
    }

    public <T> Stream<T> stream(BiFunction<? super JSONObject, String, ? extends T> mapper) {
        return getKeys().stream().map(key -> mapper.apply(this, key));
    }

    @Override
    public String deserialize() {
        return "{" + map.entrySet()
            .stream()
            .map(e -> "\"" + JSONString.escape(e.getKey()) + "\":" + e.getValue().deserialize())
            .collect(Collectors.joining(",")) + "}";
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    @Override
    public JSONObject toObject() {
        return this;
    }

    @Override
    public Object getRaw() {
        return map;
    }
}
