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
package net.vpg.vjson.parser;

import net.vpg.vjson.reader.DefaultJSONReader;
import net.vpg.vjson.reader.JSONReader;
import net.vpg.vjson.value.JSONArray;
import net.vpg.vjson.value.JSONObject;
import net.vpg.vjson.value.JSONValue;

import java.io.*;
import java.net.URL;

import static net.vpg.vjson.parser.TokenType.*;

/**
 * A parser which parses JSON from a pre-configured {@linkplain JSONReader}.
 * This parser reads tokens through a {@link JSONReader}, which may be passed as a method parameter or
 * constructed from the given arguments.
 * All {@code parse} methods have an overriden method
 *
 * @author Vaibhav Nargwani
 */
public class JSONParser {
    public JSONValue parse(String s) throws ParseException {
        return parse(new DefaultJSONReader(s), true);
    }

    public JSONValue parse(File f) throws ParseException, FileNotFoundException {
        return parse(new DefaultJSONReader(f), true);
    }

    public JSONValue parse(URL url) throws ParseException, IOException {
        return parse(new DefaultJSONReader(url), true);
    }

    public JSONValue parse(InputStream stream) throws ParseException {
        return parse(new DefaultJSONReader(stream), true);
    }

    public JSONValue parse(Reader reader) throws ParseException {
        return parse(new DefaultJSONReader(reader), true);
    }

    public JSONValue parse(JSONReader reader) throws ParseException {
        return parse(reader, false);
    }

    public JSONValue parse(JSONReader reader, boolean closeAfterParse) throws ParseException {
        try {
            return parseValue(reader);
        } finally {
            if (closeAfterParse) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                    // close silently
                }
            }
        }
    }

    private JSONValue parseValue(JSONReader reader) throws ParseException {
        if (reader.getCurrentTokenType() == null)
            reader.getNextTokenType();
        switch (reader.getCurrentTokenType()) {
            default:
                reader.error();
            case STRING:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
                return JSONValue.of(reader.getCurrentToken());
            case OBJECT_START:
                return parseObject(reader);
            case ARRAY_START:
                return parseArray(reader);
        }
    }

    private JSONObject parseObject(JSONReader reader) throws ParseException {
        JSONObject object = new JSONObject();
        reader.getNextTokenType();
        while (true) {
            TokenType type = reader.getCurrentTokenType();
            if (type == OBJECT_END)
                return object;
            if (type != STRING)
                reader.error();
            String key = reader.getCurrentToken().toString();
            reader.expectNextType(COLON);
            reader.getNextTokenType();
            object.put(key, parseValue(reader));
            switch (reader.getNextTokenType()) {
                default:
                    reader.error();
                case OBJECT_END:
                    return object;
                case COMMA:
                    reader.getNextTokenType();
            }
        }
    }

    private JSONArray parseArray(JSONReader reader) throws ParseException {
        JSONArray array = new JSONArray();
        if (reader.getNextTokenType() == ARRAY_END)
            return array;
        while (true) {
            array.add(parseValue(reader));
            switch (reader.getNextTokenType()) {
                default:
                    reader.error();
                case ARRAY_END:
                    return array;
                case COMMA:
                    reader.getNextTokenType();
            }
        }
    }
}
