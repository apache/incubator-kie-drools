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
package org.drools.docs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Documentation for a condition (pattern) in a rule's LHS.
 */
public class ConditionDoc {

    public enum ConditionType {
        PATTERN, AND, OR, NOT, EXISTS, EVAL, FORALL, ACCUMULATE, FROM
    }

    private ConditionType type;
    private String objectType;
    private String binding;
    private String expression;
    private final List<String> constraints = new ArrayList<>();
    private final List<ConditionDoc> children = new ArrayList<>();

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public List<ConditionDoc> getChildren() {
        return children;
    }
}
