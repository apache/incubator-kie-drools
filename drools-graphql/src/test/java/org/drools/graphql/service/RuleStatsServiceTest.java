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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.graphql.dto.RuleStats;
import org.drools.graphql.dto.SessionStats;
import org.kie.api.management.GenericKieSessionMonitoringMXBean;
import org.kie.api.management.GenericKieSessionMonitoringMXBean.IAgendaStatsData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RuleStatsServiceTest {

    private GenericKieSessionMonitoringMXBean monitoring;
    private RuleStatsService service;

    @BeforeEach
    void setUp() {
        monitoring = mock(GenericKieSessionMonitoringMXBean.class);
        when(monitoring.getKieSessionName()).thenReturn("test-session");
        when(monitoring.getKieBaseId()).thenReturn("test-kbase");
        when(monitoring.getTotalMatchesFired()).thenReturn(100L);
        when(monitoring.getTotalMatchesCancelled()).thenReturn(5L);
        when(monitoring.getTotalMatchesCreated()).thenReturn(110L);
        when(monitoring.getTotalFiringTime()).thenReturn(2000L);
        when(monitoring.getAverageFiringTime()).thenReturn(20.0);
        when(monitoring.getTotalSessions()).thenReturn(3L);

        Map<String, IAgendaStatsData> statsMap = new LinkedHashMap<>();
        statsMap.put("Rule A", mockStats(50, 55, 2, 1000));
        statsMap.put("Rule B", mockStats(30, 32, 1, 500));
        statsMap.put("Rule C", mockStats(20, 23, 2, 3000));
        when(monitoring.getStatsByRule()).thenReturn(statsMap);
        when(monitoring.getStatsForRule("Rule A")).thenReturn(statsMap.get("Rule A"));
        when(monitoring.getStatsForRule("Missing")).thenReturn(null);

        service = new RuleStatsService(monitoring);
    }

    @Test
    void shouldReturnSessionStats() {
        SessionStats stats = service.getSessionStats();

        assertThat(stats.getSessionName()).isEqualTo("test-session");
        assertThat(stats.getKieBaseId()).isEqualTo("test-kbase");
        assertThat(stats.getTotalMatchesFired()).isEqualTo(100L);
        assertThat(stats.getTotalMatchesCancelled()).isEqualTo(5L);
        assertThat(stats.getTotalMatchesCreated()).isEqualTo(110L);
        assertThat(stats.getTotalFiringTimeMs()).isEqualTo(2000L);
        assertThat(stats.getAverageFiringTimeMs()).isEqualTo(20.0);
        assertThat(stats.getTotalSessions()).isEqualTo(3L);
    }

    @Test
    void shouldReturnStatsForSingleRule() {
        RuleStats stats = service.getStatsForRule("Rule A");

        assertThat(stats).isNotNull();
        assertThat(stats.getRuleName()).isEqualTo("Rule A");
        assertThat(stats.getMatchesFired()).isEqualTo(50);
        assertThat(stats.getMatchesCreated()).isEqualTo(55);
        assertThat(stats.getMatchesCancelled()).isEqualTo(2);
        assertThat(stats.getFiringTimeMs()).isEqualTo(1000);
    }

    @Test
    void shouldReturnNullForMissingRule() {
        assertThat(service.getStatsForRule("Missing")).isNull();
    }

    @Test
    void shouldReturnAllRuleStats() {
        List<RuleStats> stats = service.getAllRuleStats();
        assertThat(stats).hasSize(3);
    }

    @Test
    void shouldReturnTopRulesByFiringCount() {
        List<RuleStats> top = service.getTopRulesByFiringCount(2);
        assertThat(top).hasSize(2);
        assertThat(top.get(0).getRuleName()).isEqualTo("Rule A");
        assertThat(top.get(1).getRuleName()).isEqualTo("Rule B");
    }

    @Test
    void shouldReturnTopRulesByFiringTime() {
        List<RuleStats> top = service.getTopRulesByFiringTime(2);
        assertThat(top).hasSize(2);
        assertThat(top.get(0).getRuleName()).isEqualTo("Rule C");
        assertThat(top.get(0).getFiringTimeMs()).isEqualTo(3000);
    }

    @Test
    void shouldResetStats() {
        service.resetStats();
        verify(monitoring).reset();
    }

    private static IAgendaStatsData mockStats(long fired, long created, long cancelled, long firingTime) {
        IAgendaStatsData data = mock(IAgendaStatsData.class);
        when(data.getMatchesFired()).thenReturn(fired);
        when(data.getMatchesCreated()).thenReturn(created);
        when(data.getMatchesCancelled()).thenReturn(cancelled);
        when(data.getFiringTime()).thenReturn(firingTime);
        when(data.getLastReset()).thenReturn(new Date());
        return data;
    }
}
