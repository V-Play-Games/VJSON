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

class JSONArray : JSONValue, SerializableArray, JSONContainer<Int>, Iterable<JSONValue> {
    private val list: MutableList<JSONValue>

    constructor() {
        this.list = ArrayList<JSONValue>()
    }

    private constructor(list: List<*>) {
        this.list = list.asSequence().map { of(it) }.toMutableList()
    }

    val size: Int
        get() = list.size

    fun isEmpty() = list.isEmpty()

    override fun get(t: Int) = of(list[t])

    fun add(index: Int, value: Any?) = also { list.add(index, of(value)) }

    fun add(value: Any?) = also { list.add(of(value)) }

    fun addAll(values: Collection<*>) = also { values.forEach { add(it) } }

    fun addAll(array: JSONArray) = also { list.addAll(array.list) }

    fun remove(index: Int) = also { list.removeAt(index) }

    fun toList() = list

    fun <T> toList(converter: (JSONValue) -> T) = list.asSequence().map(converter).toMutableList()

    fun stream() = list.stream()

    override fun iterator() = list.iterator()

    override fun deserialize() = list.asSequence().joinToString(",", "[", "]") { it.deserialize() }

    override val type = Type.ARRAY
    override val raw: Any?
        get() = list.asSequence().map { it.raw }.toList()

    override fun toArray() = this

    override fun toPrettyString(printer: PrettyPrinter) {
        val config = printer.config
        printer.print("[")
        printer.nextLine(config.isArrayContentsOnNewLine, +1, config.isSpaceWithinBrackets)
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            iterator.next().toPrettyString(printer)
            if (!iterator.hasNext()) break
            printer.spaceIf(config.isSpaceBeforeComma)
            printer.print(",")
            printer.nextLine(config.isArrayContentsOnNewLine, 0, config.isSpaceAfterComma)
        }
        printer.nextLine(config.isArrayContentsOnNewLine, -1, config.isSpaceWithinBrackets)
        printer.print("]")
    }

    companion object {
        fun of(list: List<*>) = JSONArray(list)

        fun parse(reader: Reader) = JSONParser.parse(reader).toArray()

        fun parse(url: URL) = JSONParser.parse(url).toArray()

        fun parse(stream: InputStream) = JSONParser.parse(stream).toArray()

        fun parse(s: String) = JSONParser.parse(s).toArray()

        fun parse(f: File) = JSONParser.parse(f).toArray()

        fun parse(s: JSONReader) = JSONParser.parse(s).toArray()

        fun <T> collector() = Collector.of<T, JSONArray>(
            { JSONArray() },
            { obj, value -> obj.add(value) },
            { obj, array -> obj.addAll(array) }
        )
    }
}
