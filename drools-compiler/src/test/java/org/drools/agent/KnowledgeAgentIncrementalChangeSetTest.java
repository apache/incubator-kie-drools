package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.util.DroolsStreamUtils;
import org.drools.util.FileManager;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentIncrementalChangeSetTest extends TestCase {

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
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
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

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule3\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testRemoveFileUrlIncremental() throws Exception {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
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

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        // Delete the file so only rule 2 fires
        f1.delete();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule2"));

        //Delete f2 now, no rules should fire
        list.clear();
        f2.delete();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(0, list.size());

        kagent.monitorResourceChangeEvents(false);
    }



    /**
     * Tests that if we have two DRL files, where one file overwrites a rule in
     * a prior file, that if we modify the first file that was overwritten, that
     * it will gain precedence and overwrite the other.
     *
     * @throws Exception
     */
    public void testModifyFileUrlOverwriteIncremental() throws Exception {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        String rule2 = "";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";

        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.write(rule2);
        output.close();

        String rule1v2 = "";
        rule1v2 += "package org.drools.test\n";
        rule1v2 += "global java.util.List list\n";
        rule1v2 += "rule rule1\n";
        rule1v2 += "when\n";
        rule1v2 += "then\n";
        rule1v2 += "list.add( drools.getRule().getName() + \"_v2\");\n";
        rule1v2 += "end\n";
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule1v2);
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

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1_v2"));
        assertTrue(list.contains("rule2"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        String rule1v3 = "";
        rule1v3 += "package org.drools.test\n";
        rule1v3 += "global java.util.List list\n";
        rule1v3 += "rule rule1\n";
        rule1v3 += "when\n";
        rule1v3 += "then\n";
        rule1v3 += "list.add( drools.getRule().getName() + \"_v3\" );\n";
        rule1v3 += "end\n";
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule1v3);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1_v3"));
        assertTrue(list.contains("rule2"));

        //Delete f2 now, rule1 should still fire if the indexing worked properly
        list.clear();
        f2.delete();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule2"));

        kagent.monitorResourceChangeEvents(false);
    }



    /**
     * Creates two rules (rule1 and rule2) in a drl file. Then it modifies the
     * drl file to change rule2 with rule3.
     * @throws Exception
     */
    public void testMultipleRulesOnFileUrlIncremental() throws Exception {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n\n";


        String rule2 = "";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName());\n";
        rule2 += "end\n";

        File f1 = fileManager.newFile("rules.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/rules.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

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

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        String rule3 = "";
        rule3 += "rule rule3\n";
        rule3 += "when\n";
        rule3 += "then\n";
        rule3 += "list.add( drools.getRule().getName());\n";
        rule3 += "end\n";
        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.write(rule3);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule3"));

        kagent.monitorResourceChangeEvents(false);
    }


    public void testMultipleRulesOnFilesUrlIncremental() throws Exception {
        String header = "";
        header += "package org.drools.test\n";
        header += "global java.util.List list\n\n";

        String rule1 = "";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n\n";


        String rule2 = "";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName());\n";
        rule2 += "end\n";

        String rule3 = "";
        rule3 += "rule rule3\n";
        rule3 += "when\n";
        rule3 += "then\n";
        rule3 += "list.add( drools.getRule().getName());\n";
        rule3 += "end\n";

        String rule4 = "";
        rule4 += "rule rule4\n";
        rule4 += "when\n";
        rule4 += "then\n";
        rule4 += "list.add( drools.getRule().getName());\n";
        rule4 += "end\n";

        String rule5 = "";
        rule5 += "rule rule5\n";
        rule5 += "when\n";
        rule5 += "then\n";
        rule5 += "list.add( drools.getRule().getName());\n";
        rule5 += "end\n";

        File f1 = fileManager.newFile("rules1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(rule1);
        output.write(rule2);
        output.close();

        File f2 = fileManager.newFile("rules2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule3);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/rules1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:9000/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

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


        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule4);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule4"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);


        output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(rule1);
        output.write(rule5);
        output.close();
        Thread.sleep(3000);


        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule5"));
        assertTrue(list.contains("rule4"));

        list.clear();

        kagent.monitorResourceChangeEvents(false);
    }


    public void testModifyPackageUrlIncremental() throws Exception {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";

        // Do only Rule1 in the first package
        File pkg1 = fileManager.newFile("pkg1.pkg");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg, pkg1);

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/pkg1.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        Writer output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        aconf.setProperty("drools.agent.newInstance", "false");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule3\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg, pkg1);

        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testUpdatePackageUrlIncremental() throws Exception {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";

        // Add Rule1 and Rule2 in the first package
        File pkg1 = fileManager.newFile("pkg1.pkg");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg, pkg1);

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/pkg1.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        Writer output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        aconf.setProperty("drools.agent.newInstance", "false");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        String rule3 = "";
        rule3 += "package org.drools.test\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule rule3\n";
        rule3 += "when\n";
        rule3 += "then\n";
        rule3 += "list.add( drools.getRule().getName() );\n";
        rule3 += "end\n";

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule3.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg, pkg1);

        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));
        kagent.monitorResourceChangeEvents(false);
    }


    public void testUpdatePackageUrlOverwriteIncremental() throws Exception {
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        String rule1v2 = "";
        rule1v2 += "package org.drools.test\n";
        rule1v2 += "global java.util.List list\n";
        rule1v2 += "rule rule1\n";
        rule1v2 += "when\n";
        rule1v2 += "then\n";
        rule1v2 += "list.add( drools.getRule().getName()+\"_V2\");\n";
        rule1v2 += "end\n";

        String rule2 = "";
        rule2 += "package org.drools.test\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule rule2\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "list.add( drools.getRule().getName() );\n";
        rule2 += "end\n";

        String rule3 = "";
        rule3 += "package org.drools.test\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule rule3\n";
        rule3 += "when\n";
        rule3 += "then\n";
        rule3 += "list.add( drools.getRule().getName() );\n";
        rule3 += "end\n";

        // Add Rule1 and Rule2 in the first package
        File pkgF1 = fileManager.newFile("pkg1.pkg");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        KnowledgePackage pkg1 = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg1, pkgF1);

        // Add Rule3 in the second package
        File pkgF2 = fileManager.newFile("pkg2.pkg");
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule3.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        KnowledgePackage pkg2 = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg2, pkgF2);

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/pkg1.pkg' type='PKG' />";
        xml += "        <resource source='http://localhost:9000/pkg2.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        Writer output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        aconf.setProperty("drools.agent.newInstance", "false");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

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

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1v2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        pkg2 = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg2, pkgF2);

        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule1_V2"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }


    public void completeTest() throws Exception {
        String header = "";
        header += "package org.drools.test\n";
        header += "global java.util.List list\n\n";

        String rule1 = this.createCommonRule("rule1");
        String rule1V2 = this.createCommonRule("rule1", "2");
        String rule1V3 = this.createCommonRule("rule1", "3");
        String rule2 = this.createCommonRule("rule2");
        String rule3 = this.createCommonRule("rule3");
        String rule3V2 = this.createCommonRule("rule3","2");
        String rule4 = this.createCommonRule("rule4");


        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header);
        output.write(rule1);
        output.write(rule2);
        output.close();

        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule3);
        output.close();

        File f3 = fileManager.newFile("rule3.drl");
        output = new BufferedWriter(new FileWriter(f3));
        output.write(header);
        output.write(rule1V2);
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

        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:9000/rule3.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());
        assertTrue(list.contains("rule1-V2"));
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));


        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule3);
        output.write(rule4);
        output.close();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(4, list.size());
        assertTrue(list.contains("rule1-V2"));
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule4"));


        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        //removes rule1 from rules3.drl and add a new definition for rule3 in that file
        output = new BufferedWriter(new FileWriter(f3));
        output.write(header);
        output.write(rule3V2);
        output.close();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(3, list.size());
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3-V2"));
        assertTrue(list.contains("rule4"));

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        //removes rule3 from rules3.drl
        output = new BufferedWriter(new FileWriter(f3));
        output.write(header);
        output.close();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule4"));

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        //removes rule3 from rules3.drl
        f3.delete();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule4"));


        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        //adds rules1-V3 definition to rules2.drl
        output = new BufferedWriter(new FileWriter(f2));
        output.write(header);
        output.write(rule1V3);
        output.write(rule3);
        output.write(rule4);
        output.close();
        Thread.sleep(3000);

        ksession = kbase.newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(4, list.size());
        assertTrue(list.contains("rule1-V3"));
        assertTrue(list.contains("rule2"));
        //rule3 reapears because rules2.drl was reporcessed
        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule4"));

        kagent.monitorResourceChangeEvents(false);

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


    private KnowledgeAgent createKAgent(KnowledgeBase kbase) {
        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        //System.setProperty(KnowledgeAgentFactory.PROVIDER_CLASS_NAME_PROPERTY_NAME, "org.drools.agent.impl.KnowledgeAgentProviderImpl");

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

    private String createCommonDSLRRule(String ruleName) {
        StringBuilder sb = new StringBuilder();
        sb.append("rule ");
        sb.append(ruleName);
        sb.append("\n");
        sb.append("when\n");
        sb.append("There is a String\n");
        sb.append("then\n");
        sb.append("add rule's name to list;\n");
        sb.append("end\n");

        return sb.toString();
    }

    private String createCommonRule(String ruleName, String version) {
        StringBuilder sb = new StringBuilder();
        sb.append("rule ");
        sb.append(ruleName);
        sb.append("\n");
        sb.append("when\n");
        sb.append("then\n");
        sb.append("list.add( drools.getRule().getName()+\"-V" + version + "\");\n");
        sb.append("end\n");

        return sb.toString();
    }

    private String createCommonDSL() {
        StringBuilder sb = new StringBuilder();
        sb.append("[condition][]There is a String = String()\n");
        sb.append("[consequence][]add rule's name to list = list.add( drools.getRule().getName() );\n");
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

    private String createCommonFunction(String functionName, String valueToAdd) {
        StringBuilder sb = new StringBuilder();
        sb.append("function void  ");
        sb.append(functionName);
        sb.append("(java.util.List myList,String source){\n");
        sb.append(" myList.add(\"");
        sb.append(valueToAdd);
        sb.append(" from \"+source);\n");
        sb.append("}\n");

        return sb.toString();
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
