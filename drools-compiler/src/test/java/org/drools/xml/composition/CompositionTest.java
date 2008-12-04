package org.drools.xml.composition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.KnowledgeAgentConfigurationImpl;
import org.drools.agent.impl.KnowledgeAgentImpl;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.FileSystemResource;
import org.drools.io.impl.KnowledgeComposition;
import org.drools.io.impl.KnowledgeResource;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.xml.XmlCompositionReader;
import org.xml.sax.SAXException;

public class CompositionTest extends TestCase {
    public void testXmlParser() throws SAXException,
                               IOException {

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        XmlCompositionReader xmlReader = new XmlCompositionReader( conf.getSemanticModules() );

        String str = "";
        str += "<composition ";
        str += "xmlns='http://drools.org/drools-4.0/composition' ";
        str += "xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' ";
        str += "xs:schemaLocation='http://drools.org/drools-4.0/composition drools-composition-4.0.xsd' >";
        str += "    <resource source='http://www.domain.com/test.drl' type='DRL' />";
        str += "    <resource source='http://www.domain.com/test.xls' type='DTABLE' >";
        str += "        <decisiontable-conf worksheet-name='sheet10' input-type='XLS' />";
        str += "    </resource>";
        str += "</composition>";

        StringReader reader = new StringReader( str );
        KnowledgeComposition composition = xmlReader.read( reader );

        assertEquals( 2,
                      composition.getResources().size() );
        KnowledgeResource resource = composition.getResources().get( 0 );
        assertNull( resource.getConfiguration() );
        assertEquals( "http://www.domain.com/test.drl",
                      resource.getSource() );
        assertEquals( KnowledgeType.DRL,
                      resource.getType() );

        resource = composition.getResources().get( 1 );
        assertEquals( "http://www.domain.com/test.xls",
                      resource.getSource() );
        assertEquals( KnowledgeType.DTABLE,
                      resource.getType() );
        DecisionTableConfiguration dtConf = (DecisionTableConfiguration) resource.getConfiguration();
        assertEquals( DecisionTableInputType.XLS,
                      dtConf.getInputType() );
    }

    public void testIntegregation() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "composition1Test.xml",
                                                            getClass() ),
                      KnowledgeType.COMPOSITION );
        assertFalse( kbuilder.hasErrors() );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.containsAll( Arrays.asList( new String[]{"rule1", "rule2"} ) ) );
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
        File f1 = File.createTempFile( "rule1",
                                       ".drl" );
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
        File f2 = File.createTempFile( "rule2",
                                       ".drl" );
        f2.deleteOnExit();
        output = new BufferedWriter( new FileWriter( f2 ) );
        output.write( rule2 );
        output.close();

        String xml = "";
        xml += "<composition xmlns='http://drools.org/drools-4.0/composition'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-4.0/composition drools-composition-4.0.xsd' >";
        xml += "    <resource source='" + f1.toURI().toURL() + "' type='DRL' />";
        xml += "    <resource source='" + f2.toURI().toURL() + "' type='DRL' />";
        xml += "</composition>";
        File fxml = File.createTempFile( "composition",
                                         ".xml" );
        fxml.deleteOnExit();
        output = new BufferedWriter( new FileWriter( fxml ) );
        output.write( xml );
        output.close();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newUrlResource( fxml.toURI().toURL() ),
                      KnowledgeType.COMPOSITION );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty( "drools.resource.scanner.interval",
                           "2" );
        ResourceFactory.getResourceChangeScannerService().configure( sconf );

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanResources",
                           "true" );
        aconf.setProperty( "drools.agent.newInstance",
                           "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent",
                                                                         kbase,
                                                                         aconf );

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
    }

    public void testModifyDirectory() throws IOException,
                                     InterruptedException {
        File dir = File.createTempFile( UUID.randomUUID().toString(),
                                        "" );
        dir = dir.getParentFile();
        dir.deleteOnExit();
        
        dir = new File( dir, UUID.randomUUID().toString() );
        dir.mkdir();
        dir.deleteOnExit();
        
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        File f1 = File.createTempFile( "rule1",
                                       ".drl",
                                       dir );
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
        File f2 = File.createTempFile( "rule2",
                                       ".drl",
                                       dir );
        f2.deleteOnExit();
        output = new BufferedWriter( new FileWriter( f2 ) );
        output.write( rule2 );
        output.close();

        String xml = "";
        xml += "<composition xmlns='http://drools.org/drools-4.0/composition'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-4.0/composition drools-composition-4.0.xsd' >";
        xml += "    <resource source='" + f1.getParentFile().toURI().toURL() + "' type='DRL' />";
        xml += "</composition>";
        File fxml = File.createTempFile( "composition",
                                         ".xml" );
        fxml.deleteOnExit();
        output = new BufferedWriter( new FileWriter( fxml ) );
        output.write( xml );
        output.close();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newUrlResource( fxml.toURI().toURL() ),
                      KnowledgeType.COMPOSITION );
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty( "drools.resource.scanner.interval",
                           "2" );
        ResourceFactory.getResourceChangeScannerService().configure( sconf );

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanResources",
                           "true" );
        aconf.setProperty( "drools.agent.newInstance",
                           "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent",
                                                                         kbase,
                                                                         aconf );

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
        File f3 = File.createTempFile( "rule3",
                                       ".drl",
                                       dir );
        f3.deleteOnExit();
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
    }

}
