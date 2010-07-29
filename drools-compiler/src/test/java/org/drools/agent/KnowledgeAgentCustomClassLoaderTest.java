package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListener;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.core.util.FileManager;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentCustomClassLoaderTest extends TestCase {

    FileManager fileManager;
    private Server server;
    private final List<String> kagentWarnings = new ArrayList<String>();

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
    }

    private int getPort(){
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

    public void testCustomKnowledgeBuilderConfigurationWithIncrementalProcessing() throws Exception{
        this.testCustomKnowledgeBuilderConfiguration(false);
    }

    public void testCustomKnowledgeBuilderConfigurationWithoutIncrementalProcessing() throws Exception{
        this.testCustomKnowledgeBuilderConfiguration(true);
    }

    public void testUseKBaseClassLoaderForCompilingPropertyWithIncrementalProcessing() throws Exception{
        this.testUseKBaseClassLoaderForCompilingProperty(false);
    }

    public void testUseKBaseClassLoaderForCompilingPropertyWithoutIncrementalProcessing() throws Exception{
        this.testUseKBaseClassLoaderForCompilingProperty(true);
    }

    private void testCustomKnowledgeBuilderConfiguration(boolean newInstance) throws Exception {

        //A simple rule using a class (org.drools.agent.test.KnowledgeAgentInstance)
        //that is not present in the classloader.
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "import org.drools.agent.test.KnowledgeAgentInstance\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "  KnowledgeAgentInstance($id: instanceId)\n";
        rule1 += "then\n";
        rule1 += "  list.add(\"Instance number \"+$id);\n";
        rule1 += "end\n";
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        //The change set to process the created resource
        String xml = "";
        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        //We are going to add KAModelTest.jar to the kbase classloader.
        //This kbase will be the one used by the agent.
        URL jarURL = this.getClass().getResource("/KAModelTest.jar");
        URLClassLoader ucl = new URLClassLoader(new URL[]{jarURL}, this.getClass().getClassLoader());

        //Add the classloader to the kbase
        KnowledgeBaseConfiguration kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, ucl);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);

        //Create a kagent with the kbase
        KnowledgeAgent kagent = this.createKAgent(kbase,newInstance,false);

        //The agent processes the changeset (Because the compiler created internally
        //by the agent doesn't have a definition for KnowledgeAgentInstance,
        //the compilation will fail.
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        //Because the compilation failed, no package was created.
        assertTrue(kagent.getKnowledgeBase().getKnowledgePackages().isEmpty());

        //There should be more than 1 warning
        assertTrue(kagentWarnings.size() >= 1);
        //One of the warnings should be "KnowledgeAgent has KnowledgeBuilder errors "
        assertTrue(kagentWarnings.contains("KnowledgeAgent has KnowledgeBuilder errors "));

        kagentWarnings.clear();

        //Stop monitoring resources
        kagent.monitorResourceChangeEvents(false);

        //Now we create a new kagent, but passing a custom KnowledgeBuilderConfiguration
        //having the correct classloader
        KnowledgeBuilderConfiguration kbuilderConfig =
                KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, ucl);
        kagent = this.createKAgent(kbase, newInstance, false, kbuilderConfig);

        //The agent processes the changeset. The rule should be compiled
        //succesfully
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        //One package should be created
        assertEquals(1, kagent.getKnowledgeBase().getKnowledgePackages().size());

        //We create a session to test if the rule runs ok
        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        //Create a new KnowledgeAgentInstance and set its instanceId = 2
        Class<?> modelClass = ucl.loadClass("org.drools.agent.test.KnowledgeAgentInstance");
        Object modelInstance = modelClass.newInstance();
        modelClass.getMethod("setInstanceId", int.class).invoke(modelInstance, 2);

        //insert the KnowledgeAgentInstance
        ksession.insert(modelInstance);

        //fire all rules
        ksession.fireAllRules();
        ksession.dispose();

        //The global list should contain 1 element
        assertEquals(1, list.size());
        assertTrue(list.contains("Instance number 2"));
        kagent.monitorResourceChangeEvents(false);
    }


    private void testUseKBaseClassLoaderForCompilingProperty(boolean newInstance) throws Exception {

        //A simple rule using a class (org.drools.agent.test.KnowledgeAgentInstance)
        //that is not present in the classloader.
        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "import org.drools.agent.test.KnowledgeAgentInstance\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "  KnowledgeAgentInstance($id: instanceId)\n";
        rule1 += "then\n";
        rule1 += "  list.add(\"Instance number \"+$id);\n";
        rule1 += "end\n";
        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(rule1);
        output.close();

        //The change set to process the created resource
        String xml = "";
        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        //We are going to add KAModelTest.jar to the kbase classloader.
        //This kbase will be the one used by the agent.
        URL jarURL = this.getClass().getResource("/KAModelTest.jar");
        URLClassLoader ucl = new URLClassLoader(new URL[]{jarURL}, this.getClass().getClassLoader());

        //Add the classloader to the kbase
        KnowledgeBaseConfiguration kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, ucl);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);

        //Create a kagent with the kbase
        KnowledgeAgent kagent = this.createKAgent(kbase,newInstance,false);

        //The agent processes the changeset (Because the compiler created internally
        //by the agent doesn't have a definition for KnowledgeAgentInstance,
        //the compilation will fail.
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        //Because the compilation failed, no package was created.
        assertTrue(kagent.getKnowledgeBase().getKnowledgePackages().isEmpty());

        //There should be more than 1 warning
        assertTrue(kagentWarnings.size() >= 1);
        //One of the warnings should be "KnowledgeAgent has KnowledgeBuilder errors "
        assertTrue(kagentWarnings.contains("KnowledgeAgent has KnowledgeBuilder errors "));

        kagentWarnings.clear();

        //Stop monitoring resources
        kagent.monitorResourceChangeEvents(false);

        //Create a kagent with the kbase and using useKBaseClassLoaderForCompiling.
        //The kbase's classloader should be used for compilation too
        KnowledgeBuilderConfiguration kbuilderConfig =
                KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, ucl);
        kagent = this.createKAgent(kbase, newInstance, true, kbuilderConfig);

        //The agent processes the changeset. The rule should be compiled
        //succesfully
        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        //One package should be created
        assertEquals(1, kagent.getKnowledgeBase().getKnowledgePackages().size());

        //We create a session to test if the rule runs ok
        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        //Create a new KnowledgeAgentInstance and set its instanceId = 2
        Class<?> modelClass = ucl.loadClass("org.drools.agent.test.KnowledgeAgentInstance");
        Object modelInstance = modelClass.newInstance();
        modelClass.getMethod("setInstanceId", int.class).invoke(modelInstance, 2);

        //insert the KnowledgeAgentInstance
        ksession.insert(modelInstance);

        //fire all rules
        ksession.fireAllRules();
        ksession.dispose();

        //The global list should contain 1 element
        assertEquals(1, list.size());
        assertTrue(list.contains("Instance number 2"));
        kagent.monitorResourceChangeEvents(false);
    }

    private KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance, boolean useKBaseClassLoaderForCompiling) {
        return this.createKAgent(kbase, newInstance, useKBaseClassLoaderForCompiling, null);
    }

    private KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance, boolean useKBaseClassLoaderForCompiling, KnowledgeBuilderConfiguration builderConf) {
        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        //System.setProperty(KnowledgeAgentFactory.PROVIDER_CLASS_NAME_PROPERTY_NAME, "org.drools.agent.impl.KnowledgeAgentProviderImpl");

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        aconf.setProperty("drools.agent.newInstance", ""+newInstance);
        aconf.setProperty("drools.agent.useKBaseClassLoaderForCompiling", ""+useKBaseClassLoaderForCompiling);


        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf, builderConf);

        kagent.setSystemEventListener(new SystemEventListener() {

            public void info(String message) {
            }

            public void info(String message, Object object) {
            }

            public void warning(String message) {
                kagentWarnings.add(message);
            }

            public void warning(String message, Object object) {
                kagentWarnings.add(message);
            }

            public void exception(String message, Throwable e) {
            }

            public void exception(Throwable e) {
            }

            public void debug(String message) {
            }

            public void debug(String message, Object object) {
            }
        });

        assertEquals("test agent", kagent.getName());

        return kagent;
    }
}
