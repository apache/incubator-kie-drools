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

package org.kie.submarine.process.bpmn2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BpmnVariables {

    private final Map<String, Object> variables = new HashMap<>();

    private BpmnVariables(){}

    public static BpmnVariables create() {
        return new BpmnVariables();
    }

    public Object get(String v) {
        return variables.get(v);
    }

    public BpmnVariables set(String v, Object o) {
        variables.put(v, o);
        return this;
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(variables);
    }

    public void fromMap(Map<String, Object> vs) {
        variables.putAll(vs);
    }
}
