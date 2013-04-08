package org.jbpm.test.timer;

import java.util.List;

import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.internal.runtime.manager.RuntimeEngine;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public abstract class TimerBaseTest {
    private static PoolingDataSource pds;
    
    private static PoolingDataSource setupPoolingDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:test;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();
        return pds;
    }
    
    @BeforeClass
    public static void setUpOnce() {
        pds = setupPoolingDataSource();
    }
    
    @AfterClass
    public static void tearDownOnce() {
        pds.close();
    }
    
    protected class TestRegisterableItemsFactory extends DefaultRegisterableItemsFactory {
        private ProcessEventListener plistener;
        private AgendaEventListener alistener;
        
        public TestRegisterableItemsFactory(ProcessEventListener listener) {
            this.plistener = listener;
        }
        
        public TestRegisterableItemsFactory(AgendaEventListener listener) {
            this.alistener = listener;
        }

        @Override
        public List<ProcessEventListener> getProcessEventListeners(
                RuntimeEngine runtime) {
            
            List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
            if (plistener != null) {
                listeners.add(plistener);
            }
            
            return listeners;
        }
        @Override
        public List<AgendaEventListener> getAgendaEventListeners(
                RuntimeEngine runtime) {
            
            List<AgendaEventListener> listeners = super.getAgendaEventListeners(runtime);
            if (alistener != null) { 
                listeners.add(alistener);
            }
            
            return listeners;
        } 
        
    }
}
