package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListener;
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

public class KnowledgeAgentDisposeTest extends TestCase {

    FileManager fileManager;
    private Server server;
    private final Object lock = new Object();
    private volatile boolean kbaseUpdated;
    private boolean compilationErrors;

    private int resourceChangeNotificationCount = 0;


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

        this.kbaseUpdated = false;
        this.resourceChangeNotificationCount = 0;
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

    public void testMonitorResourceChangeEvents() throws Exception {

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

        //Create a new Agent with newInstace=true
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);

        //Agent: take care of them!
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        kbaseUpdated = false;
        resourceChangeNotificationCount = 0;
        
        Thread.sleep(2000);
        //the dsl is now modified.
        f1 = fileManager.newFile("myExpander.dsl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(this.createCommonDSL("name == \"John\""));
        output.close();

        //the drl file is marked as modified too
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();


        this.waitUntilKBaseUpdate();

        //two resources were modified, but only one change set is created
        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;
        Thread.sleep(2000);

        //let's add a new rule
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.write(this.createCommonDSLRRule("Rule2"));
        output.close();

        this.waitUntilKBaseUpdate();

        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;

        //the kagent is stopped
        kagent.monitorResourceChangeEvents(false);

        //the drl file is marked as modified
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        //we can't wait until kbase update, because the agent is not monitoring
        //change sets anymore
        Thread.sleep(5000);

        assertEquals(0, resourceChangeNotificationCount);


        //let start the agent again
        kagent.monitorResourceChangeEvents(true);

        //the drl file is marked as modified
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

         Thread.sleep(2000);

        assertEquals(1, resourceChangeNotificationCount);

        kagent.monitorResourceChangeEvents(false);
    }


    public void testDispose() throws Exception {

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

        //Create a new Agent with newInstace=true
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);

        //Agent: take care of them!
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        kbaseUpdated = false;
        resourceChangeNotificationCount = 0;

        Thread.sleep(2000);
        //the dsl is now modified.
        f1 = fileManager.newFile("myExpander.dsl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(this.createCommonDSL("name == \"John\""));
        output.close();

        //the drl file is marked as modified too
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();


        this.waitUntilKBaseUpdate();

        //two resources were modified, but only one change set is created
        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;

        //let us create a new ksession and fire all the rules
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", new ArrayList<String>());
        ksession.fireAllRules();

        Thread.sleep(2000);

        //let's add a new rule
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.write(this.createCommonDSLRRule("Rule2"));
        output.close();

        this.waitUntilKBaseUpdate();

        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;

        //the old ksession can be reused
        ksession.fireAllRules();


        //We will try to dispose the kagent: but an active stateful ksession exists.
        try{
            kagent.dispose();
            fail("The agent should failed");
        } catch (IllegalStateException ex){
        }

        //We need to dispose the ksession first
        ksession.dispose();

        //Now it is safe to dispose the kagent
        kagent.dispose();

        //the drl file is marked as modified
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        //we can't wait until kbase update, because the agent is not monitoring
        //change sets anymore
        Thread.sleep(5000);

        assertEquals(0, resourceChangeNotificationCount);




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

        kagent.setSystemEventListener(new SystemEventListener() {

            public void info(String message) {
            }

            public void info(String message, Object object) {
            }

            public void warning(String message) {
            }

            public void warning(String message, Object object) {
            }

            public void exception(String message, Throwable e) {
            }

            public void exception(Throwable e) {
            }

            public void debug(String message) {
                if ("KnowledgeAgent received ChangeSet changed notification".equals(message)){
                    resourceChangeNotificationCount++;
                }
            }

            public void debug(String message, Object object) {
            }
        });

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
