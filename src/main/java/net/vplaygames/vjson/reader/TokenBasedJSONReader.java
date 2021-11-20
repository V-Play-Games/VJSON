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

import java.io.*;

import static net.vplaygames.vjson.parser.TokenType.*;

public class TokenBasedJSONReader extends JSONReaderImpl {
    private StringBuilder builder = new StringBuilder();
    private Reader reader;
    private Object currentToken;
    private int currentTokenType;
    private int currentPosition = -1;
    private int lastPos;
    private char[] buffer;
    private boolean closeUnderlyingResource;
    private boolean isStringBased;

    public TokenBasedJSONReader(File f) throws FileNotFoundException {
        this(new FileReader(f));
    }

    public TokenBasedJSONReader(InputStream in) {
        this(in, false);
    }

    public TokenBasedJSONReader(InputStream in, boolean closeUnderlyingResource) {
        this(new InputStreamReader(in), closeUnderlyingResource);
    }

    public TokenBasedJSONReader(Reader in) {
        this(in, false);
    }

    public TokenBasedJSONReader(Reader in, boolean closeUnderlyingResource) {
        buffer = new char[1048576];
        reader = in;
        this.closeUnderlyingResource = closeUnderlyingResource;
        isStringBased = false;
        buffer();
    }

    public TokenBasedJSONReader(String s) {
        buffer = s.toCharArray();
        isStringBased = true;
        closeUnderlyingResource = false;
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
            throw new ParseException(currentPosition, exc);
        }
        // End of File
        return true;
    }

    private char nextChar() {
        if (isEOF()) {
            thr();
        }
        return buffer[++currentPosition];
    }

    @Override
    public int getPosition() {
        checkOpen();
        return currentPosition;
    }

    @Override
    public int getCurrentTokenType() {
        checkOpen();
        return currentTokenType;
    }

    @Override
    public int getNextTokenType() {
        checkOpen();
        return currentTokenType = getNextTokenType0();
    }

    @Override
    public Object getCurrentToken() {
        checkOpen();
        return currentToken;
    }

    private boolean isEOF() {
        if (lastPos - currentPosition == 1) {
            if (isStringBased) {
                return true;
            } else {
                buffer[0] = buffer[lastPos - 1];
                currentPosition = -1;
                lastPos = 1;
                return buffer();
            }
        }
        return false;
    }

    protected int getNextTokenType0() {
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
                currentToken = getNumber(true);
                return NUMBER;
            case '.':
                builder.delete(0, builder.length());
                builder.append(0);
                currentToken = getDouble(false);
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
                currentToken = getNumber(false);
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
                thr();
                return -1;
        }
    }

    private String getString() {
        builder.delete(0, builder.length());
        boolean backslashMode = false;
        loop:
        while (true) {
            char c = nextChar();
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
                    continue;
                case '"':
                    break loop;
                case '/':
                default:
                    builder.append(c);
            }
        }
        return builder.toString();
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

    private double getDouble(boolean negative) {
        char c = buffer[currentPosition];
        do {
            builder.append(c);
        } while (!((c = nextChar()) < '0' || c > '9') || c == '+' || c == '-' || c == 'e' || c == 'E');
        currentPosition--;
        return negative ? -Double.parseDouble(builder.toString()) : Double.parseDouble(builder.toString());
    }

    private Number getNumber(boolean negative) {
        builder.delete(0, builder.length());
        char c = buffer[currentPosition];
        do {
            builder.append(c);
        } while (!((c = nextChar()) < '0' || c > '9'));
        if (c == '.' || c == 'e' || c == 'E') return getDouble(negative);
        currentPosition--;
        return negative ? -Long.parseLong(builder.toString()) : Long.parseLong(builder.toString());
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
        throw new ParseException(currentPosition, buffer[currentPosition]);
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
