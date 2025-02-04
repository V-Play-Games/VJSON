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
package net.vpg.vjson.pretty

class PrettyPrintConfig {
    var indent: String? = "  "
        private set
    var isSpaceWithinBraces: Boolean = false
        private set
    var isSpaceWithinBrackets: Boolean = false
        private set
    var isSpaceBeforeComma: Boolean = false
        private set
    var isSpaceAfterComma: Boolean = true
        private set
    var isSpaceBeforeColon: Boolean = false
        private set
    var isSpaceAfterColon: Boolean = true
        private set
    var isArrayContentsOnNewLine: Boolean = true
        private set
    var isObjectContentsOnNewLine: Boolean = true
        private set

    fun setIndent(indent: String?): PrettyPrintConfig {
        this.indent = indent
        return this
    }

    fun setSpaceWithinBraces(spaceWithinBraces: Boolean): PrettyPrintConfig {
        this.isSpaceWithinBraces = spaceWithinBraces
        return this
    }

    fun setSpaceWithinBrackets(spaceWithinBrackets: Boolean): PrettyPrintConfig {
        this.isSpaceWithinBrackets = spaceWithinBrackets
        return this
    }

    fun setSpaceBeforeComma(spaceBeforeComma: Boolean): PrettyPrintConfig {
        this.isSpaceBeforeComma = spaceBeforeComma
        return this
    }

    fun setSpaceAfterComma(spaceAfterComma: Boolean): PrettyPrintConfig {
        this.isSpaceAfterComma = spaceAfterComma
        return this
    }

    fun setSpaceBeforeColon(spaceBeforeColon: Boolean): PrettyPrintConfig {
        this.isSpaceBeforeColon = spaceBeforeColon
        return this
    }

    fun setSpaceAfterColon(spaceAfterColon: Boolean): PrettyPrintConfig {
        this.isSpaceAfterColon = spaceAfterColon
        return this
    }

    fun setArrayContentsOnNewLine(arrayContentsOnNewLine: Boolean): PrettyPrintConfig {
        this.isArrayContentsOnNewLine = arrayContentsOnNewLine
        return this
    }

    fun setObjectContentsOnNewLine(objectContentsOnNewLine: Boolean): PrettyPrintConfig {
        this.isObjectContentsOnNewLine = objectContentsOnNewLine
        return this
    }
}
