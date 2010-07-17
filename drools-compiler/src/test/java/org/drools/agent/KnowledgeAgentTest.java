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
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.FileManager;
import org.drools.definition.KnowledgePackage;
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
import org.drools.runtime.StatelessKnowledgeSession;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentTest extends TestCase {

    FileManager fileManager;
    private Server server;
    private final Object lock = new Object();
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

    public void testModifyFileUrl() throws Exception {
        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");

        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule2.drl' type='DRL' />";
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
        aconf.setProperty("drools.agent.newInstance", "true");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

        assertEquals("test agent", kagent.getName());

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
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

        rule1 = this.createDefaultRule("rule3");

        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();
        Thread.sleep(3000);

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    /**
     * Tests that if we change a ChangeSet that is referenced by another change
     * set or added by another ChangeSet, that the changes are picked up.
     *
     * @throws Exception
     *             If an unexpected exception occurs.
     */
    public void testChangeSetInChangeSet() throws Exception {
        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml1 = "";
        xml1 += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml1 += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml1 += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml1 += "    <add> ";
        xml1 += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml1 += "        <resource source='http://localhost:" + this.getPort() + "/rule2.drl' type='DRL' />";
        xml1 += "    </add> ";
        xml1 += "</change-set>";
        File fxml1 = fileManager.newFile("changeset2.xml");
        output = new BufferedWriter(new FileWriter(fxml1));
        output.write(xml1);
        output.close();

        String xml2 = "";
        xml2 += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml2 += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml2 += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml2 += "    <add> ";
        xml2 += "        <resource source='http://localhost:" + this.getPort() + "/changeset2.xml' type='CHANGE_SET' />";
        xml2 += "    </add> ";
        xml2 += "</change-set>";
        File fxml2 = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml2));
        output.write(xml2);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml2.toURI().toURL()));
        kbaseUpdated = false;

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
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

        rule1 = this.createDefaultRule("rule3");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        this.waitUntilKBaseUpdate();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));

        kagent.monitorResourceChangeEvents(false);
    }

    public void testModifyFileUrlWithStateless() throws Exception {
        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rule2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;

        StatelessKnowledgeSession ksession = kagent.newStatelessKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.execute("hello");

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        rule1 = this.createDefaultRule("rule3");
        System.out.println("root : " + f1.getPath());
        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        this.waitUntilKBaseUpdate();

        ksession.execute("hello");

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testModifyPackageUrl() throws Exception {
        String rule1 = this.createDefaultRule("rule1");

        String rule2 = this.createDefaultRule("rule2");

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg, fileManager.newFile("pkg1.pkg"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg1.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        Writer output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
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

        rule1 = this.createDefaultRule("rule3");

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage(pkg, fileManager.newFile("pkg1.pkg"));

        this.waitUntilKBaseUpdate();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testDeletePackageUrl() throws Exception {
        String rule1 = this.createDefaultRule("rule1","org.drools.test1");

        String rule2 = this.createDefaultRule("rule2","org.drools.test2");

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        Map<String, KnowledgePackage> map = new HashMap<String, KnowledgePackage>();
        for (KnowledgePackage pkg : kbuilder.getKnowledgePackages()) {
            map.put(pkg.getName(), pkg);
        }
        writePackage((KnowledgePackage) map.get("org.drools.test1"),
                fileManager.newFile("pkg1.pkg"));
        writePackage((KnowledgePackage) map.get("org.drools.test2"),
                fileManager.newFile("pkg2.pkg"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg1.pkg' type='PKG' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg2.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml.getBytes()));
        kbaseUpdated = false;

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <remove> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkg2.pkg' type='PKG' />";
        xml += "    </remove> ";
        xml += "</change-set>";

        kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml.getBytes()));
        kbaseUpdated = false;

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());

        assertTrue(list.contains("rule1"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testOldSchoolPackageUrl() throws Exception {
        String rule1 = this.createDefaultRule("rule1");

        String rule2 = this.createDefaultRule("rule2");

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
                ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        KnowledgeBuilderImpl kbi = (KnowledgeBuilderImpl) kbuilder;

        // KnowledgePackage pkg = ( KnowledgePackage )
        // kbuilder.getKnowledgePackages().iterator().next();
        writePackage(kbi.getPackageBuilder().getPackage(), fileManager.newFile("pkgold.pkg"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/pkgold.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        Writer output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;


        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

    }

    public void testModifyFile() throws IOException, InterruptedException {
        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='" + f1.toURI().toURL()
                + "' type='DRL' />";
        xml += "        <resource source='" + f2.toURI().toURL()
                + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;


        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
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

        rule1 = this.createDefaultRule("rule3");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        this.waitUntilKBaseUpdate();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testModifyDirectory() throws IOException, InterruptedException {
        // adds 2 files to a dir and executes then adds one and removes one and
        // detects changes
        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile("rule1.drl");

        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='"
                + f1.getParentFile().toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File newDir = fileManager.newFile("changeset");
        newDir.mkdir();
        File fxml = fileManager.newFile(newDir, "changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        // KnowledgeBuilder kbuilder =
        // KnowledgeBuilderFactory.newKnowledgeBuilder();
        // kbuilder.add( ResourceFactory.newUrlResource( fxml.toURI().toURL() ),
        // ResourceType.ChangeSet );
        // assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        // kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));

        list.clear();

        Thread.sleep(2000); // give it 2 seconds to detect and build the changes
        String rule3 = this.createDefaultRule("rule3");
        File f3 = fileManager.newFile("rule3.drl");
        output = new BufferedWriter(new FileWriter(f3));
        output.write(rule3);
        output.close();

        assertTrue(f1.delete());

        this.waitUntilKBaseUpdate();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());
        assertTrue(list.contains("rule2"));
        assertTrue(list.contains("rule3"));

        kagent.monitorResourceChangeEvents(false);
    }

    public void testModifyFileInDirectory() throws Exception {
        // Create the test directory
        File testDirectory = fileManager.newFile("test");
        testDirectory.mkdir();

        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile(testDirectory, "rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");
        File f2 = fileManager.newFile(testDirectory, "rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='file:"
                + fileManager.getRootDirectory().getAbsolutePath()
                + "/test' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;


        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
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

        rule1 = this.createDefaultRule("rule3");
        output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        this.waitUntilKBaseUpdate();

        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, list.size());

        assertTrue(list.contains("rule3"));
        assertTrue(list.contains("rule2"));
        kagent.monitorResourceChangeEvents(false);
    }

    public void testStatelessWithCommands() throws Exception {
        String rule1 = this.createDefaultRule("rule1");
        File f1 = fileManager.newFile("rule1.drl");

        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        String rule2 = this.createDefaultRule("rule2");
        File f2 = fileManager.newFile("rule2.drl");
        output = new BufferedWriter(new FileWriter(f2));
        output.write(rule2);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='"
                + f1.getParentFile().toURI().toURL() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File newDir = fileManager.newFile("changeset");
        newDir.mkdir();
        File fxml = fileManager.newFile(newDir, "changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        kbaseUpdated = false;


        StatelessKnowledgeSession ksession = kagent.newStatelessKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.execute( new InsertObjectCommand("hello") );

        assertEquals(2, list.size());
        assertTrue(list.contains("rule1"));
        assertTrue(list.contains("rule2"));
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

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        // Testing incremental build here
        aconf.setProperty("drools.agent.newInstance", "true");

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

    private String createDefaultRule(String name){
        return this.createDefaultRule(name, null);
    }

    private String createDefaultRule(String name, String packageName){
        StringBuilder rule = new StringBuilder();
        if (packageName == null){
            rule.append("package org.drools.test\n");
        }else{
            rule.append("package ");
            rule.append(packageName);
            rule.append("\n");
        }
        rule.append("global java.util.List list\n");
        rule.append("rule ");
        rule.append(name);
        rule.append("\n");
        rule.append("when\n");
        rule.append("then\n");
        rule.append("list.add( drools.getRule().getName() );\n");
        rule.append("end\n");

        return rule.toString();
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
