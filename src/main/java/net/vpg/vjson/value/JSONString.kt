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

class JSONString private constructor(private val value: String?) : JSONValue() {

    override val type= Type.STRING
    override val raw=value

    override fun toString(): String {
        return value!!
    }

    override fun deserialize(): String {
        return "\"" + escape(value) + "\""
    }

    companion object {
        fun of(value: String): JSONString {
            requireNotNull(value) { "value should not be null" }
            return JSONString(value)
        }

        fun escape(s: String?): String? {
            if (s == null) return null
            val builder: StringBuilder = StringBuilder(s.length)
            var i = 0
            val len: Int = s.length
            while (i < len) {
                builder.append(Companion.escape(s[i]))
                i++
            }
            return builder.toString()
        }

        fun escape(c: Char): String {
            return when (c) {
                '\\' -> "\\\\"
                '\b' -> "\\b"
                '\u000c' -> "\\f"
                '\n' -> "\\n"
                '\r' -> "\\r"
                '\t' -> "\\t"
                '"' -> "\\\""
                '/' -> "\\/"
                else -> Character.toString(c)
            }
        }

        fun unescape(s: String?): String? {
            if (s == null || !s.contains("\\")) {
                return s
            }

            val result = StringBuilder()
            var i = 0
            while (i < s.length) {
                val c: Char = s[i]
                if (c != '\\' || i + 1 == s.length) {
                    result.append(c)
                    i++
                    continue
                }
                val next: Char = s[i + 1]
                when (next) {
                    '\\' -> result.append('\\')
                    '/' -> result.append('/')
                    '"' -> result.append('"')
                    'b' -> result.append('\b')
                    'f' -> result.append('\u000c')
                    'n' -> result.append('\n')
                    'r' -> result.append('\r')
                    't' -> result.append('\t')
                    else -> result.append(c).append(next)
                }
                i++
                i++
            }

            return result.toString()
        }
    }
}
