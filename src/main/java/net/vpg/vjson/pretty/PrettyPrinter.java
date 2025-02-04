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

import java.util.function.Consumer;

public class PrettyPrinter {
    private final PrettyPrintConfig config;
    private final Consumer<String> write;
    private int indentLevel;

    public PrettyPrinter(PrettyPrintConfig config, Consumer<String> write) {
        this.config = config;
        this.write = write;
    }

    public PrettyPrinter(Consumer<String> write) {
        this(new PrettyPrintConfig(), write);
    }

    public PrettyPrintConfig getConfig() {
        return config;
    }

    public void print(String s) {
        write.accept(s);
    }

    public void nextLine(boolean newLineCondition, int indentDelta, boolean spaceCondition) {
        if (newLineCondition) {
            indentLevel += indentDelta;
            print("\n" + config.getIndent().repeat(indentLevel));
        } else
            spaceIf(spaceCondition);
    }

    public void spaceIf(boolean condition) {
        if (condition)
            print(" ");
    }
}
