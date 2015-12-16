/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend;

import java.util.HashSet;
import java.util.List;

public class ScenarioSettings {

    private HashSet<String> ruleList = new HashSet<String>();
    private boolean inclusive;
    private int maxRuleFirings;

    public void setRuleList(List<String> ruleList) {
        this.ruleList.addAll(ruleList);
    }

    public HashSet<String> getRuleList() {
        return ruleList;
    }

    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public int getMaxRuleFirings() {
        return maxRuleFirings;
    }

    public void setMaxRuleFirings(int maxRuleFirings) {
        this.maxRuleFirings = maxRuleFirings;
    }
}
