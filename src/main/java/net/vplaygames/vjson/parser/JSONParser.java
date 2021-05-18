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
import net.vplaygames.vjson.reader.TokenBasedJSONReader;

import java.io.*;

import static net.vplaygames.vjson.parser.TokenType.*;

/**
 * Parser for JSON getString. Please note that JSONParser is NOT thread-safe.
 *
 * @author Vaibhav Nargwani
 */
public class JSONParser {
    public static final int IN_ERROR = -1;
    public static final int INIT = 0;
    public static final int IN_FINISHED_VALUE = 1;
    public static final int IN_OBJECT = 2;
    public static final int IN_ARRAY = 3;
    public static final int PASSED_PAIR_KEY = 4;
    public static final int EXPECT_COMMA = 5;
    public static final int EXPECT_COLON = 6;

    public JSONValue parse(String s) throws ParseException {
        return parse(s, null);
    }

    public JSONValue parse(String s, ContainerFactory containerFactory) throws ParseException {
        return parse(new TokenBasedJSONReader(s), containerFactory);
    }

    public JSONValue parse(File f) throws ParseException, FileNotFoundException {
        return parse(f, null);
    }

    public JSONValue parse(File f, ContainerFactory containerFactory) throws ParseException, FileNotFoundException {
        Reader reader = new FileReader(f);
        try {
            return parse(new TokenBasedJSONReader(reader, true), containerFactory);
        } catch (IOException ie) {
            throw new ParseException(-1, ParseException.UNEXPECTED_EXCEPTION, ie);
        }
    }

    public JSONValue parse(InputStream s) throws ParseException {
        return parse(s, null);
    }

    public JSONValue parse(InputStream s, ContainerFactory containerFactory) throws ParseException {
        try {
            return parse(new TokenBasedJSONReader(s), containerFactory);
        } catch (IOException ie) {
            throw new ParseException(-1, ParseException.UNEXPECTED_EXCEPTION, ie);
        }
    }

    public JSONValue parse(Reader in) throws ParseException {
        return parse(in, null);
    }

    /**
     * Parse JSON String java object from the input source.
     *
     * @param in               t
     * @param containerFactory the factory to create JSON object and JSON array containers
     * @return a {@link JSONValue}
     * @throws ParseException T
     */
    public JSONValue parse(Reader in, ContainerFactory containerFactory) throws ParseException {
        try {
            return parse(new TokenBasedJSONReader(in), containerFactory);
        } catch (IOException ie) {
            throw new ParseException(-1, ParseException.UNEXPECTED_EXCEPTION, ie);
        }
    }

    public JSONValue parse(JSONReader reader) throws ParseException {
        return parse(reader, null);
    }

    public JSONValue parse(JSONReader reader, ContainerFactory containerFactory) throws ParseException {
        try {
            return parse0(reader, containerFactory == null ? ContainerFactory.defaultInstance() : containerFactory);
        } catch (IOException ie) {
            throw new ParseException(reader.getPosition(), ParseException.UNEXPECTED_EXCEPTION, ie);
        }
    }

    private JSONValue parse0(JSONReader reader, ContainerFactory containerFactory) throws IOException, ParseException {
        FIFOList<Object> statusStack = new FIFOList<>();
        statusStack.add(INIT);
        FIFOList<Object> valueStack = new FIFOList<>();
        while (true) {
            int token = reader.getNextTokenType();
            switch ((int) statusStack.get()) {
                case INIT:
                    switch (token) {
                        case STRING:
                        case PRIMITIVE:
                        case NUMBER:
                            statusStack.replace(IN_FINISHED_VALUE);
                            valueStack.add(reader.getCurrentToken());
                            break;
                        case LEFT_BRACE:
                            statusStack.replace(IN_OBJECT);
                            valueStack.add(createObject(containerFactory));
                            break;
                        case LEFT_SQUARE:
                            statusStack.replace(IN_ARRAY);
                            valueStack.add(createArray(containerFactory));
                            break;
                        default:
                            statusStack.replace(IN_ERROR);
                    }
                    break;
                case IN_FINISHED_VALUE:
                    if (token == EOF)
                        return ((JSONValue) valueStack.remove());
                    else
                        statusStack.replace(IN_ERROR);
                case IN_OBJECT:
                    switch (token) {
                        case COMMA:
                            break;
                        case STRING:
                            statusStack.add(PASSED_PAIR_KEY);
                            valueStack.add(reader.getCurrentToken());
                            break;
                        case RIGHT_BRACE:
                            if (valueStack.size() > 1) {
                                statusStack.remove();
                                valueStack.remove();
                            } else
                                statusStack.replace(IN_FINISHED_VALUE);
                            break;
                        default:
                            statusStack.replace(IN_ERROR);
                    }
                    break;

                case PASSED_PAIR_KEY:
                    switch (token) {
                        case COLON:
                            break;
                        case STRING:
                        case PRIMITIVE:
                        case NUMBER:
                            statusStack.remove();
                            String key = (String) valueStack.remove();
                            ((JSONValue) valueStack.get()).asObject().put(key, JSONValue.of(reader.getCurrentToken()));
                            break;
                        case LEFT_SQUARE:
                            statusStack.remove();
                            JSONArray newArray = createArray(containerFactory);
                            key = (String) valueStack.remove();
                            ((JSONValue) valueStack.get()).asObject().put(key, newArray);
                            statusStack.add(IN_ARRAY);
                            valueStack.add(newArray);
                            break;
                        case LEFT_BRACE:
                            statusStack.remove();
                            JSONObject newObject = createObject(containerFactory);
                            key = (String) valueStack.remove();
                            ((JSONValue) valueStack.get()).asObject().put(key, newObject);
                            statusStack.add(IN_OBJECT);
                            valueStack.add(newObject);
                            break;
                        default:
                            statusStack.add(IN_ERROR);
                    }
                    break;

                case IN_ARRAY:
                    switch (token) {
                        case COMMA:
                            break;
                        case STRING:
                        case PRIMITIVE:
                        case NUMBER:
                            ((JSONValue) valueStack.get()).asArray().add(JSONValue.of(reader.getCurrentToken()));
                            break;
                        case RIGHT_SQUARE:
                            if (valueStack.size() > 1) {
                                statusStack.remove();
                                valueStack.remove();
                            } else
                                statusStack.replace(IN_FINISHED_VALUE);
                            break;
                        case LEFT_BRACE:
                            JSONObject newObject = createObject(containerFactory);
                            ((JSONValue) valueStack.get()).asArray().add(newObject);
                            statusStack.add(IN_OBJECT);
                            valueStack.add(newObject);
                            break;
                        case LEFT_SQUARE:
                            JSONArray newArray = createArray(containerFactory);
                            ((JSONValue) valueStack.get()).asArray().add(newArray);
                            statusStack.add(IN_ARRAY);
                            valueStack.add(newArray);
                            break;
                        default:
                            statusStack.add(IN_ERROR);
                    }
                    break;
                default:
                    throw new ParseException(reader.getPosition(), ParseException.UNEXPECTED_TOKEN, token);
            }
        }
    }

    private static JSONObject createObject(ContainerFactory containerFactory) {
        JSONObject jo = containerFactory.createObjectContainer();
        if (jo == null) return new JSONObject();
        return jo;
    }

    private static JSONArray createArray(ContainerFactory containerFactory) {
        JSONArray ja = containerFactory.createArrayContainer();
        if (ja == null) return new JSONArray();
        return ja;
    }
}
