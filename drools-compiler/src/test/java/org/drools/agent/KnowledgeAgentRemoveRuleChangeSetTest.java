package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.util.DroolsStreamUtils;
import org.drools.util.FileManager;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentRemoveRuleChangeSetTest extends TestCase {

    FileManager fileManager;
    private Server server;

    @Override
    protected void setUp() throws Exception {
        fileManager = new FileManager();
        fileManager.setUp();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();

        this.server = new Server(9000);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(fileManager.getRootDirectory().getPath());
        System.out.println("root : " + fileManager.getRootDirectory().getPath());

        server.setHandler(resourceHandler);

        server.start();
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

    public void testModifyFileUrlIncremental() throws Exception {

        String header = "";
        header += "package org.drools.test\n";
        header += "global java.util.List list\n\n";

        String rule1 = this.createCommonRule("rule1");
        String rule2 = this.createCommonRule("rule2");
        String rule3 = this.createCommonRule("rule3");
        String rule4 = this.createCommonRule("rule4");

        String function1 = this.createDummyFunction("function1");

        String query1 = "";
        query1 += "query \"all the Strings\"\n";
        //query1 += "query \"rule1\"\n";
        query1 += "     str : String()\n";
        query1 += "end\n";

        String type1 = "";
        type1 += "declare Address\n";
        type1 += "  number : int\n";
        type1 += "  streetName : String\n";
        type1 += "  city : String\n";
        type1 += "end\n";


        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(type1);
        output.write(query1);
        output.write(function1);
        output.write(rule1);
        output.write(rule2);
        output.close();

        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule3);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/rule1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:9000/rule2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase);
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);


        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(rule2);
        output.write(rule4);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule4"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testRemoveRuleChangeSet() throws Exception {

        String header = "";
        header += "package org.drools.test\n";
        header += "global java.util.List list\n\n";

        String rule1 = this.createCommonRule("rule1");
        String rule2 = this.createCommonRule("rule2");
        String rule3 = this.createCommonRule("rule3");

        String function1 = this.createDummyFunction("function1");

        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(function1);
        output.write(rule1);
        output.write(rule2);
        output.close();

        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule3);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/rule1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:9000/rule2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        final File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase);
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));

        list.clear();

        //Creates a new changeSet that will delete Rule1 from kbase
        ChangeSetImpl cs = new ChangeSetImpl();
        cs.setKnowledgeDefinitionsRemoved(new HashMap<Resource, String>() {

            {
                Resource r = ResourceFactory.newUrlResource("http://localhost:9000/rule1.drl");
                this.put(r, "rule1");
            }
        });

        kagent.applyChangeSet(cs);


        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));



        //Creates a new changeSet that will try to delete Rule3 from rule1.drl.
        //Because Rule3 is defined in rule2.drl, this shouldn't remove any rule.
        cs = new ChangeSetImpl();
        cs.setKnowledgeDefinitionsRemoved(new HashMap<Resource, String>() {

            {
                Resource r = ResourceFactory.newUrlResource("http://localhost:9000/rule1.drl");
                this.put(r, "rule3");
            }
        });

        kagent.applyChangeSet(cs);


        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));


        //Finally, remove Rule3
        cs = new ChangeSetImpl();
        cs.setKnowledgeDefinitionsRemoved(new HashMap<Resource, String>() {

            {
                Resource r = ResourceFactory.newUrlResource("http://localhost:9000/rule2.drl");
                this.put(r, "rule3");
            }
        });

        kagent.applyChangeSet(cs);


        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());

        assertTrue(list.contains("rule2"));

    }
    

    private KnowledgeAgent createKAgent(KnowledgeBase kbase){
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

        assertEquals("test agent", kagent.getName());

        return kagent;
    }

    private String createCommonRule(String ruleName) {
        StringBuilder sb = new StringBuilder();
        sb.append("rule ");
        sb.append(ruleName);
        sb.append("\n");
        sb.append("when\n");
        sb.append("then\n");
        sb.append("list.add( drools.getRule().getName() );\n");
        sb.append("end\n");

        return sb.toString();
    }

    private String createDummyFunction(String functionName) {
        StringBuilder sb = new StringBuilder();
        sb.append("function void  ");
        sb.append(functionName);
        sb.append("(){\n");
        sb.append(" System.out.println(\"Function executed\");\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static void writePackage(Object pkg, File p1file)
            throws IOException, FileNotFoundException {
        FileOutputStream out = new FileOutputStream(p1file);
        try {
            DroolsStreamUtils.streamOut(out, pkg);
        } finally {
            out.close();
        }
    }

    public static class ResultHandlerImpl implements ResultHandler {

        Object object;

        public void handleResult(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return this.object;
        }
    }
}
