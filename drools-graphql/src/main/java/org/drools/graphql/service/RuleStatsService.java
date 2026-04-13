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
package org.drools.graphql.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.graphql.dto.RuleStats;
import org.drools.graphql.dto.SessionStats;
import org.kie.api.management.GenericKieSessionMonitoringMXBean;
import org.kie.api.management.GenericKieSessionMonitoringMXBean.IAgendaStatsData;

/**
 * Service that extracts execution statistics from a {@link GenericKieSessionMonitoringMXBean}.
 */
public class RuleStatsService {

    private final GenericKieSessionMonitoringMXBean monitoring;

    public RuleStatsService(GenericKieSessionMonitoringMXBean monitoring) {
        this.monitoring = monitoring;
    }

    public SessionStats getSessionStats() {
        return new SessionStats(
                monitoring.getKieSessionName(),
                monitoring.getKieBaseId(),
                monitoring.getTotalMatchesFired(),
                monitoring.getTotalMatchesCancelled(),
                monitoring.getTotalMatchesCreated(),
                monitoring.getTotalFiringTime(),
                monitoring.getAverageFiringTime(),
                monitoring.getTotalSessions());
    }

    public RuleStats getStatsForRule(String ruleName) {
        IAgendaStatsData data = monitoring.getStatsForRule(ruleName);
        if (data == null) {
            return null;
        }
        return new RuleStats(
                ruleName,
                data.getMatchesFired(),
                data.getMatchesCreated(),
                data.getMatchesCancelled(),
                data.getFiringTime());
    }

    public List<RuleStats> getAllRuleStats() {
        Map<String, IAgendaStatsData> statsByRule = monitoring.getStatsByRule();
        if (statsByRule == null) {
            return Collections.emptyList();
        }
        return statsByRule.entrySet().stream()
                .map(e -> new RuleStats(
                        e.getKey(),
                        e.getValue().getMatchesFired(),
                        e.getValue().getMatchesCreated(),
                        e.getValue().getMatchesCancelled(),
                        e.getValue().getFiringTime()))
                .collect(Collectors.toList());
    }

    public List<RuleStats> getTopRulesByFiringCount(int limit) {
        return getAllRuleStats().stream()
                .sorted(Comparator.comparingLong(RuleStats::getMatchesFired).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<RuleStats> getTopRulesByFiringTime(int limit) {
        return getAllRuleStats().stream()
                .sorted(Comparator.comparingLong(RuleStats::getFiringTimeMs).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void resetStats() {
        monitoring.reset();
    }
}
