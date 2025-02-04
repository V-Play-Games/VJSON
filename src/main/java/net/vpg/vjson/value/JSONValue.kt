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
import net.vpg.vjson.parser.ParseException
import net.vpg.vjson.reader.JSONReader
import java.io.*
import java.net.URL
import java.util.*

abstract class JSONValue : DeserializableValue {
    abstract val type: Type?

    abstract val raw: Any?

    override fun equals(o: Any?): Boolean {
        return o is JSONValue && o.raw == this.raw
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.raw)
    }

    open fun toBoolean(): Boolean {
        return error<Boolean?>(Type.BOOLEAN)!!
    }

    open fun toNumber(): Number? {
        return error<Number?>(Type.NUMBER)
    }

    open fun toObject(): JSONObject? {
        return error<JSONObject?>(Type.OBJECT)
    }

    open fun toArray(): JSONArray? {
        return error<JSONArray?>(Type.ARRAY)
    }

    fun toInt(): Int {
        return toNumber()!!.toInt()
    }

    fun toLong(): Long {
        return toNumber()!!.toLong()
    }

    fun toDouble(): Double {
        return toNumber()!!.toDouble()
    }

    open val isNull: Boolean
        get() = false

    override fun toString(): String {
        return deserialize()
    }

    private fun <T> error(type: Type?): T? {
        throw UnsupportedOperationException("Cannot cast value of type " + this.type + " to type " + type)
    }

    enum class Type {
        NULL, STRING, NUMBER, BOOLEAN, OBJECT, ARRAY
    }

    companion object {
        protected var parser: JSONParser? = null
            get() = if (field == null) JSONParser().also { field = it } else field
            private set

        @Throws(ParseException::class)
        fun parse(`in`: Reader?): JSONValue? {
            return parser!!.parse(`in`)
        }

        @Throws(ParseException::class, IOException::class)
        fun parse(url: URL): JSONValue? {
            return parser!!.parse(url)
        }

        @Throws(ParseException::class)
        fun parse(`in`: InputStream?): JSONValue? {
            return parser!!.parse(`in`)
        }

        @Throws(ParseException::class)
        fun parse(s: String): JSONValue? {
            return parser!!.parse(s)
        }

        @Throws(ParseException::class, FileNotFoundException::class)
        fun parse(f: File?): JSONValue? {
            return parser!!.parse(f)
        }

        @Throws(ParseException::class)
        fun parse(s: JSONReader): JSONValue? {
            return parser!!.parse(s)
        }

        fun of(o: Any?): JSONValue? {
            return when (o) {
                null -> JSONNull.Companion.getInstance()
                -> value
                -> JSONArray.Companion.of(list)
                -> JSONObject.Companion.of(map)
                -> JSONString.Companion.of(s)
                -> JSONNumber.Companion.of(number)
                -> JSONBoolean.Companion.of(o as Boolean)
                -> arr.toArray()
                -> obj.toObject()
                -> Companion.parse(value.deserialize())
                else -> throw UnsupportedOperationException("Cannot make JSONValue of class " + o.getClass())
            }
        }
    }
}
