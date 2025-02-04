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

import net.vpg.vjson.parser.JSONParser.parse
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
import java.util.stream.Stream

class JSONArray : JSONValue, SerializableArray, JSONContainer<Int?> {
    private val list: MutableList<JSONValue?>

    constructor() {
        this.list = ArrayList<JSONValue?>()
    }

    private constructor(list: MutableList<*>) {
        this.list = list.stream().map<JSONValue?> { o: Any? -> JSONValue.Companion.of(o) }.collect(Collectors.toList())
    }

    fun size(): Int {
        return list.size
    }

    val isEmpty: Boolean
        get() = list.isEmpty()

    override fun get(index: Int): JSONValue? {
        return JSONValue.Companion.of(list.get(index))
    }

    fun add(index: Int, value: Any?): JSONArray {
        list.add(index, JSONValue.Companion.of(value))
        return this
    }

    fun add(value: Any?): JSONArray {
        list.add(JSONValue.Companion.of(value))
        return this
    }

    fun addAll(values: MutableCollection<*>): JSONArray {
        values.forEach { value: Any? -> this.add(value) }
        return this
    }

    fun addAll(array: JSONArray): JSONArray {
        list.addAll(array.list)
        return this
    }

    fun remove(index: Int): JSONArray {
        list.removeAt(index)
        return this
    }

    fun toList(): MutableList<JSONValue?> {
        return list
    }

    fun <T> toList(converter: Function<JSONValue?, T?>?): MutableList<T?> {
        return list.stream()
            .map<T?>(converter)
            .collect(Collectors.toList())
    }

    fun stream(): Stream<JSONValue?> {
        return list.stream()
    }

    override fun deserialize(): String {
        return list.stream().map<String?> { obj: JSONValue? -> obj!!.deserialize() }
            .collect(Collectors.joining(",", "[", "]"))
    }

    override fun getType(): Type {
        return Type.ARRAY
    }

    override fun getRaw(): Any {
        return list.stream().map<Any?> { obj: JSONValue? -> obj!!.getRaw() }.collect(Collectors.toList())
    }

    override fun toArray(): JSONArray {
        return this
    }

    override fun toPrettyString(printer: PrettyPrinter) {
        val config = printer.getConfig()
        printer.print("[")
        printer.nextLine(config.isArrayContentsOnNewLine(), +1, config.isSpaceWithinBrackets())
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            iterator.next()!!.toPrettyString(printer)
            if (!iterator.hasNext()) break
            printer.spaceIf(config.isSpaceBeforeComma())
            printer.print(",")
            printer.nextLine(config.isArrayContentsOnNewLine(), 0, config.isSpaceAfterComma())
        }
        printer.nextLine(config.isArrayContentsOnNewLine(), -1, config.isSpaceWithinBrackets())
        printer.print("]")
    }

    companion object {
        fun of(list: MutableList<*>): JSONArray {
            return JSONArray(list)
        }

        @Throws(ParseException::class)
        fun parse(`in`: Reader?): JSONArray? {
            return JSONValue.Companion.getParser().parse(`in`).toArray()
        }

        @Throws(ParseException::class, IOException::class)
        fun parse(url: URL): JSONArray? {
            return JSONValue.Companion.getParser().parse(url).toArray()
        }

        @Throws(ParseException::class)
        fun parse(`in`: InputStream?): JSONArray? {
            return JSONValue.Companion.getParser().parse(`in`).toArray()
        }

        @Throws(ParseException::class)
        fun parse(s: String): JSONArray? {
            return JSONValue.Companion.getParser().parse(s).toArray()
        }

        @Throws(ParseException::class, FileNotFoundException::class)
        fun parse(f: File?): JSONArray? {
            return JSONValue.Companion.getParser().parse(f).toArray()
        }

        @Throws(ParseException::class)
        fun parse(s: JSONReader): JSONArray? {
            return JSONValue.Companion.getParser().parse(s).toArray()
        }

        fun <T> collector(): Collector<T?, *, JSONArray?> {
            return Collector.of<T?, JSONArray?>(
                Supplier { JSONArray() },
                BiConsumer { obj: JSONArray?, value: T? -> obj!!.add(value) },
                BinaryOperator { obj: JSONArray?, array: JSONArray -> obj!!.addAll(array) })
        }
    }
}
