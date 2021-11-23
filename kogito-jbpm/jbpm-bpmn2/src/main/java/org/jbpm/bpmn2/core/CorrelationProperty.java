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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CorrelationProperty implements Serializable {

    private static final long serialVersionUID = 2372340998854641672L;
    private String id;
    private String name;
    private String type;

    private Map<String, Expression> retrievalExpression;

    public CorrelationProperty() {
        retrievalExpression = new HashMap<>();
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

    public String getType() {
        return type;
    }

    public List<String> getMessageRefs() {
        return retrievalExpression.keySet().stream().collect(Collectors.toList());
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRetrievalExpression(String messageRef, Expression retrievalExpression) {
        this.retrievalExpression.put(messageRef, retrievalExpression);
    }

    public Expression getRetrievalExpression(String messageRef) {
        return retrievalExpression.get(messageRef);
    }
}