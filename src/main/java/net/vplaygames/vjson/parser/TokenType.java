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

public class TokenType {
    private TokenType() {
        // the private constructor to not allow instances
    }

    //@formatter:off
    /** End of File */
    public static final int EOF = -1;
    /** Number */
    public static final int NUMBER = 0;
    /** String */
    public static final int STRING = 1;
    /** true */
    public static final int TRUE = 2;
    /** false */
    public static final int FALSE = 3;
    /** null */
    public static final int NULL = 4;
    /** Left Curly Brace ({) */
    public static final int OBJECT_START = 5;
    /** Right Curly Brace (}) */
    public static final int OBJECT_END = 6;
    /** Left Square Brace ([) */
    public static final int ARRAY_START = 7;
    /** Right Square Brace (]) */
    public static final int ARRAY_END = 8;
    /** Comma (,) */
    public static final int COMMA = 9;
    /** Colon (:) */
    public static final int COLON = 10;
    //@formatter:on
}
