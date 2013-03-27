package org.jbpm.test.timer;

import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.drools.core.time.TimerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.process.core.timer.impl.GlobalTimerService;
import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

@RunWith(Parameterized.class)
public class GlobalQuartzDBTimerServiceTest extends GlobalTimerServiceBaseTest {
    
    private int managerType;
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { 1 }, { 2 }, { 3 }  };
        return Arrays.asList(data);
    };
    
    public GlobalQuartzDBTimerServiceTest(int managerType) {
        this.managerType = managerType;
    }
    
    @Before
    public void setUp() {
        cleanupSingletonSessionId();
        System.setProperty("org.quartz.properties", "quartz-db.properties");
        testCreateQuartzSchema();
        globalScheduler = new QuartzSchedulerService();
    }
    
    @After
    public void tearDown() {
        try {
            
            globalScheduler.shutdown();
        } catch (Exception e) {
            
        }
        System.clearProperty("org.quartz.properties");
    }
    
    @Override
    protected RuntimeManager getManager(RuntimeEnvironment environment) {
        if (managerType ==1) {
            return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        } else if (managerType == 2) {
            return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        } else if (managerType == 3) {
            return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        } else {
            throw new IllegalArgumentException("Invalid runtime maanger type");
        }
    }

    private void testCreateQuartzSchema() {
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
    
    
    /**
     * Test that illustrates that jobs are persisted and survives server restart
     * and as soon as GlobalTimerService is active jobs are fired and it loads and aborts the 
     * process instance to illustrate jobs are properly removed when isntance is aborted
     * NOTE: this test is disabled by default as it requires real db (not in memory)
     * and test to be executed separately each with new jvm process
     */
    @Test 
    @Ignore
    public void testAbortGlobalTestService() throws Exception {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2);
        environment.addToConfiguration("drools.timerService", "org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate");
        RuntimeManager manger = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        
        // build GlobalTimerService instance
        
        TimerService globalTs = new GlobalTimerService(manger, globalScheduler);
        // and register it in the registry under 'default' key
        TimerServiceRegistry.getInstance().registerTimerService("default", globalTs);
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    timerExporations.add(event.getProcessInstance().getId());
                }
            }
            
        };        
        long id = -1;
        Thread.sleep(5000);
        Runtime runtime = manger.getRuntime(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        ksession.addEventListener(listener);
        
        ksession.abortProcessInstance(id);
        ProcessInstance processInstance = ksession.getProcessInstance(id);        
        assertNull(processInstance);
        // let's wait to ensure no more timers are expired and triggered
        Thread.sleep(3000);
        ksession.dispose();
        
    }
    
    /**
     * Test that illustrates that jobs are persisted and survives server restart
     * and as soon as GlobalTimerService is active jobs are fired
     * NOTE: this test is disabled by default as it requires real db (not in memory)
     * and test to be executed separately each with new jvm process
     */
    @Test
    @Ignore
    public void testContinueGlobalTestService() throws Exception {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2"), ResourceType.BPMN2);
        environment.addToConfiguration("drools.timerService", "org.jbpm.process.core.timer.impl.RegisteredTimerServiceDelegate");
        RuntimeManager manger = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        
        // build GlobalTimerService instance
        
        TimerService globalTs = new GlobalTimerService(manger, globalScheduler);
        // and register it in the registry under 'default' key
        TimerServiceRegistry.getInstance().registerTimerService("default", globalTs);
        // prepare listener to assert results
        final List<Long> timerExporations = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener(){

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    timerExporations.add(event.getProcessInstance().getId());
                }
            }
            
        };


        Thread.sleep(5000);
        
    }

}
