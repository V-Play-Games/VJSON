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
package net.vpg.vjson.parser;

public class ParseException extends RuntimeException {
    public static final int UNEXPECTED_TOKEN = 0;
    public static final int UNEXPECTED_EXCEPTION = 1;
    private final int type;
    private final int position;
    private final String token;

    public ParseException(int position, String token) {
        super("Unexpected token " + token + " at position " + position);
        this.position = position;
        this.type = UNEXPECTED_TOKEN;
        this.token = token;
    }

    public ParseException(int position, Throwable cause) {
        super("Unexpected exception at position " + position, cause);
        this.position = position;
        this.type = UNEXPECTED_EXCEPTION;
        this.token = null;
    }

    public int getType() {
        return type;
    }

    /**
     * @return The character position (starting with 0) of the input where the error occurs.
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the unexpected token, or null if {@code type} is {@link #UNEXPECTED_EXCEPTION}
     */
    public Object getToken() {
        return token;
    }
}
