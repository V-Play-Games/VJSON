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

import java.util.*
import java.util.function.*
import java.util.function.Function

interface JSONContainer<T> {
    fun get(t: T?): JSONValue

    fun isNull(index: T?): Boolean {
        return get(index).isNull()
    }

    fun isType(index: T?, type: JSONValue.Type?): Boolean {
        return get(index).getType() == type
    }

    fun opt(t: T?): Optional<JSONValue?> {
        return Optional.of<JSONValue?>(get(t)).filter(Predicate { o: JSONValue? -> !o!!.isNull() })
    }

    fun <V> get(t: T?, def: V?, convertor: Function<JSONValue?, V?>): V? {
        val `val` = get(t)
        return if (`val`.isNull()) def else convertor.apply(`val`)
    }

    fun getBoolean(t: T?): Boolean {
        return get(t).toBoolean()
    }

    fun getBoolean(t: T?, def: Boolean): Boolean {
        return get<Boolean?>(t, def, java.util.function.Function { obj: JSONValue? -> obj.toBoolean() })!!
    }

    fun optBoolean(t: T?): Optional<Boolean?> {
        return opt(t).map<Boolean?>(Function { obj: JSONValue? -> obj!!.toBoolean() })
    }

    fun getNumber(t: T?): Number? {
        return get(t).toNumber()
    }

    fun getNumber(t: T?, def: Number?): Number? {
        return get<Number?>(t, def, Function { obj: JSONValue? -> obj!!.toNumber() })
    }

    fun optNumber(t: T?): Optional<Number?> {
        return opt(t).map<Number?>(Function { obj: JSONValue? -> obj!!.toNumber() })
    }

    fun getInt(t: T?): Int {
        return get(t).toInt()
    }

    fun getInt(t: T?, def: Int): Int {
        return getNumber(t, def).intValue()
    }

    fun optInt(t: T?): OptionalInt {
        return opt(t).stream().mapToInt(ToIntFunction { obj: JSONValue? -> obj!!.toInt() }).findFirst()
    }

    fun getLong(t: T?): Long {
        return get(t).toLong()
    }

    fun getLong(t: T?, def: Long): Long {
        return getNumber(t, def).longValue()
    }

    fun optLong(t: T?): OptionalLong {
        return opt(t).stream().mapToLong(ToLongFunction { obj: JSONValue? -> obj!!.toLong() }).findFirst()
    }

    fun getDouble(t: T?): Double {
        return get(t).toLong().toDouble()
    }

    fun getDouble(t: T?, def: Long): Double {
        return getNumber(t, def).doubleValue()
    }

    fun optDouble(t: T?): OptionalDouble {
        return opt(t).stream().mapToDouble(ToDoubleFunction { obj: JSONValue? -> obj!!.toDouble() }).findFirst()
    }

    fun getString(t: T?): String? {
        return get(t).toString()
    }

    fun getString(t: T?, def: String?): String? {
        return get<String?>(t, def, Function { obj: JSONValue? -> obj.toString() })
    }

    fun optString(t: T?): Optional<String?> {
        return opt(t).map<String?>(Function { obj: JSONValue? -> obj.toString() })
    }

    fun getObject(t: T?): JSONObject? {
        return get(t).toObject()
    }

    fun getObject(t: T?, def: JSONObject?): JSONObject? {
        return get<JSONObject?>(t, def, Function { obj: JSONValue? -> obj!!.toObject() })
    }

    fun optObject(t: T?): Optional<JSONObject?> {
        return opt(t).map<JSONObject?>(Function { obj: JSONValue? -> obj!!.toObject() })
    }

    fun getArray(t: T?): JSONArray? {
        return get(t).toArray()
    }

    fun getArray(t: T?, def: JSONArray?): JSONArray? {
        return get<JSONArray?>(t, def, Function { obj: JSONValue? -> obj!!.toArray() })
    }

    fun optArray(t: T?): Optional<JSONArray?> {
        return opt(t).map<JSONArray?>(Function { obj: JSONValue? -> obj!!.toArray() })
    }
}
