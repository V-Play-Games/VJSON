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

import net.vplaygames.vjson.parser.JSONParser;
import net.vplaygames.vjson.parser.ParseException;
import net.vplaygames.vjson.reader.JSONReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Vaibhav Nargwani
 * @since 1.0.0
 */
// TODO: add toPrettyString() method
@SuppressWarnings("unused")
public class JSONValue implements JSONable {
    public static final JSONValue TRUE = new JSONValue(true);
    public static final JSONValue FALSE = new JSONValue(false);
    public static final JSONValue NULL = new JSONValue(null);
    public static JSONParser parser;
    private final Object o;
    private Type type;

    private JSONValue(Object o) {
        this.o = o;
    }

    protected JSONValue() {
        o = this;
    }

    private static JSONParser getParser() {
        return parser == null ? new JSONParser() : parser;
    }

    /**
     * Parse a JSON String from the {@link Reader}
     *
     * @param in the reader to read from
     * @return a {@link JSONValue}
     * @throws ParseException t
     * @see JSONParser
     */
    public static JSONValue parse(Reader in) throws ParseException {
        return getParser().parse(in);
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

    public static JSONValue of(Object o) {
        if (o == null)
            return NULL;
        else if (o instanceof JSONValue)
            return (JSONValue) o;
        else if (!(o instanceof Boolean))
            return new JSONValue(o);
        else if ((boolean) o)
            return TRUE;
        else
            return FALSE;
    }

    /**
     * Escapes ", \, /, \r, \n, \b, \f and \t
     *
     * @param s the String to escape characters from
     * @return the escaped String
     */
    public static String escape(String s) {
        return s == null ? null : s.replaceAll("([\"\\\\\b\f\n\r\t/])", "\\\\$1");
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

    public static String toString(Object o) {
        return Type.of(o).toString(o);
    }

    @Override
    public String toString() {
        return getType().toString(o);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JSONValue && Objects.equals(((JSONValue) obj).o, o);
    }

    @Override
    public int hashCode() {
        return o != null ? o.hashCode() : 0;
    }

    @Override
    public String toJSONString() {
        return toString();
    }

    public String toRawString() {
        return String.valueOf(o);
    }

    public Type getType() {
        return type == null ? type = Type.of(o) : type;
    }

    public boolean asBoolean() {
        return (boolean) o;
    }

    public Number asNumber() {
        return (Number) o;
    }

    public byte asByte() {
        return asNumber().byteValue();
    }

    public short asShort() {
        return asNumber().shortValue();
    }

    public int asInt() {
        return asNumber().intValue();
    }

    public long asLong() {
        return asNumber().longValue();
    }

    public float asFloat() {
        return asNumber().floatValue();
    }

    public double asDouble() {
        return asNumber().doubleValue();
    }

    public String asString() {
        return (String) o;
    }

    public String asEscapedString() {
        return escape(asString());
    }

    public JSONObject asObject() {
        return (JSONObject) o;
    }

    public <T> T asObject(Function<JSONValue, T> converter) {
        return converter.apply(this);
    }

    public JSONArray asArray() {
        return (JSONArray) o;
    }

    public <T> ArrayList<T> asList(Function<JSONValue, T> converter) {
        return asArray()
            .stream()
            .map(converter)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public enum Type {
        NULL(o -> o == null || (o instanceof Number && !Double.isFinite(((Number) o).doubleValue())), o -> "null"),
        STRING(o -> o instanceof String, o -> "\"" + escape((String) o) + "\""),
        NUMBER(o -> o instanceof Number, String::valueOf),
        BOOLEAN(o -> o instanceof Boolean, String::valueOf),
        JSONABLE(o -> o instanceof JSONable, o -> ((JSONable) o).toJSONString()),
        OBJECT(o -> o instanceof Map, o -> JSONObject.toJSONString((Map<?, ?>) o)),
        ARRAY(o -> o instanceof List, o -> JSONArray.toJSONString((List<?>) o)),
        UNKNOWN(o -> true, String::valueOf);

        final Predicate<Object> checker;
        final Function<Object, String> toString;

        Type(Predicate<Object> checker, Function<Object, String> toString) {
            this.checker = checker;
            this.toString = toString;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public static Type of(Object o) {
            return Arrays.stream(values()).filter(t -> t.checker.test(o)).findFirst().get();
        }

        public String toString(Object o) {
            if (!checker.test(o)) throw new IllegalArgumentException();
            return toString.apply(o);
        }

        public boolean isOfType(Object o) {
            return checker.test(o);
        }
    }
}
