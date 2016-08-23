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

package org.kie.api.management;

import java.util.Date;
import java.util.Map;

import javax.management.ObjectName;

/**
 * An MBean interface for {@link org.kie.api.runtime.KieSession} monitoring
 */
public interface KieSessionMonitoringMBean {

    /**
     * Resets all stats
     */
    void reset();

    /**
     * Returns this MBean name
     */
    ObjectName getName();

    /**
     * @return the associated Kie Base ID
     */
    String getKieBaseId();

    /**
     * @return the associated Kie Session ID
     */
    int getKieSessionId();

    /**
     * @return the total fact count current loaded into this session
     */
    long getTotalFactCount();

    /**
     * @return the total number of matches fired in this session since last
     * reset.
     */
    long getTotalMatchesFired();

    /**
     * @return the total number of matches cancelled in this session since
     * last reset.
     */
    long getTotalMatchesCancelled();

    /**
     * @return the total number of matches created in this session since
     * last reset.
     */
    long getTotalMatchesCreated();

    /**
     * @return the total milliseconds spent firing rules in this session since last reset.
     */
    long getTotalFiringTime();

    /**
     * @return the average firing time in milliseconds for rules in this session
     * since last reset.
     */
    double getAverageFiringTime();

    /**
     * Returns a formatted String with statistics for a single rule in this session,
     * like number of matches created, cancelled and fired as well as firing time.
     *
     * @param ruleName the name of the rule for which statistics are requested.
     *
     * @return formatted String with statistics
     */
    String getStatsForRule(String ruleName);

    /**
     * @return the timestamp of the last stats reset
     */
    Date getLastReset();

    Map<String,String> getStatsByRule();

    long getTotalProcessInstancesStarted();

    long getTotalProcessInstancesCompleted();

    String getStatsForProcess(String processId);

    Map<String,String> getStatsByProcess();

    String getStatsForProcessInstance(long processInstanceId);

    Map<Long,String> getStatsByProcessInstance();
}
