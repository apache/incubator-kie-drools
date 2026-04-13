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
package org.drools.graphql.api;

import java.util.List;

import jakarta.inject.Inject;

import org.drools.graphql.dto.ImpactAnalysisReport;
import org.drools.graphql.service.ImpactAnalysisService;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

/**
 * GraphQL API for rule impact analysis.
 *
 * <p>Example queries:
 * <pre>
 * {
 *   impactAnalysis(ruleName: "Validate Order") {
 *     targetRule
 *     totalImpacted
 *     impactedRules { ruleName packageName reactivityType }
 *     impactingRules { ruleName packageName reactivityType }
 *   }
 *   analyzedRules
 * }
 * </pre>
 */
@GraphQLApi
public class ImpactQueryApi {

    @Inject
    ImpactAnalysisService impactService;

    public ImpactQueryApi() {
    }

    public ImpactQueryApi(ImpactAnalysisService impactService) {
        this.impactService = impactService;
    }

    @Query("impactAnalysis")
    @Description("Analyze which rules are impacted by changes to a given rule (forward analysis) " +
                 "and which rules impact the given rule (backward analysis)")
    public ImpactAnalysisReport getImpactAnalysis(
            @Name("ruleName") @NonNull @Description("Name of the rule to analyze") String ruleName) {
        return impactService.analyze(ruleName);
    }

    @Query("analyzedRules")
    @Description("List all rule names available for impact analysis")
    public List<String> getAnalyzedRules() {
        return impactService.getAllAnalyzedRuleNames();
    }
}
