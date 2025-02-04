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
package net.vpg.vjson.reader

import net.vpg.vjson.parser.ParseException
import java.io.*
import java.lang.Double
import java.lang.Long
import java.net.URL
import java.util.Map
import java.util.function.Function
import kotlin.Any
import kotlin.Boolean
import kotlin.Char
import kotlin.CharArray
import kotlin.Int
import kotlin.Number
import kotlin.String
import kotlin.Throws
import kotlin.also
import kotlin.check
import kotlin.toString

class JSONReader : Closeable {
    var currentTokenType: TokenType? = null
        private set
    var currentToken: Any? = null
        private set
    var nextTokenType: TokenType? = null
        private set
        get() {
            currentTokenType = field
            field = getNextTokenType0()
            return field
        }
    var nextToken: Any? = null
        private set

    @Throws(ParseException::class)
    fun expectNextType(type: TokenType?) {
        if (this.nextTokenType != type) error<Any?>()
    }

    @Throws(ParseException::class)
    fun <T> error(): T {
        throw ParseException(this.position, java.lang.String.valueOf(this.currentToken))
    }

    enum class TokenType {
        EOF, NUMBER, STRING, TRUE, FALSE, NULL, OBJECT_START, OBJECT_END, ARRAY_START, ARRAY_END, COMMA, COLON
    }
    private val close: Boolean
    private val isStringBased: Boolean
    private var builder: StringBuilder? = StringBuilder()
    private var reader: Reader? = null
    private var totalPos = -1
    private var position = -1
    private var lastPos = 0
    private var buffer: CharArray?

    constructor(f: File) : this(FileReader(f))

    constructor(url: URL) : this(url.openStream(), true)

    @JvmOverloads
    constructor(`in`: InputStream, close: Boolean = false) : this(InputStreamReader(`in`), close)

    @JvmOverloads
    constructor(`in`: Reader?, close: Boolean = false) {
        buffer = CharArray(1048576)
        reader = `in`
        this.close = close
        isStringBased = false
        buffer()
    }

    constructor(s: String) {
        buffer = s.toCharArray()
        isStringBased = true
        close = false
        lastPos = s.length
    }

    private fun buffer(): Boolean {
        try {
            position = -1
            val numRead = reader!!.read(buffer, 0, buffer!!.size)
            if (numRead == -1) {
                return true
            }
            lastPos = numRead
            return false
        } catch (exc: IOException) {
            throw ParseException(position, exc)
        }
    }

    private fun nextChar(): Char {
        if (this.isEOF) error<Any?>()
        ++totalPos
        return buffer!![++position]
    }

    private fun decrementPosition() {
        totalPos--
        position--
    }

    private val isEOF: Boolean
        get() = position + 1 == lastPos && (isStringBased || buffer())

    fun getNextTokenType0(): TokenType? {
        checkOpen()
        if (this.isEOF) return TokenType.EOF
        val c = nextChar()
        if (Character.isDigit(c) || c == '-') {
            currentToken = this.number
            return TokenType.NUMBER
        }
        currentToken =
            tokenMap.getOrDefault(c, java.util.function.Function { r: JSONReader? -> c })!!.apply(this)
        if (" \u0000\t\r\n".indexOf(c) >= 0) return getNextTokenType0()
        val type: TokenType? = typeMap.get(c)
        if (type == null) error<Any?>()
        return type
    }

    private val string: String
        get() {
            while (true) {
                var c = nextChar()
                when (c) {
                    '\b', '\u000c', '\n', '\r', '\t' -> {
                        error<Any?>()
                        return this.builderString
                    }

                    '"' -> return this.builderString
                    '\\' -> {
                        c = when (nextChar()) {
                            '"' -> '\"'
                            '\\' -> '\\'
                            '/' -> '/'
                            'b' -> '\b'
                            'f' -> '\u000c'
                            'n' -> '\n'
                            'r' -> '\r'
                            't' -> '\t'
                            'u' -> (nextHexChar() shl 12 or (nextHexChar() shl 8) or (nextHexChar() shl 4) or nextHexChar()).toChar()
                            else -> error<Char>()
                        }
                        builder!!.append(c)
                    }

                    else -> builder!!.append(c)
                }
            }
        }

    private fun nextHexChar(): Int {
        val c = Character.digit(nextChar(), 16)
        if (c == -1) error<Any?>()
        return c
    }

    private val number: Number
        get() {
            decrementPosition()
            var c: Char
            while ("0123456789.+-eE".indexOf(nextChar().also { c = it }) >= 0) {
                builder!!.append(c)
            }
            decrementPosition()
            val s = this.builderString
            // Don't use ternary to avoid casting to Double
            if (s.contains(".")) return Double.parseDouble(s)
            else return Long.parseLong(s)
        }

    private val builderString: String
        get() {
            val tor = builder.toString()
            builder!!.setLength(0)
            return tor
        }

    private fun checkToken(token: String) {
        decrementPosition()
        for (i in 0..<token.length) if (token[i] != nextChar()) error<Any?>()
    }

    @Throws(IOException::class)
    override fun close() {
        if (lastPos == -1) return
        lastPos = -1
        totalPos = 0
        position = 0
        currentTokenType = null
        builder = null
        currentToken = null
        buffer = null
        if (close) reader!!.close()
        reader = null
    }

    fun checkOpen() {
        check(lastPos != -1) { "This JSONReader has already been closed!" }
    }

    companion object {
        private val typeMap: MutableMap<Char?, TokenType?> = Map.of<Char?, TokenType?>(
            '{', TokenType.OBJECT_START,
            '}', TokenType.OBJECT_END,
            '[', TokenType.ARRAY_START,
            ']', TokenType.ARRAY_END,
            ',', TokenType.COMMA,
            ':', TokenType.COLON,
            '"', TokenType.STRING,
            't', TokenType.TRUE,
            'f', TokenType.FALSE,
            'n', TokenType.NULL
        )
        private val tokenMap: MutableMap<Char?, Function<JSONReader?, Any?>?> =
            Map.of<Char?, Function<JSONReader?, Any?>?>(
                '"', Function { obj: JSONReader? -> obj!!.string },
                't', Function { reader: JSONReader? ->
                    reader!!.checkToken("true")
                    true
                },
                'f', Function { reader: JSONReader? ->
                    reader!!.checkToken("false")
                    false
                },
                'n', Function { reader: JSONReader? ->
                    reader!!.checkToken("null")
                    null
                }
            )
    }
}
