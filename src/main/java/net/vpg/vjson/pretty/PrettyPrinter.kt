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

import java.util.function.Consumer

class PrettyPrinter(val config: PrettyPrintConfig, private val write: Consumer<String?>) {
    private var indentLevel = 0

    constructor(write: Consumer<String?>) : this(PrettyPrintConfig(), write)

    fun print(s: String?) {
        write.accept(s)
    }

    fun nextLine(newLineCondition: Boolean, indentDelta: Int, spaceCondition: Boolean) {
        if (newLineCondition) {
            indentLevel += indentDelta
            print("\n" + config.indent!!.repeat(indentLevel))
        } else spaceIf(spaceCondition)
    }

    fun spaceIf(condition: Boolean) {
        if (condition) print(" ")
    }
}
