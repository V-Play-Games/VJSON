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
import net.vpg.vjson.reader.JSONReader.TokenType
import java.io.*
import java.net.URL

class DefaultJSONReader : AbstractJSONReader {
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

    override fun getPosition(): Int {
        checkOpen()
        return totalPos
    }

    private fun decrementPosition() {
        totalPos--
        position--
    }

    private val isEOF: Boolean
        get() = position + 1 == lastPos && (isStringBased || buffer())

    val nextTokenType0: TokenType?
        get() {
            if (this.isEOF) return TokenType.EOF
            val c = nextChar()
            if (" \u0000\t\r\n".indexOf(c) >= 0) return field
            if (Character.isDigit(c) || c == '-') {
                currentToken = this.number
                return TokenType.NUMBER
            }
            currentToken = when (c) {
                '"' -> this.string
                't' -> checkToken(true)
                'f' -> checkToken(false)
                'n' -> checkToken(null)
                else -> c
            }
            return when (c) {
                '{' -> TokenType.OBJECT_START
                '}' -> TokenType.OBJECT_END
                '[' -> TokenType.ARRAY_START
                ']' -> TokenType.ARRAY_END
                ',' -> TokenType.COMMA
                ':' -> TokenType.COLON
                '"' -> TokenType.STRING
                't' -> TokenType.TRUE
                'f' -> TokenType.FALSE
                'n' -> TokenType.NULL
                else -> error<TokenType?>()
            }
        }

    private val string: String
        get() {
            while (true) {
                var c = nextChar()
                when (c) {
                    '\b', '\f', '\n', '\r', '\t' -> {
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
                            'f' -> '\f'
                            'n' -> '\n'
                            'r' -> '\r'
                            't' -> '\t'
                            'u' -> (hex() shl 12 or (hex() shl 8) or (hex() shl 4) or hex()).toChar()
                            else -> error<kotlin.Char?>()
                        }!!
                        builder!!.append(c)
                    }

                    else -> builder!!.append(c)
                }
            }
        }

    private fun hex(): Int {
        val c = nextChar().digitToIntOrNull(16) ?: -1
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
            if (s.contains(".")) return s.toDouble()
            else return s.toLong()
        }

    private val builderString: String
        get() {
            val tor = builder.toString()
            builder!!.setLength(0)
            return tor
        }

    private fun checkToken(token: Any?): Any? {
        decrementPosition()
        val s = token.toString()
        for (i in 0..<s.length) if (s.get(i) != nextChar()) error<Any?>()
        return token
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

    override fun checkOpen() {
        check(lastPos != -1) { "This JSONReader has already been closed!" }
    }
}
