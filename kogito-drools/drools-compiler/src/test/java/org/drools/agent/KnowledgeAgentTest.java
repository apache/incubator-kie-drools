package org.drools.agent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.codehaus.janino.Java;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.impl.KnowledgeAgentImpl;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.UrlResource;
import org.drools.io.internal.InternalResource;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnowledgeAgentTest extends BaseKnowledgeAgentTest {

    @Test
    public void testModifyFileUrl() throws Exception {
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule1" ) );
        
        fileManager.write( "rule2.drl",
                           createDefaultRule( "rule2" ) );
        
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        this.fileManager.write( "rule1.drl", createDefaultRule( "rule3" ) );
        
         scan(kagent);
        
        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        
        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );
        
        kagent.dispose();
    }

    /**
     * Tests that if we change a ChangeSet that is referenced by another change
     * set or added by another ChangeSet, that the changes are picked up.
     *
     * @throws Exception
     *             If an unexpected exception occurs.
     */
    @Test
    public void testChangeSetInChangeSet() throws Exception {
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule1" ) );
        
        fileManager.write( "rule2.drl",
                           createDefaultRule( "rule2" ) );

        String xml1 = "";
        xml1 += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml1 += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml1 += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml1 += "    <add> ";
        xml1 += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml1 += "        <resource source='http://localhost:" + this.getPort() + "/rule2.drl' type='DRL' />";
        xml1 += "    </add> ";
        xml1 += "</change-set>";
        File fxml = fileManager.write( "changeset2.xml",
                                       xml1 );
        String xml2 = "";
        xml2 += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml2 += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml2 += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml2 += "    <add> ";
        xml2 += "        <resource source='http://localhost:" + this.getPort() + "/changeset2.xml' type='CHANGE_SET' />";
        xml2 += "    </add> ";
        xml2 += "</change-set>";
        File fxm2 = fileManager.write( "changeset.xml",
                                       xml1 );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxm2.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();
        
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule3" ) );

        scan(kagent);

        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );

        kagent.dispose();
    }

    @Test
    public void testModifyFileUrlWithStateless() throws Exception {
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule1" ) );
        
        fileManager.write( "rule2.drl",
                           createDefaultRule( "rule2" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatelessKnowledgeSession ksession = kagent.newStatelessKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.execute( "hello" );

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule3" ) );
        
        scan(kagent);

        ksession.execute( "hello" );

        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );
        kagent.dispose();
    }

    @Test
    public void testModifyPackageUrl() throws Exception {
        String rule1 = this.createDefaultRule( "rule1" );

        String rule2 = this.createDefaultRule( "rule2" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule2.getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      fileManager.newFile( "pkg1.pkg" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg1.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        rule1 = this.createDefaultRule( "rule3" );

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule2.getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      fileManager.newFile( "pkg1.pkg" ) );

        scan( kagent );

        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );
        kagent.dispose();
    }

    @Test 
    public void testDeletePackageUrl() throws Exception {
        String rule1 = this.createDefaultRule( "rule1",
                                               "org.drools.test1" );

        String rule2 = this.createDefaultRule( "rule2",
                                               "org.drools.test2" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule2.getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        Map<String, KnowledgePackage> map = new HashMap<String, KnowledgePackage>();
        for ( KnowledgePackage pkg : kbuilder.getKnowledgePackages() ) {
            map.put( pkg.getName(),
                     pkg );
        }
        writePackage( (KnowledgePackage) map.get( "org.drools.test1" ),
                      fileManager.newFile( "pkg1.pkg" ) );
        writePackage( (KnowledgePackage) map.get( "org.drools.test2" ),
                      fileManager.newFile( "pkg2.pkg" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg1.pkg' type='PKG' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg2.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        applyChangeSet( kagent, ResourceFactory.newByteArrayResource( xml.getBytes() )  );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <remove> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg2.pkg' type='PKG' />";
        xml += "    </remove> ";
        xml += "</change-set>";
        
        applyChangeSet( kagent, xml );
        
        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 1,
                      list.size() );

        assertTrue( list.contains( "rule1" ) );
        kagent.dispose();
    }

    @Test
    public void testOldSchoolPackageUrl() throws Exception {
        String rule1 = this.createDefaultRule( "rule1" );

        String rule2 = this.createDefaultRule( "rule2" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule2.getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBuilderImpl kbi = (KnowledgeBuilderImpl) kbuilder;

        writePackage( kbi.getPackageBuilder().getPackage(),
                      fileManager.newFile( "pkgold.pkg" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkgold.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() )  );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        
        kagent.dispose();

    }

    @Test
    public void testModifyFile() throws IOException,
                                InterruptedException {
        File f1 = fileManager.write( "rule1.drl",
                                     createDefaultRule( "rule1" ) );
        
        File f2 = fileManager.write( "rule2.drl",
                                     createDefaultRule( "rule2" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='" + f1.toURI().toURL() + "' type='DRL' />";
        xml += "        <resource source='" + f2.toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase );
        
        applyChangeSet( kagent,ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule3" ) );

        scan( kagent );

        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
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

    @Test
    public void testModifyDirectory() throws IOException,
                                     InterruptedException {
        // adds 2 files to a dir and executes then adds one and removes one and
        // detects changes
        File f1 = fileManager.write( "rule1.drl",
                                     createDefaultRule( "rule1" ) );
        
        File f2 = fileManager.write( "rule2.drl",
                                     createDefaultRule( "rule2" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='"
                + f1.getParentFile().toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset",
                                       "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        fileManager.write( "rule3.drl",
                           createDefaultRule( "rule3" ) );
        fileManager.deleteFile( f1 );

        scan( kagent );

        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );

        kagent.dispose();
    }

    @Test
    public void testModifyFileInDirectory() throws Exception {
        // Create the test directory
        File testDirectory = fileManager.newFile( "test" );
        testDirectory.mkdir();

        File f1 = fileManager.write( "test",
                                     "rule1.drl",
                                     createDefaultRule( "rule1" ) );
        
        File f2 = fileManager.write( "test",
                                     "rule2.drl",
                                     createDefaultRule( "rule2" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='file:"
                + fileManager.getRootDirectory().getAbsolutePath()
                + "/test' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset",
                                       "changeset.xml",
                                       xml );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();
        
        fileManager.write( "test",
                           "rule1.drl",
                           createDefaultRule( "rule3" ) );

        scan(kagent);

        ksession = createKnowledgeSession(kagent.getKnowledgeBase());
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );
        kagent.dispose();
    }

    @Test
    public void testStatelessWithCommands() throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createDefaultRule( "rule1" ) );
        
        File f2 = fileManager.write( "rule2.drl",
                                     createDefaultRule( "rule2" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='"
                + f1.getParentFile().toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset",
                                       "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase );
        
        applyChangeSet(kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatelessKnowledgeSession ksession = kagent.newStatelessKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        ksession.execute( new InsertObjectCommand( "hello" ) );

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
    }












    @Test
    public void testResourceMapping() throws Exception {
        String rule1 = this.createDefaultRule( "rule1" );
        String rule2 = this.createDefaultRule( "rule2" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( rule1.getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule2.getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      fileManager.newFile( "pkg1.pkg" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg1.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase, true );

        applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        Rule r1 = (Rule) kagent.getKnowledgeBase().getRule("org.drools.test","rule1");
            assertEquals(((UrlResource) r1.getResource()).getURL().toString(),"http://localhost:"+getPort()+"/pkg1.pkg");
        Rule r2 = (Rule) kagent.getKnowledgeBase().getRule("org.drools.test","rule2");
            assertEquals(((UrlResource) r2.getResource()).getURL().toString(),"http://localhost:"+getPort()+"/pkg1.pkg");
        Map<Resource, Set<KnowledgeDefinition>> defMap = ((KnowledgeAgentImpl) kagent).getRegisteredResources();
            assertTrue(defMap.containsKey(r1.getResource()));
            assertTrue(defMap.containsKey(r2.getResource()));
            Set<KnowledgeDefinition> defs = defMap.get(r1.getResource());
                assertTrue(defs.contains(r1));
                assertTrue(defs.contains(r2));
                assertEquals(2,defs.size());


        String rule3 = this.createDefaultRule( "rule3", "org.drools.test3" );

                kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
                kbuilder.add( ResourceFactory.newByteArrayResource( rule3.getBytes() ),
                              ResourceType.DRL );
                pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
                writePackage( pkg,
                              fileManager.newFile( "pkg1.pkg" ) );
                scan( kagent );


        assertNotSame(kbase, kagent.getKnowledgeBase());
        kbase = kagent.getKnowledgeBase();

        assertNull(kbase.getKnowledgePackage("org.drools.test"));
        assertEquals(1,kbase.getKnowledgePackage("org.drools.test3").getRules().size());
        Rule r3 = (Rule) kbase.getRule("org.drools.test3","rule3");
            assertEquals(((UrlResource) r3.getResource()).getURL().toString(),"http://localhost:"+getPort()+"/pkg1.pkg");
            defMap = ((KnowledgeAgentImpl) kagent).getRegisteredResources();
                    assertTrue(defMap.containsKey(r3.getResource()));
                    defs = defMap.get(r3.getResource());
                        assertTrue(defs.contains(r3));
                        assertFalse(defs.contains(r1));
                        assertFalse(defs.contains(r2));
                        assertEquals(1,defs.size());


        String rule4 = this.createDefaultRule( "rule4", "org.drools.test4" );
        ChangeSetImpl cs = new ChangeSetImpl();
            ByteArrayResource res = new ByteArrayResource( rule4.getBytes( ));
                res.setResourceType(ResourceType.DRL);
                cs.setResourcesAdded(Arrays.asList(new Resource[] {res}));
            kagent.applyChangeSet(cs);


        assertNotSame(kbase, kagent.getKnowledgeBase());
        kbase = kagent.getKnowledgeBase();


        assertEquals(1,kbase.getKnowledgePackage("org.drools.test3").getRules().size());
        assertEquals(1,kbase.getKnowledgePackage("org.drools.test4").getRules().size());
        Rule r4 = (Rule) kbase.getRule("org.drools.test4","rule4");
            assertSame(r4.getResource(),res);
                    defMap = ((KnowledgeAgentImpl) kagent).getRegisteredResources();
                    assertEquals(2,defMap.size());
                            assertTrue(defMap.containsKey(r4.getResource()));
                            defs = defMap.get(r4.getResource());
                                assertTrue(defs.contains(r4));
                                assertFalse(defs.contains(r3));
                                assertFalse(defs.contains(r2));
                                assertEquals(1,defs.size());
                            defs = defMap.get(r3.getResource());
                                assertTrue(defs.contains(r3));
                                assertFalse(defs.contains(r4));
                                assertFalse(defs.contains(r2));
                                assertEquals(1,defs.size());

        kagent.dispose();
    }






    @Test
    public void testMultipleResourcesAndPackages() {

        String drl = "package org.test.myPack;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "declare org.anotherPack.MyType\n" +
                "    field : String\n" +
                "end\n" +
                "\n" +
                "rule \"init\"\n" +
                "when \n" +
                "then \n" +
                "  System.out.println(\"X\");\n" +
                "end";

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = createKAgent( kbase, false );

        ByteArrayResource bres = (ByteArrayResource) ResourceFactory.newByteArrayResource( drl.getBytes() );
            bres.setResourceType(ResourceType.DRL);

        ChangeSetImpl cs = new ChangeSetImpl();
            cs.setResourcesAdded( Arrays.<Resource>asList(bres) );
        kagent.applyChangeSet( cs );

        KnowledgePackage pack = kagent.getKnowledgeBase().getKnowledgePackage("org.test.myPack");
            assertNotNull(pack);
            assertEquals(1,pack.getRules().size());
        KnowledgePackage pack2 = kagent.getKnowledgeBase().getKnowledgePackage("org.anotherPack");
            assertNotNull(pack2);
            assertEquals(0,pack2.getRules().size());


        String drl2 = "package org.test.myPack;\n" +
                "function void foo() { \n" +
                "} \n" +
                "\n" +
                "\n" +
                "declare org.anotherPack.MyType2\n" +
                "    field : String\n" +
                "end\n" +
                "" +
                "rule \"rool\" \n" +
                "when\n" +
                "then\n" +
                "end\n";

        ByteArrayResource bres2 = (ByteArrayResource) ResourceFactory.newByteArrayResource( drl2.getBytes() );
            bres2.setResourceType(ResourceType.DRL);
            ChangeSetImpl cs2 = new ChangeSetImpl();
                    cs2.setResourcesAdded( Arrays.<Resource>asList(bres2) );
            kagent.applyChangeSet( cs2 );

        pack = kagent.getKnowledgeBase().getKnowledgePackage("org.test.myPack");
            assertNotNull(pack);
            assertEquals(2,pack.getRules().size());
        pack2 = kagent.getKnowledgeBase().getKnowledgePackage("org.anotherPack");
            assertNotNull(pack2);
            assertEquals(0,pack2.getRules().size());



        Collection<KnowledgeDefinition> def1 = ((KnowledgeAgentImpl) kagent).getRegisteredResources().get(bres);
            assertEquals(2,def1.size());
            checkAllDefinitionsBelongToResource(def1, bres);


        Collection<KnowledgeDefinition> def2 = ((KnowledgeAgentImpl) kagent).getRegisteredResources().get(bres2);
            assertEquals(2,def2.size());
            checkAllDefinitionsBelongToResource(def2, bres2);
    }

    private void checkAllDefinitionsBelongToResource(Collection<KnowledgeDefinition> def, ByteArrayResource res) {
        for ( KnowledgeDefinition kd : def) {
            if ( kd instanceof Java.TypeDeclaration ) {
                assertEquals( res, ((TypeDeclaration) kd).getResource());
            } else if ( kd instanceof Rule) {
                assertEquals( res, ((Rule) kd).getResource());
            }
        }
    }



    @Test
    /**
     * JBRULES-3139
     */
    public void testMissingChangeset() throws Exception {
        KnowledgeAgent kAgent =  createKAgent( KnowledgeBaseFactory.newKnowledgeBase() );

        String urlString = "file://tmp/MissingChangeSet.xml";
        try {
            URL url = new URL(urlString);
            Resource urlResource = ResourceFactory.newUrlResource(url);

            assertNotNull(urlResource);

            kAgent.applyChangeSet( urlResource );

            assertEquals(0,kAgent.getKnowledgeBase().getKnowledgePackages().size());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


}
