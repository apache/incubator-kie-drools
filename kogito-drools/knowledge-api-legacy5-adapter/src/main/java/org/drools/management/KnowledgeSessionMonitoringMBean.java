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

package org.drools.management;

import java.util.Date;
import java.util.Map;

import javax.management.ObjectName;

/**
 * An MBean interface for Knowledge Session monitoring
 */
public interface KnowledgeSessionMonitoringMBean {

    /**
     * Resets all stats
     */
    public void reset();

    /**
     * Returns this MBean name
     * 
     * @return
     */
    public ObjectName getName();

    /**
     * Returns the associated knowledge base ID
     * 
     * @return
     */
    public String getKnowledgeBaseId();

    /**
     * Returns the associated knowledge session ID
     * 
     * @return
     */
    public int getKnowledgeSessionId();

    /**
     * Returns the total fact count current loaded into this session
     * 
     * @return
     */
    public long getTotalFactCount();

    /**
     * Returns the total number of activations fired in this session since last 
     * reset.
     * 
     * @return
     */
    public long getTotalActivationsFired();

    /**
     * Returns the total number of activations cancelled in this session since 
     * last reset.
     *  
     * @return
     */
    public long getTotalActivationsCancelled();

    /**
     * Returns the total number of activations created in this session since 
     * last reset.
     * 
     * @return
     */
    public long getTotalActivationsCreated();

    /**
     * Returns the total milliseconds spent firing rules in this session since last reset.
     * 
     * @return
     */
    public long getTotalFiringTime();

    /**
     * Returns the average firing time in milliseconds for rules in this session
     * since last reset.
     * 
     * @return
     */
    public double getAverageFiringTime();

    /**
     * Returns a formatted String with statistics for a single rule in this session,
     * like number of activations created, cancelled and fired as well as firing time.
     *  
     * @param ruleName the name of the rule for which statistics are requested.
     * 
     * @return
     */
    public String getStatsForRule(String ruleName);

    /**
     * Returns the timestamp of the last stats reset
     * 
     * @return
     */
    public Date getLastReset();
    
    public Map<String,String> getStatsByRule();

    public long getTotalProcessInstancesStarted();
    
    public long getTotalProcessInstancesCompleted();
    
    public String getStatsForProcess(String processId);
    
    public Map<String,String> getStatsByProcess();
    
    public String getStatsForProcessInstance(long processInstanceId);
    
    public Map<Long,String> getStatsByProcessInstance();
}
