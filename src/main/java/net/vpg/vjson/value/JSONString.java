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

public final class JSONString extends JSONValue {
    private final String value;

    private JSONString(String value) {
        this.value = value;
    }

    public static JSONString of(String value) {
        if (value == null)
            throw new IllegalArgumentException("value should not be null");
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
        return s == null || !s.contains("\\")
            ? s
            : s.replaceAll("\\\\b", "\b")
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
