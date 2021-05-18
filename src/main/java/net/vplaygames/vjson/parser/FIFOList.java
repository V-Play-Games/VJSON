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
package net.vplaygames.vjson.parser;

import java.util.ArrayList;

public class FIFOList<T> {
    ArrayList<T> elements = new ArrayList<>();
    int lastPos;

    public void add(T t) {
        if (lastPos++ == elements.size())
            elements.add(t);
        else
            elements.set(lastPos - 1, t);
    }

    public T remove() {
        T t = replace(null);
        lastPos--;
        return t;
    }

    public T replace(T t) {
        return elements.set(lastPos - 1, t);
    }

    public T get() {
        return lastPos == 0 ? null : elements.get(lastPos - 1);
    }

    public int size() {
        return lastPos;
    }
}
