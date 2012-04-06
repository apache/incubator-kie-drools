package org.drools.agent;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.definition.process.Process;
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
import org.drools.rule.Package;
import org.junit.After;
import org.junit.Test;

public class KnowledgeAgentTest {

	private static final String CHANGE_SET = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
		"<change-set xmlns=\"http://drools.org/drools-5.0/change-set\" " +
            "xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xs:schemaLocation=\"http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd\" >" +
            "<add> " +
            	"<resource source=\"{0}\" type=\"PKG\"/> " +
            "</add>" + 
    "</change-set>";
	
	@Test
	public void testRemoveRuleFlow() throws Exception {
		File tempDir = RuleBaseAssemblerTest.getTempDirectory();
		String location = tempDir.getAbsolutePath() +File.separator + "p1.pkg";
		Package p1 = new Package("dummy");
		Process process1 = new DummyProcess("1","name");
		p1.addProcess(process1);
		 
		Process process2 = new DummyProcess("2","name2");
		p1.addProcess(process2);
		 
		RuleBaseAssemblerTest.writePackage(p1, new File(location));
		final List<String> updated = new ArrayList<String>();
		String changeset = CHANGE_SET.replaceFirst("\\{0\\}", "file:"+location);
		ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
	    sconf.setProperty( "drools.resource.scanner.interval", "1" );
	    ResourceFactory.getResourceChangeScannerService().configure( sconf );
		ResourceFactory.getResourceChangeNotifierService().start();

		ResourceFactory.getResourceChangeScannerService().start();
			
		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.newInstance", "false");
		
		KnowledgeAgent agent = KnowledgeAgentFactory.newKnowledgeAgent("test", aconf);
		agent.addEventListener(new KnowledgeAgentEventListener() {
			
			public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
			}
			
			public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
				System.out.println("Knowledge Base updated");
				updated.add(event.toString());
			}
			
			public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
			}
			
			public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
			}
			
			public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
			}
			
			public void afterResourceProcessed(AfterResourceProcessedEvent event) {
			}
			
			public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
			}
			
			public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
			}
		});
		
		SystemEventListenerFactory.setSystemEventListener(new PrintStreamSystemEventListener());
		agent.applyChangeSet(ResourceFactory.newReaderResource(new StringReader(changeset)));
		KnowledgeBase kbase = agent.getKnowledgeBase();
		
		assertEquals(2, kbase.getProcesses().size());
		assertEquals(2, kbase.getKnowledgePackage("dummy").getProcesses().size());
		
		p1.getRuleFlows().remove("1");
		assertEquals(1, p1.getRuleFlows().size());
		RuleBaseAssemblerTest.writePackage(p1, new File(location));
		
		// wait until two knowledge base updated events (one for creation and second after change is pkg was discovered by scanner
		// note that thread sleep time cannot be 1 sec as it will clash with scanner intervals
		while(updated.size() < 2) {
			System.out.println("Waiting for knowledge base to be updated");
			Thread.sleep(2000);
		}
		
		kbase = agent.getKnowledgeBase();
		assertEquals(1, kbase.getProcesses().size());
		assertEquals(1, kbase.getKnowledgePackage("dummy").getProcesses().size());
	}
	
	@After
	public void clearup() {
		RuleBaseAssemblerTest.clearTempDirectory();
	}
	
}
