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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseNodeSchemaMapper {


    private static final Logger LOG = LoggerFactory.getLogger(BaseNodeSchemaMapper.class);

    private static final Map<Class<? extends BaseNode>, BiConsumer<BaseNode, Schema>> SCHEMA_MODIFIERS;

    private static BiConsumer<BaseNode, Schema> STRINGNODE_CONSUMER = (node, schema) -> populateEnumSchema(schema, ((StringNode) node).getValue());

    private static BiConsumer<BaseNode, Schema> NUMBERNODE_CONSUMER = (node, schema) -> populateEnumSchema(schema, ((NumberNode) node).getValue());

    private static BiConsumer<BaseNode, Schema> ATLITERALNODE_CONSUMER = (node, schema) -> {
        // Defaulting FEELDialect to FEEL
        EvaluationContextImpl emptyEvalCtx =
                new EvaluationContextImpl(BaseNodeSchemaMapper.class.getClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object evaluated = node.evaluate(emptyEvalCtx);
        Object toStore = evaluated != null ? evaluated : ((AtLiteralNode) node).getStringLiteral().toString();
        populateEnumSchema(schema, toStore);
    };

    private static BiConsumer<BaseNode, Schema> NULLNODE_CONSUMER = (node, schema) -> populateEnumSchema(schema, null);

    static {
        SCHEMA_MODIFIERS = new HashMap<>();
        SCHEMA_MODIFIERS.put(StringNode.class, STRINGNODE_CONSUMER);
        SCHEMA_MODIFIERS.put(NumberNode.class, NUMBERNODE_CONSUMER);
        SCHEMA_MODIFIERS.put(AtLiteralNode.class, ATLITERALNODE_CONSUMER);
        SCHEMA_MODIFIERS.put(NullNode.class, NULLNODE_CONSUMER);
    }

    static void populateSchemaFromBaseNode(Schema toPopulate, BaseNode baseNode) {
        LOG.debug("populateSchemaFromUnaryTests {} {}", toPopulate, baseNode);
        if (SCHEMA_MODIFIERS.containsKey(baseNode.getClass())) {
            SCHEMA_MODIFIERS.get(baseNode.getClass()).accept(baseNode, toPopulate);
        }
    }

    private static void populateEnumSchema(Schema toPopulate, Object toAdd) {
        LOG.debug("populateSchemaFromUnaryTests {} {}", toPopulate, toAdd);
        Set<Object> enums = new HashSet<>();
        enums.add(toAdd);
        if (toPopulate.getEnumeration() != null) {
            enums.addAll(toPopulate.getEnumeration());
        }
        toPopulate.enumeration(new ArrayList<>(enums));
    }

    private BaseNodeSchemaMapper() {
        // deliberate intention not to allow instantiation of this class.
    }
}