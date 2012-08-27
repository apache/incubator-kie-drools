package org.drools.integrationtests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.FileManager;
import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for BZ-813547 / JBRULES-3467 / BZ-814415
 */
public class FusionIncrementalKbaseTest {

	FileManager fileManager;
	KnowledgeBase kbase;

	@Before
	@SuppressWarnings("restriction")
	public void setUp() throws Exception {
		fileManager = new FileManager();
		fileManager.setUp();

		// creating initial rule
		fileManager.write("rule1.drl", createRule("initialRHSPrintMessage"));

		prepareKagent(createChangeSet());

		// scanning changes each 2 seconds
		ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
	}

	@Test
	@SuppressWarnings("restriction")
	public void testIncrementalKbaseChangesWithTemporalRules() throws Exception {
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");

		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);

		//inserting some events
		
		Event firtEventA = new Event();
		Event firtEventB = new Event();
		Event firtEventC = new Event();
		
		ksession.insert(firtEventA);
		ksession.insert(firtEventB);
		ksession.insert(firtEventC);
		
		ksession.fireAllRules();

		// now changing the rule resource
		fileManager.write("rule1.drl", createRule("changedRHSPrintMessage"));

		// sleeping 3 seconds to run ResourceChangeScanner automatically
		Thread.sleep(3000);

		// the first event should be expired (2 seconds defined by the rule) but
		// I still should be able to add others
		Event secondEvent = new Event();
		ksession.insert(secondEvent);

		// NPE
		ksession.fireAllRules();

		assertTrue(list.contains("initialRHSPrintMessage"));
		assertTrue(list.contains("changedRHSPrintMessage"));

		logger.close();

	}

	@SuppressWarnings("restriction")
	public File createChangeSet() throws Exception {
		String xml = "";
		xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
		xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
		xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
		xml += "    <add> ";
		xml += "        <resource source='file:"+ fileManager.getRootDirectory().getPath()+ "/rule1.drl' type='DRL' />";
		xml += "    </add> ";
		xml += "</change-set>";

		return fileManager.write("changeset.xml", xml);
	}

	public void prepareKagent(File changeset) throws Exception {
		KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory
				.newKnowledgeBaseConfiguration();
		kbconf.setOption(EventProcessingOption.STREAM);
		kbase = KnowledgeBaseFactory.newKnowledgeBase(kbconf);

		// Defining incremental resources to kbase
		KnowledgeAgentConfiguration kaconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
		kaconf.setProperty("drools.agent.newInstance", "false");

		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("theAgent", kbase, kaconf);
		System.out.println(changeset.toURI().toURL());
		kagent.applyChangeSet(ResourceFactory.newUrlResource(changeset.toURI().toURL()));
		kagent.addEventListener( new KnowledgeAgentEventListener() {
            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
                System.out.println(event);
            }
            
            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
                System.out.println(event);
            }
            
            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
                System.out.println(event);
            }
            
            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
                System.out.println(event);
            }
            
            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
                System.out.println(event);
            }
            
            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
                System.out.println(event);
            }
            
            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
                System.out.println(event);
            }
            
            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                System.out.println(event);
            }
        } );
		kbase = kagent.getKnowledgeBase();
	}

	public String createRule(String rhsMessage) {
		StringBuilder sb = new StringBuilder();

		sb.append("\n\npackage com.sample\n");
		sb.append("import org.drools.integrationtests.FusionIncrementalKbaseTest.Event\n\n");
		sb.append("global java.util.ArrayList<String> list\n\n");

		sb.append("declare Event\n");
		sb.append("   @role( event )\n");
		sb.append(" end\n\n");

		sb.append("rule x");
		sb.append("\n");
		sb.append("when\n");
		sb.append("Event() over window:time (2s)\n");
		sb.append("then\n");
		sb.append("   list.add(\"");
		sb.append(rhsMessage);
		sb.append("\");\n");
		sb.append("end\n\n");
		System.out.println(sb.toString());

		return sb.toString();
	}

	public static class Event {

	}
}
