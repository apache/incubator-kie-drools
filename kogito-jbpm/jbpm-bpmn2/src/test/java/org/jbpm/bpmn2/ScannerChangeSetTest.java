package org.jbpm.bpmn2;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.kie.KnowledgeBase;
import org.kie.SystemEventListenerFactory;
import org.kie.agent.KnowledgeAgent;
import org.kie.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.core.util.FileManager;
import org.kie.io.ResourceChangeScannerConfiguration;
import org.kie.io.ResourceFactory;
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

	/**
	 * 
	 * Test to reproduce failure of scanning .bpmn files with ResourceChangeScanner.
	 * 
	 * Similar bugs (the same problem with PKG and XLS):
	 * https://bugzilla.redhat.com/show_bug.cgi?id=733008
	 * https://bugzilla.redhat.com/show_bug.cgi?id=741219
	 * 
	 * @version BRMS 5.3.0 DEV4
	 * @author jsvitak@redhat.com
	 *
	 */
	@Test
    public void testBPMNByResourceChangeScanner() throws Exception {
        SystemEventListenerFactory.setSystemEventListener(new PrintStreamSystemEventListener(System.out));        
        
        // first file
        File ruleFile = new File(TMP_DIR + "temporary.bpmn");
        copy(getClass().getResourceAsStream("/BPMN2-ScannerChangeSet.bpmn"), new FileOutputStream(ruleFile));        

        // changeset
        String BPMN_CHANGESET = 
            "<change-set xmlns=\"http://drools.org/drools-5.0/change-set\"\n" +
            "            xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "            xs:schemaLocation=\"http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd\">\n" +
            "  <add>\n" +
            "    <resource source=\"file:" + ruleFile.getAbsolutePath() + "\" type=\"BPMN2\" />\n" +
            "  </add>\n" +
            "</change-set>\n";
        File bpmnChangeset = new File(TMP_DIR + "bpmnChangeset.xml");
        bpmnChangeset.deleteOnExit();
        writeToFile(bpmnChangeset, BPMN_CHANGESET);
        
        // scan every second
        ResourceChangeScannerConfiguration config = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        config.setProperty("drools.resource.scanner.interval", "1");
        ResourceFactory.getResourceChangeScannerService().configure(config);
        
        // create knowledge agent
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("bpmn agent");
        kagent.applyChangeSet(ResourceFactory.newFileResource(bpmnChangeset));
        KnowledgeBase kbase = kagent.getKnowledgeBase();
        
        // start scanning service
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();

        assertEquals(kbase.getKnowledgePackages().size(), 1);
        assertEquals(kbase.getKnowledgePackages().iterator().next().getRules().size(), 6);
        
        // sleeping and modifying content
        Thread.sleep(1500);
        ruleFile.delete();
        ruleFile = new File(TMP_DIR + "temporary.bpmn");
        copy(getClass().getResourceAsStream("/BPMN2-ScannerChangeSet2.bpmn"), new FileOutputStream(ruleFile));        

        // now the knowledge agent should have updated knowledge base
        Thread.sleep(1000);
        assertEquals(kbase.getKnowledgePackages().size(), 1);
        assertEquals(kbase.getKnowledgePackages().iterator().next().getRules().size(), 6);
        // but we have to ask for the new one, it should be 3 rules
        kbase = kagent.getKnowledgeBase();
        assertEquals(kbase.getKnowledgePackages().size(), 1);
        assertEquals(kbase.getKnowledgePackages().iterator().next().getRules().size(), 3);
        
        // stop scanning service
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        ruleFile.delete();
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
