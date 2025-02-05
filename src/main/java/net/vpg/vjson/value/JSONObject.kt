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
import net.vpg.vjson.pretty.PrettyPrinter
import net.vpg.vjson.reader.JSONReader
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.util.stream.Collector

class JSONObject() : JSONValue(), SerializableObject, JSONContainer<String> {
    private val map = LinkedHashMap<String, JSONValue>()

    private constructor(map: Map<*, *>) : this() {
        putAll(map)
    }

    val size: Int
        get() = map.size

    fun isEmpty() = map.isEmpty()

    override fun get(t: String) = of(map.get(t))

    fun put(key: String, value: Any?) = also { map.put(key, of(value)) }

    fun putAll(map: Map<*, *>) = also { map.forEach { k, v -> put(k.toString(), v) } }

    fun putAll(obj: JSONObject) = also { map.putAll(obj.map) }

    fun remove(key: String?) = also { map.remove(key) }

    fun toMap() = map

    fun <T> map(converter: (JSONObject) -> T) = converter.invoke(this)

    override fun deserialize() = map.entries
        .asSequence()
        .joinToString(",", "{", "}") {
            "\"" + JSONString.escape(it.key) + "\":" + it.value.deserialize()
        }

    override val type = Type.OBJECT
    override val raw
        get() = map.entries.asSequence().associate { it.key to it.value.raw }

    override fun toObject() = this

    override fun toPrettyString(printer: PrettyPrinter) {
        val config = printer.config
        printer.print("{")
        printer.nextLine(config.isObjectContentsOnNewLine, +1, config.isSpaceWithinBraces)
        val iterator = map.entries.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            printer.print("\"" + JSONString.Companion.escape(next.key) + "\"")
            printer.spaceIf(config.isSpaceBeforeColon)
            printer.print(":")
            printer.spaceIf(config.isSpaceAfterColon)
            next.value.toPrettyString(printer)
            if (!iterator.hasNext()) break
            printer.spaceIf(config.isSpaceBeforeComma)
            printer.print(",")
            printer.nextLine(config.isObjectContentsOnNewLine, 0, config.isSpaceAfterComma)
        }
        printer.nextLine(config.isObjectContentsOnNewLine, -1, config.isSpaceWithinBraces)
        printer.print("}")
    }

    companion object {
        fun of(map: Map<*, *>) = JSONObject(map)

        fun parse(reader: Reader) = JSONParser.parse(reader).toObject()

        @JvmStatic
        fun parse(url: URL) = JSONParser.parse(url).toObject()

        fun parse(stream: InputStream) = JSONParser.parse(stream).toObject()

        fun parse(s: String) = JSONParser.parse(s).toObject()

        fun parse(f: File) = JSONParser.parse(f).toObject()

        fun parse(s: JSONReader) = JSONParser.parse(s).toObject()

        fun <T> collector(keyMapper: (T) -> String, valueMapper: (T) -> Any?) = Collector.of<T, JSONObject>(
            { JSONObject() },
            { obj, e -> obj.put(keyMapper.invoke(e), valueMapper.invoke(e)) },
            { obj, other -> obj.putAll(other) }
        )
    }
}
