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
package net.vpg.vjson.parser

import net.vpg.vjson.reader.JSONReader
import net.vpg.vjson.reader.JSONReader.TokenType.*
import net.vpg.vjson.value.JSONArray
import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.JSONValue
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL

/**
 * A parser which parses JSON from a pre-configured [JSONReader].
 * This parser reads tokens through a [JSONReader], which may be passed as a method parameter or
 * constructed from the given arguments.
 * All `parse` methods have an overrode method
 *
 * @author Vaibhav Nargwani
 */
object JSONParser {
    fun parse(s: String) = parse(JSONReader(s))

    fun parse(f: File) = parse(JSONReader(f))

    fun parse(url: URL) = parse(JSONReader(url))

    fun parse(stream: InputStream) = parse(JSONReader(stream))

    fun parse(reader: Reader) = parse(JSONReader(reader))

    fun parse(reader: JSONReader) = reader.use { parseValue(it) }

    private fun parseValue(reader: JSONReader): JSONValue =
        when (reader.currentTokenType ?: reader.getNextTokenType()) {
            STRING, TRUE, FALSE, NULL, NUMBER -> JSONValue.of(reader.currentToken)
            OBJECT_START -> parseObject(reader)
            ARRAY_START -> parseArray(reader)
            else -> reader.error<JSONValue>()
        }

    private fun parseObject(reader: JSONReader) = JSONObject().also {
        reader.getNextTokenType()
        while (true) {
            val type = reader.currentTokenType
            if (type == OBJECT_END) return@also
            if (type != STRING) reader.error<Any?>()
            val key = reader.currentToken.toString()
            reader.expectNextType(COLON)
            reader.getNextTokenType()
            it.put(key, parseValue(reader))
            when (reader.getNextTokenType()) {
                OBJECT_END -> return@also
                COMMA -> reader.getNextTokenType()
                else -> reader.error<Any?>()
            }
        }
    }

    private fun parseArray(reader: JSONReader) = JSONArray().also {
        if (reader.getNextTokenType() == ARRAY_END) return@also
        while (true) {
            it.add(parseValue(reader))
            when (reader.getNextTokenType()) {
                ARRAY_END -> return@also
                COMMA -> reader.getNextTokenType()
                else -> reader.error<Any?>()
            }
        }
    }
}
