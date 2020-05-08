/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.bpmn2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jbpm.process.core.context.variable.Variable;
import org.kie.kogito.Model;

public class BpmnVariables implements Model {

    public static final Predicate<Variable> OUTPUTS_ONLY = v -> v.hasTag(Variable.OUTPUT_TAG);
    public static final Predicate<Variable> INPUTS_ONLY = v -> v.hasTag(Variable.INPUT_TAG);
    public static final Predicate<Variable> INTERNAL_ONLY = v -> v.hasTag(Variable.INTERNAL_TAG);    
    
    private final Map<String, Object> variables = new HashMap<>();

    private List<Variable> definitions = new ArrayList<>();

    protected BpmnVariables() {
        
    }
    
    protected BpmnVariables(Map<String, Object> variables) {
        this.variables.putAll(variables);
    }

    protected BpmnVariables(List<Variable> definitions, Map<String, Object> variables) {
        this.definitions = definitions;
        this.variables.putAll(variables);
    }

    public static BpmnVariables create() {
        return new BpmnVariables();
    }
    
    public static BpmnVariables create(Map<String, Object> variables) {
        return new BpmnVariables(variables);
    }

    public Object get(String v) {
        return variables.get(v);
    }

    public BpmnVariables set(String v, Object o) {
        variables.put(v, o);
        return this;
    }

    public void fromMap(Map<String, Object> vs) {
        variables.putAll(vs);
    }

    public List<Variable> definitions() {
        return definitions;
    }

    @Override
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(variables);
    }
    
    public Map<String, Object> toMap(Predicate<Variable> filter) {
        
        return definitions.stream()
            .filter(filter)
            .filter(v -> variables.containsKey(v.getName()))
            .collect(Collectors.toMap(v -> v.getName(), v -> v.getName()));               
    }
}
