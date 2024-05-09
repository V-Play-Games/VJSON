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

import java.io.PrintStream;
import java.util.function.Consumer;

public class PrettyPrinter {
    private final PrettyPrintConfig config;
    private final Consumer<String> write;
    private int indentLevel;

    public PrettyPrinter(PrettyPrintConfig config, Consumer<String> write) {
        this.config = config;
        this.write = write;
    }

    public PrettyPrinter(PrettyPrintConfig config, StringBuilder sb) {
        this(config, sb::append);
    }

    public PrettyPrinter(PrettyPrintConfig config, PrintStream stream) {
        this(config, stream::print);
    }

    public PrettyPrintConfig getConfig() {
        return config;
    }

    public void incrementIndentLevel() {
        indentLevel++;
    }

    public void decrementIndentLevel() {
        indentLevel--;
    }

    public void print(String s) {
        write.accept(s);
    }

    public void newLineAndIndent() {
        print("\n" + config.getIndent().repeat(indentLevel));
    }

    public void space() {
        print(" ");
    }
}
