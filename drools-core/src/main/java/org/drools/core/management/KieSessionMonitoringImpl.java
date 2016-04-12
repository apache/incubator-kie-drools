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
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.management.KieSessionMonitoringImpl.AgendaStats.AgendaStatsData;
import org.drools.core.management.KieSessionMonitoringImpl.ProcessStats.ProcessInstanceStatsData;
import org.drools.core.management.KieSessionMonitoringImpl.ProcessStats.ProcessStatsData;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.management.KieSessionMonitoringMBean;
import org.kie.api.runtime.KieSession;

import javax.management.ObjectName;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An MBean to monitor a given knowledge session
 */
public class KieSessionMonitoringImpl implements KieSessionMonitoringMBean {

    private static final String KSESSION_PREFIX = "org.drools.kbases";
    
    private static final long NANO_TO_MILLISEC = 1000000;
    
    private InternalWorkingMemory ksession;
    private InternalKnowledgeBase kbase;
    private ObjectName name;
    public AgendaStats agendaStats;
    public ProcessStats processStats;
    
    public KieSessionMonitoringImpl(InternalWorkingMemory ksession) {
        this.ksession = ksession;
        this.kbase = ksession.getKnowledgeBase();
        this.name = DroolsManagementAgent.createObjectName(KSESSION_PREFIX + ":type="+kbase.getId()+",group=Sessions,sessionId=Session-"+ksession.getIdentifier());
        this.agendaStats = new AgendaStats();
        this.processStats = new ProcessStats();
        this.ksession.addEventListener( agendaStats );
        if (ksession.internalGetProcessRuntime() != null) {
            this.ksession.internalGetProcessRuntime().addEventListener( processStats );
        }
    }
    
    public void dispose() {
        this.ksession.removeEventListener( agendaStats );
        if (ksession.internalGetProcessRuntime() != null) {
            this.ksession.internalGetProcessRuntime().removeEventListener( processStats );
        }
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#reset()
     */
    public void reset() {
        this.agendaStats.reset();
        this.processStats.reset();
    }

    public InternalWorkingMemory getKsession() {
        return ksession;
    }

    public InternalKnowledgeBase getKbase() {
        return kbase;
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getName()
     */
    public ObjectName getName() {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getKnowledgeBaseId()
     */
    public String getKieBaseId() {
        return kbase.getId();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getKnowledgeSessionId()
     */
    public int getKieSessionId() {
        return ((KieSession)ksession).getId();
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeSessionMonitoringMBean#getTotalFactCount()
     */
    public long getTotalFactCount() {
        return ksession.getTotalFactCount();
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
    public String getStatsForRule( String ruleName ) {
        AgendaStatsData data = this.agendaStats.getRuleStats( ruleName );
        String result = data == null ? "matchesCreated=0 matchesCancelled=0 matchesFired=0 firingTime=0ms" : data.toString();
        return result;
    }
    
    public Map<String,String> getStatsByRule() {
        Map<String, String> result = new HashMap<String, String>();
        for( Map.Entry<String, AgendaStatsData> entry : this.agendaStats.getRulesStats().entrySet() ) {
            result.put( entry.getKey(), entry.getValue().toString() );
        }
        return result;
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

        public static class AgendaStatsData {
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
    
    public String getStatsForProcess( String processId ) {
        ProcessStatsData data = this.processStats.getProcessStats( processId );
        String result = data == null ? "processInstancesStarted=0 processInstancesCompleted=0 processNodesTriggered=0" : data.toString();
        return result;
    }
    
    public Map<String,String> getStatsByProcess() {
        Map<String, String> result = new HashMap<String, String>();
        for( Map.Entry<String, ProcessStatsData> entry : this.processStats.getProcessStats().entrySet() ) {
            result.put( entry.getKey(), entry.getValue().toString() );
        }
        return result;
    }
    
    public String getStatsForProcessInstance( long processInstanceId ) {
        ProcessInstanceStatsData data = this.processStats.getProcessInstanceStats( processInstanceId );
        String result = data == null ? "Process instance not found" : data.toString();
        return result;
    }
    
    public Map<Long,String> getStatsByProcessInstance() {
        Map<Long, String> result = new HashMap<Long, String>();
        for( Map.Entry<Long, ProcessInstanceStatsData> entry : this.processStats.getProcessInstanceStats().entrySet() ) {
            result.put( entry.getKey(), entry.getValue().toString() );
        }
        return result;
    }
    
    public static class ProcessStats implements org.kie.api.event.process.ProcessEventListener {
        
        private GlobalProcessStatsData consolidated = new GlobalProcessStatsData();
        private ConcurrentHashMap<String, ProcessStatsData> processStats = new ConcurrentHashMap<String, ProcessStatsData>();
        private ConcurrentHashMap<Long, ProcessInstanceStatsData> processInstanceStats = new ConcurrentHashMap<Long, ProcessInstanceStatsData>();

        public GlobalProcessStatsData getConsolidatedStats() {
            return this.consolidated;
        }
        
        public Map<String, ProcessStatsData> getProcessStats() {
            return this.processStats;
        }
        
        public ProcessStatsData getProcessStats(String processId) {
            return this.processStats.get(processId);
        }
        
        public Map<Long, ProcessInstanceStatsData> getProcessInstanceStats() {
            return this.processInstanceStats;
        }
        
        public ProcessInstanceStatsData getProcessInstanceStats(Long processInstanceId) {
            return this.processInstanceStats.get(processInstanceId);
        }
        
        public void reset() {
            this.consolidated.reset();
            this.processStats.clear();
            this.processInstanceStats.clear();
        }
        
        private ProcessStatsData getProcessStatsInstance(String processId) {
            ProcessStatsData data = this.processStats.get(processId);
            if (data == null) {
                data = new ProcessStatsData();
                this.processStats.put(processId, data);
            }
            return data;
        }

        private ProcessInstanceStatsData getProcessInstanceStatsInstance(Long processInstanceId) {
            ProcessInstanceStatsData data = this.processInstanceStats.get(processInstanceId);
            if (data == null) {
                data = new ProcessInstanceStatsData();
                this.processInstanceStats.put(processInstanceId, data);
            }
            return data;
        }

        public void afterProcessStarted(ProcessStartedEvent event) {
            this.consolidated.processInstancesStarted.incrementAndGet();
            ProcessStatsData data = getProcessStatsInstance(event.getProcessInstance().getProcessId());
            data.processInstancesStarted.incrementAndGet();
            ProcessInstanceStatsData dataI = getProcessInstanceStatsInstance(event.getProcessInstance().getId());
            dataI.processStarted = new Date();
        }

        public void afterProcessCompleted(ProcessCompletedEvent event) {
            this.consolidated.processInstancesCompleted.incrementAndGet();
            ProcessStatsData data = getProcessStatsInstance(event.getProcessInstance().getProcessId());
            data.processInstancesCompleted.incrementAndGet();
            ProcessInstanceStatsData dataI = getProcessInstanceStatsInstance(event.getProcessInstance().getId());
            dataI.processCompleted = new Date();
        }

        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            ProcessStatsData data = getProcessStatsInstance(event.getProcessInstance().getProcessId());
            data.processNodesTriggered.incrementAndGet();
            ProcessInstanceStatsData dataI = getProcessInstanceStatsInstance(event.getProcessInstance().getId());
            dataI.processNodesTriggered++;
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

        public static class GlobalProcessStatsData {

            public AtomicLong processInstancesStarted;
            public AtomicLong processInstancesCompleted;
            public AtomicReference<Date> lastReset;
            
            public GlobalProcessStatsData() {
                this.processInstancesStarted = new AtomicLong(0);
                this.processInstancesCompleted = new AtomicLong(0);
                this.lastReset = new AtomicReference<Date>(new Date());
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

        public static class ProcessStatsData extends GlobalProcessStatsData {

            public AtomicLong processNodesTriggered;
            
            public ProcessStatsData() {
                this.processNodesTriggered = new AtomicLong(0);
            }
            
            public void reset() {
                super.reset();
                this.processNodesTriggered.set( 0 );
            }
            
            public String toString() {
                return super.toString() + " processNodesTriggered=" + processNodesTriggered.get();
            }
        }

        public static class ProcessInstanceStatsData {

            // no need for synch, because one process instance cannot be executed concurrently
            public Date processStarted;
            public Date processCompleted;
            public long processNodesTriggered;
            
            public ProcessInstanceStatsData() {
                this.processNodesTriggered = 0;
            }
            
            public void reset() {
                 this.processNodesTriggered = 0;
            }
            
            public String toString() {
                return
                    (processStarted != null ? "processStarted=" + processStarted + " ": "") +
                    (processCompleted != null ? "processCompleted=" + processCompleted + " ": "") +
                    "processNodesTriggered=" + processNodesTriggered;
            }
        }

    }
    
}
