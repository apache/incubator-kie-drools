package org.drools.core.management;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.management.StatelessKieSessionMonitoringMBean;

public class StatelessKieSessionMonitoringImpl extends GenericKieSessionMonitoringImpl implements StatelessKieSessionMonitoringMBean {

    public RuleRuntimeStats ruleRuntimeStats;

    public StatelessKieSessionMonitoringImpl(String containerId, String kbaseId, String ksessionName) {
        super(containerId, kbaseId, ksessionName);

        this.ruleRuntimeStats = new RuleRuntimeStats();
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
            totalCount += ((StatelessKnowledgeSessionImpl) kr).getWorkingMemoryCreatec();
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
                this.lastReset = new AtomicReference<Date>(new Date());
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
