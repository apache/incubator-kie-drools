package org.drools.decisiontable;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.FailureDetectingSystemEventListener;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.FileManager;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.KnowledgeResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.xml.XmlChangeSetReader;
import org.xml.sax.SAXException;

public class ChangeSetTest {
    
    FileManager fileManager;
    
    @Before
    public void setUp() throws Exception {
        fileManager = new FileManager();
        fileManager.setUp();
        ResourceChangeScannerConfiguration config = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        config.setProperty("drools.resource.scanner.interval", "1");
        ResourceFactory.getResourceChangeScannerService().configure(config);
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();
    }
    

    @After
    public void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
    }
    
    @Test
    public void testIntegration() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "changeset1Test.xml", getClass()), ResourceType.CHANGE_SET );
        assertFalse( kbuilder.hasErrors() );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        ksession.insert( new Cheese( "cheddar",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    25 ) );
        
        ksession.fireAllRules();
        ksession.dispose();
        
        assertEquals( 3, list.size() );
  
        assertEquals( "Young man cheddar",
                      list.get( 0 ) );
        
        assertEquals( "rule1",
                      list.get( 1 ) );
        assertEquals( "rule2",
                      list.get( 2 ) );
    }

    @Test
    public void multipleSheets() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "multipleSheetsChangeSet.xml", getClass()), ResourceType.CHANGE_SET );
        assertFalse( kbuilder.hasErrors() );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Cheese( "cheddar",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    25 ) );
        ksession.insert( new Person( "Jane",
                                    "stilton",
                                    55 ) );

        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2, list.size() );

        assertTrue(list.contains("Young man cheddar"));
        assertTrue(list.contains("Jane eats cheddar"));
    }

    @Test
    public void testCSV() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "changeSetTestCSV.xml", getClass()), ResourceType.CHANGE_SET );
        assertFalse( kbuilder.hasErrors() );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        assertEquals(1, kbase.getKnowledgePackages().size());
        assertEquals(3, kbase.getKnowledgePackages().iterator().next().getRules().size());
    }

    @Test
    public void testCSVByKnowledgeAgent() {
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("csv agent");
        kagent.applyChangeSet(ResourceFactory.newClassPathResource("changeSetTestCSV.xml", getClass()));
        KnowledgeBase kbase = kagent.getKnowledgeBase();
        
        assertEquals(1, kbase.getKnowledgePackages().size());
        assertEquals(3, kbase.getKnowledgePackages().iterator().next().getRules().size());
    }

    @Test
    public void testCSVByKnowledgeAgentWithFileReader() throws IOException {
        FailureDetectingSystemEventListener systemEventListener = new FailureDetectingSystemEventListener();
        SystemEventListenerFactory.setSystemEventListener(systemEventListener);

        try {
            File targetTestFilesDir = new File("target/testFiles");
            targetTestFilesDir.mkdirs();
            File changeSetFile = new File(targetTestFilesDir, "changeSetTestCSV.xml");
            FileUtils.copyURLToFile(getClass().getResource("changeSetTestCSV.xml"), changeSetFile);

            KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("csv agent");
            kagent.setSystemEventListener(systemEventListener);
            kagent.applyChangeSet(ResourceFactory.newFileResource(changeSetFile));
            KnowledgeBase kbase = kagent.getKnowledgeBase();

            assertEquals(1, kbase.getKnowledgePackages().size());
            assertEquals(3, kbase.getKnowledgePackages().iterator().next().getRules().size());

            if (!systemEventListener.isSuccessful()) {
                for (Throwable throwable : systemEventListener.getExceptionList()) {
                    throwable.printStackTrace();
                }
                fail("The scanner ran into exceptions");
            }
        } catch(Throwable t) {
            t.printStackTrace();
            fail( t.getMessage() );
        } finally {
        }
    }

}
