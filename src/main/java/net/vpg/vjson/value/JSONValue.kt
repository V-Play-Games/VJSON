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
package net.vpg.vjson.value

import net.vpg.vjson.parser.JSONParser
import net.vpg.vjson.reader.JSONReader
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.util.*

abstract class JSONValue : DeserializableValue {
    abstract val type: Type

    abstract val raw: Any?

    override fun equals(other: Any?) = other is JSONValue && other.raw == this.raw

    override fun hashCode() = Objects.hashCode(raw)

    open fun toBoolean() = error<Boolean>(Type.BOOLEAN)

    open fun toNumber() = error<Number>(Type.NUMBER)

    open fun toObject() = error<JSONObject>(Type.OBJECT)

    open fun toArray() = error<JSONArray>(Type.ARRAY)

    fun toInt() = toNumber().toInt()

    fun toLong() = toNumber().toLong()

    fun toDouble() = toNumber().toDouble()

    open val isNull = false

    override fun toString() = deserialize()

    private fun <T> error(type: Type): T =
        throw UnsupportedOperationException("Cannot cast value of type " + this.type + " to type " + type)

    enum class Type {
        NULL, STRING, NUMBER, BOOLEAN, OBJECT, ARRAY
    }

    companion object {
        fun parse(reader: Reader) = JSONParser.parse(reader)

        fun parse(url: URL) = JSONParser.parse(url)

        fun parse(stream: InputStream) = JSONParser.parse(stream)

        fun parse(s: String) = JSONParser.parse(s)

        fun parse(f: File) = JSONParser.parse(f)

        fun parse(s: JSONReader) = JSONParser.parse(s)

        fun of(o: Any?) = when (o) {
            null -> JSONNull
            is JSONValue -> o
            is List<*> -> JSONArray.of(o)
            is Map<*, *> -> JSONObject.of(o)
            is String -> JSONString.of(o)
            is Number -> JSONNumber.of(o)
            is Boolean -> JSONBoolean.of(o)
            is SerializableArray -> o.toArray()
            is SerializableObject -> o.toObject()
            is DeserializableValue -> parse(o.deserialize())
            else -> throw UnsupportedOperationException("Cannot make JSONValue of class " + o.javaClass)
        }
    }
}
