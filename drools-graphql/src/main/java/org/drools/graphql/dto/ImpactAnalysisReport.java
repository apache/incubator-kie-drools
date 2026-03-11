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
package org.drools.graphql.dto;

import java.util.List;

import org.eclipse.microprofile.graphql.Description;

@Description("Impact analysis results showing which rules are affected by changes to a target rule")
public class ImpactAnalysisReport {

    private String targetRule;
    private String targetPackage;
    private List<ImpactedRuleInfo> impactedRules;
    private List<ImpactedRuleInfo> impactingRules;
    private int totalImpacted;
    private int totalImpacting;

    public ImpactAnalysisReport() {
    }

    public String getTargetRule() {
        return targetRule;
    }

    public void setTargetRule(String targetRule) {
        this.targetRule = targetRule;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    @Description("Rules that would be impacted if this rule changes (forward analysis)")
    public List<ImpactedRuleInfo> getImpactedRules() {
        return impactedRules;
    }

    public void setImpactedRules(List<ImpactedRuleInfo> impactedRules) {
        this.impactedRules = impactedRules;
    }

    @Description("Rules that impact this rule (backward analysis)")
    public List<ImpactedRuleInfo> getImpactingRules() {
        return impactingRules;
    }

    public void setImpactingRules(List<ImpactedRuleInfo> impactingRules) {
        this.impactingRules = impactingRules;
    }

    public int getTotalImpacted() {
        return totalImpacted;
    }

    public void setTotalImpacted(int totalImpacted) {
        this.totalImpacted = totalImpacted;
    }

    public int getTotalImpacting() {
        return totalImpacting;
    }

    public void setTotalImpacting(int totalImpacting) {
        this.totalImpacting = totalImpacting;
    }
}
