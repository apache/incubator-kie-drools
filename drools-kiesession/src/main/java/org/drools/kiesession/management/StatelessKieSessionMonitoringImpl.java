/**
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
package org.drools.kiesession.management;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.management.ObjectName;

import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.management.GenericKieSessionMonitoringImpl;
import org.drools.kiesession.session.StatelessKnowledgeSessionImpl;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.management.StatelessKieSessionMonitoringMXBean;

public class StatelessKieSessionMonitoringImpl extends GenericKieSessionMonitoringImpl implements StatelessKieSessionMonitoringMXBean {

    private ObjectName name;
    public RuleRuntimeStats ruleRuntimeStats;

    public StatelessKieSessionMonitoringImpl(String containerId, String kbaseId, String ksessionName) {
        super(containerId, kbaseId, ksessionName);
        
        this.name = DroolsManagementAgent.createObjectNameBy(containerId, kbaseId, KieSessionType.STATELESS, ksessionName);
        this.ruleRuntimeStats = new RuleRuntimeStats();
    }
    
    @Override
    public ObjectName getName() {
        return this.name;
    }

    public void attach(KieRuntimeEventManager ksession) {
        ksession.addEventListener( ruleRuntimeStats );
        super.attach(ksession);
    }
    
    public void detach(KieRuntimeEventManager ksession) {
        ksession.removeEventListener( ruleRuntimeStats );
        super.detach(ksession);
    }
    
    public void dispose() {
        for (KieRuntimeEventManager ksession : ksessions) {
            ksession.removeEventListener( ruleRuntimeStats );
        }
        super.dispose();
    }
    
    public void reset() {
        this.ruleRuntimeStats.reset();
        super.reset();
    }
    
    @Override
    public long getTotalSessions() {
        long totalCount = 0;
        for (KieRuntimeEventManager kr : ksessions) {
            totalCount += ((StatelessKnowledgeSessionImpl) kr).getWorkingMemoryCreated();
        }
        return totalCount;
    }
    
    @Override
    public long getTotalObjectsInserted() {
        return this.ruleRuntimeStats.getConsolidatedStats().objectsInserted.get();
    }
    
    @Override
    public long getTotalObjectsDeleted() {
        return this.ruleRuntimeStats.getConsolidatedStats().objectsDeleted.get();
    }
    
    public static class RuleRuntimeStats implements org.kie.api.event.rule.RuleRuntimeEventListener {
        private RuleRuntimeStatsData data = new RuleRuntimeStatsData();

        public RuleRuntimeStats() {
        }
        
        public RuleRuntimeStatsData getConsolidatedStats() {
            return this.data;
        }
        
        public void reset() {
            this.data.reset();
        }
        
        @Override
        public void objectInserted(ObjectInsertedEvent event) {
            this.data.objectsInserted.incrementAndGet();
        }

        @Override
        public void objectUpdated(ObjectUpdatedEvent event) { }

        @Override
        public void objectDeleted(ObjectDeletedEvent event) {
            this.data.objectsDeleted.incrementAndGet();
        }
        
        public static class RuleRuntimeStatsData {
            public AtomicLong objectsInserted;
            public AtomicLong objectsDeleted;

            public AtomicReference<Date> lastReset;
            
            public RuleRuntimeStatsData() {
                this.objectsInserted = new AtomicLong(0);
                this.objectsDeleted = new AtomicLong(0);
                this.lastReset = new AtomicReference<>(new Date());
            }
            
            public void reset() {
                this.objectsInserted.set( 0 );
                this.objectsDeleted.set( 0 );
                this.lastReset.set( new Date() );
            }
            
            public String toString() {
                return "objectsInserted="+objectsInserted.get()+" objectsDeleted="+objectsDeleted.get();
            }
        }
        
    }
}
