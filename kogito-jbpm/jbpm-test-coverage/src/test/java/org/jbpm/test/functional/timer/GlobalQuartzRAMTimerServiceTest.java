/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.timer;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Persistence;

import org.jbpm.process.core.timer.impl.QuartzSchedulerService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;

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
        tearDownOnce();
        setUpOnce();
        cleanupSingletonSessionId();
        emf = Persistence.createEntityManagerFactory("org.jbpm.test.persistence");
        System.setProperty("org.quartz.properties", "quartz-ram.properties");
        globalScheduler = new QuartzSchedulerService();
        ((QuartzSchedulerService)globalScheduler).forceShutdown();
    }
    
    @After
    public void tearDown(){
        try {
            globalScheduler.shutdown();
        } catch (Exception e) {
            
        }   
        cleanup();
        tearDownOnce();
    }

    @Override
    protected RuntimeManager getManager(RuntimeEnvironment environment, boolean waitOnStart) {
    	RuntimeManager manager = null;
    	if (managerType ==1) {
    		manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        } else if (managerType == 2) {
        	manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
        } else if (managerType == 3) {
        	manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        } else {
            throw new IllegalArgumentException("Invalid runtime maanger type");
        }
    	if (waitOnStart) {
	        // wait for the 2 seconds (default startup delay for quartz)
	    	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// do nothing
			}
    	}
    	return manager;
    }

}
