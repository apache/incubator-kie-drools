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
import java.util.function.Function;

public class TypeConverterRegistry {

    private static TypeConverterRegistry INSTANCE = new TypeConverterRegistry();
    
    private Map<String, Function<String, ? extends Object>> converters = new HashMap<>();
    private Function<String, String> defaultConverter = new NoOpTypeConverter();
    
    private TypeConverterRegistry() {
        converters.put("java.util.Date", new DateTypeConverter());        
    }
    
    public Function<String, ? extends Object> forType(String type) {
        return converters.getOrDefault(type, defaultConverter);
    }
    
    public void register(String type, Function<String, ? extends Object> converter) {
        this.converters.putIfAbsent(type, converter);
    }
    
    public static TypeConverterRegistry get() {
        return INSTANCE;
    }
}
