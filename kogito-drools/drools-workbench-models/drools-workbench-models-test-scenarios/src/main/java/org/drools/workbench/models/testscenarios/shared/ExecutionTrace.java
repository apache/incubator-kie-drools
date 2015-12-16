/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.shared;

import java.util.Date;

/**
 * This contains lists of rules to include in the scenario (or exclude, as the case may be !).
 * This will be used to filter the rule engines behaviour under test.
 */
public class ExecutionTrace
        implements Fixture {

    private static final long serialVersionUID = 510l;

    /**
     * This is the simulated date - leaving it as null means it will use
     * the current time.
     */
    private Date scenarioSimulatedDate = null;

    /**
     * The time taken for execution.
     */
    private Long executionTimeResult;

    /**
     * This is pretty obvious really. The total firing count of all rules.
     */
    private Long numberOfRulesFired;

    /**
     * A summary of the rules fired.
     */
    private String[] rulesFired;

    public ExecutionTrace() {
    }

    public void setScenarioSimulatedDate( final Date scenarioSimulatedDate ) {
        this.scenarioSimulatedDate = scenarioSimulatedDate;
    }

    public Date getScenarioSimulatedDate() {
        return scenarioSimulatedDate;
    }

    public void setExecutionTimeResult( final Long executionTimeResult ) {
        this.executionTimeResult = executionTimeResult;
    }

    public Long getExecutionTimeResult() {
        return executionTimeResult;
    }

    public void setNumberOfRulesFired( final Long numberOfRulesFired ) {
        this.numberOfRulesFired = numberOfRulesFired;
    }

    public Long getNumberOfRulesFired() {
        return numberOfRulesFired;
    }

    public void setRulesFired( final String[] rulesFired ) {
        this.rulesFired = rulesFired;
    }

    public String[] getRulesFired() {
        return rulesFired;
    }

}
