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
import net.vpg.vjson.parser.ParseException;
import net.vpg.vjson.pretty.PrettyPrintConfig;
import net.vpg.vjson.pretty.PrettyPrinter;
import net.vpg.vjson.reader.JSONReader;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONArray extends JSONValue implements SerializableArray, JSONContainer<Integer> {
    private final List<JSONValue> list;

    public JSONArray() {
        this.list = new ArrayList<>();
    }

    private JSONArray(List<?> list) {
        this.list = list.stream().map(JSONValue::of).collect(Collectors.toList());
    }

    public static JSONArray of(List<?> list) {
        return new JSONArray(list);
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

    public static <T> Collector<T, ?, JSONArray> collector() {
        return Collector.of(JSONArray::new, JSONArray::add, JSONArray::addAll);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public JSONValue get(Integer index) {
        return JSONValue.of(list.get(index));
    }

    public JSONArray add(int index, Object value) {
        list.add(index, JSONValue.of(value));
        return this;
    }

    public JSONArray add(Object value) {
        list.add(JSONValue.of(value));
        return this;
    }

    public JSONArray addAll(Collection<?> values) {
        values.forEach(this::add);
        return this;
    }

    public JSONArray addAll(JSONArray array) {
        list.addAll(array.list);
        return this;
    }

    public JSONArray remove(int index) {
        list.remove(index);
        return this;
    }

    public List<JSONValue> toList() {
        return list;
    }

    public <T> List<T> toList(Function<JSONValue, T> converter) {
        return list.stream()
            .map(converter)
            .collect(Collectors.toList());
    }

    public Stream<JSONValue> stream() {
        return list.stream();
    }

    @Override
    public String deserialize() {
        return list.stream().map(JSONValue::deserialize).collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    @Override
    public Object getRaw() {
        return list.stream().map(JSONValue::getRaw).collect(Collectors.toList());
    }

    @Override
    public JSONArray toArray() {
        return this;
    }

    @Override
    public void toPrettyString(PrettyPrinter printer) {
        PrettyPrintConfig config = printer.getConfig();
        printer.print("[");
        if (config.isArrayContentsOnSameLine()) {
            if (config.isSpaceWithinBrackets())
                printer.space();
        } else {
            printer.incrementIndentLevel();
            printer.newLineAndIndent();
        }
        Iterator<JSONValue> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next().toPrettyString(printer);
            if (iterator.hasNext()) {
                if (config.isSpaceBeforeComma())
                    printer.space();
                printer.print(",");
                if (config.isArrayContentsOnSameLine()) {
                    if (config.isSpaceAfterComma())
                        printer.space();
                } else
                    printer.newLineAndIndent();
            }
        }
        if (config.isArrayContentsOnSameLine()) {
            if (config.isSpaceWithinBrackets())
                printer.space();
        } else {
            printer.decrementIndentLevel();
            printer.newLineAndIndent();
        }
        printer.print("}");
    }
}
