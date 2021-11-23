/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CorrelationSubscription implements Serializable {

    private static final long serialVersionUID = 1206313928584632165L;

    private String id;
    private String name;
    private String correlationKeyRef;
    private Map<String, Expression> propertyExpressions;

    public CorrelationSubscription() {
        this.propertyExpressions = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorrelationKeyRef() {
        return correlationKeyRef;
    }

    public void setCorrelationKeyRef(String correlationKeyRef) {
        this.correlationKeyRef = correlationKeyRef;
    }

    public Map<String, Expression> getPropertyExpressions() {
        return propertyExpressions;
    }
}