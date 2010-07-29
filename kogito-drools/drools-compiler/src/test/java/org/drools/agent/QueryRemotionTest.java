package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
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
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

/**
 * Removes a query from kbase using kagent and incremental changeset build.
 * @author esteban.aliverti@gmail.com
 */
public class QueryRemotionTest extends TestCase {

    private final Object lock = new Object();
    FileManager fileManager;
    private Server server;
    private volatile boolean kbaseUpdated;

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
        System.out.println("root : " + fileManager.getRootDirectory().getPath());

        server.setHandler(resourceHandler);

        server.start();
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

    public void testRemoveQueryChangeSet() throws Exception {

        String header = "";
        header += "package org.drools.test\n";
        header += "global java.util.List list\n\n";

        String query1 = "";
        query1 += "query \"all the Strings\"\n";
        query1 += "     $strings : String()\n";
        query1 += "end\n";

        File f1 = fileManager.newFile("rules.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(query1);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase);
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        this.kbaseUpdated = false;

        assertEquals(1, kbase.getKnowledgePackages().iterator().next().getRules().size());
        assertTrue(kbase.getKnowledgePackages().iterator().next().getRules().iterator().next().getName().equals("all the Strings"));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.insert("Some String");

        QueryResults queryResults = ksession.getQueryResults("all the Strings");

        assertTrue(queryResults.size() == 1);
        assertTrue(queryResults.iterator().next().get("$strings").equals("Some String"));

        Thread.sleep(2000);

        query1 = "";
        query1 += "query \"all the Strings 2\"\n";
        query1 += "     $strings : String()\n";
        query1 += "end\n";

        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(query1);
        output.close();

        this.waitUntilKBaseUpdate();

        assertEquals(1, kbase.getKnowledgePackages().iterator().next().getRules().size());
        assertTrue(kbase.getKnowledgePackages().iterator().next().getRules().iterator().next().getName().equals("all the Strings 2"));

        queryResults = ksession.getQueryResults("all the Strings 2");

        assertTrue(queryResults.size() == 1);
        assertTrue(queryResults.iterator().next().get("$strings").equals("Some String"));


        ksession.dispose();

    }

    private KnowledgeAgent createKAgent(KnowledgeBase kbase) {
        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        // Testing incremental build here
        aconf.setProperty("drools.agent.newInstance", "false");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

        kagent.addEventListener(new KnowledgeAgentEventListener() {

            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }

            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
            }

            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
            }

            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }

            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }

            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }

            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
                System.out.println("KBase was updated");
                synchronized (lock) {
                    kbaseUpdated = true;
                    lock.notifyAll();
                }
            }

            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
            }
        });

        assertEquals("test agent", kagent.getName());

        return kagent;
    }

    private void waitUntilKBaseUpdate() {
        synchronized (lock) {
            while (!kbaseUpdated) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
                System.out.println("Waking up!");
            }
            kbaseUpdated = false;
        }
    }
}
