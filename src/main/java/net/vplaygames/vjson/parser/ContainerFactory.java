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

import net.vplaygames.vjson.JSONArray;
import net.vplaygames.vjson.JSONObject;

import java.util.function.Supplier;

/**
 * Container factory for creating {@link JSONObject JSONObjects} and {@link JSONArray JSONArrays}
 * to be used as containers for parsed JSON in the #parse methods of {@link JSONParser}
 * with {@link #newObject()} and {@link #newArray()} methods respectively
 *
 * @author Vaibhav Nargwani
 */
public interface ContainerFactory {
    /**
     * Creates a new {@link JSONObject JSON Object container}
     *
     * @return a new {@link JSONObject JSON Object container}
     */
    JSONObject newObject();

    /**
     * Creates a new {@link JSONArray JSON Array container}
     *
     * @return a new {@link JSONArray JSON Array container}
     */
    JSONArray newArray();

    /**
     * Creates a new Container Factory which creates new
     * {@link JSONObject JSONObjects} and {@link JSONArray JSONArrays}
     * from the {@link Supplier Suppliers} provided as parameters passed
     *
     * @param objectSupplier the {@linkplain Supplier} which supplies new {@link JSONObject JSONObjects}
     * @param arraySupplier the {@linkplain Supplier} which supplies new {@link JSONArray JSONArrays}
     * @return new Container Factory which creates new JSON Objects and JSON Arrays from the Suppliers provided
     */
    static ContainerFactory of(Supplier<JSONObject> objectSupplier, Supplier<JSONArray> arraySupplier) {
        return new ContainerFactory() {
            @Override
            public JSONObject newObject() {
                return objectSupplier.get();
            }

            @Override
            public JSONArray newArray() {
                return arraySupplier.get();
            }
        };
    }

    /**
     * the default Container Factory which creates new
     * {@link JSONObject JSONObjects} and {@link JSONArray JSONArrays} when asked for
     *
     * @return the default Container Factory
     */
    static ContainerFactory defaultFactory() {
        return JSONParser.defaultFactory;
    }
}
