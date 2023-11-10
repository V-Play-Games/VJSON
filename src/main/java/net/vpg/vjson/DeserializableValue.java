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
package net.vpg.vjson;

import net.vpg.vjson.pretty.PrettyPrintConfig;
import net.vpg.vjson.pretty.PrettyPrinter;

public interface DeserializableValue {
    String deserialize();

    default String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        toPrettyString(new PrettyPrinter(new PrettyPrintConfig(), sb));
        return sb.toString();
    }

    default void toPrettyString(PrettyPrinter printer) {
        printer.print(deserialize());
    }
}
