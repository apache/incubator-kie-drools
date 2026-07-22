/*
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
package org.kie.kogito.jackson.utils;

import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class PrefixJsonNode<T> extends FunctionBaseJsonNode {

    private static final long serialVersionUID = 1L;

    private transient final String prefix;
    private transient final Function<String, Optional<T>> function;
    private transient Optional<T> value;

    public PrefixJsonNode(Function<String, Optional<T>> function) {
        this(null, function);
    }

    public PrefixJsonNode(String prefix, Function<String, Optional<T>> function) {
        this.prefix = prefix;
        this.function = function;
        this.value = prefix == null ? Optional.empty() : function.apply(prefix);
    }

    @Override
    public JsonNodeType getNodeType() {
        return value.isPresent() ? JsonNodeType.STRING : JsonNodeType.OBJECT;
    }

    @Override
    public String asText() {
        return value.map(Object::toString).orElse(null);
    }

    @Override
    public JsonNode get(String fieldName) {
        return new PrefixJsonNode<>(prefix == null ? fieldName : prefix + "." + fieldName, function);
    }
}
