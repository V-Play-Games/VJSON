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

/**
 * A first-in-first-out list with limited functionality
 *
 * @author Vaibhav Nargwani
 * @since 1.0.0
 */
public class FIFOList {
    private static final Object[] PLACEHOLDER_ARRAY = {};
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    Object[] elementData = PLACEHOLDER_ARRAY;
    int lastPos;

    /**
     * Add the given element to the top of the list
     *
     * @param t the element to add
     */
    public void add(Object t) {
        int minCapacity = elementData.length == 0 ? Math.max(10, lastPos + 1) : lastPos + 1;
        if (minCapacity - elementData.length > 0) {
            int newCapacity = elementData.length + (elementData.length >> 1);
            newCapacity = newCapacity - minCapacity < 0 ? minCapacity : newCapacity;
            newCapacity = newCapacity - MAX_ARRAY_SIZE <= 0 ? newCapacity : minCapacity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
            elementData = java.util.Arrays.copyOf(elementData, newCapacity);
        }
        elementData[lastPos++] = t;
    }

    /**
     *
     */
    public Object remove() {
        Object o = get();
        elementData[lastPos--] = null;
        return o;
    }

    /**
     *
     */
    public Object replace(Object o) {
        Object old = get();
        elementData[lastPos - 1] = o;
        return old;
    }

    /**
     *
     */
    public Object get() {
        return lastPos == 0 ? null : elementData[lastPos - 1];
    }

    /**
     *
     */
    public int size() {
        return lastPos;
    }
}
