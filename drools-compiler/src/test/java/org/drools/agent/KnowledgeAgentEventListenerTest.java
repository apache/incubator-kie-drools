package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.core.util.FileManager;
import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentEventListenerTest extends BaseKnowledgeAgentTest {

    private final Object     lock = new Object();
    private volatile boolean changeSetApplied;
    private boolean          compilationErrors;
    private boolean          kbaseUpdated;
    private int              beforeChangeSetProcessed;
    private int              afterChangeSetProcessed;
    private int              beforeChangeSetApplied;
    private int              afterChangeSetApplied;
    private int              beforeResourceProcessed;
    private int              afterResourceProcessed;

    @Test @Ignore
    public void testEventListenerWithIncrementalChangeSet() throws Exception {
        fileManager.write( "myExpander.dsl",
                           this.createCommonDSL( null ) );

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule1" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.drl' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstace=true
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        //Agent: take care of them!
        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 2,
                      this.beforeResourceProcessed );
        assertEquals( 2,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Person( "John" ) );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule1" ) );
        list.clear();

        File f2 = fileManager.write( "myExpander.dsl",
                                     this.createCommonDSL( "name == \"John\"" ) );

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule1" ) );

        scan( kagent );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 2,
                      this.beforeResourceProcessed );
        assertEquals( 2,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule1" ) );
        list.clear();

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( new String[]{"Rule1", "Rule2"} ) );

        scan( kagent );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 1,
                      this.beforeResourceProcessed );
        assertEquals( 1,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule2" ) );
        list.clear();

        //let's remove Rule1 and Rule2 and add a new rule: Rule3
        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule3" ) );
        scan( kagent );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 1,
                      this.beforeResourceProcessed );
        assertEquals( 1,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule3" ) );
        list.clear();

        //let's delete the dsl file (errors are expected)
        this.fileManager.deleteFile( f2 );
        scan( kagent );

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule1" ) );
        scan( kagent );
        assertEquals( 2,
                      this.beforeChangeSetApplied );
        assertEquals( 2,
                      this.afterChangeSetApplied );
        assertEquals( 2,
                      this.beforeChangeSetProcessed );
        assertEquals( 2,
                      this.afterChangeSetProcessed );
        assertEquals( 2,
                      this.beforeResourceProcessed );
        assertEquals( 2,
                      this.afterResourceProcessed );
        assertTrue( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession.dispose();
        kagent.dispose();
    }

    @Test @Ignore
    public void testEventListenerWithoutIncrementalChangeSet() throws Exception {
        fileManager.write( "myExpander.dsl",
                           this.createCommonDSL( null ) );

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule1" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.drl' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        List<String> list = new ArrayList<String>();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        //Create a new Agent with newInstace=true
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        //Agent: take care of them!
        applyChangeSet( kagent,
                        ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 2,
                      this.beforeResourceProcessed );
        assertEquals( 2,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Person( "John" ) );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule1" ) );
        list.clear();

        File f2 = fileManager.write( "myExpander.dsl",
                                     this.createCommonDSL( "name == \"John\"" ) );

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule1" ) );

        scan( kagent );

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Person( "John" ) );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule1" ) );
        list.clear();

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 2,
                      this.beforeResourceProcessed );
        assertEquals( 2,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Person( "John" ) );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule1" ) );
        list.clear();

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( new String[]{"Rule1", "Rule2"} ) );

        scan( kagent );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 1,
                      this.beforeResourceProcessed );
        assertEquals( 1,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Person( "John" ) );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "Rule1" ) );
        assertTrue( list.contains( "Rule2" ) );
        list.clear();

        //let's remove Rule1 and Rule2 and add a new rule: Rule3
        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule3" ) );
        scan( kagent );

        assertEquals( 1,
                      this.beforeChangeSetApplied );
        assertEquals( 1,
                      this.afterChangeSetApplied );
        assertEquals( 1,
                      this.beforeChangeSetProcessed );
        assertEquals( 1,
                      this.afterChangeSetProcessed );
        assertEquals( 1,
                      this.beforeResourceProcessed );
        assertEquals( 1,
                      this.afterResourceProcessed );
        assertFalse( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Person( "John" ) );
        ksession.fireAllRules();
        ksession.dispose();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "Rule3" ) );
        list.clear();

        //let's delete the dsl file (errors are expected)
        this.fileManager.deleteFile( f2 );
        scan( kagent );

        fileManager.write( "rules.drl",
                           createCommonDSLRRule( "Rule1" ) );
        scan( kagent );
        assertEquals( 2,
                      this.beforeChangeSetApplied );
        assertEquals( 2,
                      this.afterChangeSetApplied );
        assertEquals( 2,
                      this.beforeChangeSetProcessed );
        assertEquals( 2,
                      this.afterChangeSetProcessed );
        assertEquals( 2,
                      this.beforeResourceProcessed );
        assertEquals( 2,
                      this.afterResourceProcessed );
        assertTrue( this.compilationErrors );
        assertTrue( this.kbaseUpdated );
        this.resetEventCounters();

        ksession.dispose();
        kagent.dispose();
    }

    public KnowledgeAgent createKAgent(KnowledgeBase kbase,
                                       boolean newInstance) {
        KnowledgeAgent kagent = super.createKAgent( kbase,
                                                    newInstance );

        kagent.addEventListener( new KnowledgeAgentEventListener() {

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
                beforeChangeSetApplied++;
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                afterChangeSetApplied++;
                synchronized ( lock ) {
                    changeSetApplied = true;
                    lock.notifyAll();
                }
            }

            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
                beforeChangeSetProcessed++;
            }

            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
                afterChangeSetProcessed++;
            }

            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
                beforeResourceProcessed++;
            }

            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
                afterResourceProcessed++;
            }

            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
                kbaseUpdated = true;
            }

            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
                compilationErrors = true;
                System.out.println( event.getKnowledgeBuilder().getErrors().toString() );
            }
        } );

        assertEquals( "test agent",
                      kagent.getName() );

        return kagent;
    }

    private void resetEventCounters() {
        this.beforeChangeSetApplied = 0;
        this.beforeChangeSetProcessed = 0;
        this.beforeResourceProcessed = 0;
        this.afterChangeSetApplied = 0;
        this.afterChangeSetProcessed = 0;
        this.afterResourceProcessed = 0;
        this.compilationErrors = false;
        this.changeSetApplied = false;
        this.kbaseUpdated = false;
    }
}
