package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class KnowledgeAgentDisposeTest extends BaseKnowledgeAgentTest {

    private int resourceChangeNotificationCount = 0;

    public void testMonitorResourceChangeEvents() throws Exception {
        //create a basic dsl file
        this.fileManager.write("myExpander.dsl", this.createCommonDSL(null));

        //create a basic dslr file
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.dslr' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        File fxml = fileManager.write( "changeset.xml",
                                       xml );
        
        //Create a new Agent with newInstace=true
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        resourceChangeNotificationCount = 0;
        
        //the dsl is now modified.
        this.fileManager.write("myExpander.dsl", this.createCommonDSL("name == \"John\""));

        //the drl file is marked as modified too
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        this.scan(kagent);

        //two resources were modified, but only one change set is created
        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;

        //let's add a new rule
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule(new String[]{"Rule1","Rule2"}));

        this.scan(kagent);

        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;

        //the kagent is stopped
        kagent.monitorResourceChangeEvents(false);

        //the dsrl file is marked as modified
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        try{
            this.scan(kagent);
            fail("The agent didn't process any change set. This should be failed.");
        }catch (RuntimeException e){
            assertEquals(e.getMessage(), "Event for KnowlegeBase update, due to scan, was never received");
        }
        assertEquals(0, resourceChangeNotificationCount);
        
        //let start the agent again
        kagent.monitorResourceChangeEvents(true);

        //the dsrl file is marked as modified
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        this.scan(kagent);

        assertEquals(1, resourceChangeNotificationCount);

        kagent.monitorResourceChangeEvents(false);
    }


    public void testDispose() throws Exception {

        //create a basic dsl file
        this.fileManager.write("myExpander.dsl", this.createCommonDSL(null));

        //create a basic dslr file
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.dslr' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        //Create a new Agent with newInstace=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        //Agent: take care of them!
        this.applyChangeSet(kagent,ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        resourceChangeNotificationCount = 0;

        //the dsl is now modified.
        this.fileManager.write("myExpander.dsl", this.createCommonDSL("name == \"John\""));

        //We also need to mark the dslr file as modified, so the rules could
        //be regenerated
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        this.scan(kagent);
        
        //two resources were modified, but only one change set is created
        assertEquals(1, resourceChangeNotificationCount);
        resourceChangeNotificationCount = 0;

        //let us create a new ksession and fire all the rules
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", new ArrayList<String>());
        ksession.fireAllRules();


        //let's add a new rule
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule(new String[]{"Rule1","Rule2"}));

        this.scan(kagent);

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

        //the dsrl file is modified
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        try{
            this.scan(kagent);
            fail("The agent didn't process any change set. This should be failed.");
        }catch (RuntimeException e){
            assertEquals(e.getMessage(), "Event for KnowlegeBase update, due to scan, was never received");
        }
        assertEquals(0, resourceChangeNotificationCount);


    }


    @Override
    public KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance) {
        KnowledgeAgent kagent = super.createKAgent(kbase, newInstance);

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

        return kagent;
    }
}
