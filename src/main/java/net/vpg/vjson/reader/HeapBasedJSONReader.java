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

import static net.vpg.vjson.parser.TokenType.*;

public class HeapBasedJSONReader extends AbstractJSONReader {
    // lexical state that denotes that the lexer is not parsing a string currently
    static final int INITIAL = 0;
    // lexical state that denotes that the lexer is parsing a string currently
    static final int IN_STRING = 1;

    // Translates characters to character classes
    private static final char[] C_MAP;
    // ATTRIBUTE[n] contains the attributes of state 'n'
    private static final int[] ATTRIBUTE;
    // Translates DFA states to action switch labels.
    private static final int[] ACTION;
    // Translates a state to a row index in the transition table
    private static final int[] ROW_MAP;
    // The transition table of the DFA
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

    static {
        String C_MAP_PACKED =
            "\11\0\1\7\1\7\2\0\1\7\22\0\1\7\1\0\1\11\10\0" +
                "\1\6\1\31\1\2\1\4\1\12\12\3\1\32\6\0\4\1\1\5" +
                "\1\1\24\0\1\27\1\10\1\30\3\0\1\22\1\13\2\1\1\21" +
                "\1\14\5\0\1\23\1\0\1\15\3\0\1\16\1\24\1\17\1\20" +
                "\5\0\1\25\1\0\1\26\uff82\0";
        C_MAP = new char[0x10000];
        int i = 0; // index in packed string
        int j = 0; // index in unpacked array
        while (i < C_MAP_PACKED.length()) {
            int count = C_MAP_PACKED.charAt(i++);
            char value = C_MAP_PACKED.charAt(i++);
            do C_MAP[j++] = value; while (--count > 0);
        }
        String ACTION_PACKED =
            "\2\0\2\1\1\2\1\3\1\4\3\1\1\5\1\6" +
                "\1\7\1\10\1\11\1\12\1\13\1\14\1\15\5\0" +
                "\1\14\1\16\1\17\1\20\1\21\1\22\1\23\1\24" +
                "\1\0\1\25\1\0\1\25\4\0\1\26\1\27\2\0" +
                "\1\30";
        ACTION = new int[45];
        i = j = 0;
        while (i < ACTION_PACKED.length()) {
            int count = ACTION_PACKED.charAt(i++);
            int value = ACTION_PACKED.charAt(i++);
            do ACTION[j++] = value; while (--count > 0);
        }
        String ROW_MAP_PACKED =
            "\0\0\0\33\0\66\0\121\0\154\0\207\0\66\0\242" +
                "\0\275\0\330\0\66\0\66\0\66\0\66\0\66\0\66" +
                "\0\363\0\u010e\0\66\0\u0129\0\u0144\0\u015f\0\u017a\0\u0195" +
                "\0\66\0\66\0\66\0\66\0\66\0\66\0\66\0\66" +
                "\0\u01b0\0\u01cb\0\u01e6\0\u01e6\0\u0201\0\u021c\0\u0237\0\u0252" +
                "\0\66\0\66\0\u026d\0\u0288\0\66";
        ROW_MAP = new int[45];
        i = j = 0;
        while (i < ROW_MAP_PACKED.length()) {
            int high = ROW_MAP_PACKED.charAt(i++) << 16;
            ROW_MAP[j++] = high | ROW_MAP_PACKED.charAt(i++);
        }
        String ATTRIBUTE_PACKED = "\2\0\1\11\3\1\1\11\3\1\6\11\2\1\1\11" +
            "\5\0\10\11\1\0\1\1\1\0\1\1\4\0\2\11" +
            "\2\0\1\11";
        ATTRIBUTE = new int[45];
        i = j = 0;
        while (i < ATTRIBUTE_PACKED.length()) {
            int count = ATTRIBUTE_PACKED.charAt(i++);
            int value = ATTRIBUTE_PACKED.charAt(i++);
            do ATTRIBUTE[j++] = value; while (--count > 0);
        }
    }

    private Reader reader;
    private int lexicalState = INITIAL;
    private char[] buffer;
    // the text position at the last accepting state
    private int markedPos;
    // the current text position in the buffer
    private int currentPos;
    // startRead marks the beginning of the getString() string in the buffer
    private int startRead;
    // endRead marks the last character in the buffer, that has been read from input
    private int endRead;
    // the number of characters up to the start of the matched text
    private int position;
    private StringBuilder sb = new StringBuilder();
    private boolean closeUnderlyingResource;
    private boolean isStringBased;

    public HeapBasedJSONReader(File f) throws FileNotFoundException {
        this(new FileReader(f), true);
    }

    public HeapBasedJSONReader(InputStream in) {
        this(in, false);
    }

    public HeapBasedJSONReader(InputStream in, boolean closeUnderlyingResource) {
        this(new InputStreamReader(in), closeUnderlyingResource);
    }

    public HeapBasedJSONReader(Reader in) {
        this(in, false);
    }

    public HeapBasedJSONReader(Reader in, boolean closeUnderlyingResource) {
        this(in, new char[16384], closeUnderlyingResource, false);
    }

    public HeapBasedJSONReader(String in) {
        this(null, in.toCharArray(), false, true);
    }

    private HeapBasedJSONReader(Reader in, char[] buffer, boolean closeUnderlyingResource, boolean isStringBased) {
        this.reader = in;
        this.buffer = buffer;
        this.closeUnderlyingResource = closeUnderlyingResource;
        this.isStringBased = isStringBased;
    }

    @Override
    public int getPosition() {
        checkOpen();
        return position;
    }

    @Override
    public void thr() {
        throw new ParseException(position, getString(0));
    }

    @Override
    public void close() throws IOException {
        if (sb == null) return;
        sb = null;
        if (closeUnderlyingResource) reader.close();
        currentToken = null;
        reader = null;
        buffer = null;
        currentTokenType = null;
        lexicalState = currentPos = markedPos = startRead = endRead = position = 0;
    }

    protected String getString(int offset) {
        return new String(buffer, startRead + offset, markedPos - startRead);
    }

    /**
     * Refills the input buffer
     *
     * @return <code>false</code>, if there was new input
     */
    protected boolean atEOF() {
        if (isStringBased) return true;
        // then make room if possible
        if (startRead > 0) {
            endRead -= startRead;
            System.arraycopy(buffer, startRead, buffer, 0, endRead);
            // translate stored positions
            currentPos -= startRead;
            markedPos -= startRead;
            startRead = 0;
        }
        // is the buffer big enough?
        if (currentPos >= buffer.length) {
            // if not, blow it up
            char[] newBuffer = new char[currentPos * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
        try {
            // finally, fill the buffer with new input
            int numRead = reader.read(buffer, endRead, buffer.length - endRead);
            if (numRead > 0) {
                endRead += numRead;
                return false;
            }
            // unlikely but not impossible: read 0 characters, but not at the end of stream
            if (numRead == 0) {
                int c = reader.read();
                if (c == -1) {
                    return true;
                } else {
                    buffer[endRead++] = (char) c;
                    return false;
                }
            }
        } catch (IOException exc) {
            throw new ParseException(position, exc);
        }
        // numRead < 0
        return true;
    }

    /**
     * Resumes scanning until the next token is found,
     * the end of input is encountered, or any I/O-Error occurs
     *
     * @return the next token
     */
    protected TokenType getNextTokenType0() throws ParseException {
        while (true) {
            int action = -1;
            position += markedPos - startRead;
            currentPos = startRead = markedPos;
            int state = lexicalState;
            while (true) {
                if (currentPos < endRead) {
                    state = TRANSITION_TABLE[ROW_MAP[state] + C_MAP[buffer[currentPos++]]];
                    if (state == -1) break;
                    int attributes = ATTRIBUTE[state];
                    if ((attributes & 1) == 1) {
                        action = state;
                        markedPos = currentPos;
                        if ((attributes & 8) == 8) break;
                    }
                } else if (atEOF())
                    return EOF;
            }
            switch (ACTION[action]) {
                case 1:
                    throw new ParseException(position, getString(0));
                case 2:
                    currentToken = Long.parseLong(getString(0));
                    return NUMBER;
                case 4:
                    sb.setLength(0);
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
                    sb.append('\\');
                    break;
                case 13:
                    lexicalState = INITIAL;
                    currentToken = sb.toString();
                    return STRING;
                case 14:
                    sb.append('"');
                    break;
                case 15:
                    sb.append('/');
                    break;
                case 16:
                    sb.append('\b');
                    break;
                case 17:
                    sb.append('\f');
                    break;
                case 18:
                    sb.append('\n');
                    break;
                case 19:
                    sb.append('\r');
                    break;
                case 20:
                    sb.append('\t');
                    break;
                case 21:
                    currentToken = Double.parseDouble(getString(0));
                    return NUMBER;
                case 22:
                    currentToken = null;
                    return NULL;
                case 23:
                    return (boolean) (currentToken = getString(0).equalsIgnoreCase("true")) ? TRUE : FALSE;
                case 24:
                    try {
                        sb.append((char) Integer.parseInt(getString(2), 16));
                    } catch (NumberFormatException e) {
                        throw new ParseException(position, e);
                    }
                    break;
                case 11:
                    sb.append(getString(0));
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
                    throw new Error("Error: could not match input");
            }
        }
    }

    protected void checkOpen() {
        if (sb == null) throw new IllegalStateException("This JSONReader has already been closed!");
    }
}
