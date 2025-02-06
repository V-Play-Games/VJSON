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

import net.vpg.vjson.parser.JSONParser.parseToJSON
import net.vpg.vjson.value.JSONArray.Companion.toJSON
import net.vpg.vjson.value.JSONBoolean.Companion.toJSON
import net.vpg.vjson.value.JSONNumber.Companion.toJSON
import net.vpg.vjson.value.JSONObject.Companion.toJSON
import net.vpg.vjson.value.JSONString.Companion.toJSON
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
        fun Any?.toJSONValue() = when (this) {
            null -> JSONNull
            is JSONValue -> this
            is List<*> -> this.toJSON()
            is Map<*, *> -> this.toJSON()
            is String -> this.toJSON()
            is Number -> this.toJSON()
            is Boolean -> this.toJSON()
            is SerializableArray -> this.toArray()
            is SerializableObject -> this.toObject()
            is DeserializableValue -> this.deserialize().parseToJSON()
            else -> throw UnsupportedOperationException("Cannot make JSONValue of class " + this.javaClass)
        }
    }
}
