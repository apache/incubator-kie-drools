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
import org.drools.agent.KnowledgeAgentTest.ResultHandlerImpl;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.FileManager;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.PipelineFactory;
import org.drools.runtime.pipeline.ResultHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentTest extends TestCase {
	FileManager fileManager;

	private Server server;

	protected void setUp() throws Exception {
		fileManager = new FileManager();
		fileManager.setUp();
		((ResourceChangeScannerImpl) ResourceFactory
				.getResourceChangeScannerService()).reset();
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();

		this.server = new Server(0);
                
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(fileManager.getRootDirectory()
				.getPath());
		System.out
				.println("root : " + fileManager.getRootDirectory().getPath());

		server.setHandler(resourceHandler);

		server.start();
                System.out.println("Server running on port "+this.getPort());
	}

        private int getPort(){
            return this.server.getConnectors()[0].getLocalPort();
        }

	protected void tearDown() throws Exception {
		fileManager.tearDown();
		ResourceFactory.getResourceChangeNotifierService().stop();
		ResourceFactory.getResourceChangeScannerService().stop();
		((ResourceChangeNotifierImpl) ResourceFactory
				.getResourceChangeNotifierService()).reset();
		((ResourceChangeScannerImpl) ResourceFactory
				.getResourceChangeScannerService()).reset();

		server.stop();
	}

	public void testModifyFileUrl() throws Exception {
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
		xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
		xml += "        <resource source='http://localhost:"+this.getPort()+"/rule2.drl' type='DRL' />";
		xml += "    </add> ";
		xml += "</change-set>";
		File fxml = fileManager.newFile("changeset.xml");
		output = new BufferedWriter(new FileWriter(fxml));
		output.write(xml);
		output.close();

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		assertEquals("test agent", kagent.getName());

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
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

		String xml1 = "";
		xml1 += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
		xml1 += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
		xml1 += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
		xml1 += "    <add> ";
		xml1 += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
		xml1 += "        <resource source='http://localhost:"+this.getPort()+"/rule2.drl' type='DRL' />";
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
		xml2 += "        <resource source='http://localhost:"+this.getPort()+"/changeset2.xml' type='CHANGE_SET' />";
		xml2 += "    </add> ";
		xml2 += "</change-set>";
		File fxml2 = fileManager.newFile("changeset.xml");
		output = new BufferedWriter(new FileWriter(fxml2));
		output.write(xml2);
		output.close();

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		assertEquals("test agent", kagent.getName());

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml2.toURI()
				.toURL()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
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
		xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
		xml += "        <resource source='http://localhost:"+this.getPort()+"/rule2.drl' type='DRL' />";
		xml += "    </add> ";
		xml += "</change-set>";
		File fxml = fileManager.newFile("changeset.xml");
		output = new BufferedWriter(new FileWriter(fxml));
		output.write(xml);
		output.close();

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		StatelessKnowledgeSession ksession = kagent
				.newStatelessKnowledgeSession();
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

		rule1 = "";
		rule1 += "package org.drools.test\n";
		rule1 += "global java.util.List list\n";
		rule1 += "rule rule3\n";
		rule1 += "when\n";
		rule1 += "then\n";
		rule1 += "list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";
		System.out.println("root : " + f1.getPath());
		output = new BufferedWriter(new FileWriter(f1));
		output.write(rule1);
		output.close();
		Thread.sleep(3000);

		ksession.execute("hello");

		assertEquals(2, list.size());

		assertTrue(list.contains("rule3"));
		assertTrue(list.contains("rule2"));
		kagent.monitorResourceChangeEvents(false);
	}

	public void testModifyPackageUrl() throws Exception {
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

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
				ResourceType.DRL);
		kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
				ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString());
		}
		KnowledgePackage pkg = (KnowledgePackage) kbuilder
				.getKnowledgePackages().iterator().next();
		writePackage(pkg, fileManager.newFile("pkg1.pkg"));

		String xml = "";
		xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
		xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
		xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
		xml += "    <add> ";
		xml += "        <resource source='http://localhost:"+this.getPort()+"/pkg1.pkg' type='PKG' />";
		xml += "    </add> ";
		xml += "</change-set>";
		File fxml = fileManager.newFile("changeset.xml");
		Writer output = new BufferedWriter(new FileWriter(fxml));
		output.write(xml);
		output.close();

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
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

		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes()),
				ResourceType.DRL);
		kbuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes()),
				ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			fail(kbuilder.getErrors().toString());
		}
		pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator()
				.next();
		writePackage(pkg, fileManager.newFile("pkg1.pkg"));

		// KnowledgePackage pkg2 = ( KnowledgePackage )
		// DroolsStreamUtils.streamIn( new FileInputStream( fileManager.newFile(
		// "pkg1.pkg" ) ) );

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

	public void testDeletePackageUrl() throws Exception {
		String rule1 = "";
		rule1 += "package org.drools.test1\n";
		rule1 += "global java.util.List list\n";
		rule1 += "rule rule1\n";
		rule1 += "when\n";
		rule1 += "then\n";
		rule1 += "list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";

		String rule2 = "";
		rule2 += "package org.drools.test2\n";
		rule2 += "global java.util.List list\n";
		rule2 += "rule rule2\n";
		rule2 += "when\n";
		rule2 += "then\n";
		rule2 += "list.add( drools.getRule().getName() );\n";
		rule2 += "end\n";

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
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
		xml += "        <resource source='http://localhost:"+this.getPort()+"/pkg1.pkg' type='PKG' />";
		xml += "        <resource source='http://localhost:"+this.getPort()+"/pkg2.pkg' type='PKG' />";
		xml += "    </add> ";
		xml += "</change-set>";

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml
				.getBytes()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
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

		xml = "";
		xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
		xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
		xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
		xml += "    <remove> ";
		xml += "        <resource source='http://localhost:"+this.getPort()+"/pkg2.pkg' type='PKG' />";
		xml += "    </remove> ";
		xml += "</change-set>";

		kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml
				.getBytes()));

		Thread.sleep(3000);

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

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
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
		writePackage(kbi.getPackageBuilder().getPackage(), fileManager
				.newFile("pkgold.pkg"));

		String xml = "";
		xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
		xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
		xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' >";
		xml += "    <add> ";
		xml += "        <resource source='http://localhost:"+this.getPort()+"/pkgold.pkg' type='PKG' />";
		xml += "    </add> ";
		xml += "</change-set>";
		File fxml = fileManager.newFile("changeset.xml");
		Writer output = new BufferedWriter(new FileWriter(fxml));
		output.write(xml);
		output.close();

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);
		ksession.fireAllRules();
		ksession.dispose();

		assertEquals(2, list.size());
		assertTrue(list.contains("rule1"));
		assertTrue(list.contains("rule2"));

	}

	public void testModifyFile() throws IOException, InterruptedException {
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

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
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

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.newInstance", "true");

		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);
		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		Thread.sleep(3000); // give it 2 seconds to detect and build the changes
		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);
		ksession.fireAllRules();
		ksession.dispose();

		assertEquals(2, list.size());
		assertTrue(list.contains("rule1"));
		assertTrue(list.contains("rule2"));

		list.clear();
		String rule3 = "";
		rule3 += "package org.drools.test\n";
		rule3 += "global java.util.List list\n";
		rule3 += "rule rule3\n";
		rule3 += "when\n";
		rule3 += "then\n";
		rule3 += "list.add( drools.getRule().getName() );\n";
		rule3 += "end\n";
		File f3 = fileManager.newFile("rule3.drl");
		output = new BufferedWriter(new FileWriter(f3));
		output.write(rule3);
		output.close();

		assertTrue(f1.delete());

		Thread.sleep(3000);

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

		String rule1 = "";
		rule1 += "package org.drools.test\n";
		rule1 += "global java.util.List list\n";
		rule1 += "rule rule1\n";
		rule1 += "when\n";
		rule1 += "then\n";
		rule1 += "list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";
		File f1 = fileManager.newFile(testDirectory, "rule1.drl");
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

		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval", "2");
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.scanResources", "true");
		aconf.setProperty("drools.agent.newInstance", "true");
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", kbase, aconf);

		assertEquals("test agent", kagent.getName());

		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		StatefulKnowledgeSession ksession = kagent.getKnowledgeBase()
				.newStatefulKnowledgeSession();
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

	public void testStatelessWithPipeline() throws Exception {
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

		KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		aconf.setProperty("drools.agent.scanDirectories", "true");
		aconf.setProperty("drools.agent.newInstance", "true");

		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
				"test agent", aconf);
		kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI()
				.toURL()));

		Thread.sleep(3000); // give it 2 seconds to detect and build the changes
		StatelessKnowledgeSession ksession = kagent
				.newStatelessKnowledgeSession();

		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);

		Action executeResultHandler = PipelineFactory.newExecuteResultHandler();

		Action assignResult = PipelineFactory.newAssignObjectAsResult();
		assignResult.setReceiver(executeResultHandler);

		KnowledgeRuntimeCommand batchExecution = PipelineFactory
				.newCommandExecutor();
		batchExecution.setReceiver(assignResult);

		KnowledgeRuntimeCommand insertStage = PipelineFactory
				.newInsertObjectCommand();
		insertStage.setReceiver(batchExecution);

		Pipeline pipeline = PipelineFactory
				.newStatelessKnowledgeSessionPipeline(ksession);
		pipeline.setReceiver(insertStage);

		ResultHandlerImpl resultHandler = new ResultHandlerImpl();
		pipeline.insert("hello", resultHandler);

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