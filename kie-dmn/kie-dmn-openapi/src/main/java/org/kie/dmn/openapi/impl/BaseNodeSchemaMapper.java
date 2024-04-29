/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class BaseNodeSchemaMapper {

    private static final Map<Class<? extends BaseNode>, BiConsumer<BaseNode, Schema>> SCHEMA_MODIFIERS;

    private static BiConsumer<BaseNode, Schema> STRINGNODE_CONSUMER = (node, schema) -> {
        populateEnumSchema(schema, ((StringNode) node).getValue());
    };

    private static BiConsumer<BaseNode, Schema> NUMBERNODE_CONSUMER = (node, schema) -> {
        populateEnumSchema(schema, ((NumberNode) node).getValue());
    };

    static {
        SCHEMA_MODIFIERS = new HashMap<>();
        SCHEMA_MODIFIERS.put(StringNode.class, STRINGNODE_CONSUMER);
        SCHEMA_MODIFIERS.put(NumberNode.class, NUMBERNODE_CONSUMER);
    }

    static void populateSchemaFromBaseNode(BaseNode baseNode, Schema schema) {
        if (SCHEMA_MODIFIERS.containsKey(baseNode.getClass())) {
            SCHEMA_MODIFIERS.get(baseNode.getClass()).accept(baseNode, schema);
        }
    }


    private static void populateEnumSchema(Schema schema, Object toAdd) {
        Set<Object> enums = new HashSet<>();
        enums.add(toAdd);
        if (schema.getEnumeration() != null) {
            enums.addAll(schema.getEnumeration());
        }
        schema.enumeration(new ArrayList<>(enums));
    }
}