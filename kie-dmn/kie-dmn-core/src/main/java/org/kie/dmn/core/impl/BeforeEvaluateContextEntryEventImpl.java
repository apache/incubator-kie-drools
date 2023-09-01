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
package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;

public class BeforeEvaluateContextEntryEventImpl
        implements BeforeEvaluateContextEntryEvent {

    private final String nodeName;
    private final String variableName;
    private final String variableId;
    private final String expressionId;
    private final DMNResult result;

    public BeforeEvaluateContextEntryEventImpl(String nodeName, String variableName, String variableId, String expressionId, DMNResult result) {
        this.nodeName = nodeName;
        this.variableName = variableName;
        this.variableId = variableId;
        this.expressionId = expressionId;
        this.result = result;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public String getVariableId() {
        return variableId;
    }

    @Override
    public String getExpressionId() {
        return expressionId;
    }

    @Override
    public DMNResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "BeforeEvaluateContextEntryEventImpl{" +
                "nodeName='" + nodeName + '\'' +
                ", variableName='" + variableName + '\'' +
                ", variableId='" + variableId + '\'' +
                ", expressionId='" + expressionId + '\'' +
                '}';
    }
}
