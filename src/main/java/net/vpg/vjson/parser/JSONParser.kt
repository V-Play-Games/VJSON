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

import net.vpg.vjson.reader.DefaultJSONReader
import net.vpg.vjson.reader.JSONReader
import net.vpg.vjson.reader.JSONReader.TokenType
import net.vpg.vjson.value.JSONArray
import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.JSONValue
import java.io.*
import java.net.URL

/**
 * A parser which parses JSON from a pre-configured [JSONReader].
 * This parser reads tokens through a [JSONReader], which may be passed as a method parameter or
 * constructed from the given arguments.
 * All `parse` methods have an overrode method
 *
 * @author Vaibhav Nargwani
 */
class JSONParser {
    @Throws(ParseException::class)
    fun parse(s: String): JSONValue? {
        return parse(DefaultJSONReader(s), true)
    }

    @Throws(ParseException::class, FileNotFoundException::class)
    fun parse(f: File?): JSONValue? {
        return parse(DefaultJSONReader(f), true)
    }

    @Throws(ParseException::class, IOException::class)
    fun parse(url: URL): JSONValue? {
        return parse(DefaultJSONReader(url), true)
    }

    @Throws(ParseException::class)
    fun parse(stream: InputStream?): JSONValue? {
        return parse(DefaultJSONReader(stream), true)
    }

    @Throws(ParseException::class)
    fun parse(reader: Reader?): JSONValue? {
        return parse(DefaultJSONReader(reader), true)
    }

    @JvmOverloads
    @Throws(ParseException::class)
    fun parse(reader: JSONReader, closeAfterParse: Boolean = false): JSONValue? {
        try {
            return parseValue(reader)
        } finally {
            if (closeAfterParse) {
                try {
                    reader.close()
                } catch (ignore: IOException) {
                    // close silently
                }
            }
        }
    }

    @Throws(ParseException::class)
    private fun parseValue(reader: JSONReader): JSONValue? {
        if (reader.currentTokenType == null) reader.nextTokenType
        return when (reader.currentTokenType) {
            TokenType.STRING, TokenType.TRUE, TokenType.FALSE, TokenType.NULL, TokenType.NUMBER -> JSONValue.of(reader.currentToken)
            TokenType.OBJECT_START -> parseObject(reader)
            TokenType.ARRAY_START -> parseArray(reader)
            else -> reader.error<JSONValue?>()
        }
    }

    @Throws(ParseException::class)
    private fun parseObject(reader: JSONReader): JSONObject {
        val `object` = JSONObject()
        reader.nextTokenType
        while (true) {
            val type = reader.currentTokenType
            if (type == TokenType.OBJECT_END) return `object`
            if (type != TokenType.STRING) reader.error<Any?>()
            val key: String? = reader.currentToken.toString()
            reader.expectNextType(TokenType.COLON)
            reader.nextTokenType
            `object`.put(key, parseValue(reader))
            when (reader.nextTokenType) {
                TokenType.OBJECT_END -> return `object`
                TokenType.COMMA -> reader.nextTokenType
                else -> {
                    reader.error<Any?>()
                    return `object`
                }
            }
        }
    }

    @Throws(ParseException::class)
    private fun parseArray(reader: JSONReader): JSONArray {
        val array = JSONArray()
        if (reader.nextTokenType == TokenType.ARRAY_END) return array
        while (true) {
            array.add(parseValue(reader))
            when (reader.nextTokenType) {
                TokenType.ARRAY_END -> return array
                TokenType.COMMA -> reader.nextTokenType
                else -> {
                    reader.error<Any?>()
                    return array
                }
            }
        }
    }
}
