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
import java.io.Reader;

import static net.vplaygames.vjson.parser.TokenType.*;

public class HeapBasedJSONReader extends JSONReaderImpl {
    /** lexical state that denotes that the lexer is not parsing a string currently */
    static final int INITIAL = 0;
    /** lexical state that denotes that the lexer is parsing a string currently */
    static final int IN_STRING = 1;

    /** Translates characters to character classes */
    private static final int[] C_MAP;
    /** ATTRIBUTE[state] contains the attributes of state {@code state} */
    private static final int[] ATTRIBUTE;
    /** Translates DFA states to action switch labels. */
    private static final int[] ACTION;
    /** Translates a state to a row index in the transition table */
    private static final int[] ROW_MAP;

    static {
        unpack("\11\0\1\7\1\7\2\0\1\7\22\0\1\7\1\0\1\11\10\0" +
            "\1\6\1\31\1\2\1\4\1\12\12\3\1\32\6\0\4\1\1\5" +
            "\1\1\24\0\1\27\1\10\1\30\3\0\1\22\1\13\2\1\1\21" +
            "\1\14\5\0\1\23\1\0\1\15\3\0\1\16\1\24\1\17\1\20" +
            "\5\0\1\25\1\0\1\26\uff82\0", C_MAP = new int[0x10000]);
        unpack("\2\0\2\1\1\2\1\3\1\4\3\1\1\5\1\6" +
            "\1\7\1\10\1\11\1\12\1\13\1\14\1\15\5\0" +
            "\1\14\1\16\1\17\1\20\1\21\1\22\1\23\1\24" +
            "\1\0\1\25\1\0\1\25\4\0\1\26\1\27\2\0" +
            "\1\30", ACTION = new int[45]);
        unpack("\2\0\1\11\3\1\1\11\3\1\6\11\2\1\1\11\5\0\10\11\1\0\1\1\1\0\1\1\4\0\2\11\2\0\1\11", ATTRIBUTE = new int[45]);
        String ROW_MAP_PACKED =
            "\0\0\0\33\0\66\0\121\0\154\0\207\0\66\0\242" +
                "\0\275\0\330\0\66\0\66\0\66\0\66\0\66\0\66" +
                "\0\363\0\u010e\0\66\0\u0129\0\u0144\0\u015f\0\u017a\0\u0195" +
                "\0\66\0\66\0\66\0\66\0\66\0\66\0\66\0\66" +
                "\0\u01b0\0\u01cb\0\u01e6\0\u01e6\0\u0201\0\u021c\0\u0237\0\u0252" +
                "\0\66\0\66\0\u026d\0\u0288\0\66";
        ROW_MAP = new int[45];
        int i = 0;
        int j = 0;
        while (i < ROW_MAP_PACKED.length()) {
            ROW_MAP[j++] = (ROW_MAP_PACKED.charAt(i++) << 16) | ROW_MAP_PACKED.charAt(i++);
        }
    }

    private static void unpack(String packed, int[] unpacked) {
        int i = 0; // index in packed string
        int j = 0; // index in unpacked array
        while (i < packed.length()) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do unpacked[j++] = value; while (--count > 0);
        }
    }

    /** The transition table of the DFA */
    private static final int[] TRANSITION_TABLE = {
        2, 2, 3, 4, 2, 2, 2, 5, 2, 6,
        2, 2, 7, 8, 2, 9, 2, 2, 2, 2,
        2, 10, 11, 12, 13, 14, 15, 16, 16, 16,
        16, 16, 16, 16, 16, 17, 18, 16, 16, 16,
        16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
        16, 16, 16, 16, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, 4, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 4, 19, 20, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, 20, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, 5, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        21, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, 22, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        23, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 16, 16, 16, 16, 16, 16, 16,
        16, -1, -1, 16, 16, 16, 16, 16, 16, 16,
        16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
        -1, -1, -1, -1, -1, -1, -1, -1, 24, 25,
        26, 27, 28, 29, 30, 31, 32, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        33, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, 34, 35, -1, -1,
        34, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        36, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, 37, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 38, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 39, -1, 39, -1, 39, -1, -1,
        -1, -1, -1, 39, 39, -1, -1, -1, -1, 39,
        39, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, 33, -1, 20, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, 20, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 35,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 38, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 40,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, 41, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, 42, -1, 42, -1, 42,
        -1, -1, -1, -1, -1, 42, 42, -1, -1, -1,
        -1, 42, 42, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, 43, -1, 43, -1, 43, -1, -1, -1,
        -1, -1, 43, 43, -1, -1, -1, -1, 43, 43,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 44,
        -1, 44, -1, 44, -1, -1, -1, -1, -1, 44,
        44, -1, -1, -1, -1, 44, 44, -1, -1, -1,
        -1, -1, -1, -1, -1,
    };

    /** the input device */
    private Reader reader;
    /** the current lexical state */
    private int lexicalState = INITIAL;
    /** the text position at the last accepting state */
    private int markedPos;
    /** startRead marks the beginning of the getString() string in the buffer */
    private int startRead;

    /**
     * Creates a new scanner
     *
     * @param in the Reader to read input from
     */
    public HeapBasedJSONReader(Reader in) {
        this(in, true);
    }

    public HeapBasedJSONReader(Reader in, boolean closeUnderlyingResource) {
        super(new char[16384], closeUnderlyingResource, false, 0);
        this.reader = in;
    }

    public HeapBasedJSONReader(String in) {
        super(in.toCharArray(), false, true, 0);
    }

    @Override
    public void close() throws IOException {
        if (builder == null) return;
        builder = null;
        if (closeUnderlyingResource) reader.close();
        currentToken = null;
        reader = null;
        buffer = null;
        lexicalState = currentPosition = markedPos = startRead = lastPos = position = currentTokenType = 0;
        // TODO: Recycle the current buffer for future use
    }

    /**
     * Returns the text matched by the current regular expression.
     */
    final String getString(int offset) {
        return new String(buffer, startRead + offset, markedPos - startRead);
    }

    /**
     * Refills the input buffer
     *
     * @return <code>false</code>, if there was new input
     * @throws IOException if any I/O-Error occurs
     */
    private boolean buffer() throws IOException {
        if (isStringBased) return true;
        // then make room if possible
        if (startRead > 0) {
            System.arraycopy(buffer, startRead,
                buffer, 0,
                lastPos - startRead);
            // translate stored positions
            lastPos -= startRead;
            currentPosition -= startRead;
            markedPos -= startRead;
            startRead = 0;
        }
        // is the buffer big enough?
        if (currentPosition >= buffer.length) {
            // if not, blow it up
            // TODO: Recycle the current buffer for future use
            char[] newBuffer = new char[currentPosition * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
        // finally, fill the buffer with new input
        int numRead = reader.read(buffer, lastPos,
            buffer.length - lastPos);
        if (numRead > 0) {
            lastPos += numRead;
            return false;
        }
        // unlikely but not impossible: read 0 characters, but not at the end of stream
        if (numRead == 0) {
            int c = reader.read();
            if (c == -1) {
                return true;
            } else {
                buffer[lastPos++] = (char) c;
                return false;
            }
        }
        // numRead < 0
        return true;
    }

    /**
     * Resumes scanning until the next token is found,
     * the end of input is encountered, or any I/O-Error occurs
     *
     * @return the next token
     * @throws IOException if any I/O-Error occurs
     */
    @Override
    protected int getNextTokenType0() throws IOException, ParseException {
        while (true) {
            position += markedPos - startRead;
            currentPosition = startRead = markedPos;
            int state = lexicalState;
            while (true) {
                if (currentPosition < lastPos) {
                    state = TRANSITION_TABLE[ROW_MAP[state] + C_MAP[buffer[currentPosition++]]];
                    if (state == -1) break;
                    int attributes = ATTRIBUTE[state];
                    if ((attributes & 1) == 1) {
                        markedPos = currentPosition;
                        if ((attributes & 8) == 8) break;
                    }
                }
                if (buffer())
                    return EOF;
            }
            switch (ACTION[state]) {
                case 1:
                    throw new ParseException(position, buffer[startRead]);
                case 2:
                    currentToken = Long.parseLong(getString(0));
                    return NUMBER;
                case 4:
                    builder.delete(0, builder.length());
                    lexicalState = IN_STRING;
                    break;
                case 5:
                    return OBJECT_START;
                case 6:
                    return OBJECT_END;
                case 7:
                    return ARRAY_START;
                case 8:
                    return ARRAY_END;
                case 9:
                    return COMMA;
                case 10:
                    return COLON;
                case 12:
                    builder.append('\\');
                    break;
                case 13:
                    lexicalState = INITIAL;
                    currentToken = builder.toString();
                    return STRING;
                case 14:
                    builder.append('"');
                    break;
                case 15:
                    builder.append('/');
                    break;
                case 16:
                    builder.append('\b');
                    break;
                case 17:
                    builder.append('\f');
                    break;
                case 18:
                    builder.append('\n');
                    break;
                case 19:
                    builder.append('\r');
                    break;
                case 20:
                    builder.append('\t');
                    break;
                case 21:
                    currentToken = Double.parseDouble(getString(0));
                    return NUMBER;
                case 22:
                    currentToken = null;
                    return NULL;
                case 23:
                    currentToken = getString(0).equalsIgnoreCase("true");
                    return (boolean) currentToken ? TRUE : FALSE;
                case 24:
                    try {
                        builder.append((char) Integer.parseInt(getString(2), 16));
                    } catch (Exception e) {
                        throw new ParseException(position, e);
                    }
                    break;
                case 11:
                    builder.append(getString(0));
                    break;
                case 3:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                    break;
                default:
                    thr();
            }
        }
    }

    protected void checkOpen() {
        if (builder == null) throw new IllegalStateException("This JSONReader has already been closed!");
    }
}
