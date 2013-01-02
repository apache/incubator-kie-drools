package org.jbpm.timer;

import java.util.Arrays;
import java.util.Collection;

import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

@RunWith(Parameterized.class)
public class GlobalQuartzRAMTimerServiceTest extends GlobalTimerServiceBaseTest {
    
    private int managerType;
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { 1 }, { 2 }, { 3 }  };
        return Arrays.asList(data);
    };
    
    public GlobalQuartzRAMTimerServiceTest(int managerType) {
        this.managerType = managerType;
    }
    
    @Before
    public void setUp() {
        cleanupSingletonSessionId();
        System.setProperty("org.quartz.properties", "quartz-ram.properties");
        globalScheduler = new QuartzSchedulerService();
    }
    
    @After
    public void tearDown(){
        try {
            globalScheduler.shutdown();
        } catch (Exception e) {
            
        }        
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

}
