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

package org.kie.dmn.feel.runtime.events;

import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report all matches for a decision table
 */
public class DecisionTableRulesMatchedEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String        nodeName;
    private final String        dtName;
    private final List<Integer> matches;

    public DecisionTableRulesMatchedEvent(Severity severity, String msg, String nodeName, String dtName, List<Integer> matches) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.dtName = dtName;
        this.matches = matches;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getDecisionTableName() {
        return dtName;
    }

    public List<Integer> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return "DecisionTableRulesMatchedEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", dtName='" + dtName + '\'' +
               ", matches='" + matches + '\'' +
               '}';
    }
}
