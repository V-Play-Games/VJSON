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

import net.vpg.vjson.parser.ParseException;
import net.vpg.vjson.parser.TokenType;

import java.io.*;
import java.net.URL;

import static net.vpg.vjson.parser.TokenType.*;

public class DefaultJSONReader extends AbstractJSONReader {
    private final boolean close;
    private final boolean isStringBased;
    private StringBuilder builder = new StringBuilder();
    private Reader reader;
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
            // fill the buffer with new input
            int numRead = reader.read(buffer, lastPos, buffer.length - lastPos);
            if (numRead > 0) {
                lastPos = numRead;
                return false;
            }
            // it is unlikely but not impossible that we read 0 characters, but not at the end of reader
            if (numRead == 0) {
                int c = reader.read();
                if (c == -1) {
                    return true;
                } else {
                    buffer[lastPos++] = (char) c;
                    return false;
                }
            }
        } catch (IOException exc) {
            throw new ParseException(position, exc);
        }
        // End of File
        return true;
    }

    private char nextChar() {
        if (isEOF()) {
            thr();
        }
        return buffer[++position];
    }

    @Override
    public int getPosition() {
        checkOpen();
        return position;
    }

    private boolean isEOF() {
        if (lastPos - position == 1) {
            if (isStringBased) {
                return true;
            } else {
                buffer[0] = buffer[lastPos - 1];
                position = -1;
                lastPos = 1;
                return buffer();
            }
        }
        return false;
    }

    protected TokenType getNextTokenType0() {
        if (isEOF()) return EOF;
        switch (nextChar()) {
            case '{':
                return OBJECT_START;
            case '}':
                return OBJECT_END;
            case '[':
                return ARRAY_START;
            case ']':
                return ARRAY_END;
            case ',':
                return COMMA;
            case ':':
                return COLON;
            case '"':
                currentToken = getString();
                return STRING;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                currentToken = getNumber();
                return NUMBER;
            case 't':
                checkTrue();
                currentToken = true;
                return TRUE;
            case 'f':
                checkFalse();
                currentToken = false;
                return FALSE;
            case 'n':
                checkNull();
                currentToken = null;
                return NULL;
            case ' ':
            case '\0':
            case '\t':
            case '\r':
            case '\n':
                return getNextTokenType0();
            default:
                currentToken = buffer[position - 1];
                thr();
                return null;
        }
    }

    private String getString() {
        boolean backslashMode = false;
        loop:
        while (true) {
            char c = nextChar();
            if (backslashMode) {
                switch (c) {
                    case '"':
                        append('\"');
                        break;
                    case '\\':
                        append('\\');
                        break;
                    case '/':
                        append('/');
                        break;
                    case 'b':
                        append('\b');
                        break;
                    case 'f':
                        append('\f');
                        break;
                    case 'n':
                        append('\n');
                        break;
                    case 'r':
                        append('\r');
                        break;
                    case 't':
                        append('\t');
                        break;
                    case 'u':
                        append((char) (nextHexChar() << 12 | nextHexChar() << 8 | nextHexChar() << 4 | nextHexChar()));
                        break;
                    default:
                        thr();
                }
                c = nextChar();
                backslashMode = false;
            }
            switch (c) {
                case '\b':
                case '\f':
                case '\n':
                case '\r':
                case '\t':
                    thr();
                    break;
                case '\\':
                    backslashMode = true;
                    continue;
                case '"':
                    break loop;
                case '/':
                default:
                    append(c);
            }
        }
        return getBuilderString();
    }

    private int nextHexChar() {
        int c = Character.digit(nextChar(), 16);
        thr(c == -1);
        return c;
    }

    private Number getNumber() {
        position--;
        char c;
        while (Character.isDigit(c = nextChar()) || c == '.' || c == '+' || c == '-' || c == 'e' || c == 'E') {
            append(c);
        }
        position--;
        String s = getBuilderString();
        return s.contains(".") ? Double.parseDouble(s) : Long.parseLong(s);
    }

    private void append(char c) {
        builder.append(c);
    }

    private String getBuilderString() {
        String tor = builder.toString();
        builder.setLength(0);
        return tor;
    }

    private void checkTrue() {
        thr(nextChar() != 'r' || nextChar() != 'u' || nextChar() != 'e');
    }

    private void checkFalse() {
        thr(nextChar() != 'a' || nextChar() != 'l' || nextChar() != 's' || nextChar() != 'e');
    }

    private void checkNull() {
        thr(nextChar() != 'u' || nextChar() != 'l' || nextChar() != 'l');
    }

    public void thr() {
        throw new ParseException(position, String.valueOf(currentToken));
    }

    @Override
    public void close() throws IOException {
        if (lastPos == -1) return;
        lastPos = -1;
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
