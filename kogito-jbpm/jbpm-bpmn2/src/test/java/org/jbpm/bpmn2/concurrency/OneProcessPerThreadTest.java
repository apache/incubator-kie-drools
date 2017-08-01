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

package org.jbpm.bpmn2.concurrency;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jbpm.bpmn2.objects.Status;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test takes time and resources, please only run it locally
 */
@Ignore
public class OneProcessPerThreadTest extends AbstractBaseTest {
    
    private static final int THREAD_COUNT = 1000;
    private static volatile AtomicInteger started = new AtomicInteger(0);
    private static volatile AtomicInteger done = new AtomicInteger(0);
    
    private static final Logger logger = LoggerFactory.getLogger(OneProcessPerThreadTest.class);
    
    protected KieSession createStatefulKnowledgeSession(KieBase kbase) { 
        return kbase.newKieSession();
    }
    
    @Test
    public void testMultiThreadProcessInstanceWorkItem() throws Exception {
    	final ConcurrentHashMap<Long, Long> workItems = new ConcurrentHashMap<Long, Long>();
    	
        try {
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultiThreadServiceProcess.bpmn"), ResourceType.BPMN2 );
            KieBase kbase = kbuilder.newKieBase();
            
            KieSession ksession = createStatefulKnowledgeSession(kbase);
            
            ksession.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					Long threadId = (Long) workItem.getParameter("id");
					workItems.put(workItem.getProcessInstanceId(), threadId);
				}
				public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
				}
            });

            startThreads(ksession);
            
            assertEquals(THREAD_COUNT, workItems.size());
        } catch ( Throwable t ) {
            t.printStackTrace();
            fail( "Should not raise any exception: " + t.getMessage() );
        }
        
        int i = 0;
        while(started.get() > done.get() ) { 
            logger.info("{} > {}", started, done );
            Thread.sleep(10*1000);
            if( ++i > 10 ) { 
                fail("Not all threads completed.");
            }
        }
	}
	
    private static void startThreads(KieSession ksession) throws Throwable { 
        boolean success = true;
        final Thread[] t = new Thread[THREAD_COUNT];
        
        final ProcessInstanceStartRunner[] r = new ProcessInstanceStartRunner[THREAD_COUNT];
        for ( int i = 0; i < t.length; i++ ) {
            r[i] = new ProcessInstanceStartRunner(ksession, i, "org.drools.integrationtests.multithread");
            t[i] = new Thread( r[i], "thread-" + i );
            try { 
                t[i].start();
            } catch( Throwable fault ) {
               fail( "Unable to complete test: " + fault.getMessage() ); 
            }
        }
        for ( int i = 0; i < t.length; i++ ) {
            t[i].join();
            if ( r[i].getStatus() == Status.FAIL ) {
                success = false;
            }
        }
        if ( !success ) {
            fail( "Multithread test failed. Look at the stack traces for details. " );
        }
    }
    
    public static class ProcessInstanceStartRunner implements Runnable {

    	private KieSession ksession;
	    private String processId;
        private long id;
        private Status status;
	
	    public ProcessInstanceStartRunner(KieSession ksession, int id, String processId) {
	    	this.ksession = ksession;
	        this.id = id;
	        this.processId = processId;
	    }
	
	    public void run() {
	        started.incrementAndGet();
	        try {
	        	Map<String, Object> params = new HashMap<String, Object>();
	        	params.put("id", id);
	        	ksession.startProcess(processId, params);
	        } catch ( Throwable t ) {
	            this.status = Status.FAIL;
	            logger.error("{} failed: {}",  Thread.currentThread().getName(), t.getMessage() );
	            t.printStackTrace();
	        }
	        done.incrementAndGet();
	    }
	
	    public long getId() {
	        return id;
	    }
	
	    public Status getStatus() {
	        return status;
	    }
	}

}
