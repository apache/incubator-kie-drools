package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.core.util.FileManager;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScannerChangeSetTest {

    public static final String TMP_DIR = "target/classes/";
    
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

	@Test
	public void testCSVByResourceChangeScanner() throws InterruptedException,
			IOException {

		// load contents of resource decision tables
		String first = fileManager
				.readInputStreamReaderAsString(new InputStreamReader(getClass()
						.getResourceAsStream("changeSetTestCSV.csv")));
		String second = fileManager
				.readInputStreamReaderAsString(new InputStreamReader(getClass()
						.getResourceAsStream("changeSetTestCSV2.csv")));
		
		// write first version of the decision table rules
		File file = new File( TMP_DIR + "scannerChangeSetTestCSV.csv");
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
			kagent.dispose();
		}
	}
	
	/**
	 * 
	 * Test to reproduce bug - failure of ResourceChangeScanner when trying to scan for XLS resource.
	 * 
	 * May be related to similar ResourceChangeScanner bug, where scanner fails on scanning Guvnor's PKG.
	 * https://bugzilla.redhat.com/show_bug.cgi?id=733008
	 * 
	 * Maybe the reason is that both are compiled resources.
	 * 
	 * @version BRMS 5.2.0 ER4
	 * @author jsvitak@redhat.com
	 *
	 */
	@Test
    public void testXLSByResourceChangeScanner() throws Exception {
        
        SystemEventListenerFactory.setSystemEventListener(new PrintStreamSystemEventListener(System.out));        
        
        // first file
        File ruleFile = new File(TMP_DIR + "sample.xls");
        copy(getClass().getResourceAsStream("sample.xls"), new FileOutputStream(ruleFile));        
        
        // changeset
        String XLS_CHANGESET = 
            "<change-set xmlns=\"http://drools.org/drools-5.0/change-set\"\n" +
            "            xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "            xs:schemaLocation=\"http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd\">\n" +
            "  <add>\n" +
            "    <resource source=\"file:" + ruleFile.getAbsolutePath() + "\" type=\"DTABLE\">\n" +
            "      <decisiontable-conf input-type=\"XLS\" worksheet-name=\"Tables\"/>\n" +
            "    </resource>" +    
            "  </add>\n" +
            "</change-set>\n";
        File xlsChangeset = new File(TMP_DIR + "xlsChangeset.xml");
        xlsChangeset.deleteOnExit();
        writeToFile(xlsChangeset, XLS_CHANGESET);
        
        // scan every second
        ResourceChangeScannerConfiguration config = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        config.setProperty("drools.resource.scanner.interval", "1");
        ResourceFactory.getResourceChangeScannerService().configure(config);
        
        // create knowledge agent
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("xls agent");
        kagent.applyChangeSet(ResourceFactory.newFileResource(xlsChangeset));
        KnowledgeBase kbase = kagent.getKnowledgeBase();
        
        // ---------------------------------------------------------------
        // start scanning service - scanner's thread throws exception here
        // ---------------------------------------------------------------
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();
        
        assertEquals(kbase.getKnowledgePackages().size(), 1);
        assertEquals(kbase.getKnowledgePackages().iterator().next().getRules().size(), 3);
        
        // sleeping and modifying content
        Thread.sleep(1500);
        ruleFile.delete();
        ruleFile = new File(TMP_DIR + "sample.xls");
        copy(getClass().getResourceAsStream("sample2.xls"), new FileOutputStream(ruleFile));

        Thread.sleep(1000);
        assertEquals(kbase.getKnowledgePackages().size(), 1);
        assertEquals(kbase.getKnowledgePackages().iterator().next().getRules().size(), 3);
        // there should be just 2 rules now, but scanner didn't notice the change
        kbase = kagent.getKnowledgeBase();
        
        assertEquals(kbase.getKnowledgePackages().size(), 1);
        assertEquals(kbase.getKnowledgePackages().iterator().next().getRules().size(), 2);
        
        // stop scanning service
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        // file could remain and we will see, that it has changed, but scanner didn't register that
        //ruleFile.delete();
        kagent.dispose();   
    }
    
	private static void copy(InputStream in, OutputStream out) throws IOException {
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    private static void writeToFile(File file, String content) throws Exception {
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
            fw.write(content);
        } finally {
            if (fw != null) fw.close();
        }
    }
    
    public static class Person {
        private int id;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
        
    }
    
    public class Message implements Serializable {
        private static final long serialVersionUID = -7176392345381065685L;

        private String message;

        public Message() {
            message = "";
        }

        public Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "org.jboss.qa.drools.domain.Message[message='" + message + "']";
        }
    }
    
}
