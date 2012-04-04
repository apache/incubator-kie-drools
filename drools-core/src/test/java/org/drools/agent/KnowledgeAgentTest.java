package org.drools.agent;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.definition.process.Process;
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
		
		String changeset = CHANGE_SET.replaceFirst("\\{0\\}", "file:"+location);
		ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
	    sconf.setProperty( "drools.resource.scanner.interval", "1" );
	    ResourceFactory.getResourceChangeScannerService().configure( sconf );
		ResourceFactory.getResourceChangeNotifierService().start();

		ResourceFactory.getResourceChangeScannerService().start();
			
		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.newInstance", "false");
		
		KnowledgeAgent agent = KnowledgeAgentFactory.newKnowledgeAgent("test", aconf);
		
		SystemEventListenerFactory.setSystemEventListener(new PrintStreamSystemEventListener());
		agent.applyChangeSet(ResourceFactory.newReaderResource(new StringReader(changeset)));
		KnowledgeBase kbase = agent.getKnowledgeBase();
		
		assertEquals(2, kbase.getProcesses().size());
		assertEquals(2, kbase.getKnowledgePackage("dummy").getProcesses().size());
		
		p1.getRuleFlows().remove("1");
		assertEquals(1, p1.getRuleFlows().size());
		RuleBaseAssemblerTest.writePackage(p1, new File(location));
		Thread.sleep(1000);
		
		kbase = agent.getKnowledgeBase();
		assertEquals(1, kbase.getProcesses().size());
		assertEquals(1, kbase.getKnowledgePackage("dummy").getProcesses().size());
	}
	
	@After
	public void clearup() {
		RuleBaseAssemblerTest.clearTempDirectory();
	}
	
}
