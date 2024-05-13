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
package org.kie.dmn.core.internal.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.DateFunction;
import org.kie.dmn.feel.runtime.functions.DurationFunction;
import org.kie.dmn.feel.runtime.functions.TimeFunction;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.typesafe.DMNTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicDMNContextBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicDMNContextBuilder.class);
    private final DMNContext context;
    private final DMNModel model;
    private final Deque<NameAndDMNType> nameStack = new ArrayDeque<>();

    public DynamicDMNContextBuilder(DMNContext emptyContext, DMNModel model) {
        this.context = emptyContext;
        this.model = model;
    }

    public DMNContext populateContextWith(Map<String, Object> json) {
        for (Entry<String, Object> kv : json.entrySet()) {
            InputDataNode idn = model.getInputByName(kv.getKey());
            if (idn != null) {
                processInputDataNode(kv, idn);
            } else {
                DecisionNode dn = model.getDecisionByName(kv.getKey());
                if (dn != null) {
                    processDecisionNode(kv, dn);
                } else {
                    LOG.debug("The key {} was not a InputData nor a Decision to override, setting it as-is.", kv.getKey());
                    context.set(kv.getKey(), kv.getValue());
                }
            }
        }
        return context;
    }

    public DMNContext populateContextForDecisionServiceWith(String decisionServiceName, Map<String, Object> json) {
        DecisionServiceNode dsNode = model.getDecisionServices().stream().filter(ds -> ds.getName().equals(decisionServiceName)).findFirst().orElseThrow(IllegalArgumentException::new);
        for (Entry<String, Object> kv : json.entrySet()) {
            DecisionServiceNodeImpl dsNodeImpl = (DecisionServiceNodeImpl) dsNode;
            DMNNode node = dsNodeImpl.getInputParameters().get(kv.getKey());
            if (node instanceof InputDataNode) {
                processInputDataNode(kv, (InputDataNode) node);
            } else if (node instanceof DecisionNode) {
                processDecisionNode(kv, (DecisionNode) node);
            } else {
                LOG.debug("The key {} was not a RequiredInput nor a RequiredDecision for the DecisionService, setting it as-is.", kv.getKey());
                context.set(kv.getKey(), kv.getValue());
            }
        }
        return context;
    }

    private void processInputDataNode(Entry<String, Object> kv, InputDataNode idn) {
        nameStack.push(new NameAndDMNType(kv.getKey(), idn.getType()));
        Object recursed = recurseType(kv.getValue(), idn.getType());
        context.set(kv.getKey(), recursed);
        nameStack.pop();
    }

    private void processDecisionNode(Entry<String, Object> kv, DecisionNode dn) {
        nameStack.push(new NameAndDMNType(kv.getKey(), dn.getResultType()));
        Object recursed = recurseType(kv.getValue(), dn.getResultType());
        context.set(kv.getKey(), recursed);
        nameStack.pop();
    }

    private Object recurseType(Object value, DMNType resultType) {
        if (resultType == null) {
            debugStack();
            LOG.debug("unknown type, and passing as-is");
            return value;
        } else if (DMNTypeUtils.isFEELBuiltInType(resultType)) {
            return getAsFEELBuiltinType(value, resultType);
        } else if (resultType.isCollection()) {
            return recurseCollection(value, resultType);
        } else if (resultType instanceof CompositeTypeImpl) {
            return recurseComposite(value, (CompositeTypeImpl) resultType);
        } else if (resultType instanceof SimpleTypeImpl) {
            return recurseType(value, resultType.getBaseType());
        }
        debugStack();
        LOG.debug("unknown case for type {} and passing as-is", resultType);
        return value;
    }

    private Object recurseComposite(Object value, CompositeTypeImpl compositeType) {
        if (value instanceof Map) {
            Map<String, Object> results = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> valueMap = (Map<String, Object>) value;
            for (Entry<String, Object> kv : valueMap.entrySet()) {
                if (compositeType.getFields().containsKey(kv.getKey())) {
                    DMNType keyType = compositeType.getFields().get(kv.getKey());
                    nameStack.push(new NameAndDMNType(kv.getKey(), keyType));
                    Object recursed = recurseType(kv.getValue(), keyType);
                    results.put(kv.getKey(), recursed);
                    nameStack.pop();
                } else {
                    nameStack.push(new NameAndDMNType(kv.getKey(), null));
                    debugStack();
                    LOG.debug("undefined type for key {} in {}, passing as-is", kv.getKey(), compositeType);
                    results.put(kv.getKey(), kv.getValue());
                    nameStack.pop();
                }
            }
            for (String k : compositeType.getFields().keySet()) {
                if (!results.containsKey(k)) {
                    results.put(k, null);
                }
            }
            return results;
        } else {
            debugStack();
            LOG.debug("The type {} is a composite type, but the current value is not a Map, passing as-is", compositeType);
            return value;
        }
    }

    private Object recurseCollection(Object value, DMNType resultType) {
        if (value instanceof Iterable<?>) {
            List<Object> results = new ArrayList<>();
            Iterable<?> iterable = (Iterable<?>) value;
            for (Object next : iterable) {
                Object recursed = recurseType(next, resultType.getBaseType());
                results.add(recursed);
            }
            return results;
        } else {
            debugStack();
            LOG.debug("The type {} has DMN-isCollection set, but the current value is not Iterable, passing as-is", resultType);
            return value;
        }
    }

    private Object getAsFEELBuiltinType(Object value, DMNType resultType) {
        BuiltInType builtin = DMNTypeUtils.getFEELBuiltInType(resultType);
        try {
            switch (builtin) {
                case UNKNOWN:
                    return value;
                case DATE:
                    return new DateFunction().invoke((String) value).getOrElseThrow(FailedConversionException::new);
                case TIME:
                    return new TimeFunction().invoke((String) value).getOrElseThrow(FailedConversionException::new);
                case DATE_TIME:
                    return new DateAndTimeFunction().invoke((String) value).getOrElseThrow(FailedConversionException::new);
                case BOOLEAN:
                    return value;
                case NUMBER:
                    return NumberEvalHelper.getBigDecimalOrNull(value);
                case STRING:
                    return value;
                case DURATION:
                    return new DurationFunction().invoke((String) value).getOrElseThrow(FailedConversionException::new);
                default:
                    throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            debugStack();
            LOG.debug("Problem while coercing value for FEEL Built-in type {}, passing as-is", resultType);
            return value;
        }
    }

    private void debugStack() {
        LOG.debug("{}", nameStack);
    }

    public static class NameAndDMNType {
        public final String name;
        public final DMNType type;

        public NameAndDMNType(String name, DMNType type) {
            super();
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return "NameAndDMNType [name=" + name + ", type=" + type + "]";
        }
    }
}
