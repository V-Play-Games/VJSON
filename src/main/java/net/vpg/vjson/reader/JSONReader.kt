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
package net.vpg.vjson.reader

import net.vpg.vjson.parser.ParseException
import java.io.Closeable
import java.io.IOException
import java.lang.String
import kotlin.Any
import kotlin.Int
import kotlin.Throws

interface JSONReader : Closeable {
    val position: Int

    val currentTokenType: TokenType?

    val nextTokenType: TokenType?

    val currentToken: Any?

    @get:Throws(IOException::class)
    val nextToken: Any?

    @Throws(ParseException::class)
    fun expectNextType(type: TokenType?) {
        if (this.nextTokenType != type) error<Any?>()
    }

    @Throws(ParseException::class)
    fun <T> error(): T {
        throw ParseException(this.position, String.valueOf(this.currentToken))
    }

    enum class TokenType {
        EOF, NUMBER, STRING, TRUE, FALSE, NULL, OBJECT_START, OBJECT_END, ARRAY_START, ARRAY_END, COMMA, COLON
    }
}
