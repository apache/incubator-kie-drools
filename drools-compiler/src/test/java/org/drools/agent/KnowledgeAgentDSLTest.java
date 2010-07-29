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

public class KnowledgeAgentDSLTest extends TestCase {

    FileManager fileManager;
    private Server server;
    private final Object lock = new Object();
    private volatile boolean kbaseUpdated;
    private boolean compilationErrors;


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

    public void testDSLAndIncrementalChangeSet() throws Exception {

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
        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();


        ksession.setGlobal("list", list);
        ksession.insert(new Person());
        ksession.fireAllRules();

        //The rule should be fired
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));

        list.clear();
        kbaseUpdated = false;
        
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


        this.waitUntilKBaseUpdate();

        ksession.insert(new Person());
        ksession.fireAllRules();

        //The rule was modified then no rule got fired.
        assertEquals(0, list.size());

        Person p = new Person();
        p.setName("John");
        ksession.insert(p);
        ksession.fireAllRules();

        //The new fact activated and fired the modified rule
        Thread.sleep(2000);
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));

        //let's add a new rule
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.write(this.createCommonDSLRRule("Rule2"));
        output.close();

        this.waitUntilKBaseUpdate();

        list.clear();
        ksession.fireAllRules();

        //because we already had a John inserted, Rule2 gets fired.
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule2"));

        //let's remove Rule1 and Rule2 and add a new rule: Rule3
        Thread.sleep(2000);
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule3"));
        output.close();

        this.waitUntilKBaseUpdate();

        //we don't want to use a new ksession.
        ksession.dispose();
        ksession = kbase.newStatefulKnowledgeSession();

        //insert John
        list.clear();
        ksession.setGlobal("list", list);
        ksession.insert(p);
        ksession.fireAllRules();

        //The only existing rule must be fired
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule3"));

        //let's delete the dsl file (errors are expected)
        Thread.sleep(2000);
        f1 = fileManager.newFile("myExpander.dsl");
        f1.delete();

        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        this.waitUntilKBaseUpdate();

        //Compilation errors should occurred
        assertTrue(this.compilationErrors);
        this.compilationErrors=false;

        ksession.dispose();
        kagent.monitorResourceChangeEvents(false);
    }

    public void testDSLAndNewInstance() throws Exception {

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
        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
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
        this.kbaseUpdated = false;
        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();


        ksession.setGlobal("list", list);
        ksession.insert(new Person());
        ksession.fireAllRules();

        //The rule should be fired
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));
        list.clear();

        ksession.dispose();

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

        this.waitUntilKBaseUpdate();

        //get a new ksession
        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        //A Person without name shouldn't fire any rule now (because it was
        //modified)
        ksession.setGlobal("list", list);
        ksession.insert(new Person());
        ksession.fireAllRules();

        assertEquals(0, list.size());

        //A "John" Person should fire the modified rule
        Person p = new Person();
        p.setName("John");
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));

        ksession.dispose();

        Thread.sleep(2000);
        //Let's add a new Rule
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.write(this.createCommonDSLRRule("Rule2"));
        output.close();

        this.waitUntilKBaseUpdate();

        //get a new ksession
        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        //A "John" Person now should fire 2 rules
        list.clear();
        ksession.setGlobal("list", list);
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("Rule1"));
        assertTrue(list.contains("Rule2"));

        ksession.dispose();

        Thread.sleep(2000);
        //Let's remove both rules and add a new one: Rule3
        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(this.createCommonDSLRRule("Rule3"));
        output.close();

        this.waitUntilKBaseUpdate();

        //get a new ksession
        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        //A "John" Person now should only Rule3 (the other two rules were removes)
        list.clear();
        ksession.setGlobal("list", list);
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("Rule3"));

        ksession.dispose();

        Thread.sleep(2000);
        //let's delete the dsl file (errors are expected)
        f1 = fileManager.newFile("myExpander.dsl");
        f1.delete();

        f1 = fileManager.newFile("rules.drl");
        output = new BufferedWriter(new FileWriter(f1));

        output.write(header);
        output.write(this.createCommonDSLRRule("Rule1"));
        output.close();

        this.waitUntilKBaseUpdate();

        //Compilation errors should occurred
        assertTrue(this.compilationErrors);
        this.compilationErrors=false;


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

        final KnowledgeAgentDSLTest test = this;
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
