/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.shared;

import java.util.ArrayList;
import java.util.List;

public class SingleScenarioResult {

    private ScenarioRunResult result;

    /**
     * Maps from event type to message to display.
     */
    private List<String[]> auditLog = new ArrayList<String[]>();

    public SingleScenarioResult() {
    }

    public SingleScenarioResult( final ScenarioRunResult scenarioRunResult ) {
        result = scenarioRunResult;
    }

    public SingleScenarioResult( final ScenarioRunResult scenarioRunResult,
                                 final List<String[]> auditLog ) {
        this( scenarioRunResult );
        this.auditLog = auditLog;
    }

    public ScenarioRunResult getResult() {
        return result;
    }

    public List<String[]> getAuditLog() {
        return auditLog;
    }

}
