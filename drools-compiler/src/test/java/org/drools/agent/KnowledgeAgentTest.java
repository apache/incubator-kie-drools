package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.util.FileManager;

public class KnowledgeAgentTest extends TestCase {
    FileManager fileManager;
    
    protected void setUp() throws Exception {        
        fileManager = new FileManager();
        fileManager.setUp();
        ((ResourceChangeScannerImpl)ResourceFactory.getResourceChangeScannerService()).reset();
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();
    }
    

    protected void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        ((ResourceChangeScannerImpl)ResourceFactory.getResourceChangeScannerService()).reset();
    }
    
    public void testModifyFile() throws IOException,
                                InterruptedException {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        File f1 = fileManager.newFile( "rule1.drl" );
        f1.deleteOnExit();
        Writer output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( rule1 );
        output.close();

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";
        File f2 = fileManager.newFile(  "rule2.drl" );
        f2.deleteOnExit();
        output = new BufferedWriter( new FileWriter( f2 ) );
        output.write( rule2 );
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-4.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='" + f1.toURI().toURL() + "' type='DRL' />";
        xml += "        <resource source='" + f2.toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile( "changeset.xml" );
        fxml.deleteOnExit();
        output = new BufferedWriter( new FileWriter( fxml ) );
        output.write( xml );
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty( "drools.resource.scanner.interval",
                           "2" );
        ResourceFactory.getResourceChangeScannerService().configure( sconf );

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanDirectories",
                           "true" );
        aconf.setProperty( "drools.agent.scanResources",
                            "true" );        
        aconf.setProperty( "drools.agent.newInstance",
                           "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent",
                                                                         kbase,
                                                                         aconf );
        
        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();
        
        // have to sleep here as linux lastModified does not do milliseconds http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep( 2000 );
        
        rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule3\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( rule1 );
        output.close();
        Thread.sleep( 3000 );

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );
        kagent.monitorResourceChangeEvents( false );
    }

    public void testModifyDirectory() throws IOException,
                                     InterruptedException {        
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        File f1 =  fileManager.newFile( "rule1.drl" );

        Writer output = new BufferedWriter( new FileWriter( f1 ) );
        output.write( rule1 );
        output.close();

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";
        File f2 = fileManager.newFile( "rule2.drl" );
        output = new BufferedWriter( new FileWriter( f2 ) );
        output.write( rule2 );
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";        
        xml += "        <resource source='" + f1.getParentFile().toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";        
        xml += "</change-set>";
        File newDir = fileManager.newFile( "changeset" );
        newDir.mkdir();
        File fxml = fileManager.newFile( newDir, "changeset.xml" );
        output = new BufferedWriter( new FileWriter( fxml ) );
        output.write( xml );
        output.close();

//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        kbuilder.add( ResourceFactory.newUrlResource( fxml.toURI().toURL() ),
//                      ResourceType.ChangeSet );
//        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        //kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty( "drools.resource.scanner.interval",
                           "2" );
        ResourceFactory.getResourceChangeScannerService().configure( sconf );

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanDirectories",
                           "true" );
        aconf.setProperty( "drools.agent.newInstance",
                           "true" );
        
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent",
                                                                         kbase,
                                                                         aconf );
        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        Thread.sleep( 3000 ); // give it 2 seconds to detect and build the changes
        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();
        String rule3 = "";
        rule3 += "package org.drools.test\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule rule3\n";
        rule3 += "when\n";
        rule3 += "then\n";
        rule3 += "list.add( drools.getRule().getName() );\n";
        rule3 += "end\n";
        File f3 = fileManager.newFile( "rule3.drl" );
        output = new BufferedWriter( new FileWriter( f3 ) );
        output.write( rule3 );
        output.close();
        
        assertTrue( f1.delete() );
        
        
        Thread.sleep( 3000 );

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );
        
        kagent.monitorResourceChangeEvents( false );
    }

}
