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
package net.vplaygames.vjson.parser;

import net.vplaygames.vjson.JSONArray;
import net.vplaygames.vjson.JSONObject;
import net.vplaygames.vjson.JSONValue;
import net.vplaygames.vjson.reader.JSONReader;
import net.vplaygames.vjson.reader.JSONReaderImpl;
import net.vplaygames.vjson.reader.TokenBasedJSONReader;

import java.io.*;

import static net.vplaygames.vjson.parser.TokenType.*;

/**
 * A parser which parses JSON String using a raw {@code String}, a {@linkplain File},
 * an {@linkplain InputStream}, a {@linkplain Reader}, or a pre-configured {@linkplain JSONReader}.
 * This parser reads tokens through a {@link JSONReader}, which may be passed as a method parameter or
 * constructed from a given {@code String}, {@linkplain File}, {@linkplain InputStream} or {@linkplain Reader}.
 * All {@code parse} methods have an overriden method
 *
 * @author Vaibhav Nargwani
 */
public class JSONParser {
    /**
     * The default Container Factory which creates new
     * {@link JSONObject JSONObjects} and {@link JSONArray JSONArrays} when asked for
     *
     * @see ContainerFactory#deafaultFactory()
     */
    static ContainerFactory defaultFactory = ContainerFactory.of(JSONObject::new, JSONArray::new);

    public JSONValue parse(String s) throws ParseException {
        return parse(s, null);
    }

    public JSONValue parse(String s, ContainerFactory containerFactory) throws ParseException {
        return parse(new TokenBasedJSONReader(s), containerFactory, true);
    }

    public JSONValue parse(File f) throws ParseException, FileNotFoundException {
        return parse(f, null);
    }

    public JSONValue parse(File f, ContainerFactory containerFactory) throws ParseException, FileNotFoundException {
        Reader reader = new FileReader(f);
        try {
            return parse(new TokenBasedJSONReader(reader, true), containerFactory, true);
        } catch (IOException ie) {
            throw new ParseException(-1, ie);
        }
    }

    public JSONValue parse(InputStream s) throws ParseException {
        return parse(s, null);
    }

    public JSONValue parse(InputStream s, ContainerFactory containerFactory) throws ParseException {
        try {
            return parse(new TokenBasedJSONReader(s), containerFactory, true);
        } catch (IOException ie) {
            throw new ParseException(-1, ie);
        }
    }

    public JSONValue parse(Reader in) throws ParseException {
        return parse(in, null);
    }

    public JSONValue parse(Reader in, ContainerFactory containerFactory) throws ParseException {
        try {
            return parse(new TokenBasedJSONReader(in), containerFactory, true);
        } catch (IOException ie) {
            throw new ParseException(-1, ie);
        }
    }

    public JSONValue parse(JSONReader reader) throws ParseException {
        return parse(reader, null);
    }

    public JSONValue parse(JSONReader reader, ContainerFactory containerFactory) throws ParseException {
        return parse(reader, containerFactory, false);
    }

    public JSONValue parse(JSONReader reader, ContainerFactory containerFactory, boolean closeAfterParse) throws ParseException {
        try {
            return parseValue(reader, containerFactory == null ? defaultFactory : containerFactory);
        } catch (IOException ie) {
            throw new ParseException(reader.getPosition(), ie);
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

    private JSONValue parseValue(JSONReader reader, ContainerFactory containerFactory) throws IOException, ParseException {
        switch (reader.getNextTokenType()) {
            case STRING:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
                return JSONValue.of(reader.getCurrentToken());
            case OBJECT_START:
                return parseObject(reader, containerFactory);
            case ARRAY_START:
                return parseArray(reader, containerFactory);
            default:
                thr(reader);
                return null;
        }
    }

    private JSONObject parseObject(JSONReader reader, ContainerFactory containerFactory) throws IOException, ParseException {
        JSONObject object = createObject(containerFactory);
        if (reader.getNextTokenType() == OBJECT_END) return object;
        if (reader.getCurrentTokenType() != STRING) thr(reader);
        while (true) {
            reader.expectNextType(COLON);
            String key = (String) reader.getCurrentToken();
            switch (reader.getNextTokenType()) {
                case ARRAY_START:
                    object.put(key, parseArray(reader, containerFactory));
                    break;
                case OBJECT_START:
                    object.put(key, parseObject(reader, containerFactory));
                    break;
                case STRING:
                case TRUE:
                case FALSE:
                case NULL:
                case NUMBER:
                    object.put(key, JSONValue.of(reader.getCurrentToken()));
                    break;
                default:
                    thr(reader);
            }
            switch (reader.getNextTokenType()) {
                case COMMA:
                    reader.expectNextType(STRING);
                    continue;
                case OBJECT_END:
                    return object;
                default:
                    thr(reader);
            }
        }
    }

    private JSONArray parseArray(JSONReader reader, ContainerFactory containerFactory) throws IOException, ParseException {
        JSONArray array = createArray(containerFactory);
        if (reader.getNextTokenType() == ARRAY_END) return array;
        while (true) {
            switch (reader.getCurrentTokenType()) {
                case ARRAY_START:
                    array.add(parseArray(reader, containerFactory));
                    break;
                case OBJECT_START:
                    array.add(parseObject(reader, containerFactory));
                    break;
                case STRING:
                case TRUE:
                case FALSE:
                case NULL:
                case NUMBER:
                    array.add(JSONValue.of(reader.getCurrentToken()));
                    break;
                default:
                    thr(reader);
            }
            switch (reader.getNextTokenType()) {
                case COMMA:
                    reader.getNextTokenType();
                    continue;
                case ARRAY_END:
                    return array;
                default:
                    thr(reader);
            }
        }
    }

    private static void thr(JSONReader reader) {
        if (reader instanceof JSONReaderImpl)
            ((JSONReaderImpl) reader).thr();
        else
            throw new ParseException(reader.getPosition(), JSONValue.escape(reader.getCurrentToken() + ""));

    }

    private static JSONObject createObject(ContainerFactory containerFactory) {
        JSONObject jo = containerFactory.newObject();
        return jo == null ? new JSONObject() : jo;
    }

    private static JSONArray createArray(ContainerFactory containerFactory) {
        JSONArray ja = containerFactory.newArray();
        return ja == null ? new JSONArray() : ja;
    }
}
