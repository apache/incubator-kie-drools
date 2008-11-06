package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.runtime.process.ProcessInstance;

public class ProcessMultiThreadTest extends TestCase {
	
	public void testMultiThreadProcessInstanceSignalling() {
        final int THREAD_COUNT = 2;
        try {
            boolean success = true;
            final Thread[] t = new Thread[THREAD_COUNT];
            
            final PackageBuilder builder = new PackageBuilder();
            builder.addProcessFromXml(new InputStreamReader( getClass().getResourceAsStream( "test_ProcessMultithreadEvent.rf" ) ) );
            if (builder.getErrors().getErrors().length > 0) {
            	for (DroolsError error: builder.getErrors().getErrors()) {
            		System.err.println(error);
            	}
            	fail("Could not parse process");
            }
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage( builder.getPackage() );
            ruleBase = SerializationHelper.serializeObject(ruleBase);
            StatefulSession session = ruleBase.newStatefulSession();
            session = SerializationHelper.getSerialisedStatefulSession(session);
            List<String> list = new ArrayList<String>();
            session.setGlobal("list", list);
            ProcessInstance processInstance = session.startProcess("org.drools.integrationtests.multithread");
            final ProcessInstanceSignalRunner[] r = new ProcessInstanceSignalRunner[THREAD_COUNT];
            for ( int i = 0; i < t.length; i++ ) {
                r[i] = new ProcessInstanceSignalRunner(i, processInstance, "event" + (i+1));
                t[i] = new Thread( r[i], "thread-" + i );
                t[i].start();
            }
            for ( int i = 0; i < t.length; i++ ) {
                t[i].join();
                if ( r[i].getStatus() == ProcessInstanceSignalRunner.Status.FAIL ) {
                    success = false;
                }
            }
            if ( !success ) {
                fail( "Multithread test failed. Look at the stack traces for details. " );
            }
            assertEquals(2, list.size());
            assertFalse(list.get(0).equals(list.get(1)));
            assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Should not raise any exception: " + e.getMessage() );
        }
	}
	
    public static class ProcessInstanceSignalRunner implements Runnable {

	    private ProcessInstance processInstance;
	    private String type;
        private Status status;
        private int id;
	
	    public ProcessInstanceSignalRunner(int id, ProcessInstance processInstance, String type) {
	        this.id = id;
	    	this.processInstance = processInstance;
	    	this.type = type;
	        this.status = Status.SUCCESS;
	    }
	
	    public void run() {
	        try {
	        	processInstance.signalEvent(type, null);
	        } catch ( Exception e ) {
	            this.status = Status.FAIL;
	            System.out.println( Thread.currentThread().getName() + " failed: " + e.getMessage() );
	            e.printStackTrace();
	        }
	    }
	
	    public static enum Status {
	        SUCCESS, FAIL
	    }
	
	    public int getId() {
	        return id;
	    }
	
	    public Status getStatus() {
	        return status;
	    }
	
	}
    
}
