/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.executor;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jbpm.executor.events.listeners.DefaultExecutorEventListener;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author salaboy
 */
public class NoCDIWithFactorySimpleExecutorTest extends BasicExecutorBaseTest{
    
    public NoCDIWithFactorySimpleExecutorTest() {
    }
    
    
    @Before
    public void setUp() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        
        Logger logger = LogManager.getLogManager().getLogger("");
        
        ExecutorServiceFactory.setEmf(emf);
        executorService = ExecutorServiceFactory.newExecutorService();
        
        DefaultExecutorEventListener eventListener = new DefaultExecutorEventListener();
        eventListener.setLogger(logger);
        
        executorService.registerExecutorEventListener(eventListener);

        super.setUp();
    }
    
    @After
    public void tearDown() {
        super.tearDown();
    }
   
    
}