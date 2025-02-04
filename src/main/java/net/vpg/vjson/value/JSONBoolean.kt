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

class JSONBoolean private constructor(private val value: Boolean) : JSONValue() {
    override fun getType(): Type {
        return Type.BOOLEAN
    }

    override fun toBoolean(): Boolean {
        return value
    }

    override fun getRaw(): Any {
        return value
    }

    override fun deserialize(): String {
        return java.lang.Boolean.toString(value)
    }

    companion object {
        private val TRUE = JSONBoolean(true)
        private val FALSE = JSONBoolean(false)
        fun ofTrue(): JSONBoolean {
            return TRUE
        }

        fun ofFalse(): JSONBoolean {
            return FALSE
        }

        fun of(value: Boolean): JSONBoolean? {
            return if (value) TRUE else FALSE
        }
    }
}
