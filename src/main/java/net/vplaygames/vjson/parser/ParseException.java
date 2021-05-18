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
    private int errorType;
    private int position;
    private Object unexpectedObject;

    public ParseException(int position, int errorType, Object unexpectedObject) {
        this.position = position;
        this.errorType = errorType;
        this.unexpectedObject = unexpectedObject;
    }

    public ParseException(Throwable cause) {
        super(cause);
        this.position = -1;
        this.errorType = UNEXPECTED_EXCEPTION;
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

    public String toString() {
        switch (errorType) {
            case UNEXPECTED_CHAR:
                return "Unexpected character (" + JSONValue.escape(unexpectedObject + "") + ") at position " + position + ".";
            case UNEXPECTED_TOKEN:
                return "Unexpected token " + unexpectedObject + " at position " + position + ".";
            case UNEXPECTED_EXCEPTION:
                return "Unexpected exception at position " + position + ": " + JSONValue.escape(String.valueOf(unexpectedObject));
            default:
                return "Unknown error at position " + position + ".";
        }
    }
}
