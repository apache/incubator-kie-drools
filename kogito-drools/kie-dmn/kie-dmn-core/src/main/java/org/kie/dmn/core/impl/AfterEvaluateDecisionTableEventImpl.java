/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.impl;

import org.kie.dmn.core.api.DMNResult;
import org.kie.dmn.core.api.event.AfterEvaluateDecisionTableEvent;

public class AfterEvaluateDecisionTableEventImpl
        implements AfterEvaluateDecisionTableEvent {

    private String dtName;
    private DMNResult result;

    public AfterEvaluateDecisionTableEventImpl(String dtName, DMNResultImpl result) {
        this.dtName = dtName;
        this.result = result;
    }

    @Override
    public String getDecisionTableName() {
        return dtName;
    }

    @Override
    public DMNResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "AfterEvaluateDecisionTableEvent{ name='"+dtName+"' }";
    }

}
