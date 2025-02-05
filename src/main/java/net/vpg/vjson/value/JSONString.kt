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

class JSONString private constructor(private val value: String) : JSONValue() {
    override val type = Type.STRING
    override val raw = value

    override fun toString() = value

    override fun deserialize() = "\"" + escape(value) + "\""

    companion object {
        fun of(value: String) = JSONString(requireNotNull(value) { "value should not be null" })

        fun escape(s: String) = buildString(s.length) {
            for (c in s) {
                append(escape(c))
            }
        }

        fun escape(c: Char) = when (c) {
            '\\' -> "\\\\"
            '\b' -> "\\b"
            '\u000c' -> "\\f"
            '\n' -> "\\n"
            '\r' -> "\\r"
            '\t' -> "\\t"
            '"' -> "\\\""
            '/' -> "\\/"
            else -> c.toString()
        }

        fun unescape(s: String) = if (!s.contains("\\")) s else buildString {
            var i = 0
            while (i < s.length) {
                val c = s[i]
                if (c != '\\' || i + 1 == s.length) {
                    append(c)
                    i++
                    continue
                }
                val next = s[i + 1]
                when (next) {
                    '\\' -> append('\\')
                    '/' -> append('/')
                    '"' -> append('"')
                    'b' -> append('\b')
                    'f' -> append('\u000c')
                    'n' -> append('\n')
                    'r' -> append('\r')
                    't' -> append('\t')
                    else -> append(c).append(next)
                }
                i += 2
            }
        }
    }
}
