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
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;

public class BeforeEvaluateDecisionEventImpl
        implements BeforeEvaluateDecisionEvent {

    private DecisionNode  decision;
    private DMNResult     result;
    private long          timestamp;

    public BeforeEvaluateDecisionEventImpl(DecisionNode decision, DMNResult result) {
        this.decision = decision;
        this.result = result;
    }

    @Override
    public DecisionNode getDecision() {
        return this.decision;
    }

    @Override
    public DMNResult getResult() {
        return this.result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BeforeEvaluateDecisionEvent{ name='" + decision.getName() + "' id='" + decision.getId() + "' }";
    }
}
