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

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;

import java.util.Collections;
import java.util.List;

public class AfterEvaluateDecisionTableEventImpl
        implements AfterEvaluateDecisionTableEvent {

    private final String        nodeName;
    private final String        dtName;
    private final DMNResult     result;
    private final List<Integer> matches;
    private final List<Integer> fired;

    public AfterEvaluateDecisionTableEventImpl(String nodeName, String dtName, DMNResult result, List<Integer> matches, List<Integer> fired) {
        this.nodeName = nodeName;
        this.dtName = dtName;
        this.result = result;
        this.matches = matches;
        this.fired = fired;
    }

    @Override
    public String getNodeName() {
        return nodeName;
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
    public List<Integer> getMatches() {
        return matches == null ? Collections.emptyList() : matches;
    }

    @Override
    public List<Integer> getSelected() {
        return fired == null ? Collections.emptyList() : fired;
    }

    @Override
    public String toString() {
        return "AfterEvaluateDecisionTableEvent{ nodeName='"+nodeName+"' decisionTableName='" + dtName + "' matches=" + getMatches() + " fired=" + getSelected() + " }";
    }

}
