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
package net.vplaygames.vjson;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
 *
 * @author Vaibhav Nargwani
 */
public class JSONObject extends JSONValue implements Map<String, JSONValue>, Cloneable {
    LinkedHashMap<String, JSONValue> map;

    public JSONObject() {
        this(new LinkedHashMap<>());
    }

    /**
     * Creates a new JSONObject from a Map and copies key-value pairs
     *
     * @param map the map to copy key-value pairs from
     */
    public JSONObject(Map<?, ?> map) {
        this();
        map.forEach((k, v) -> put(String.valueOf(k), JSONValue.of(v)));
    }

    JSONObject(LinkedHashMap<String, JSONValue> map) {
        this.map = map;
    }

    /**
     * Convert a map to JSON String
     *
     * @param map the map to convert
     * @return JSON String, or "null" if map is null.
     */
    public static String toJSONString(Map<?, ?> map) {
        if (map == null)
            return "null";
        return map.entrySet()
            .stream()
            .collect(() -> new StringJoiner(",", "{", "}"),
                (joiner, e) -> joiner.add("\"" + JSONValue.escape(String.valueOf(e.getKey())) + "\":" + JSONValue.toString(e.getValue())),
                StringJoiner::merge)
            .toString();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return map.equals(obj);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected JSONObject clone() {
        return new JSONObject((LinkedHashMap<String, JSONValue>) map.clone());
    }

    @Override
    public String toRawString() {
        return toString();
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    @Override
    public boolean asBoolean() {
        throw new ClassCastException();
    }

    @Override
    public Number asNumber() {
        throw new ClassCastException();
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public JSONObject asObject() {
        return this;
    }

    @Override
    public JSONArray asArray() {
        throw new ClassCastException();
    }

    @Override
    public String toJSONString() {
        return toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public JSONValue get(Object key) {
        return map.get(key);
    }

    @Override
    public JSONValue put(String key, JSONValue value) {
        return map.put(key, value);
    }

    @Override
    public JSONValue remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends JSONValue> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<JSONValue> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, JSONValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public JSONValue getOrDefault(Object key, JSONValue defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super JSONValue> action) {
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super JSONValue, ? extends JSONValue> function) {
        map.replaceAll(function);
    }

    @Override
    public JSONValue putIfAbsent(String key, JSONValue value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean replace(String key, JSONValue oldValue, JSONValue newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public JSONValue replace(String key, JSONValue value) {
        return map.replace(key, value);
    }

    @Override
    public JSONValue computeIfAbsent(String key, Function<? super String, ? extends JSONValue> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public JSONValue computeIfPresent(String key, BiFunction<? super String, ? super JSONValue, ? extends JSONValue> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public JSONValue compute(String key, BiFunction<? super String, ? super JSONValue, ? extends JSONValue> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public JSONValue merge(String key, JSONValue value, BiFunction<? super JSONValue, ? super JSONValue, ? extends JSONValue> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }
}
