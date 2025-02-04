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

package net.vpg.vjson.reader;

import java.io.*;
import java.net.URL;

import net.vpg.vjson.parser.ParseException;

import static net.vpg.vjson.reader.JSONReader.TokenType.*;

public class DefaultJSONReader extends AbstractJSONReader {
    private final boolean close;
    private final boolean isStringBased;
    private StringBuilder builder = new StringBuilder();
    private Reader reader;
    private int totalPos = -1;
    private int position = -1;
    private int lastPos;
    private char[] buffer;

    public DefaultJSONReader(File f) throws FileNotFoundException {
        this(new FileReader(f));
    }

    public DefaultJSONReader(URL url) throws IOException {
        this(url.openStream(), true);
    }

    public DefaultJSONReader(InputStream in) {
        this(in, false);
    }

    public DefaultJSONReader(InputStream in, boolean close) {
        this(new InputStreamReader(in), close);
    }

    public DefaultJSONReader(Reader in) {
        this(in, false);
    }

    public DefaultJSONReader(Reader in, boolean close) {
        buffer = new char[1048576];
        reader = in;
        this.close = close;
        isStringBased = false;
        buffer();
    }

    public DefaultJSONReader(String s) {
        buffer = s.toCharArray();
        isStringBased = true;
        close = false;
        lastPos = s.length();
    }

    private boolean buffer() {
        try {
            position = -1;
            int numRead = reader.read(buffer, 0, buffer.length);
            if (numRead == -1) {
                return true;
            }
            lastPos = numRead;
            return false;
        } catch (IOException exc) {
            throw new ParseException(position, exc);
        }
    }

    private char nextChar() {
        if (isEOF())
            error();
        ++totalPos;
        return buffer[++position];
    }

    @Override
    public int getPosition() {
        checkOpen();
        return totalPos;
    }

    private void decrementPosition() {
        totalPos--;
        position--;
    }

    private boolean isEOF() {
        return position + 1 == lastPos && (isStringBased || buffer());
    }

    protected TokenType getNextTokenType0() {
        if (isEOF())
            return EOF;
        char c = nextChar();
        if (" \0\t\r\n".indexOf(c) >= 0)
            return getNextTokenType0();
        if (Character.isDigit(c) || c == '-') {
            currentToken = getNumber();
            return NUMBER;
        }
        currentToken = switch (c) {
            case '"' -> getString();
            case 't' -> checkToken(true);
            case 'f' -> checkToken(false);
            case 'n' -> checkToken(null);
            default -> c;
        };
        return switch (c) {
            case '{' -> OBJECT_START;
            case '}' -> OBJECT_END;
            case '[' -> ARRAY_START;
            case ']' -> ARRAY_END;
            case ',' -> COMMA;
            case ':' -> COLON;
            case '"' -> STRING;
            case 't' -> TRUE;
            case 'f' -> FALSE;
            case 'n' -> NULL;
            default -> error();
        };
    }

    private String getString() {
        while (true) {
            char c = nextChar();
            switch (c) {
                case '\b':
                case '\f':
                case '\n':
                case '\r':
                case '\t':
                    error();
                case '"':
                    return getBuilderString();
                case '\\':
                    c = switch (nextChar()) {
                        case '"' -> '\"';
                        case '\\' -> '\\';
                        case '/' -> '/';
                        case 'b' -> '\b';
                        case 'f' -> '\f';
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        case 'u' -> (char) (hex() << 12 | hex() << 8 | hex() << 4 | hex());
                        default -> error();
                    };
                default:
                    builder.append(c);
            }
        }
    }

    private int hex() {
        int c = Character.digit(nextChar(), 16);
        if (c == -1) error();
        return c;
    }

    private Number getNumber() {
        decrementPosition();
        char c;
        while ("0123456789.+-eE".indexOf(c = nextChar()) >= 0) {
            builder.append(c);
        }
        decrementPosition();
        String s = getBuilderString();
        // Don't use ternary to avoid casting to Double
        if (s.contains("."))
            return Double.parseDouble(s);
        else
            return Long.parseLong(s);
    }

    private String getBuilderString() {
        String tor = builder.toString();
        builder.setLength(0);
        return tor;
    }

    private Object checkToken(Object token) {
        decrementPosition();
        String s = String.valueOf(token);
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) != nextChar())
                error();
        return token;
    }

    @Override
    public void close() throws IOException {
        if (lastPos == -1) return;
        lastPos = -1;
        totalPos = 0;
        position = 0;
        currentTokenType = null;
        builder = null;
        currentToken = null;
        buffer = null;
        if (close) reader.close();
        reader = null;
    }

    protected void checkOpen() {
        if (lastPos == -1) throw new IllegalStateException("This JSONReader has already been closed!");
    }
}
