package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulSession;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Message;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.drools.rule.Package;

public class ProcessTimerTest extends TestCase {
	
	@SuppressWarnings("unchecked")
	public void testSimpleProcess() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Message\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <end id=\"2\" name=\"End\" />\n" +
			"    <timerNode id=\"3\" name=\"Timer\" delay=\"800ms\" period=\"200ms\" />\n" +
			"    <actionNode id=\"4\" name=\"Action\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >System.out.println(\"Triggered\");\n" +
			"myList.add( new Message() );\n" +
			"insert( new Message() );\n" +
			"</action>\n" +
			"    </actionNode>\n" + 
			"    <milestone id=\"5\" name=\"Wait\" >\n" +
			"      <constraint type=\"rule\" dialect=\"mvel\" >Number( intValue &gt;= 5 ) from accumulate ( m: Message( ), count( m ) )</constraint>\n" +
			"    </milestone>\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"5\" to=\"2\" />\n" +
			"    <connection from=\"1\" to=\"3\" />\n" +
			"    <connection from=\"3\" to=\"4\" />\n" +
			"    <connection from=\"4\" to=\"5\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		if (!builder.getErrors().isEmpty()) {
			for (DroolsError error: builder.getErrors().getErrors()) {
				System.err.println(error);
			}
			fail("Could not build process");
		}
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		final StatefulSession session = ruleBase.newStatefulSession();
		List<Message> myList = new ArrayList<Message>();
		session.setGlobal("myList", myList);
		
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();
		
        ProcessInstance processInstance = ( ProcessInstance )
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, session.getTimerManager().getTimers().size());
        session.halt();
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        myList = (List<Message>) session2.getGlobal( "myList" );
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
        processInstance = ( ProcessInstance ) session2.getProcessInstance( processInstance.getId() );
        
        assertEquals(1, session2.getTimerManager().getTimers().size());

        // test that the delay works
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(0, myList.size());
        
        // test that the period works
        try {
        	Thread.sleep(1300);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(5, myList.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        session2.halt();
	}
	
	public void testIncorrectTimerNode() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <end id=\"2\" name=\"End\" />\n" +
			"    <timerNode id=\"3\" name=\"Timer\" delay=\"800msdss\" period=\"200mssds\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"3\" />\n" +
			"    <connection from=\"3\" to=\"2\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		assertEquals(2, builder.getErrors().size());
		for (DroolsError error: builder.getErrors().getErrors()) {
			System.err.println(error);
		}
	}

	@SuppressWarnings("unchecked")
	public void testOnEntryTimerExecuted() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Wait\" >\n" +
			"      <timers>\n" +
			"        <timer id=\"1\" delay=\"300\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" +
			"        </timer>\n" +
			"      </timers>\n" +
			"      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" +
			"    </milestone>\n" +
			"    <end id=\"3\" name=\"End\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"2\" />\n" +
			"    <connection from=\"2\" to=\"3\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		final StatefulSession session = ruleBase.newStatefulSession();
		List<String> myList = new ArrayList<String>();
		session.setGlobal("myList", myList);
		
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();
		
        ProcessInstance processInstance = ( ProcessInstance )
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, session.getTimerManager().getTimers().size());
        
        session.halt();
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        myList = (List<String>) session2.getGlobal( "myList" );
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
		
        processInstance = ( ProcessInstance ) session2.getProcessInstance( processInstance.getId() );
        
        assertEquals(1, session2.getTimerManager().getTimers().size());

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(1, myList.size());
        
        session2.halt();
	}

	@SuppressWarnings("unchecked")
	public void testOnEntryTimerWorkItemExecuted() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"2\" name=\"Work\" >\n" +
			"      <timers>\n" +
			"        <timer id=\"1\" delay=\"300\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" +
			"        </timer>\n" +
			"      </timers>\n" +
            "      <work name=\"Human Task\" >\n" +
            "      </work>\n" +
            "    </workItem>\n" +
			"    <end id=\"3\" name=\"End\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"2\" />\n" +
			"    <connection from=\"2\" to=\"3\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		final StatefulSession session = ruleBase.newStatefulSession();
		List<String> myList = new ArrayList<String>();
		session.setGlobal("myList", myList);
		session.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
		
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();
		
        ProcessInstance processInstance = ( ProcessInstance )
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, session.getTimerManager().getTimers().size());
        
        session.halt();
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        myList = (List<String>) session2.getGlobal( "myList" );
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
		
        processInstance = ( ProcessInstance ) session2.getProcessInstance( processInstance.getId() );
        
        assertEquals(1, session2.getTimerManager().getTimers().size());

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(1, myList.size());
        
        session2.halt();
	}

	public void testIncorrectOnEntryTimer() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Wait\" >\n" +
			"      <timers>\n" +
			"        <timer id=\"1\" delay=\"300asdf\" period=\"asfd\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" +
			"        </timer>\n" +
			"      </timers>\n" +
			"      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" +
			"    </milestone>\n" +
			"    <end id=\"3\" name=\"End\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"2\" />\n" +
			"    <connection from=\"2\" to=\"3\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		assertEquals(2, builder.getErrors().size());
		for (DroolsError error: builder.getErrors().getErrors()) {
			System.err.println(error);
		}
	}

	@SuppressWarnings("unchecked")
	public void testOnEntryTimerExecutedMultipleTimes() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Wait\" >\n" +
			"      <timers>\n" +
			"        <timer id=\"1\" delay=\"300\" period =\"200\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" +
			"        </timer>\n" +
			"      </timers>\n" +
			"      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" +
			"    </milestone>\n" +
			"    <end id=\"3\" name=\"End\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"2\" />\n" +
			"    <connection from=\"2\" to=\"3\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		final StatefulSession session = ruleBase.newStatefulSession();
		List<String> myList = new ArrayList<String>();
		session.setGlobal("myList", myList);
		
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();

        ProcessInstance processInstance = ( ProcessInstance )
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, session.getTimerManager().getTimers().size());
        session.halt();
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        myList = (List<String>) session2.getGlobal( "myList" );
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();

        processInstance = ( ProcessInstance ) session2.getProcessInstance( processInstance.getId() );
        
        assertEquals(1, session2.getTimerManager().getTimers().size());

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(2, myList.size());
        
        session2.halt();
	}
	
	@SuppressWarnings("unchecked")
	public void testMultipleTimers() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Wait\" >\n" +
			"      <timers>\n" +
			"        <timer id=\"1\" delay=\"600\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer1\");</action>\n" +
			"        </timer>\n" +
			"        <timer id=\"2\" delay=\"200\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer2\");</action>\n" +
			"        </timer>\n" +
			"      </timers>\n" +
			"      <constraint type=\"rule\" dialect=\"mvel\" >eval(false)</constraint>\n" +
			"    </milestone>\n" +
			"    <end id=\"3\" name=\"End\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"2\" />\n" +
			"    <connection from=\"2\" to=\"3\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		final StatefulSession session = ruleBase.newStatefulSession();
		List<String> myList = new ArrayList<String>();
		session.setGlobal("myList", myList);
		
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();

        ProcessInstance processInstance = ( ProcessInstance )
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, session.getTimerManager().getTimers().size());
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        myList = (List<String>) session2.getGlobal( "myList" );
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
		
        assertEquals(2, session2.getTimerManager().getTimers().size());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(1, myList.size());
        assertEquals("Executing timer2", myList.get(0));
        session2.halt();
        
        final StatefulSession session3 = getSerialisedStatefulSession( session2 );
        myList = (List<String>) session.getGlobal( "myList" );
        
		new Thread(new Runnable() {
			public void run() {
	        	session3.fireUntilHalt();       	
			}
        }).start();
		
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(2, myList.size());
        
        session3.halt();
	}
	
	@SuppressWarnings("unchecked")
	public void testOnEntryTimerCancelled() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Wait\" >\n" +
			"      <timers>\n" +
			"        <timer id=\"1\" delay=\"2000\" >\n" +
			"          <action type=\"expression\" dialect=\"java\" >myList.add(\"Executing timer\");</action>\n" +
			"        </timer>\n" +
			"      </timers>\n" +
			"      <constraint type=\"rule\" dialect=\"mvel\" >org.drools.Message( )</constraint>\n" +
			"    </milestone>\n" +
			"    <end id=\"3\" name=\"End\" />\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"1\" to=\"2\" />\n" +
			"    <connection from=\"2\" to=\"3\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		final StatefulSession session = ruleBase.newStatefulSession();
		List<String> myList = new ArrayList<String>();
		session.setGlobal("myList", myList);
		
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();
		
        ProcessInstance processInstance = ( ProcessInstance )
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, session.getTimerManager().getTimers().size());
        session.halt();
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        myList = (List<String>) session2.getGlobal( "myList" );
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
		
        session2.insert(new Message());
        assertEquals(0, myList.size());
        assertEquals(0, session2.getTimerManager().getTimers().size());
        
        session2.halt();
	}
	
}
