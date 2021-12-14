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
package net.vplaygames.vjson.value;

import net.vplaygames.vjson.SerializableArray;
import net.vplaygames.vjson.SerializableObject;
import net.vplaygames.vjson.parser.ParseException;
import net.vplaygames.vjson.reader.JSONReader;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class JSONArray extends JSONValue implements SerializableArray {
    List<JSONValue> data;

    public JSONArray() {
        this.data = new ArrayList<>();
    }

    public JSONArray(List<Object> data) {
        this.data = data.stream().map(JSONValue::of).collect(Collectors.toList());
    }

    public static JSONArray parse(Reader in) throws ParseException {
        return getParser().parse(in).toArray();
    }

    public static JSONArray parse(URL url) throws ParseException, IOException {
        return getParser().parse(url).toArray();
    }

    public static JSONArray parse(InputStream in) throws ParseException {
        return getParser().parse(in).toArray();
    }

    public static JSONArray parse(String s) throws ParseException {
        return getParser().parse(s).toArray();
    }

    public static JSONArray parse(File f) throws ParseException, FileNotFoundException {
        return getParser().parse(f).toArray();
    }

    public static JSONArray parse(JSONReader s) throws ParseException {
        return getParser().parse(s).toArray();
    }

    public static JSONArray parseFile(String path) throws ParseException, FileNotFoundException {
        return parse(new File(path));
    }

    public boolean isNull(int index) {
        return get(index) == null;
    }

    public boolean isType(int index, Type type) {
        return get(index).getType() == type;
    }

    public int length() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    @Override
    public Object getRaw() {
        return data;
    }

    @Override
    public JSONArray toArray() {
        return this;
    }

    public JSONValue get(int index) {
        return JSONValue.of(data.get(index));
    }

    public Optional<JSONValue> opt(int index) {
        JSONValue o = data.get(index);
        return Optional.ofNullable(o);
    }

    public boolean getBoolean(int index) {
        return get(index).toBoolean();
    }

    public boolean getBoolean(int index, boolean defaultValue) {
        JSONValue value = get(index);
        return value == null ? defaultValue : value.toBoolean();
    }

    public Optional<Boolean> optBoolean(int index) {
        return opt(index).map(JSONValue::toBoolean);
    }

    public int getInt(int index) {
        return get(index).toInt();
    }

    public int getInt(int index, int defaultValue) {
        JSONValue value = get(index);
        return value == null ? defaultValue : value.toInt();
    }

    public OptionalInt optInt(int index) {
        JSONValue value = get(index);
        if (value == null) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(value.toInt());
        }
    }

    public long getLong(int index) {
        return get(index).toLong();
    }

    public long getLong(int index, long defaultValue) {
        JSONValue value = get(index);
        return value == null ? defaultValue : value.toLong();
    }

    public OptionalLong optLong(int index) {
        JSONValue value = get(index);
        if (value == null) {
            return OptionalLong.empty();
        } else {
            return OptionalLong.of(value.toLong());
        }
    }

    public double getDouble(int index) {
        return get(index).toLong();
    }

    public double getDouble(int index, long defaultValue) {
        JSONValue value = get(index);
        return value == null ? defaultValue : value.toLong();
    }

    public OptionalDouble optDouble(int index) {
        JSONValue value = get(index);
        if (value == null) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(value.toDouble());
        }
    }

    public String getString(int index) {
        return get(index).toString();
    }

    public String getString(int index, String defaultValue) {
        JSONValue value = get(index);
        return value == null ? defaultValue : value.toString();
    }

    public Optional<String> optString(int index) {
        return opt(index).map(JSONValue::toString);
    }

    public JSONObject getObject(int index) {
        return get(index).toObject();
    }

    public Optional<JSONObject> optObject(int index) {
        return opt(index).map(JSONValue::toObject);
    }

    public JSONArray getArray(int index) {
        return get(index).toArray();
    }

    public Optional<JSONArray> optArray(int index) {
        return opt(index).map(JSONValue::toArray);
    }

    public JSONArray add(int index, Object value) {
        data.add(index, getValue(value));
        return this;
    }

    public JSONArray add(Object value) {
        data.add(getValue(value));
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

    public JSONArray addAll(Collection<?> values) {
        values.forEach(this::add);
        return this;
    }

    public JSONArray addAll(JSONArray array) {
        return addAll(array.data);
    }

    public JSONArray remove(int index) {
        data.remove(index);
        return this;
    }

    public List<JSONValue> toList() {
        return data;
    }

    public <T> Stream<T> stream(BiFunction<? super JSONArray, Integer, ? extends T> mapper) {
        return IntStream.range(0, length()).mapToObj(index -> mapper.apply(this, index));
    }

    @Override
    public String deserialize() {
        return "[" + data.stream().map(JSONValue::deserialize).collect(Collectors.joining(",")) + "]";
    }
}
