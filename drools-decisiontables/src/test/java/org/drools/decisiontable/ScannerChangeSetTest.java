package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.core.util.FileManager;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ScannerChangeSetTest {

	FileManager fileManager;

	@Before
	public void setUp() throws Exception {
		fileManager = new FileManager();
		fileManager.setUp();
	}

	@After
	public void tearDown() throws Exception {
		fileManager.tearDown();
	}

	@Test @Ignore
	public void testCSVByResourceChangeScanner() throws InterruptedException,
			IOException {

		// load contents of resource decision tables
		String first = fileManager
				.readInputStreamReaderAsString(new InputStreamReader(getClass()
						.getResourceAsStream("changeSetTestCSV.csv")));
		String second = fileManager
				.readInputStreamReaderAsString(new InputStreamReader(getClass()
						.getResourceAsStream("changeSetTestCSV2.csv")));
		System.out.println(first);
		System.out.println(second);
		
		// write first version of the decision table rules
		File file = new File(
				"target/classes/scannerChangeSetTestCSV.csv");
		file.delete();
		fileManager.write(file, first);
		Thread.sleep(1100);
		
		// start scanning service with interval 1s
		ResourceChangeScannerConfiguration config = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		config.setProperty("drools.resource.scanner.interval", "1");
		ResourceFactory.getResourceChangeScannerService().configure(config);
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();

		// load knowledge base via knowledge agent
		KnowledgeAgent kagent = KnowledgeAgentFactory
				.newKnowledgeAgent("csv agent");
		kagent.applyChangeSet(ResourceFactory.newClassPathResource(
				"scannerChangeSetTestCSV.xml", getClass()));
		KnowledgeBase kbase = kagent.getKnowledgeBase();

		assertEquals(1, kbase.getKnowledgePackages().size());
		assertEquals(3, kbase.getKnowledgePackages().iterator().next()
				.getRules().size());

		// after some waiting we change number of rules in decision table,
		// scanner should notice the change
		Thread.sleep(1100);
		file.delete();
		fileManager.write(file, second);
		Thread.sleep(1100);

		try {
			kbase = kagent.getKnowledgeBase();
			// fails here - see surefire report, knowledge agent fails to load the change
			assertEquals(1, kbase.getKnowledgePackages().size());
			assertEquals(2, kbase.getKnowledgePackages().iterator().next()
					.getRules().size());
		} finally {
			ResourceFactory.getResourceChangeNotifierService().stop();
			ResourceFactory.getResourceChangeScannerService().stop();
			file.delete();
		}
	}
}
