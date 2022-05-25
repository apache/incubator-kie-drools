/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.datatype.impl.coverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.kie.kogito.jackson.utils.JsonNodeConverter;
import org.kie.kogito.jackson.utils.StringConverter;

import com.fasterxml.jackson.databind.JsonNode;

public class TypeConverterRegistry {

    private static TypeConverterRegistry INSTANCE = new TypeConverterRegistry();

    private Map<String, Function<String, ? extends Object>> converters = new HashMap<>();
    private Map<String, Function<? extends Object, String>> unconverters = new HashMap<>();
    private Map<String, UnaryOperator<Object>> cloners = new HashMap<>();

    private Function<String, String> defaultConverter = new NoOpTypeConverter();

    private TypeConverterRegistry() {
        converters.put("java.util.Date", new DateTypeConverter());
        converters.put(JsonNode.class.getName(), new JsonNodeConverter());
        unconverters.put(JsonNode.class.getName(), new StringConverter());
        cloners.put(JsonNode.class.getName(), o -> ((JsonNode) o).deepCopy());
    }

    public boolean isRegistered(String type) {
        return converters.containsKey(type);
    }

    public Function<String, ? extends Object> forType(String type) {
        return converters.getOrDefault(type, defaultConverter);
    }

    public <T> Function<T, String> forTypeReverse(T obj) {
        Function<T, String> result = null;
        Class<?> clazz = obj.getClass();
        do {
            result = (Function<T, String>) unconverters.get(clazz.getName());
            clazz = clazz.getSuperclass();
        } while (clazz != null && result == null);
        return result == null ? Object::toString : result;
    }

    public UnaryOperator<Object> forTypeCloner(String type) {
        return cloners.getOrDefault(type, CloneHelper::clone);
    }

    public void register(String type, Function<String, ? extends Object> converter) {
        register(type, converter, Optional.empty(), Optional.empty());
    }

    public <T> void register(String type, Function<String, T> converter, Function<T, String> unconverter) {
        register(type, converter, Optional.of(unconverter), Optional.empty());
    }

    public <T> void register(String type, Function<String, T> converter, Optional<Function<T, String>> unconverter, Optional<UnaryOperator<Object>> cloner) {
        this.converters.put(type, converter);
        unconverter.ifPresent(u -> this.unconverters.put(type, u));
        cloner.ifPresent(c -> this.cloners.put(type, c));
    }

    public static TypeConverterRegistry get() {
        return INSTANCE;
    }
}
