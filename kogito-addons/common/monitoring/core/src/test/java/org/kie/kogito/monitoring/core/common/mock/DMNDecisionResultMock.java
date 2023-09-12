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
package org.kie.kogito.monitoring.core.common.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;

public class DMNDecisionResultMock implements DMNDecisionResult {

    private String decisionName;

    private Object result;

    private String decisionId;

    public DMNDecisionResultMock(String decisionName, Object result) {
        this.decisionName = decisionName;
        this.result = result;
        this.decisionId = String.valueOf(new Random().nextInt());
    }

    @Override
    public String getDecisionId() {
        return decisionId;
    }

    @Override
    public String getDecisionName() {
        return decisionName;
    }

    @Override
    public DecisionEvaluationStatus getEvaluationStatus() {
        return DecisionEvaluationStatus.SUCCEEDED;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public List<DMNMessage> getMessages() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
