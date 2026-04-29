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
package org.drools.swagger.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.swagger.model.ExecutionRequest;
import org.drools.swagger.model.ExecutionResponse;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(RuleExecutionService.class);

    private final KieBase kieBase;
    private final ObjectMapper objectMapper;

    public RuleExecutionService(KieBase kieBase) {
        this.kieBase = kieBase;
        this.objectMapper = new ObjectMapper();
    }

    public ExecutionResponse execute(ExecutionRequest request) {
        ExecutionResponse response = new ExecutionResponse();
        long startTime = System.currentTimeMillis();

        KieSession session = kieBase.newKieSession();
        try {
            List<String> firedRuleNames = new ArrayList<>();

            session.addEventListener(new DefaultAgendaEventListener() {
                @Override
                public void afterMatchFired(AfterMatchFiredEvent event) {
                    firedRuleNames.add(event.getMatch().getRule().getName());
                }
            });

            if (request.getGlobals() != null) {
                for (Map.Entry<String, Object> entry : request.getGlobals().entrySet()) {
                    session.setGlobal(entry.getKey(), entry.getValue());
                }
            }

            if (request.getFacts() != null) {
                for (ExecutionRequest.FactInput factInput : request.getFacts()) {
                    Object fact = createFact(factInput);
                    if (fact != null) {
                        session.insert(fact);
                    }
                }
            }

            AgendaFilter filter = null;
            if (request.getAgendaFilter() != null && !request.getAgendaFilter().isEmpty()) {
                String filterPattern = request.getAgendaFilter();
                filter = (Match match) -> match.getRule().getName().matches(filterPattern);
            }

            int rulesFired;
            if (request.getMaxRules() != null && request.getMaxRules() > 0) {
                rulesFired = filter != null
                        ? session.fireAllRules(filter, request.getMaxRules())
                        : session.fireAllRules(request.getMaxRules());
            } else {
                rulesFired = filter != null
                        ? session.fireAllRules(filter)
                        : session.fireAllRules();
            }

            response.setRulesFired(rulesFired);
            response.setFiredRuleNames(firedRuleNames);
            response.setResultFacts(extractSessionFacts(session));

        } catch (Exception e) {
            LOG.error("Rule execution failed", e);
            response.setError(e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            session.dispose();
        }

        response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return response;
    }

    private Object createFact(ExecutionRequest.FactInput factInput) throws Exception {
        String typeName = factInput.getType();
        Map<String, Object> data = factInput.getData();

        if (typeName == null || data == null) {
            throw new IllegalArgumentException("Fact input must have both 'type' and 'data'");
        }

        String packageName = "";
        String simpleName = typeName;
        int lastDot = typeName.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = typeName.substring(0, lastDot);
            simpleName = typeName.substring(lastDot + 1);
        }

        FactType factType = kieBase.getFactType(packageName, simpleName);
        if (factType != null) {
            return createDeclaredFact(factType, data);
        }

        return createJavaFact(typeName, data);
    }

    private Object createDeclaredFact(FactType factType, Map<String, Object> data) throws Exception {
        Object instance = factType.newInstance();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            FactField field = factType.getField(entry.getKey());
            if (field != null) {
                Object value = coerceValue(entry.getValue(), field.getType());
                factType.set(instance, entry.getKey(), value);
            }
        }
        return instance;
    }

    private Object createJavaFact(String className, Map<String, Object> data) throws Exception {
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        return objectMapper.convertValue(data, clazz);
    }

    private Object coerceValue(Object value, Class<?> targetType) {
        if (value == null || targetType == null) {
            return value;
        }
        if (targetType.isInstance(value)) {
            return value;
        }

        try {
            return objectMapper.convertValue(value, targetType);
        } catch (Exception e) {
            LOG.warn("Could not coerce value {} to type {}", value, targetType.getName());
            return value;
        }
    }

    private List<Map<String, Object>> extractSessionFacts(KieSession session) {
        List<Map<String, Object>> result = new ArrayList<>();
        Collection<FactHandle> factHandles = session.getFactHandles();

        for (FactHandle fh : factHandles) {
            Object obj = session.getObject(fh);
            if (obj != null) {
                result.add(objectToMap(obj));
            }
        }
        return result;
    }

    private Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("_type", obj.getClass().getName());

        String packageName = obj.getClass().getPackage() != null ? obj.getClass().getPackage().getName() : "";
        String simpleName = obj.getClass().getSimpleName();
        FactType factType = kieBase.getFactType(packageName, simpleName);

        if (factType != null) {
            map.putAll(factType.getAsMap(obj));
        } else {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object val = field.get(obj);
                    if (val != null && !isComplexObject(val)) {
                        map.put(field.getName(), val);
                    } else if (val != null) {
                        map.put(field.getName(), val.toString());
                    }
                } catch (Exception e) {
                    LOG.trace("Cannot read field {}", field.getName());
                }
            }
        }
        return map;
    }

    private boolean isComplexObject(Object obj) {
        return !(obj instanceof Number || obj instanceof String ||
                obj instanceof Boolean || obj instanceof Character);
    }
}
