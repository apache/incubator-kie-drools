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
package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;

import java.util.Collections;
import java.util.List;

public class AfterEvaluateDecisionTableEventImpl
        implements AfterEvaluateDecisionTableEvent {

    private final String nodeName;
    private final String decisionTableName;
    private final String        dtId;
    private final DMNResult     result;
    private final List<Integer> matches;
    private final List<Integer> fired;
    private final List<String> matchesIds;
    private final List<String> firedIds;

    public AfterEvaluateDecisionTableEventImpl(String nodeName, String decisionTableName, String dtId, DMNResult result, List<Integer> matches, List<Integer> fired, List<String> matchesIds, List<String> firedIds) {
        this.nodeName = nodeName;
        this.decisionTableName = decisionTableName;
        this.dtId = dtId;
        this.result = result;
        this.matches = matches;
        this.fired = fired;
        this.matchesIds = matchesIds;
        this.firedIds = firedIds;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getDecisionTableName() {
        return decisionTableName;
    }

    @Override
    public String getDecisionTableId() {
        return dtId;
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
    public List<String> getMatchesIds() {
        return matchesIds == null ? Collections.emptyList() : matchesIds;
    }

    @Override
    public List<String> getSelectedIds() {return firedIds == null ? Collections.emptyList() : firedIds;
    }

    @Override
    public String toString() {
        return "AfterEvaluateDecisionTableEvent{ nodeName='"+ nodeName +"' decisionTableName='" + decisionTableName + "' matches=" + getMatches() + " fired=" + getSelected() + "' matchesIds=" + getMatchesIds() + " firedIds=" + getSelectedIds() + " }";
    }

}
