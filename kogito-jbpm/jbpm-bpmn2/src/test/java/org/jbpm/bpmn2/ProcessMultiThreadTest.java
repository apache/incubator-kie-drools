package org.jbpm.bpmn2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ProcessMultiThreadTest extends TestCase {

    public void testMultiThreadProcessInstanceWorkItem() {
    	final ConcurrentHashMap<Long, Long> workItems = new ConcurrentHashMap<Long, Long>();
        final int THREAD_COUNT = 10000;
        try {
            boolean success = true;
            final Thread[] t = new Thread[THREAD_COUNT];
            
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource( "BPMN2-MultiThreadServiceProcess.bpmn" ), ResourceType.BPMN2 );
            KnowledgeBase kbase = kbuilder.newKnowledgeBase();
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            ksession.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					Long threadId = (Long) workItem.getParameter("id");
//					System.out.println("Executing work item " + workItem.getId() + " for thread " + threadId);
					workItems.put(workItem.getId(), threadId);
				}
				public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
				}
            });
            final ProcessInstanceStartRunner[] r = new ProcessInstanceStartRunner[THREAD_COUNT];
            for ( int i = 0; i < t.length; i++ ) {
                r[i] = new ProcessInstanceStartRunner(ksession, i, "org.drools.integrationtests.multithread");
                t[i] = new Thread( r[i], "thread-" + i );
                t[i].start();
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
            assertEquals(THREAD_COUNT, workItems.size());
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception: " + e.getMessage() );
        }
	}
	
    public void testMultiThreadProcessInstance() {
    	final ConcurrentHashMap<Long, Long> workItems = new ConcurrentHashMap<Long, Long>();
        final int THREAD_COUNT = 10000;
        try {
            boolean success = true;
            final Thread[] t = new Thread[THREAD_COUNT];
            
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource( "BPMN2-MultiThreadServiceProcess.bpmn" ), ResourceType.BPMN2 );
            KnowledgeBase kbase = kbuilder.newKnowledgeBase();
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            ksession.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					Long threadId = (Long) workItem.getParameter("id");
//					System.out.println("Executing process instance " + workItem.getProcessInstanceId() + " for thread " + threadId);
					workItems.put(workItem.getProcessInstanceId(), threadId);
				}
				public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
				}
            });
            final ProcessInstanceStartRunner[] r = new ProcessInstanceStartRunner[THREAD_COUNT];
            for ( int i = 0; i < t.length; i++ ) {
                r[i] = new ProcessInstanceStartRunner(ksession, i, "org.drools.integrationtests.multithread");
                t[i] = new Thread( r[i], "thread-" + i );
                t[i].start();
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
            assertEquals(THREAD_COUNT, workItems.size());
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception: " + e.getMessage() );
        }
	}
	
    public static class ProcessInstanceStartRunner implements Runnable {

    	private StatefulKnowledgeSession ksession;
	    private String processId;
        private long id;
        private Status status;
	
	    public ProcessInstanceStartRunner(StatefulKnowledgeSession ksession, int id, String processId) {
	    	this.ksession = ksession;
	        this.id = id;
	        this.processId = processId;
	    }
	
	    public void run() {
	        try {
	        	Map<String, Object> params = new HashMap<String, Object>();
	        	params.put("id", id);
	        	ksession.startProcess(processId, params);
	        } catch ( Exception e ) {
	            this.status = Status.FAIL;
	            System.out.println( Thread.currentThread().getName() + " failed: " + e.getMessage() );
	            e.printStackTrace();
	        }
	    }
	
	    public long getId() {
	        return id;
	    }
	
	    public Status getStatus() {
	        return status;
	    }
	
	}
    
    public static enum Status {
        SUCCESS, FAIL
    }

}
