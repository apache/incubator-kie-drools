/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.management;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.ObjectName;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.management.KnowledgeSessionMonitoring.AgendaStats.AgendaStatsData;

/**
 * An MBean to monitor a given knowledge session
 * 
 * @author etirelli
 */
public class KnowledgeSessionMonitoring implements KnowledgeSessionMonitoringMBean {

    private static final String KSESSION_PREFIX = "org.drools.kbases";
    
    private static final long NANO_TO_MILLISEC = 1000000;
    
    private InternalWorkingMemory ksession;
    private InternalRuleBase kbase;
    private ObjectName name;
    public AgendaStats agendaStats;
    
    public KnowledgeSessionMonitoring(InternalWorkingMemory ksession) {
        this.ksession = ksession;
        this.kbase = (InternalRuleBase) ksession.getRuleBase();
        this.name = DroolsManagementAgent.createObjectName(KSESSION_PREFIX + ":type="+kbase.getId()+",group=Sessions,sessionId=Session-"+ksession.getId());
        this.agendaStats = new AgendaStats();
        this.ksession.addEventListener( agendaStats );
    }
    
    public void dispose() {
        this.ksession.removeEventListener( agendaStats );
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#reset()
     */
    public void reset() {
        this.agendaStats.reset();
    }

    public InternalWorkingMemory getKsession() {
        return ksession;
    }

    public InternalRuleBase getKbase() {
        return kbase;
    }

    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getName()
     */
    public ObjectName getName() {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getKnowledgeBaseId()
     */
    public String getKnowledgeBaseId() {
        return kbase.getId();
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getKnowledgeSessionId()
     */
    public int getKnowledgeSessionId() {
        return ksession.getId();
    }

    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getTotalFactCount()
     */
    public long getTotalFactCount() {
        return ksession.getTotalFactCount();
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getTotalActivationsFired()
     */
    public long getTotalActivationsFired() {
        return this.agendaStats.getConsolidatedStats().activationsFired.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getTotalActivationsCancelled()
     */
    public long getTotalActivationsCancelled() {
        return this.agendaStats.getConsolidatedStats().activationsCancelled.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getTotalActivationsCreated()
     */
    public long getTotalActivationsCreated() {
        return this.agendaStats.getConsolidatedStats().activationsCreated.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getTotalFiringTime()
     */
    public long getTotalFiringTime() {
        // converting nano secs to milli secs
        return this.agendaStats.getConsolidatedStats().firingTime.get()/NANO_TO_MILLISEC;
    }
    
    public Date getLastReset() {
        return this.agendaStats.getConsolidatedStats().lastReset.get();
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getAverageFiringTime()
     */
    public double getAverageFiringTime() {
        long fires = this.agendaStats.getConsolidatedStats().activationsFired.get();
        long time = this.agendaStats.getConsolidatedStats().firingTime.get();
        // calculating the average and converting it from nano secs to milli secs
        return fires > 0 ? (((double) time / (double) fires) / (double) NANO_TO_MILLISEC) : 0;
    }
    
    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeSessionMonitoringMBean#getStatsForRule(java.lang.String)
     */
    public String getStatsForRule( String ruleName ) {
        AgendaStatsData data = this.agendaStats.getRuleStats( ruleName );
        String result = data == null ? "activationsCreated=0 activationsCancelled=0 activationsFired=0 firingTime=0ms" : data.toString();
        return result;
    }
    
    public Map<String,String> getStatsByRule() {
        Map<String, String> result = new HashMap<String, String>();
        for( Map.Entry<String, AgendaStatsData> entry : this.agendaStats.getRulesStats().entrySet() ) {
            result.put( entry.getKey(), entry.getValue().toString() );
        }
        return result;
    }
    
    public static class AgendaStats implements AgendaEventListener {
        
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
        
        public void activationCancelled(ActivationCancelledEvent event,
                                        WorkingMemory workingMemory) {
            this.consolidated.activationsCancelled.incrementAndGet();
            AgendaStatsData data = getRuleStatsInstance( event.getActivation().getRule().getName() );
            data.activationsCancelled.incrementAndGet();
        }

        public void activationCreated(ActivationCreatedEvent event,
                                      WorkingMemory workingMemory) {
            this.consolidated.activationsCreated.incrementAndGet();
            AgendaStatsData data = getRuleStatsInstance( event.getActivation().getRule().getName() );
            data.activationsCreated.incrementAndGet();
        }

        public void afterActivationFired(AfterActivationFiredEvent event,
                                         WorkingMemory workingMemory) {
            AgendaStatsData data = getRuleStatsInstance( event.getActivation().getRule().getName() );
            this.consolidated.stopFireClock();
            data.stopFireClock();
            this.consolidated.activationsFired.incrementAndGet();
            data.activationsFired.incrementAndGet();
        }

        public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                                      WorkingMemory workingMemory) {
            // no stats gathered for now
        }

        public void agendaGroupPushed(AgendaGroupPushedEvent event,
                                      WorkingMemory workingMemory) {
            // no stats gathered for now
        }

        public void beforeActivationFired(BeforeActivationFiredEvent event,
                                          WorkingMemory workingMemory) {
            AgendaStatsData data = getRuleStatsInstance( event.getActivation().getRule().getName() );
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
            public AtomicLong activationsFired;
            public AtomicLong activationsCreated;
            public AtomicLong activationsCancelled;
            public AtomicLong firingTime;

            public AtomicReference<Date> lastReset;
            
            // no need for synch, because two activations cannot fire concurrently 
            public long start;

            public AgendaStatsData() {
                this.activationsFired = new AtomicLong(0);
                this.activationsCreated = new AtomicLong(0);
                this.activationsCancelled = new AtomicLong(0);
                this.firingTime = new AtomicLong(0);
                this.lastReset = new AtomicReference(new Date());
            }
            
            public void startFireClock() {
                this.start = System.nanoTime();
            }
            
            public void stopFireClock() {
                this.firingTime.addAndGet( System.nanoTime()-this.start );
            }
            
            public void reset() {
                this.activationsFired.set( 0 );
                this.activationsCreated.set( 0 );
                this.activationsCancelled.set( 0 );
                this.firingTime.set( 0 );
                this.lastReset.set( new Date() );
            }
            
            public String toString() {
                return "activationsCreated="+activationsCreated.get()+" activationsCancelled="+activationsCancelled.get()+
                       " activationsFired="+this.activationsFired.get()+" firingTime="+(firingTime.get()/NANO_TO_MILLISEC)+"ms";
            }
        }    
    }
    
}
