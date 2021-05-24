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

import net.vplaygames.vjson.JSONValue;

public class ParseException extends RuntimeException {
    public static final int UNEXPECTED_CHAR = 0;
    public static final int UNEXPECTED_TOKEN = 1;
    public static final int UNEXPECTED_EXCEPTION = 2;
    private final int errorType;
    private final int position;
    private final Object unexpectedObject;

    public ParseException(int position, char c) {
        super("Unexpected character (" + JSONValue.escape(c + "") + ") at position " + position);
        this.position = position;
        this.errorType = UNEXPECTED_CHAR;
        this.unexpectedObject = c;
    }

    public ParseException(int position, String token) {
        super("Unexpected token " + JSONValue.escape(token) + " at position " + position);
        this.position = position;
        this.errorType = UNEXPECTED_TOKEN;
        this.unexpectedObject = token;
    }

    public ParseException(int position, Throwable cause) {
        super("Unexpected exception at position " + position, cause);
        this.position = position;
        this.errorType = UNEXPECTED_EXCEPTION;
        this.unexpectedObject = cause;
    }

    public int getErrorType() {
        return errorType;
    }

    /**
     * @return The character position (starting with 0) of the input where the error occurs.
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return One of the following base on the value of errorType:
     * ERROR_UNEXPECTED_CHAR      - {@link Character}
     * ERROR_UNEXPECTED_TOKEN     - {@link TokenType}
     * ERROR_UNEXPECTED_EXCEPTION - {@link Throwable}
     * @see TokenType
     */
    public Object getUnexpectedObject() {
        return unexpectedObject;
    }
}
