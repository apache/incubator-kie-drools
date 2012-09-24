package org.drools.compiler.xml.changeset;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.ChangeSet;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.xml.XmlChangeSetReader;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ChangeSetTest extends CommonTestMethodBase {

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
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testCustomClassLoader() throws Exception {
        // JBRULES-3630
        String absolutePath = new File("file").getAbsolutePath();
        String jarPath = absolutePath.contains("drools-compiler") ?
                "src/test/resources/org/drools/compiler/xml/changeset/changeset.jar" :
                "drools-compiler/src/test/resources/org/drools/compiler/xml/changeset/changeset.jar";
        File jar = new File(jarPath);
        assertTrue(jar.exists());
        ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()}, getClass().getClassLoader());
        Resource changeSet = ResourceFactory.newClassPathResource("changeset1.xml", classLoader);
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        builder.add(changeSet, ResourceType.CHANGE_SET);
    }
}
