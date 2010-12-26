package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Message;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

/**
 * This is a sample class to launch a rule.
 */
public class HelloWorldTest {

    @Test
    public void testHelloWorld() throws Exception {
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
		// go !
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);
		ksession.insert(message);
		ksession.fireAllRules();
		logger.close();
	}

    @Test
    public void testHelloWorldDebug() throws Exception {
		final List<String> knownVariables = new ArrayList<String>();
		MVELRuntime.resetDebugger();
		MVELDebugHandler.setDebugMode(true);
		MVELRuntime.setThreadDebugger(new Debugger() {
            public int onBreak(Frame frame) {
                System.out.println("onBreak");
                for (String var: frame.getFactory().getKnownVariables()) {
                	System.out.println("  " + var);
                    knownVariables.add(var);
                }
                return 0;
            }
        });
        String source = "org.drools.integrationtests.Rule_Hello_World_0";
		MVELRuntime.registerBreakpoint(source, 1);
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
		// go !
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);
		ksession.insert(message);
		ksession.fireAllRules();
		logger.close();
		assertEquals( 2, knownVariables.size() );
		assertTrue(knownVariables.contains("m"));
		assertTrue(knownVariables.contains("myMessage"));
	}

	private KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(
			ResourceFactory.newClassPathResource("Sample.drl", HelloWorldTest.class),
			ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

}
