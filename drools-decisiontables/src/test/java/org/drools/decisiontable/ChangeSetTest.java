package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.agent.KnowledgeAgent;
import org.kie.internal.agent.KnowledgeAgentFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.io.ResourceChangeScannerConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;

public class ChangeSetTest {
    
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

    @Test @Ignore
    public void testCSVByKnowledgeAgent() {
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("csv agent");
        kagent.applyChangeSet(ResourceFactory.newClassPathResource("changeSetTestCSV.xml", getClass()));
        KnowledgeBase kbase = kagent.getKnowledgeBase();
        
        assertEquals(1, kbase.getKnowledgePackages().size());
        assertEquals(3, kbase.getKnowledgePackages().iterator().next().getRules().size());
    }

    @Test @Ignore
    public void testCSVByKnowledgeAgentWithFileReader() throws IOException {

        try {
            File targetTestFilesDir = new File("target/testFiles");
            targetTestFilesDir.mkdirs();
            File changeSetFile = new File(targetTestFilesDir, "changeSetTestCSV.xml");
            FileUtils.copyURLToFile(getClass().getResource("changeSetTestCSV.xml"), changeSetFile);

            KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("csv agent");
            kagent.applyChangeSet(ResourceFactory.newFileResource(changeSetFile));
            KnowledgeBase kbase = kagent.getKnowledgeBase();

            assertEquals(1, kbase.getKnowledgePackages().size());
            assertEquals(3, kbase.getKnowledgePackages().iterator().next().getRules().size());

        } catch(Throwable t) {
            t.printStackTrace();
            fail( t.getMessage() );
        } finally {
        }
    }

}
