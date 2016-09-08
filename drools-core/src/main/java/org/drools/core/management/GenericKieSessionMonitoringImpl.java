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

package org.drools.core.management;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.management.GenericKieSessionMonitoringImpl.AgendaStats.AgendaStatsData;
import org.drools.core.management.GenericKieSessionMonitoringImpl.ProcessStats.ProcessStatsData;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.management.GenericKieSessionMonitoringMXBean;

import javax.management.ObjectName;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An MBean to monitor a given knowledge session
 */
public abstract class GenericKieSessionMonitoringImpl implements GenericKieSessionMonitoringMXBean {

    private static final long NANO_TO_MILLISEC = 1000000;
    
    protected List<KieRuntimeEventManager> ksessions = new CopyOnWriteArrayList<KieRuntimeEventManager>();

    public AgendaStats agendaStats;
    public ProcessStats processStats;

    private String containerId;
    private String kbaseId;
    private String ksessionName;
    
    public GenericKieSessionMonitoringImpl(String containerId, String kbaseId, String ksessionName) {
        this.containerId = containerId;
        this.kbaseId = kbaseId;
        this.ksessionName = ksessionName;
        this.agendaStats = new AgendaStats();
        this.processStats = new ProcessStats();
    }
    
    public void attach(KieRuntimeEventManager ksession) {
        ksession.addEventListener( agendaStats );
        ksession.addEventListener( processStats );
        ksessions.add(ksession);
    }
    
    public void detach(KieRuntimeEventManager ksession) {
        ksession.removeEventListener( agendaStats );
        ksession.removeEventListener( processStats );
        ksessions.remove(ksession);
    }
    
    public void dispose() {
        for (KieRuntimeEventManager ksession : ksessions) {
            ksession.removeEventListener( agendaStats );
            ksession.removeEventListener( processStats );
        }
        ksessions.clear();
    }
    
    public void reset() {
        this.agendaStats.reset();
        this.processStats.reset();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getKnowledgeBaseId()
     */
    public String getKieBaseId() {
        return kbaseId;
    }
    
    @Override
    public String getKieSessionName() {
        return this.ksessionName;
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getTotalMatchesFired()
     */
    public long getTotalMatchesFired() {
        return this.agendaStats.getConsolidatedStats().matchesFired.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getTotalMatchesCancelled()
     */
    public long getTotalMatchesCancelled() {
        return this.agendaStats.getConsolidatedStats().matchesCancelled.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getTotalMatchesCreated()
     */
    public long getTotalMatchesCreated() {
        return this.agendaStats.getConsolidatedStats().matchesCreated.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getTotalFiringTime()
     */
    public long getTotalFiringTime() {
        // converting nano secs to milli secs
        return this.agendaStats.getConsolidatedStats().firingTime.get()/NANO_TO_MILLISEC;
    }
    
    public Date getLastReset() {
        return this.agendaStats.getConsolidatedStats().lastReset.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getAverageFiringTime()
     */
    public double getAverageFiringTime() {
        long fires = this.agendaStats.getConsolidatedStats().matchesFired.get();
        long time = this.agendaStats.getConsolidatedStats().firingTime.get();
        // calculating the average and converting it from nano secs to milli secs
        return fires > 0 ? (((double) time / (double) fires) / (double) NANO_TO_MILLISEC) : 0;
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getStatsForRule(java.lang.String)
     */
    public IAgendaStatsData getStatsForRule( String ruleName ) {
        AgendaStatsData data = this.agendaStats.getRuleStats( ruleName );
        return ( data == null ) ? null : data;
    }
    
    public Map<String,IAgendaStatsData> getStatsByRule() {
        return Collections.unmodifiableMap(this.agendaStats.getRulesStats());
    }
    
    public static class AgendaStats implements org.kie.api.event.rule.AgendaEventListener {
        
        private AgendaStatsData consolidated = new AgendaStatsData();
        private ConcurrentHashMap<String, AgendaStatsData> ruleStats = new ConcurrentHashMap<String, AgendaStatsData>();

        public AgendaStats() {
        }
        
        public AgendaStatsData getConsolidatedStats() {
            return this.consolidated;
        }
        
        public Map<String, AgendaStatsData> getRulesStats() {
            return this.ruleStats;
        }
        
        public AgendaStatsData getRuleStats( String ruleName ) {
            return this.ruleStats.get( ruleName );
        }
        
        public void reset() {
            this.consolidated.reset();
            this.ruleStats.clear();
        }
        
        public void matchCancelled(MatchCancelledEvent event) {
            this.consolidated.matchesCancelled.incrementAndGet();
            AgendaStatsData data = getRuleStatsInstance( event.getMatch().getRule().getName() );
            data.matchesCancelled.incrementAndGet();
        }

        public void matchCreated(MatchCreatedEvent event) {
            this.consolidated.matchesCreated.incrementAndGet();
            AgendaStatsData data = getRuleStatsInstance( event.getMatch().getRule().getName() );
            data.matchesCreated.incrementAndGet();
        }

        public void afterMatchFired(AfterMatchFiredEvent event) {
            AgendaStatsData data = getRuleStatsInstance( event.getMatch().getRule().getName() );
            this.consolidated.stopFireClock();
            data.stopFireClock();
            this.consolidated.matchesFired.incrementAndGet();
            data.matchesFired.incrementAndGet();
        }

        public void agendaGroupPopped(org.kie.api.event.rule.AgendaGroupPoppedEvent event) { }

        public void agendaGroupPushed(org.kie.api.event.rule.AgendaGroupPushedEvent event) { }

        public void beforeRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) { }

        public void afterRuleFlowGroupActivated(org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) { }

        public void beforeRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) { }

        public void afterRuleFlowGroupDeactivated(org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) { }

        public void beforeMatchFired(BeforeMatchFiredEvent event) {
            AgendaStatsData data = getRuleStatsInstance( event.getMatch().getRule().getName() );
            this.consolidated.startFireClock();
            data.startFireClock();
        }
        
        private AgendaStatsData getRuleStatsInstance(String ruleName) {
            AgendaStatsData data = this.ruleStats.get( ruleName );
            if( data == null ) {
                data = new AgendaStatsData();
                this.ruleStats.put( ruleName, data );
            }
            return data;
        }

        public static class AgendaStatsData implements IAgendaStatsData {
            public AtomicLong matchesFired;
            public AtomicLong matchesCreated;
            public AtomicLong matchesCancelled;
            public AtomicLong firingTime;

            public AtomicReference<Date> lastReset;
            
            // no need for synch, because two matches cannot fire concurrently 
            public long start;

            public AgendaStatsData() {
                this.matchesFired = new AtomicLong(0);
                this.matchesCreated = new AtomicLong(0);
                this.matchesCancelled = new AtomicLong(0);
                this.firingTime = new AtomicLong(0);
                this.lastReset = new AtomicReference<Date>(new Date());
            }
            
            @Override
            public long getMatchesFired() {
                return matchesFired.get();
            }
            @Override
            public long getMatchesCreated() {
                return matchesCreated.get();
            }
            @Override
            public long getMatchesCancelled() {
                return matchesCancelled.get();
            }
            @Override
            public long getFiringTime() {
                return firingTime.get();
            }
            @Override
            public Date getLastReset() {
                return lastReset.get();
            }

            public void startFireClock() {
                this.start = System.nanoTime();
            }
            
            public void stopFireClock() {
                this.firingTime.addAndGet( System.nanoTime()-this.start );
            }
            
            public void reset() {
                this.matchesFired.set( 0 );
                this.matchesCreated.set( 0 );
                this.matchesCancelled.set( 0 );
                this.firingTime.set( 0 );
                this.lastReset.set( new Date() );
            }
            
            public String toString() {
                return "matchesCreated="+matchesCreated.get()+" matchesCancelled="+matchesCancelled.get()+
                       " matchesFired="+this.matchesFired.get()+" firingTime="+(firingTime.get()/NANO_TO_MILLISEC)+"ms";
            }
        }
    }
    
    public long getTotalProcessInstancesStarted() {
        return this.processStats.getConsolidatedStats().processInstancesStarted.get();
    }
    
    public long getTotalProcessInstancesCompleted() {
        return this.processStats.getConsolidatedStats().processInstancesCompleted.get();
    }
    
    public IProcessStatsData getStatsForProcess( String processId ) {
        ProcessStatsData data = this.processStats.getProcessStats( processId );
        return ( data == null ) ? null : data;
    }
    
    public Map<String,IProcessStatsData> getStatsByProcess() {
        return Collections.unmodifiableMap(this.processStats.getProcessStats());
    }
    
    public static class ProcessStats implements org.kie.api.event.process.ProcessEventListener {
        
        private GlobalProcessStatsData consolidated = new GlobalProcessStatsData();
        private ConcurrentHashMap<String, ProcessStatsData> processStats = new ConcurrentHashMap<String, ProcessStatsData>();

        public GlobalProcessStatsData getConsolidatedStats() {
            return this.consolidated;
        }
        
        public Map<String, ProcessStatsData> getProcessStats() {
            return this.processStats;
        }
        
        public ProcessStatsData getProcessStats(String processId) {
            return this.processStats.get(processId);
        }
        
        public void reset() {
            this.consolidated.reset();
            this.processStats.clear();
        }
        
        private ProcessStatsData getProcessStatsInstance(String processId) {
            ProcessStatsData data = this.processStats.get(processId);
            if (data == null) {
                data = new ProcessStatsData();
                this.processStats.put(processId, data);
            }
            return data;
        }

        public void afterProcessStarted(ProcessStartedEvent event) {
            this.consolidated.processInstancesStarted.incrementAndGet();
            ProcessStatsData data = getProcessStatsInstance(event.getProcessInstance().getProcessId());
            data.processInstancesStarted.incrementAndGet();
        }

        public void afterProcessCompleted(ProcessCompletedEvent event) {
            this.consolidated.processInstancesCompleted.incrementAndGet();
            ProcessStatsData data = getProcessStatsInstance(event.getProcessInstance().getProcessId());
            data.processInstancesCompleted.incrementAndGet();
        }

        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            ProcessStatsData data = getProcessStatsInstance(event.getProcessInstance().getProcessId());
            data.processNodesTriggered.incrementAndGet();
        }

        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeNodeLeft(ProcessNodeLeftEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeProcessCompleted(ProcessCompletedEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeProcessStarted(ProcessStartedEvent event) {
            // TODO Auto-generated method stub

        }
        
        public void afterVariableChanged(ProcessVariableChangedEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeVariableChanged(ProcessVariableChangedEvent event) {
            // TODO Auto-generated method stub

        }

        public static class GlobalProcessStatsData implements IGlobalProcessStatsData {

            public AtomicLong processInstancesStarted;
            public AtomicLong processInstancesCompleted;
            public AtomicReference<Date> lastReset;
            
            public GlobalProcessStatsData() {
                this.processInstancesStarted = new AtomicLong(0);
                this.processInstancesCompleted = new AtomicLong(0);
                this.lastReset = new AtomicReference<Date>(new Date());
            }
            
            @Override
            public long getProcessInstancesStarted() {
                return processInstancesStarted.get();
            }
            @Override
            public long getProcessInstancesCompleted() {
                return processInstancesCompleted.get();
            }
            @Override
            public Date getLastReset() {
                return lastReset.get();
            }

            public void reset() {
                this.processInstancesStarted.set( 0 );
                this.processInstancesCompleted.set( 0 );
                this.lastReset.set( new Date() );
            }
            
            public String toString() {
                return "processInstancesStarted=" + processInstancesStarted.get()
                    + " processInstancesCompleted=" + processInstancesCompleted.get();
            }
        }

        public static class ProcessStatsData extends GlobalProcessStatsData implements IProcessStatsData {

            public AtomicLong processNodesTriggered;
            
            public ProcessStatsData() {
                this.processNodesTriggered = new AtomicLong(0);
            }
            
            @Override
            public long getProcessNodesTriggered() {
                return processNodesTriggered.get();
            }

            public void reset() {
                super.reset();
                this.processNodesTriggered.set( 0 );
            }
            
            public String toString() {
                return super.toString() + " processNodesTriggered=" + processNodesTriggered.get();
            }
        }

    }
}
