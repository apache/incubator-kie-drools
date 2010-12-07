package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
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
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentEventListenerTest extends TestCase {

    FileManager fileManager;
    private Server server;
    private final Object lock = new Object();
    private volatile boolean changeSetApplied;
    private boolean compilationErrors;
    private boolean kbaseUpdated;
    private int beforeChangeSetProcessed;
    private int afterChangeSetProcessed;
    private int beforeChangeSetApplied;
    private int afterChangeSetApplied;
    private int beforeResourceProcessed;
    private int afterResourceProcessed;


    @Override
    protected void setUp() throws Exception {
        fileManager = new FileManager();
        fileManager.setUp();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();

        this.server = new Server(0);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(fileManager.getRootDirectory().getPath());

        server.setHandler(resourceHandler);

        server.start();

        this.resetEventCounters();
    }

    private int getPort() {
        return this.server.getConnectors()[0].getLocalPort();
    }

    @Override
    protected void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        ((ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService()).reset();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        server.stop();
    }
    
    public void testDummy() {
    }

    public void FIXMEtestEventListenerWithIncrementalChangeSet() throws Exception {

        String header = "";
        header += "package org.drools.test\n";
        header += "import org.drools.Person\n\n";
        header += "global java.util.List list\n\n";

        //create a basic dsl file
        File f1 = fileManager.newFile("myExpander.dsl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(this.createCommonDSL(null));
        output.close();

        //create a basic dslr file
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.drl' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstace=true
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);

        //Agent: take care of them!
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(2, this.beforeResourceProcessed);
        assertEquals(2, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        Thread.sleep(2000);
        //the dsl is now modified.
        f1 = fileManager.newFile("myExpander.dsl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(this.createCommonDSL("name == \"John\""));
        output.close();

        //We also need to mark the dslr file as modified, so the rules could
        //be regenerated
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();


        this.waitUntilChangeSetApplied();
        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(2, this.beforeResourceProcessed);
        assertEquals(2, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        //The new fact activated and fired the modified rule
        Thread.sleep(2000);

        //let's add a new rule
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.write(this.createCommonDSLRRule("Rule2"));
        output.close();

        this.waitUntilChangeSetApplied();

        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(1, this.beforeResourceProcessed);
        assertEquals(1, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        //let's remove Rule1 and Rule2 and add a new rule: Rule3
        Thread.sleep(2000);
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule3"));
        output.close();

        this.waitUntilChangeSetApplied();

        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(1, this.beforeResourceProcessed);
        assertEquals(1, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        //let's delete the dsl file (errors are expected)
        Thread.sleep(2000);
        f1 = fileManager.newFile("myExpander.dsl");
        f1.delete();

        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        this.waitUntilChangeSetApplied();
        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(2, this.beforeResourceProcessed);
        assertEquals(2, this.afterResourceProcessed);
        assertTrue(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        kagent.monitorResourceChangeEvents(false);
    }

    public void FIXMEtestEventListenerWithoutIncrementalChangeSet() throws Exception {

        System.out.println("\n\ntestDSLAndNewInstance\n\n");

        String header = "";
        header += "package org.drools.test\n";
        header += "import org.drools.Person\n\n";
        header += "global java.util.List list\n\n";

        //create a basic dsl file
        File f1 = fileManager.newFile("myExpander.dsl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(this.createCommonDSL(null));
        output.close();

        //create a basic dslr file
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.drl' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        List<String> list = new ArrayList<String>();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        //Create a new Agent with newInstace=true
        KnowledgeAgent kagent = this.createKAgent(kbase,true);

        //Agent: take care of them!
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));


        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(2, this.beforeResourceProcessed);
        assertEquals(2, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        Thread.sleep(2000);
        //Let's modify the dsl file
        f1 = fileManager.newFile("myExpander.dsl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(this.createCommonDSL("name == \"John\""));
        output.close();

        //We need to mark the dslr file as modified (even when it was not) so
        //the agent could recreate the rules it contains using the new dsl.
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        this.waitUntilChangeSetApplied();
        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(2, this.beforeResourceProcessed);
        assertEquals(2, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        Thread.sleep(2000);
        //Let's add a new Rule
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.write(this.createCommonDSLRRule("Rule2"));
        output.close();

        this.waitUntilChangeSetApplied();
        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(1, this.beforeResourceProcessed);
        assertEquals(1, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        Thread.sleep(2000);
        //Let's remove both rules and add a new one: Rule3
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule3"));
        output.close();

        this.waitUntilChangeSetApplied();
        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(1, this.beforeResourceProcessed);
        assertEquals(1, this.afterResourceProcessed);
        assertFalse(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();

        Thread.sleep(2000);
        //let's delete the dsl file (errors are expected)
        f1 = fileManager.newFile("myExpander.dsl");
        f1.delete();

        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));

        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        this.waitUntilChangeSetApplied();
        assertEquals(1, this.beforeChangeSetApplied);
        assertEquals(1, this.afterChangeSetApplied);
        assertEquals(1, this.beforeChangeSetProcessed);
        assertEquals(1, this.afterChangeSetProcessed);
        assertEquals(2, this.beforeResourceProcessed);
        assertEquals(2, this.afterResourceProcessed);
        assertTrue(this.compilationErrors);
        assertTrue(this.kbaseUpdated);
        this.resetEventCounters();


        kagent.monitorResourceChangeEvents(false);
    }

    private KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance) {
        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        //System.setProperty(KnowledgeAgentFactory.PROVIDER_CLASS_NAME_PROPERTY_NAME, "org.drools.agent.impl.KnowledgeAgentProviderImpl");

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        // Testing incremental build here
        aconf.setProperty("drools.agent.newInstance", "" + newInstance);

        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

        final KnowledgeAgentEventListenerTest test = this;
        kagent.addEventListener(new KnowledgeAgentEventListener() {

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
                beforeChangeSetApplied++;
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                afterChangeSetApplied++;
                synchronized (lock) {
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
            }
        });

        assertEquals("test agent", kagent.getName());

        return kagent;
    }

    private String createCommonDSLRRule(String ruleName) {
        StringBuilder sb = new StringBuilder();
        sb.append("rule ");
        sb.append(ruleName);
        sb.append("\n");
        sb.append("when\n");
        sb.append("There is a Person\n");
        sb.append("then\n");
        sb.append("add rule's name to list;\n");
        sb.append("end\n");

        return sb.toString();
    }

    private String createCommonDSL(String restriction) {
        StringBuilder sb = new StringBuilder();
        sb.append("[condition][]There is a Person = Person(");
        if (restriction != null) {
            sb.append(restriction);
        }
        sb.append(")\n");
        sb.append("[consequence][]add rule's name to list = list.add( drools.getRule().getName() );\n");
        return sb.toString();
    }

    private void waitUntilChangeSetApplied() {
        synchronized (lock) {
            while (!changeSetApplied) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
                System.out.println("Waking up!");
            }
            changeSetApplied = false;
        }
    }

    private void resetEventCounters(){
        this.beforeChangeSetApplied=0;
        this.beforeChangeSetProcessed=0;
        this.beforeResourceProcessed=0;
        this.afterChangeSetApplied=0;
        this.afterChangeSetProcessed=0;
        this.afterResourceProcessed=0;
        this.compilationErrors = false;
        this.changeSetApplied = false;
        this.kbaseUpdated = false;
    }
}
