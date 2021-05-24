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
package net.vplaygames.vjson.reader;

import net.vplaygames.vjson.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static net.vplaygames.vjson.parser.TokenType.*;

public class TokenBasedJSONReader extends JSONReaderImpl {
    private Reader reader;

    public TokenBasedJSONReader(InputStream in) throws IOException {
        this(in, false);
    }

    public TokenBasedJSONReader(InputStream in, boolean closeUnderlyingResource) throws IOException {
        this(new InputStreamReader(in), closeUnderlyingResource);
    }

    public TokenBasedJSONReader(Reader in) throws IOException {
        this(in, false);
    }

    public TokenBasedJSONReader(Reader in, boolean closeUnderlyingResource) throws IOException {
        super(new char[1048576], closeUnderlyingResource, false, -1);
        reader = in;
        buffer();
    }

    public TokenBasedJSONReader(String s) {
        super(s.toCharArray(), false, true, -1);
        lastPos = s.length();
    }

    private boolean buffer() throws IOException {
        // fill the buffer with new input
        int numRead = reader.read(buffer, lastPos, buffer.length - lastPos);
        if (numRead > 0) {
            lastPos = numRead;
            position += numRead;
            return false;
        }
        // it is unlikely but not impossible that we read 0 characters, but not at the end of reader
        if (numRead == 0) {
            int c = reader.read();
            if (c == -1) {
                return true;
            } else {
                buffer[lastPos++] = (char) c;
                position++;
                return false;
            }
        }
        // End of File
        return true;
    }

    private char nextChar() {
        if (buffer.length - currentPosition == 1 && !isStringBased) {
            try {
                buffer[0] = buffer[buffer.length - 1];
                currentPosition = -1;
                lastPos = 1;
                buffer();
            } catch (IOException exc) {
                throw new ParseException(currentPosition, ParseException.UNEXPECTED_EXCEPTION, exc);
            }
        }
        return buffer[++currentPosition];
    }

    protected int getNextTokenType0() throws IOException {
        if (lastPos - currentPosition == 1 && (isStringBased || buffer())) return EOF;
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
                currentToken = -getNumber().doubleValue();
                return NUMBER;
            case '.':
                builder.replace(0, builder.length(), "0");
                currentToken = getDouble();
                return NUMBER;
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
                // check next characters
                thr(nextChar() != 'r' || nextChar() != 'u' || nextChar() != 'e');
                currentToken = true;
                return TRUE;
            case 'f':
                // check next characters
                thr(nextChar() != 'a' || nextChar() != 'l' || nextChar() != 's' || nextChar() != 'e');
                currentToken = false;
                return FALSE;
            case 'n':
                // check next characters
                thr(nextChar() != 'u' || nextChar() != 'l' || nextChar() != 'l');
                currentToken = null;
                return NULL;
            case ' ':
            case '\0':
            case '\t':
            case '\r':
            case '\n':
                return getNextTokenType0();
            default:
                thr();
                return -1;
        }
    }

    private String getString() {
        builder.delete(0, builder.length());
        boolean backslashMode = false;
        char c;
        while (true) {
            c = nextChar();
            if (backslashMode) {
                switch (c) {
                    case '"':
                        builder.append('\"');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    case '/':
                        builder.append('/');
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'u':
                        builder.append((char) (nextHexChar() << 12 | nextHexChar() << 8 | nextHexChar() << 4 | nextHexChar()));
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
                    break;
                case '"':
                    return builder.toString();
                case '/':
                default:
                    builder.append(c);
            }
        }
    }

    int nextHexChar() {
        char c = nextChar();
        if (!(c < '0' || c > '9'))
            return c - '0';
        if (!(c < 'A' || c > 'F'))
            return c - 'A' + 10;
        if (!(c < 'a' || c > 'f'))
            return c - 'a' + 10;
        thr();
        // it'll never reach here but still...
        return -1;
    }

    private double getDouble() {
        char c = buffer[currentPosition];
        do {
            builder.append(c);
        } while (!((c = nextChar()) < '0' || c > '9') || c == '+' || c == '-' || c == 'e' || c == 'E');
        currentPosition--;
        return Double.parseDouble(builder.toString());
    }

    private Number getNumber() {
        builder.delete(0, builder.length());
        char c = buffer[currentPosition];
        do {
            builder.append(c);
        } while (!((c = nextChar()) < '0' || c > '9'));
        if (c == '.' || c == 'e' || c == 'E') return getDouble();
        currentPosition--;
        return Long.parseLong(builder.toString());
    }

    @Override
    public void close() throws IOException {
        if (lastPos == -1) return;
        lastPos = -1;
        currentPosition = currentTokenType = 0;
        builder = null;
        currentToken = null;
        // TODO: Recycle the current buffer for future use
        buffer = null;
        if (closeUnderlyingResource) reader.close();
        reader = null;
    }

    protected void checkOpen() {
        if (lastPos == -1) throw new IllegalStateException("This JSONReader has already been closed!");
    }
}
