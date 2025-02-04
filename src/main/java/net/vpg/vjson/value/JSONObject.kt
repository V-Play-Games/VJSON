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

import net.vpg.vjson.parser.ParseException
import net.vpg.vjson.pretty.PrettyPrinter
import net.vpg.vjson.reader.JSONReader
import java.io.*
import java.net.URL
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Throws
import kotlin.collections.LinkedHashMap
import kotlin.collections.MutableIterator
import kotlin.collections.MutableMap
import kotlin.collections.iterator
import kotlin.collections.map
import kotlin.collections.remove
import kotlin.io.iterator
import kotlin.map
import kotlin.sequences.map
import kotlin.text.iterator
import kotlin.text.map

class JSONObject() : JSONValue(), SerializableObject, JSONContainer<kotlin.String> {
    private val map: MutableMap<String?, JSONValue?>

    init {
        map = LinkedHashMap<String?, JSONValue?>()
    }

    private constructor(map: Map<*, *>) : this() {
        putAll(map)
    }

    fun size(): Int {
        return map.size
    }

    val isEmpty: Boolean
        get() = map.isEmpty()

    override fun get(key: String): JSONValue {
        return JSONValue.Companion.of(map.get(key))
    }

    fun put(key: String?, `val`: Any?): JSONObject {
        map.put(key, JSONValue.Companion.of(`val`))
        return this
    }

    fun putAll(map: Map<*, *>): JSONObject {
        map.forEach { k: Any?, v: Any? -> put(k?.toString(), v) }
        return this
    }

    fun putAll(`object`: JSONObject): JSONObject {
        map.putAll(`object`.map)
        return this
    }

    fun remove(key: kotlin.String?): JSONObject {
        map.remove(key)
        return this
    }

    fun toMap(): MutableMap<kotlin.String?, JSONValue?> {
        return map
    }

    fun <T> map(converter: Function<JSONObject?, T?>): T? {
        return converter.apply(this)
    }

    override fun deserialize(): kotlin.String {
        return map.entries
                .stream()
                .map<kotlin.String?>(Function { e: MutableMap.MutableEntry<kotlin.String?, JSONValue?>? ->
                    "\"" + JSONString.Companion.escape(
                            e?.key
                    ) + "\":" + e?.value?.deserialize()
                })
                .collect(Collectors.joining(",", "{", "}"))
    }

    override val type: Type?
        get() = Type.OBJECT
    override val raw: Any?
        get() = map.entries.stream().collect(
                Collectors.toMap(
                        Function { it.key },
                        Function { entry: MutableMap.MutableEntry<kotlin.String?, JSONValue?>? -> entry?.value?.raw })
        )

    override fun toObject(): JSONObject {
        return this
    }

    override fun toPrettyString(printer: PrettyPrinter) {
        val config = printer.config
        printer.print("{")
        printer.nextLine(config.isObjectContentsOnNewLine, +1, config.isSpaceWithinBraces)
        val iterator: MutableIterator<MutableMap.MutableEntry<kotlin.String?, JSONValue?>?> = map.entries.iterator()
        while (iterator.hasNext()) {
            val next: MutableMap.MutableEntry<kotlin.String?, JSONValue?> = iterator.next()!!
            printer.print("\"" + JSONString.Companion.escape(next.key) + "\"")
            printer.spaceIf(config.isSpaceBeforeColon)
            printer.print(":")
            printer.spaceIf(config.isSpaceAfterColon)
            next.value?.toPrettyString(printer)
            if (!iterator.hasNext()) break
            printer.spaceIf(config.isSpaceBeforeComma)
            printer.print(",")
            printer.nextLine(config.isObjectContentsOnNewLine, 0, config.isSpaceAfterComma)
        }
        printer.nextLine(config.isObjectContentsOnNewLine, -1, config.isSpaceWithinBraces)
        printer.print("}")
    }

    companion object {
        fun of(map: Map<Any?, Any?>): JSONObject {
            return JSONObject(map)
        }

        @Throws(ParseException::class)
        fun parse(`in`: Reader?): JSONObject? {
            return JSONValue.Companion.parser?.parse(`in`)?.toObject()
        }

        @JvmStatic
        @Throws(ParseException::class, IOException::class)
        fun parse(url: URL): JSONObject? {
            return JSONValue.Companion.parser?.parse(url)?.toObject()
        }

        @Throws(ParseException::class)
        fun parse(`in`: InputStream): JSONObject? {
            return JSONValue.Companion.parser?.parse(`in`)?.toObject()
        }

        @Throws(ParseException::class)
        fun parse(s: kotlin.String): JSONObject? {
            return JSONValue.Companion.parser?.parse(s)?.toObject()
        }

        @Throws(ParseException::class, FileNotFoundException::class)
        fun parse(f: File): JSONObject? {
            return JSONValue.Companion.parser?.parse(f)?.toObject()
        }

        @Throws(ParseException::class)
        fun parse(s: JSONReader): JSONObject? {
            return JSONValue.Companion.parser?.parse(s)?.toObject()
        }

        fun <T> collector(
            keyMapper: Function<T?, kotlin.String?>,
            valueMapper: Function<T?, *>
        ): Collector<T?, *, JSONObject?> {
            return Collector.of<T?, JSONObject?>(
                Supplier { JSONObject() },
                BiConsumer { obj: JSONObject?, e: T? -> obj!!.put(keyMapper.apply(e), valueMapper.apply(e)) },
                BinaryOperator { obj: JSONObject?, `object`: JSONObject -> obj!!.putAll(`object`) })
        }
    }
}
