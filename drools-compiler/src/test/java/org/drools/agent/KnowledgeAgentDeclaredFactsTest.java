package org.drools.agent;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.ScanDirectoriesOption;
import org.drools.agent.conf.ScanResourcesOption;
import org.drools.definition.type.FactType;
import org.drools.event.knowledgeagent.*;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.*;

import java.io.File;
import java.io.FileWriter;

/**
 * JBRULES - 2962  / JBRULES - 3033
 */
public class KnowledgeAgentDeclaredFactsTest  {

    private static Logger LOG = Logger.getLogger(KnowledgeAgentDeclaredFactsTest.class);

    private boolean kbaseUpdated;
    private KnowledgeAgent kagent;
    private KnowledgeBase kbase;
    private String xml;

    @BeforeClass
    public static void startServices() {

    }

    @AfterClass
    public static void stopServices() {
    }

    @Before
    public void setUp() throws Exception {
        kbaseUpdated = false;
        createRuleResource();


        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='file:rule.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        kbase = KnowledgeBaseFactory.newKnowledgeBase();

        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();
        Thread.sleep( 2000 );
        System.out.println( "Started notifiers" );
    }


    @After
    public void tearDown() throws Exception {
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        Thread.sleep( 2000 );
        System.out.println( "Stopped notifiers" );

        kagent.dispose();
        removeRuleResource();
    }

    @Test
    public void testStatefulSessionNewInstance() throws Exception {
        System.out.println("************ Running Stateful Session New Instance");
        runAgent(true, true);
    }

    @Test
    public void testStatefulSessionSameInstance() throws Exception {
        System.out.println("************ Running Stateful Session Same Instance");
        runAgent(true, false);
    }

    @Test
    public void testStatelessSessionNewInstance() throws Exception {
        System.out.println("************ Running Stateless Session New Instance");
        runAgent(false, true);
    }

    @Test
    public void testStatelessSessionSameInstance() throws Exception {
        System.out.println("************ Running Stateless Session Same Instance");
        runAgent(false, false);
    }


    private void runAgent(boolean stateful, boolean newInstance) throws Exception {
        String result;

        kagent = createKAgent(kbase, newInstance);
        kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml.getBytes()));

        result = insertMessageAndFire("test1", stateful);
        Assert.assertEquals("Echo:test1", result);

        modifyRuleResource();
        kbaseUpdated = false;
        waitUntilKBaseUpdate();

        result = insertMessageAndFire("test2", stateful);
        Assert.assertEquals("Echo:test2", result);
    }

    private String insertMessageAndFire(String message, boolean stateful) throws IllegalAccessException, InstantiationException {
        System.out.println("********** Firing rules");

        String result = null;

        FactType testFactType = kagent.getKnowledgeBase().getFactType("test", "TestFact");
        Object fact = testFactType.newInstance();
        testFactType.set(fact, "message", message);

        if (stateful) {
            StatefulKnowledgeSession session = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
            session.insert(fact);
            session.fireAllRules();
            result = (String) testFactType.get(fact, "message");
            session.dispose();
        } else {
            StatelessKnowledgeSession session = kagent.getKnowledgeBase().newStatelessKnowledgeSession();
            session.execute(fact);
            result = (String) testFactType.get(fact, "message");
        }

        return result;
    }

    private void createRuleResource() {
        String ruleString = "package test; \n" +
                "declare TestFact \n" +
                "  message : String \n" +
                "end \n" +
                "rule test1 \n" +
                "  when \n" +
                "    $m : TestFact( message == \"test1\") \n" +
                "  then \n" +
                "    System.out.println(\"********** FOUND \" + $m.getMessage()); \n" +
                "    $m.setMessage(\"Echo:\" + $m.getMessage()); \n" +
                "end \n";
        try {
            FileWriter fw = new FileWriter("rule.drl");
            fw.write(ruleString);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyRuleResource() {
        String ruleString = "rule test2 \n" +
                "  when \n" +
                "    $m : TestFact( message == \"test2\") \n" +
                "  then \n" +
                "    System.out.println(\"********** FOUND \" + $m.getMessage()); \n" +
                "    $m.setMessage(\"Echo:\" + $m.getMessage()); \n" +
                "end \n";

        try {
            FileWriter fw = new FileWriter("rule.drl", true);
            fw.write(ruleString);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRuleResource() {
        File file = new File("rule.drl");
        file.delete();
    }

    private KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance) {
        ResourceChangeScannerConfiguration sconf = ResourceFactory
                .getResourceChangeScannerService()
                .newResourceChangeScannerConfiguration();
        sconf.setProperty( "drools.resource.scanner.interval", "1" );
        ResourceFactory.getResourceChangeScannerService().configure( sconf );

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory
                .newKnowledgeAgentConfiguration();
        aconf.setProperty( ScanDirectoriesOption.PROPERTY_NAME, "" + ScanDirectoriesOption.YES.isScanDirectories() );
        aconf.setProperty( ScanResourcesOption.PROPERTY_NAME, "" + ScanResourcesOption.YES.isScanResources() );
        // Testing incremental build here
        aconf.setProperty(NewInstanceOption.PROPERTY_NAME, "" + newInstance );

        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);
//        kagent.setSystemEventListener( new PrintStreamSystemEventListener() );
        kagent.monitorResourceChangeEvents( true );

        return kagent;
    }

    private void waitUntilKBaseUpdate() {
        int count = 0;
        while (!kbaseUpdated && count < 10) {
            try {
                Thread.sleep(1000);
                count++;
            } catch (Exception e) {
                // ignore
            }
        }
        kbaseUpdated = false;
    }
}