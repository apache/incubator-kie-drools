package org.jbpm.test.timer;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.test.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public abstract class TimerBaseTest extends AbstractBaseTest {
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
        if (pds == null) {
            pds = setupPoolingDataSource();
        }
    }
    
    @AfterClass
    public static void tearDownOnce() {
        if (pds != null) {
            pds.close();
            pds = null;
        }
    }
    

    protected void testCreateQuartzSchema() {
        Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/quartz_tables_h2.sql")).useDelimiter(";");
        try {
            Connection connection = ((DataSource)InitialContext.doLookup("jdbc/jbpm-ds")).getConnection();
            Statement stmt = connection.createStatement();
            while (scanner.hasNext()) {
                String sql = scanner.next();
                stmt.executeUpdate(sql);
            }
            stmt.close();
            connection.close();
        } catch (Exception e) {
            
        }
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
