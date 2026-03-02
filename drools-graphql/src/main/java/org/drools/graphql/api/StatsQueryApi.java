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

import org.drools.graphql.dto.RuleStats;
import org.drools.graphql.dto.SessionStats;
import org.drools.graphql.service.RuleStatsService;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

/**
 * GraphQL API for querying execution statistics.
 *
 * <p>Example queries:
 * <pre>
 * {
 *   sessionStats { sessionName totalMatchesFired totalFiringTimeMs averageFiringTimeMs }
 *   ruleStats(ruleName: "Validate Order") { matchesFired matchesCancelled firingTimeMs }
 *   topRulesByFiringCount(limit: 5) { ruleName matchesFired averageFiringTimeMs }
 * }
 * </pre>
 */
@GraphQLApi
public class StatsQueryApi {

    @Inject
    RuleStatsService statsService;

    public StatsQueryApi() {
    }

    public StatsQueryApi(RuleStatsService statsService) {
        this.statsService = statsService;
    }

    @Query("sessionStats")
    @Description("Aggregate execution statistics for the KIE session")
    public SessionStats getSessionStats() {
        return statsService.getSessionStats();
    }

    @Query("ruleStats")
    @Description("Execution statistics for a specific rule")
    public RuleStats getRuleStats(@Name("ruleName") @NonNull String ruleName) {
        return statsService.getStatsForRule(ruleName);
    }

    @Query("allRuleStats")
    @Description("Execution statistics for all rules that have been activated")
    public List<RuleStats> getAllRuleStats() {
        return statsService.getAllRuleStats();
    }

    @Query("topRulesByFiringCount")
    @Description("Rules sorted by most matches fired, limited to the top N")
    public List<RuleStats> getTopRulesByFiringCount(
            @Name("limit") @Description("Maximum number of rules to return") int limit) {
        return statsService.getTopRulesByFiringCount(limit);
    }

    @Query("topRulesByFiringTime")
    @Description("Rules sorted by total firing time (slowest first), limited to the top N")
    public List<RuleStats> getTopRulesByFiringTime(
            @Name("limit") @Description("Maximum number of rules to return") int limit) {
        return statsService.getTopRulesByFiringTime(limit);
    }

    @Mutation("resetStats")
    @Description("Reset all session execution statistics")
    public boolean resetStats() {
        statsService.resetStats();
        return true;
    }
}
