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

import java.util.Optional

interface JSONContainer<T> {
    operator fun get(t: T): JSONValue

    fun isNull(index: T) = get(index).isNull

    fun isType(index: T, type: JSONValue.Type) = get(index).type == type

    fun opt(t: T) = Optional.of(get(t)).filter { !it.isNull }

    fun <V> get(t: T, def: V, convertor: (JSONValue) -> V) = get(t).let {
        if (it.isNull) def else convertor.invoke(it)
    }

    fun getBoolean(t: T) = get(t).toBoolean()

    fun getBoolean(t: T, def: Boolean) = get(t, def) { it.toBoolean() }

    fun optBoolean(t: T) = opt(t).map { it.toBoolean() }

    fun getNumber(t: T) = get(t).toNumber()

    fun getNumber(t: T, def: Number) = get(t, def) { it.toNumber() }

    fun optNumber(t: T) = opt(t).map { it.toNumber() }

    fun getInt(t: T) = get(t).toInt()

    fun getInt(t: T, def: Int) = getNumber(t, def).toInt()

    fun optInt(t: T) = opt(t).stream().mapToInt { it.toInt() }.findFirst()

    fun getLong(t: T) = get(t).toLong()

    fun getLong(t: T, def: Long) = getNumber(t, def).toLong()

    fun optLong(t: T) = opt(t).stream().mapToLong { it.toLong() }.findFirst()

    fun getDouble(t: T) = get(t).toDouble()

    fun getDouble(t: T, def: Long) = getNumber(t, def).toDouble()

    fun optDouble(t: T) = opt(t).stream().mapToDouble { it.toDouble() }.findFirst()

    fun getString(t: T) = get(t).toString()

    fun getString(t: T, def: String?) = get(t, def) { it.toString() }

    fun optString(t: T) = opt(t).map { it.toString() }

    fun getObject(t: T) = get(t).toObject()

    fun getObject(t: T, def: JSONObject) = get(t, def, { it.toObject() })

    fun optObject(t: T) = opt(t).map { it.toObject() }

    fun getArray(t: T) = get(t).toArray()

    fun getArray(t: T, def: JSONArray) = get(t, def) { it.toArray() }

    fun optArray(t: T) = opt(t).map { it.toArray() }
}
