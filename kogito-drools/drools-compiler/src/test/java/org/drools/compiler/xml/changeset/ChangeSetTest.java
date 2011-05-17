package org.drools.compiler.xml.changeset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ChangeSet;
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
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.FileManager;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.FileSystemResource;
import org.drools.io.impl.KnowledgeResource;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.xml.XmlChangeSetReader;
import org.xml.sax.SAXException;

public class ChangeSetTest {

    @Test
    public void testXmlParser() throws SAXException,
                               IOException {

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        XmlChangeSetReader xmlReader = new XmlChangeSetReader( conf.getSemanticModules() );
        xmlReader.setClassLoader( ChangeSetTest.class.getClassLoader(), ChangeSetTest.class );

        String str = "";
        str += "<change-set ";
        str += "xmlns='http://drools.org/drools-5.0/change-set' ";
        str += "xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' ";
        str += "xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        str += "    <add> ";
        str += "        <resource source='http://www.domain.com/test.drl' type='DRL' />";
        str += "        <resource source='http://www.domain.com/test.xls' type='DTABLE' >";
        str += "            <decisiontable-conf worksheet-name='sheet10' input-type='XLS' />";
        str += "        </resource>";
        str += "    </add> ";
        str += "</change-set>";

        StringReader reader = new StringReader( str );
        ChangeSet changeSet = xmlReader.read( reader );

        assertEquals( 2,
                      changeSet.getResourcesAdded().size() );
        UrlResource resource = ( UrlResource ) ((List)changeSet.getResourcesAdded()).get( 0 );
        assertNull( resource.getConfiguration() );
        assertEquals( "http://www.domain.com/test.drl",
                      resource.getURL().toString() );
        assertEquals( ResourceType.DRL,
                      resource.getResourceType() );

        resource =  ( UrlResource ) ((List)changeSet.getResourcesAdded()).get( 1 );
        
        assertEquals( "http://www.domain.com/test.xls",
                      resource.getURL().toString() );
        assertEquals( ResourceType.DTABLE,
                      resource.getResourceType() );
        DecisionTableConfiguration dtConf = (DecisionTableConfiguration) resource.getConfiguration();
        assertEquals( DecisionTableInputType.XLS,
                      dtConf.getInputType() );
    }

    @Test
    public void testIntegregation() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "changeset1Test.xml",
                                                            getClass() ),
                      ResourceType.CHANGE_SET );
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

    @Test
    public void testBasicAuthentication() throws SAXException,
                               IOException {

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        XmlChangeSetReader xmlReader = new XmlChangeSetReader( conf.getSemanticModules() );
        xmlReader.setClassLoader( ChangeSetTest.class.getClassLoader(), ChangeSetTest.class );

        String str = "";
        str += "<change-set ";
        str += "xmlns='http://drools.org/drools-5.0/change-set' ";
        str += "xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' ";
        str += "xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        str += "    <add> ";
        str += "        <resource source='http://localhost:8081/jboss-brms/org.drools.guvnor.Guvnor/package/defaultPackage/LATEST' type='PKG' basicAuthentication='enabled' username='admin' password='pwd'/>";
        str += "    </add> ";
        str += "</change-set>";

        StringReader reader = new StringReader( str );
        ChangeSet changeSet = xmlReader.read( reader );

        assertEquals( 1,
                      changeSet.getResourcesAdded().size() );
        UrlResource resource = ( UrlResource ) ((List)changeSet.getResourcesAdded()).get( 0 );
        assertNull( resource.getConfiguration() );
        assertEquals( "http://localhost:8081/jboss-brms/org.drools.guvnor.Guvnor/package/defaultPackage/LATEST",
                      resource.getURL().toString() );
        assertEquals( "enabled", resource.getBasicAuthentication() );
        assertEquals( "admin", resource.getUsername() );
        assertEquals( "pwd", resource.getPassword() );
        assertEquals( ResourceType.PKG,
                      resource.getResourceType() );
    }


    
}
