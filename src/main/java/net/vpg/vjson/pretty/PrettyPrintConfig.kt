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

package net.vpg.vjson.pretty;

public class PrettyPrintConfig {
    private String indent = "  ";
    private boolean spaceWithinBraces = false;
    private boolean spaceWithinBrackets = false;
    private boolean spaceBeforeComma = false;
    private boolean spaceAfterComma = true;
    private boolean spaceBeforeColon = false;
    private boolean spaceAfterColon = true;
    private boolean arrayContentsOnNewLine = true;
    private boolean objectContentsOnNewLine = true;

    public String getIndent() {
        return indent;
    }

    public PrettyPrintConfig setIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public boolean isSpaceWithinBraces() {
        return spaceWithinBraces;
    }

    public PrettyPrintConfig setSpaceWithinBraces(boolean spaceWithinBraces) {
        this.spaceWithinBraces = spaceWithinBraces;
        return this;
    }

    public boolean isSpaceWithinBrackets() {
        return spaceWithinBrackets;
    }

    public PrettyPrintConfig setSpaceWithinBrackets(boolean spaceWithinBrackets) {
        this.spaceWithinBrackets = spaceWithinBrackets;
        return this;
    }

    public boolean isSpaceBeforeComma() {
        return spaceBeforeComma;
    }

    public PrettyPrintConfig setSpaceBeforeComma(boolean spaceBeforeComma) {
        this.spaceBeforeComma = spaceBeforeComma;
        return this;
    }

    public boolean isSpaceAfterComma() {
        return spaceAfterComma;
    }

    public PrettyPrintConfig setSpaceAfterComma(boolean spaceAfterComma) {
        this.spaceAfterComma = spaceAfterComma;
        return this;
    }

    public boolean isSpaceBeforeColon() {
        return spaceBeforeColon;
    }

    public PrettyPrintConfig setSpaceBeforeColon(boolean spaceBeforeColon) {
        this.spaceBeforeColon = spaceBeforeColon;
        return this;
    }

    public boolean isSpaceAfterColon() {
        return spaceAfterColon;
    }

    public PrettyPrintConfig setSpaceAfterColon(boolean spaceAfterColon) {
        this.spaceAfterColon = spaceAfterColon;
        return this;
    }

    public boolean isArrayContentsOnNewLine() {
        return arrayContentsOnNewLine;
    }

    public PrettyPrintConfig setArrayContentsOnNewLine(boolean arrayContentsOnNewLine) {
        this.arrayContentsOnNewLine = arrayContentsOnNewLine;
        return this;
    }

    public boolean isObjectContentsOnNewLine() {
        return objectContentsOnNewLine;
    }

    public PrettyPrintConfig setObjectContentsOnNewLine(boolean objectContentsOnNewLine) {
        this.objectContentsOnNewLine = objectContentsOnNewLine;
        return this;
    }
}
