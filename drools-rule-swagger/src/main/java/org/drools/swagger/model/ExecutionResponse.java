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
package org.drools.swagger.model;

import java.util.List;
import java.util.Map;

public class ExecutionResponse {

    private int rulesFired;
    private List<String> firedRuleNames;
    private List<Map<String, Object>> resultFacts;
    private Map<String, Object> globals;
    private long executionTimeMs;
    private String error;

    public ExecutionResponse() {
    }

    public int getRulesFired() {
        return rulesFired;
    }

    public void setRulesFired(int rulesFired) {
        this.rulesFired = rulesFired;
    }

    public List<String> getFiredRuleNames() {
        return firedRuleNames;
    }

    public void setFiredRuleNames(List<String> firedRuleNames) {
        this.firedRuleNames = firedRuleNames;
    }

    public List<Map<String, Object>> getResultFacts() {
        return resultFacts;
    }

    public void setResultFacts(List<Map<String, Object>> resultFacts) {
        this.resultFacts = resultFacts;
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, Object> globals) {
        this.globals = globals;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
