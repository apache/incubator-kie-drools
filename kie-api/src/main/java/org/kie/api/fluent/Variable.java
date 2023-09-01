/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.api.fluent;

import java.util.HashMap;
import java.util.Map;

/** 
 * Builder pattern like class used to build a variable.<br>
 * A variable requires a name and a data type.<br> 
 * Value and metadata are optional.<br> 
 * Usage:
 * <pre>
 *  Variable.var("test",String.class)
 *          .value("example value")
 *          .metadata("readOnly",true).
 *          .metadata("required",false)
 * </pre>
 *
 * @param <T> data type of the variable
 * @see NodeContainerBuilder#variable(Variable)
 */
public class Variable<T> {
    
    private String name;
    private T value;
    private Class<T> type;
    private Map<String, Object> metadata;

    private Variable(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }
    
    public static <T> Variable<T> var(String name, Class<T> type) {
        return new Variable<>(name, type);
    }

    public Variable<T> value(T value) {
        this.value = value;
        return this;
    }

    public Variable<T> metadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public Class<T> getType() {
        return type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "Variable [name=" + name + ", value=" + value + ", type=" + type + ", metadata=" + metadata + "]";
    }
}
