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

public enum TokenType {
    EOF("EOF"),
    NUMBER("Number"),
    STRING("String"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    OBJECT_START("{"),
    OBJECT_END("}"),
    ARRAY_START("["),
    ARRAY_END("]"),
    COMMA(","),
    COLON(":");
    String value;

    TokenType(String value) {
        this.value = value;
    }
}
