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

import net.vpg.vjson.SerializableObject;
import net.vpg.vjson.parser.ParseException;
import net.vpg.vjson.pretty.PrettyPrintConfig;
import net.vpg.vjson.pretty.PrettyPrinter;
import net.vpg.vjson.reader.JSONReader;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JSONObject extends JSONValue implements SerializableObject, JSONContainer<String> {
    private final Map<String, JSONValue> map;

    public JSONObject() {
        map = new HashMap<>();
    }

    private JSONObject(Map<?, ?> map) {
        this();
        putAll(map);
    }

    public static JSONObject of(Map<?, ?> map) {
        return new JSONObject(map);
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

    public static <T> Collector<T, ?, JSONObject> collector(Function<T, String> keyMapper, Function<T, ?> valueMapper) {
        return Collector.of(JSONObject::new,
            (obj, e) -> obj.put(keyMapper.apply(e), valueMapper.apply(e)),
            JSONObject::putAll);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public JSONValue get(String key) {
        return JSONValue.of(map.get(key));
    }

    public JSONObject put(String key, Object val) {
        map.put(key, JSONValue.of(val));
        return this;
    }

    public JSONObject putAll(Map<?, ?> map) {
        map.forEach((k, v) -> put(String.valueOf(k), v));
        return this;
    }

    public JSONObject putAll(JSONObject object) {
        map.putAll(object.map);
        return this;
    }

    public JSONObject remove(String key) {
        map.remove(key);
        return this;
    }

    public Map<String, JSONValue> toMap() {
        return map;
    }

    public <T> T map(Function<JSONObject, T> converter) {
        return converter.apply(this);
    }

    @Override
    public String deserialize() {
        return map.entrySet()
            .stream()
            .map(e -> "\"" + JSONString.escape(e.getKey()) + "\":" + e.getValue().deserialize())
            .collect(Collectors.joining(",", "{", "}"));
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    @Override
    public Object getRaw() {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getRaw()));
    }

    @Override
    public JSONObject toObject() {
        return this;
    }

    @Override
    public void toPrettyString(PrettyPrinter printer) {
        PrettyPrintConfig config = printer.getConfig();
        printer.print("{");
        if (!config.isObjectContentsOnSameLine()) {
            printer.incrementIndentLevel();
            printer.newLineAndIndent();
        } else if (config.isSpaceWithinBraces())
            printer.space();
        Iterator<Map.Entry<String, JSONValue>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JSONValue> next = iterator.next();
            printer.print("\"" + next.getKey() + "\"");
            if (config.isSpaceBeforeColon())
                printer.space();
            printer.print(":");
            if (config.isSpaceAfterColon())
                printer.space();
            next.getValue().toPrettyString(printer);
            if (iterator.hasNext()) {
                if (config.isSpaceBeforeComma())
                    printer.space();
                printer.print(",");
                if (!config.isObjectContentsOnSameLine())
                    printer.newLineAndIndent();
                else if (config.isSpaceAfterComma())
                    printer.space();
            }
        }
        if (!config.isObjectContentsOnSameLine()) {
            printer.decrementIndentLevel();
            printer.newLineAndIndent();
        } else if (config.isSpaceWithinBraces())
            printer.space();
        printer.print("}");
    }
}
