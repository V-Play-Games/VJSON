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
    var indent = "  "
    var isSpaceWithinBraces = false
    var isSpaceWithinBrackets = false
    var isSpaceBeforeComma = false
    var isSpaceAfterComma = true
    var isSpaceBeforeColon = false
    var isSpaceAfterColon = true
    var isArrayContentsOnNewLine = true
    var isObjectContentsOnNewLine = true

    fun setIndent(indent: String) = also { this.indent = indent }

    fun setSpaceWithinBraces(spaceWithinBraces: Boolean) = also { isSpaceWithinBraces = spaceWithinBraces }

    fun setSpaceWithinBrackets(spaceWithinBrackets: Boolean) = also { isSpaceWithinBrackets = spaceWithinBrackets }

    fun setSpaceBeforeComma(spaceBeforeComma: Boolean) = also { isSpaceBeforeComma = spaceBeforeComma }

    fun setSpaceAfterComma(spaceAfterComma: Boolean) = also { isSpaceAfterComma = spaceAfterComma }

    fun setSpaceBeforeColon(spaceBeforeColon: Boolean) = also { isSpaceBeforeColon = spaceBeforeColon }

    fun setSpaceAfterColon(spaceAfterColon: Boolean) = also { isSpaceAfterColon = spaceAfterColon }

    fun setArrayContentsOnNewLine(arrayContentsOnNewLine: Boolean) =
        also { isArrayContentsOnNewLine = arrayContentsOnNewLine }

    fun setObjectContentsOnNewLine(objectContentsOnNewLine: Boolean) =
        also { isObjectContentsOnNewLine = objectContentsOnNewLine }
}
