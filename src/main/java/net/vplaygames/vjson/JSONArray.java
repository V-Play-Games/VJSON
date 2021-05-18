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
package net.vplaygames.vjson;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class JSONArray extends JSONValue implements List<JSONValue>, RandomAccess, Cloneable, java.io.Serializable {
    ArrayList<JSONValue> list = new ArrayList<>();

    /**
     * Convert a list to a JSON String
     *
     * @param list the list to convert
     * @return the JSON String, or "null" if the list is null
     */
    public static String toJSONString(List<?> list) {
        return list == null ? "null" :
            list.stream().collect(() -> new StringJoiner(",", "[", "]").setEmptyValue("null"),
                (joiner, value) -> joiner.add(new JSONValue(value).toString()),
                StringJoiner::merge)
                .toString();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return list.equals(obj);
    }

    @Override
    public String toRawString() {
        return toString();
    }

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    @Override
    public boolean asBoolean() {
        throw new ClassCastException();
    }

    @Override
    public Number asNumber() {
        throw new ClassCastException();
    }

    @Override
    public byte asByte() {
        throw new ClassCastException();
    }

    @Override
    public short asShort() {
        throw new ClassCastException();
    }

    @Override
    public int asInt() {
        throw new ClassCastException();
    }

    @Override
    public long asLong() {
        throw new ClassCastException();
    }

    @Override
    public float asFloat() {
        throw new ClassCastException();
    }

    @Override
    public double asDouble() {
        throw new ClassCastException();
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public JSONObject asObject() {
        throw new ClassCastException();
    }

    @Override
    public JSONArray asArray() {
        return this;
    }

    @Override
    public String toJSONString() {
        return toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<JSONValue> iterator() {
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super JSONValue> action) {
        list.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return list.toArray(a);
    }

    @Override
    public boolean add(JSONValue jsonValue) {
        return list.add(jsonValue);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends JSONValue> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends JSONValue> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super JSONValue> filter) {
        return list.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<JSONValue> operator) {
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super JSONValue> c) {
        list.sort(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public JSONValue get(int index) {
        return list.get(index);
    }

    @Override
    public JSONValue set(int index, JSONValue element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, JSONValue element) {
        list.add(index, element);
    }

    @Override
    public JSONValue remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<JSONValue> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<JSONValue> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<JSONValue> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<JSONValue> spliterator() {
        return list.spliterator();
    }

    @Override
    public Stream<JSONValue> stream() {
        return list.stream();
    }

    @Override
    public Stream<JSONValue> parallelStream() {
        return list.parallelStream();
    }
}