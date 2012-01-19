package org.drools.agent;

import java.io.File;
import java.util.*;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.impl.KnowledgeAgentImpl;
import org.drools.definition.KnowledgeDefinition;
import org.drools.io.Resource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.rule.QueryResults;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class KnowledgeAgentIncrementalChangeSetTest extends BaseKnowledgeAgentTest {

    @Test
    public void testModifyFileUrlIncremental() throws Exception {
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
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule3" ) );

        scan( kagent );

        // Use the same session for incremental build test
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 1,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );
        ksession.dispose();

        // Check rule2 is still there
        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testRemoveFileUrlIncremental() throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createLhsRule( "rule1",
                                                    "String()" ) );

        File f2 = fileManager.write( "rule2.drl",
                                     createLhsRule( "rule2",
                                                    "String()" ) );

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

        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        // Delete the file so only rule 2 fires
        this.fileManager.deleteFile( f1 );
        scan( kagent );

        ksession.insert( "String2" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );

        //Delete f2 now, no rules should fire
        list.clear();

        this.fileManager.deleteFile( f2 );
        scan( kagent );

        ksession.insert( "String3" );
        ksession.fireAllRules();

        assertEquals( 0,
                      list.size() );

        ksession.dispose();

        kagent.dispose();
    }

    /**
     * Tests that if we have two DRL files, where one file overwrites a rule in
     * a prior file, that if we modify the first file that was overwritten, that
     * it will gain precedence and overwrite the other.
     *
     * @throws Exception
     */
    @Test
    public void testModifyFileUrlOverwriteIncremental() throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createLhsRule( new String[]{"rule1", "rule2"},
                                                    "String()\n" ) );

        File f2 = fileManager.write( "rule2.drl",
                                     createVersionedRule( null,
                                                          new String[]{"rule1"},
                                                          null,
                                                          "String()\n",
                                                          "2" ) );

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

        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1-V2" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        File f3 = fileManager.write( "rule2.drl",
                                     createVersionedRule( "rule1",
                                                          "3" ) );

        scan( kagent );

        ksession.insert( "String2" );

        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1-V3" ) );
        assertTrue( list.contains( "rule2" ) );

        //Delete f2 now, rule1 should still fire if the indexing worked properly
        list.clear();
        this.fileManager.deleteFile( f2 );

        scan( kagent );

        ksession.insert( "String3" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );

        ksession.dispose();
        kagent.dispose();
    }

    /**
     * Creates two rules (rule1 and rule2) in a drl file. Then it modifies the
     * drl file to change rule2 with rule3.
     * @throws Exception
     */
    @Test @Ignore
    public void testMultipleRulesOnFileUrlIncremental() throws Exception {

        File f1 = fileManager.write( "rules.drl",
                                     createLhsRule( new String[]{"rule1", "rule2"},
                                                    "String()\n" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        fileManager.write( "rules.drl",
                            createLhsRule( new String[]{"rule1", "rule3"},
                                           "String()\n" ) );

        scan( kagent );

        // Use the same session for incremental build test
        ksession.insert( "String2" );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule3" ) );

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testMultipleRulesOnFilesUrlIncremental() throws Exception {

        File f1 = fileManager.write( "rules1.drl",
                                     createLhsRule( new String[]{"rule1", "rule2"},
                                                    "String()\n" ) );

        fileManager.write( "rules2.drl",
                           createLhsRule( "rule3",
                                          "String()\n" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = createKAgent( kbase,
                                              false );

        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        FactHandle h1 = ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );

        list.clear();
        fileManager.write( "rules2.drl",
                           createLhsRule( "rule4",
                                          "String()\n" ) );
        scan( kagent );

        // Use the same session for incremental build test
        // Fact is still there, so should match against latest new rule
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule4" ) );

        list.clear();
        ksession.retract( h1 );

        ksession.insert( "String2" );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule4" ) );

        list.clear();
        fileManager.write( "rules1.drl",
                           createLhsRule( new String[]{"rule1", "rule5"},
                                          "String()\n" ) );
        scan( kagent );

        // Fact is still there, so should match against latest new rule        
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule5" ) );

        ksession.retract( h1 );
        list.clear();

        ksession.insert( "String3" );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule5" ) );
        assertTrue( list.contains( "rule4" ) );

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testModifyPackageUrlIncremental() throws Exception {

        // Put just Rule1 in the first package
        File pkg1 = fileManager.newFile( "pkg1.pkg" );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule1",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      pkg1 );

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
        KnowledgeAgent kagent = createKAgent( kbase,
                                              false );
        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );

        list.clear();

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule3",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule2",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      pkg1 );

        scan( kagent );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();
        ksession.insert( "String2" );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule2" ) );

        ksession.dispose();
        kagent.dispose();
    }

    @Test 
    public void testUpdatePackageUrlIncremental() throws Exception {

        // Add Rule1 and Rule2 in the first package
        File pkg1 = fileManager.newFile( "pkg1.pkg" );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule1",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule2",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      pkg1 );

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

        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule2",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule3",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg,
                      pkg1 );

        scan( kagent );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule3" ) );

        list.clear();
        ksession.insert( "String2" );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );
        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testUpdatePackageUrlOverwriteIncremental() throws Exception {

        // Add Rule1 and Rule2 in the first package
        File pkgF1 = fileManager.newFile( "pkg1.pkg" );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule1",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( createLhsRule( "rule2",
                                                                           "String()\n" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg1 = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg1,
                      pkgF1 );

        // Add Rule3 in the second package
        File pkgF2 = fileManager.newFile( "pkg2.pkg" );
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createDefaultRule( "rule3" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg2 = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg2,
                      pkgF2 );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg1.pkg' type='PKG' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg2.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        FactHandle h1 = ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );

        list.clear();

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( createVersionedRule( null,
                                                                                 new String[]{"rule1"},
                                                                                 null,
                                                                                 "String()",
                                                                                 "2" ).getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        pkg2 = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg2,
                      pkgF2 );

        scan( kagent );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1-V2" ) );
        list.clear();

        ksession.retract( h1 );
        ksession.insert( "String2" );

        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( "rule1-V2" ) );
        assertTrue( list.contains( "rule2" ) );

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testCompleteRuleScenario() throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createLhsRule( new String[]{"rule1", "rule2"},
                                                    "String()\n" ) );

        File f2 = fileManager.write( "rule2.drl",
                                     createLhsRule( "rule3",
                                                    "String()\n" ) );

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
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        FactHandle h1 = ksession.insert( "String1" );

        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );

        list.clear();

        File f3 = fileManager.write( "rule3.drl",
                                     createVersionedRule( null,
                                                          new String[]{"rule1"},
                                                          null,
                                                          "String()\n",
                                                          "2" ) );
        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule3.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        fxml = fileManager.write( "changeset.xml",
                                      xml );

        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        // Check as a result of old data against new rules
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        
        assertTrue( list.contains( "rule1-V2" ) );
        list.clear();

        // Check all rules are still there with new data
        ksession.retract( h1 );
        h1 = ksession.insert( "String2" );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1-V2" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );
        list.clear();

        f2 = fileManager.write( "rule2.drl",
                                createLhsRule( new String[]{"rule3",
                                                            "rule4"},
                                               "String()\n" ) );
        scan( kagent );

        // Check as a result of old data against new rules
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule4" ) );
        list.clear();

        // Check all rules are still there with new data
        ksession.retract( h1 );
        h1 = ksession.insert( "String3" );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );
        assertTrue( list.contains( "rule1-V2" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3" ) );
        assertTrue( list.contains( "rule4" ) );
        list.clear();

        f3 = fileManager.write( "rule3.drl",
                                createVersionedRule( null,
                                                     new String[]{"rule3"},
                                                     null,
                                                     "String()",
                                                     "2" ) );

        scan( kagent );

        // Check as a result of old data against new rules
        // Even if rule3 and rule3-V2 are defined in different resources, both
        // are the same rule (they have the same name ('rule3') and are defined 
        // in the same package), so in the kbase, rule3 is overwiritten by rule3-V2
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule3-V2" ) );
        list.clear();

        // Check all rules are still there with new data
        ksession.retract( h1 );
        h1 = ksession.insert( "String4" );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule3-V2" ) );
        assertTrue( list.contains( "rule4" ) );
        list.clear();

        this.fileManager.deleteFile( f3 );
        scan( kagent );

        // Check remaining rules are still there with new data
        ksession.retract( h1 );
        h1 = ksession.insert( "String5" );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule4" ) );
        list.clear();

        String str = createHeader( "org.drools.test" ) +
                     createVersionedRule( false,
                                          null,
                                          new String[]{"rule1"},
                                          null,
                                          "String()\n",
                                          "3" ) +
                     createVersionedRule( false,
                                          null,
                                          new String[]{"rule3", "rule4"},
                                          null,
                                          "String()\n",
                                          null );

        System.out.println( str );

        f2 = fileManager.write( "rule2.drl",
                                str );
        scan( kagent );

        // Check remaining rules are still there with new data
        ksession.retract( h1 );
        h1 = ksession.insert( "String6" );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1-V3" ) );
        assertTrue( list.contains( "rule2" ) );
        //Rule3 doesn't appear again because a new version was defined earleir
        //in rule3.drl. So, from the Agent's point of view, the deffinition of
        //Rule3 didn't change in this file
        //assertTrue( list.contains( "rule3" ) );  
        assertTrue( list.contains( "rule4" ) );
        list.clear();
        
        
        //Add a new version of Rule1 in rule3.drl
              f3 =  fileManager.write( "rule3.drl",
                                             createVersionedRule( null,
                                                                  new String[]{"rule1"},
                                                                  null,
                                                                  "String()\n",
                                                                  "4" ) );
              
        //rule3.drl was removed, so we need to force the agent to start 
        //monitoring it again.
        applyChangeSet(kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ));         
        
        // Check remaining rules are still there with new data
        ksession.retract( h1 );
        h1 = ksession.insert( "String7" );
        ksession.fireAllRules();
        
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "rule1-V4" ) );
        assertTrue( list.contains( "rule2" ) );
        assertTrue( list.contains( "rule4" ) );
        list.clear();
        
        ksession.dispose();
        kagent.dispose();

    }

    @Test
    public void testAddModifyFunctionIncremental() throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createCustomRule( true, null, new String[] { "rule1" },
                                                       null, "String()\n", "function1 (list,drools.getRule().getName());\n") );
        
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        try {
            applyChangeSet( kagent,
                            ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );
            fail( "Knowledge should fail to compile" );
        } catch (Exception e) {
            
        }
        KnowledgePackage knowledgePackage = kbase.getKnowledgePackage( "org.drools.test" );

        //the resource didn't compile because function1 doesn't exist
        assertNull( knowledgePackage );

        //we are going to add function1 now
        String function1 = this.createCommonFunction( "function1",
                                                      "function1" );
        fileManager.write( "rule1.drl",
                           function1 +
                           createCustomRule( false, null, new String[] { "rule1" },
                                             null, "String()\n", "function1 (list, drools.getRule().getName());\n") );
        scan( kagent );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( "String1" );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "function1 from rule1" ) );
        list.clear();

        String function2 = this.createCommonFunction( "function1",
                                                      "function1-V2" );
        fileManager.write( "rule1.drl",
                           function2 +
                           createCustomRule( false, null, new String[] { "rule1" },
                                             null, "String()\n", "function1 (list, drools.getRule().getName());\n") );
        
        //we are going to modify the definition of function1()
        //we are going to modify function1 now
        scan( kagent );

        ksession.fireAllRules();

        // Rule 1 already existed as is, so should not cause data propagation
        assertEquals( 0,
                      list.size() );
        
        ksession.insert( "String2" );
        ksession.fireAllRules();
        
        assertEquals( 1,
                      list.size() );
        
        assertTrue( list.contains( "function1-V2 from rule1" ) );

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testAddModifyQueryIncremental() throws Exception {
    
            String query1 = "";
            query1 += "query \"all the Strings\"\n";
            query1 += "     $strings : String()\n";
            query1 += "end\n";
    
            fileManager.write("rule1.drl", this.createDefaultRule("rule1") );
    
            String xml = "";
            xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
            xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
            xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
            xml += "    <add> ";
            xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
            xml += "    </add> ";
            xml += "</change-set>";
            File fxml = fileManager.write( "changeset.xml",
                                       xml );

            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            KnowledgeAgent kagent = this.createKAgent( kbase, false );

            try {
                applyChangeSet( kagent,
                                ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );
            } catch (Exception e) {
                fail( "Knowledge shouldn't fail to compile" );
            }
    
            KnowledgePackage knowledgePackage = kbase.getKnowledgePackage("org.drools.test");
    
            assertNotNull(knowledgePackage);
    
            Rule allTheStringsQuery = ((KnowledgePackageImp) knowledgePackage).getRule("all the Strings");
    
            assertNull(allTheStringsQuery);
    
                
            //we are going to add the query now
            fileManager.write("rule1.drl", this.createDefaultRule("rule1") + " \n " + query1 );
            
            this.scan(kagent);
    
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            List<String> list = new ArrayList<String>();
            ksession.setGlobal("list", list);
            ksession.insert("Some String");
            ksession.insert("Some Other String");
    
            QueryResults queryResults = ksession.getQueryResults("all the Strings");
    
            assertEquals(2, queryResults.size());
    
            Iterator<QueryResultsRow> iterator = queryResults.iterator();
            while (iterator.hasNext()){
                System.out.println("Row= "+iterator.next().get("$strings"));
            }
    
            //we are going to modify the query definition
            String query1V2 = "";
            query1V2 += "query \"all the Strings\"\n";
            query1V2 += "     $strings : String(this == \"Some String\")\n";
            query1V2 += "end\n";
            
            fileManager.write("rule1.drl", this.createDefaultRule("rule1") + " \n " + query1V2 );
            
            this.scan(kagent);
    
            queryResults = ksession.getQueryResults("all the Strings");
    
    
            assertEquals(1, queryResults.size());
            assertEquals("Some String",queryResults.iterator().next().get("$strings"));
    
            ksession.dispose();
            kagent.dispose();
        }

    /**
     * Creates kagent on a .drl file and then modifies the .drl with a malformed
     * rule. After the kbase is reconstructed (with 0 rules), the .drl is modified
     * again with a valid rule.
     * This test tends to prove that JBRULES-2904 is solved
     * @throws Exception 
     */
    @Test
    public void testCompilationFailndFixIncremental() throws Exception {
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule1" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        assertEquals(1,kbase.getKnowledgePackages().iterator().next().getRules().size());
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );

        list.clear();

        //malformed rule
        fileManager.write( "rule1.drl",
                           createLhsRule("rule1", "String())") );

        try{
            scan( kagent );
            fail("The compilation should fail!");
        }catch (RuntimeException ex){
            //expected expcetion
            System.err.println(ex);
        }

        
        assertTrue(kbase.getKnowledgePackages().iterator().next().getRules().isEmpty());
        
        //wellformed rule again
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule1" ) );
        
        scan( kagent );
        
        list.clear();
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );

        ksession.dispose();
        kagent.dispose();
    }

    /**
     * Same as {@link #testCompilationFailndFixIncremental() } but with two drl
     * files and only one failing.
     * This test tends to prove that JBRULES-2904 is solved
     * @throws Exception 
     */
    @Test
    public void testCompilationFailndFixIncremental2() throws Exception {
        fileManager.write( "rule1.drl",
                           createLhsRule("rule1", "String()") );
        fileManager.write( "rule2.drl",
                           createLhsRule("rule2", "String()") );

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
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        assertEquals(2,kbase.getKnowledgePackages().iterator().next().getRules().size());
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.insert("Some String");
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        list.clear();

        //malformed rule
        fileManager.write( "rule1.drl",
                           createLhsRule("rule1", "String())") );

        try{
            scan( kagent );
            fail("The compilation should fail!");
        }catch (RuntimeException ex){
            //expected expcetion
        }

        
        assertEquals(1,kbase.getKnowledgePackages().iterator().next().getRules().size());
        ksession.insert("Some Other String");
        ksession.fireAllRules();
        
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule2" ) );
        list.clear();
        
        //wellformed rule again
        fileManager.write( "rule1.drl",
                           createDefaultRule( "rule1" ) );
        
        scan( kagent );
        
        ksession.insert("Yet Another String");
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );
        assertTrue( list.contains( "rule2" ) );

        ksession.dispose();
        kagent.dispose();
    }
    
    @Test
    public void testMultiplePackages() throws Exception {
        //One rule in some.pkg package
        File f1 = fileManager.write( "rule1.drl",
                                        createLhsRule("some.pkg","rule1", "String()") );
        //Two rules in some.other.pkg package
        File f2 = fileManager.write( "rule2.drl",
                                        createLhsRule("some.other.pkg",new String[]{"rule2","rule3"}, "String()") );

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
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet(ResourceFactory.newUrlResource( fxml.toURI().toURL() ));

        //Did the agent respected the packges?
        assertEquals(2, kbase.getKnowledgePackages().size());
        
        //some.pkg has 1 rule
        KnowledgePackage somePkg = kbase.getKnowledgePackage("some.pkg");
        assertNotNull(somePkg);
        assertEquals(1, somePkg.getRules().size());
        assertEquals("rule1", somePkg.getRules().iterator().next().getName());
        
        //some.other.pkg has 2 rules
        KnowledgePackage someOtherPkg = kbase.getKnowledgePackage("some.other.pkg");
        assertNotNull(someOtherPkg);
        assertEquals(2, someOtherPkg.getRules().size());
        Iterator<Rule> someOtherPkgRules = someOtherPkg.getRules().iterator();
        assertEquals("rule2", someOtherPkgRules.next().getName());
        assertEquals("rule3", someOtherPkgRules.next().getName());
        
        //Delete rule2.drl content
        this.fileManager.deleteFile(f2);
        scan( kagent );
        
        //the empty package remains in the kbase
        assertEquals(2, kbase.getKnowledgePackages().size());
        
        //some.pkg has 1 rule
        somePkg = kbase.getKnowledgePackage("some.pkg");
        assertNotNull(somePkg);
        assertEquals(1, somePkg.getRules().size());
        assertEquals("rule1", somePkg.getRules().iterator().next().getName());
        
        //some.other.pkg is empty
        someOtherPkg = kbase.getKnowledgePackage("some.other.pkg");
        assertNotNull(someOtherPkg);
        assertTrue(someOtherPkg.getRules().isEmpty());
        
        
        //Add 1 more rule in a different package
        File f3 = fileManager.write( "rule3.drl",
                                        createLhsRule("yet.another.pkg","rule4", "String()") );
        
        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule3.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        
        this.applyChangeSet(kagent, xml);
        
        //There should be 3 packages now
        assertEquals(3, kbase.getKnowledgePackages().size());
        
        //some.pkg has 1 rule
        somePkg = kbase.getKnowledgePackage("some.pkg");
        assertNotNull(somePkg);
        assertEquals(1, somePkg.getRules().size());
        assertEquals("rule1", somePkg.getRules().iterator().next().getName());
        
        //some.other.pkg is empty
        someOtherPkg = kbase.getKnowledgePackage("some.other.pkg");
        assertNotNull(someOtherPkg);
        assertTrue(someOtherPkg.getRules().isEmpty());
        //yet.another.pkg has 1 rule
        KnowledgePackage yetAnotherPkg = kbase.getKnowledgePackage("yet.another.pkg");
        assertNotNull(yetAnotherPkg);
        assertEquals(1, yetAnotherPkg.getRules().size());
        assertEquals("rule4", yetAnotherPkg.getRules().iterator().next().getName());
        
        
        kagent.dispose();
    }





    @Test
       public void testResourceMappingWithIncrementalBuild() throws Exception {
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

           KnowledgeAgent kagent = this.createKAgent( kbase, false );

           applyChangeSet( kagent, ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

           StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

           org.drools.rule.Rule r1 = (org.drools.rule.Rule) ksession.getKnowledgeBase().getRule("org.drools.test","rule1");
               assertEquals(((UrlResource) r1.getResource()).getURL().toString(),"http://localhost:"+getPort()+"/pkg1.pkg");
           org.drools.rule.Rule r2 = (org.drools.rule.Rule) ksession.getKnowledgeBase().getRule("org.drools.test","rule2");
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


           assertEquals(0,ksession.getKnowledgeBase().getKnowledgePackage("org.drools.test").getRules().size());
           assertEquals(1,ksession.getKnowledgeBase().getKnowledgePackage("org.drools.test3").getRules().size());
           org.drools.rule.Rule r3 = (org.drools.rule.Rule) ksession.getKnowledgeBase().getRule("org.drools.test3","rule3");
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


           assertEquals(1,ksession.getKnowledgeBase().getKnowledgePackage("org.drools.test3").getRules().size());
           assertEquals(1,ksession.getKnowledgeBase().getKnowledgePackage("org.drools.test4").getRules().size());
           org.drools.rule.Rule r4 = (org.drools.rule.Rule) ksession.getKnowledgeBase().getRule("org.drools.test4","rule4");
                       defMap = ((KnowledgeAgentImpl) kagent).getRegisteredResources();
                       assertEquals(2,defMap.size());
                               assertTrue(defMap.containsKey(r4.getResource()));
                               defs = defMap.get(r4.getResource());
                                   assertTrue(defs.contains(r4));
                                   assertFalse(defs.contains(r3));
                                   assertFalse(defs.contains(r2));
                                   assertEquals(1,defs.size());

           ksession.dispose();
           kagent.dispose();
       }

}
