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
 * An event class to report a hit policy violation on a decision table
 */
public class HitPolicyViolationEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String nodeName;
    private final List<Integer> offendingRules;

    public HitPolicyViolationEvent(Severity severity, String msg, String nodeName, List<Integer> offendingRules ) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.offendingRules = offendingRules;
    }

    public String getNodeName() {
        return nodeName;
    }

    public List<Integer> getOffendingRules() {
        return offendingRules;
    }

    @Override
    public String toString() {
        return "HitPolicyViolationEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", offendingRules=" + (offendingRules != null ? offendingRules.toString() : "[]") +
               '}';
    }
}
