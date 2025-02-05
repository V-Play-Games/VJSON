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
import java.net.URL
import java.util.Map
import java.util.function.Function

class JSONReader : Closeable {
    var currentTokenType: TokenType? = null
        private set
    var currentToken: Any? = null
        private set

    fun getNextTokenType() = getNextTokenType0().also { currentTokenType = it }

    fun expectNextType(type: TokenType?) {
        if (this.getNextTokenType() != type) error<Any?>()
    }

    fun <T> error(): T = throw ParseException(position, currentToken.toString())

    enum class TokenType {
        EOF, NUMBER, STRING, TRUE, FALSE, NULL, OBJECT_START, OBJECT_END, ARRAY_START, ARRAY_END, COMMA, COLON
    }

    private val close: Boolean
    private val isStringBased: Boolean
    private var builder = StringBuilder()
    private var reader: Reader? = null
    private var totalPos = -1
    private var position = -1
    private var lastPos = 0
    private var buffer: CharArray

    constructor(f: File) : this(FileReader(f))

    constructor(url: URL) : this(url.openStream(), true)

    @JvmOverloads
    constructor(stream: InputStream, close: Boolean = false) : this(InputStreamReader(stream), close)

    @JvmOverloads
    constructor(reader: Reader?, close: Boolean = false) {
        buffer = CharArray(1048576)
        this.reader = reader
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
            val numRead = reader!!.read(buffer, 0, buffer.size)
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
        if (isEOF) error<Any?>()
        ++totalPos
        return buffer[++position]
    }

    private fun decrementPosition() {
        totalPos--
        position--
    }

    private val isEOF: Boolean
        get() = position + 1 == lastPos && (isStringBased || buffer())

    fun getNextTokenType0(): TokenType? {
        checkOpen()
        if (isEOF) return TokenType.EOF
        val c = nextChar()
        if (Character.isDigit(c) || c == '-') {
            currentToken = readNumber()
            return TokenType.NUMBER
        }
        currentToken =
            tokenMap.getOrDefault(c) { c }!!.apply(this)
        if (" \u0000\t\r\n".indexOf(c) >= 0) return getNextTokenType0()
        val type: TokenType? = typeMap.get(c)
        if (type == null) error<Any?>()
        return type
    }

    private fun readString(): String {
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
                        'u' -> (hex() shl 4 or hex() shl 4 or hex() shl 4 or hex()).toChar()
                        else -> error<Char>()
                    }
                    builder.append(c)
                }

                else -> builder.append(c)
            }
        }
    }

    private fun hex() = Character.digit(nextChar(), 16)
        .takeIf { it != -1 } ?: error<Int>()

    private fun readNumber(): Number {
        decrementPosition()
        var c: Char
        while ("0123456789.+-eE".indexOf(nextChar().also { c = it }) >= 0) {
            builder.append(c)
        }
        decrementPosition()
        return builderString.let { if (it.contains(".")) it.toDouble() else it.toLong() }
    }

    private val builderString: String
        get() = builder.toString().also { builder.setLength(0) }

    private fun checkToken(token: String) {
        decrementPosition()
        for (c in token) if (c != nextChar()) error<Any?>()
    }

    override fun close() {
        if (lastPos == -1) return
        lastPos = -1
        totalPos = 0
        position = 0
        currentTokenType = null
        currentToken = null
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
                '"', Function { obj: JSONReader? -> obj!!.readString() },
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
