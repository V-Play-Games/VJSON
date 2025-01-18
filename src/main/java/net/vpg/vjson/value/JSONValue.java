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

import net.vpg.vjson.parser.JSONParser;
import net.vpg.vjson.parser.ParseException;
import net.vpg.vjson.reader.JSONReader;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class JSONValue implements DeserializableValue {
    private static JSONParser parser;

    protected static JSONParser getParser() {
        return parser == null ? parser = new JSONParser() : parser;
    }

    public static JSONValue parse(Reader in) throws ParseException {
        return getParser().parse(in);
    }

    public static JSONValue parse(URL url) throws ParseException, IOException {
        return getParser().parse(url);
    }

    public static JSONValue parse(InputStream in) throws ParseException {
        return getParser().parse(in);
    }

    public static JSONValue parse(String s) throws ParseException {
        return getParser().parse(s);
    }

    public static JSONValue parse(File f) throws ParseException, FileNotFoundException {
        return getParser().parse(f);
    }

    public static JSONValue parse(JSONReader s) throws ParseException {
        return getParser().parse(s);
    }

    @SuppressWarnings("rawtypes")
    public static JSONValue of(Object o) {
        return switch (o) {
            case null -> JSONNull.getInstance();
            case JSONValue value -> value;
            case List list -> JSONArray.of(list);
            case Map map -> JSONObject.of(map);
            case String s -> JSONString.of(s);
            case Number number -> JSONNumber.of(number);
            case Boolean b -> JSONBoolean.of((boolean) o);
            case SerializableArray arr -> arr.toArray();
            case SerializableObject obj -> obj.toObject();
            case DeserializableValue value -> parse(value.deserialize());
            default -> throw new UnsupportedOperationException("Cannot make JSONValue of class " + o.getClass());
        };
    }

    public abstract Type getType();

    public abstract Object getRaw();

    @Override
    public boolean equals(Object o) {
        return o instanceof JSONValue && Objects.equals(((JSONValue) o).getRaw(), getRaw());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRaw());
    }

    public boolean toBoolean() {
        return error(Type.BOOLEAN);
    }

    public Number toNumber() {
        return error(Type.NUMBER);
    }

    public JSONObject toObject() {
        return error(Type.OBJECT);
    }

    public JSONArray toArray() {
        return error(Type.ARRAY);
    }

    public int toInt() {
        return toNumber().intValue();
    }

    public long toLong() {
        return toNumber().longValue();
    }

    public double toDouble() {
        return toNumber().doubleValue();
    }

    public boolean isNull() {
        return false;
    }

    @Override
    public String toString() {
        return deserialize();
    }

    private <T> T error(Type type) {
        throw new UnsupportedOperationException("Cannot cast value of type " + getType() + " to type " + type);
    }

    public enum Type {
        NULL, STRING, NUMBER, BOOLEAN, OBJECT, ARRAY
    }
}
