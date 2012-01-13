package org.drools.agent;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class KnowledgeAgentCustomClassLoaderTest extends BaseKnowledgeAgentTest {

    
    @Test
    public void testCustomKnowledgeBuilderConfigurationWithIncrementalProcessing() throws Exception{
        this.testCustomKnowledgeBuilderConfiguration(false);
    }

    @Test
    public void testCustomKnowledgeBuilderConfigurationWithoutIncrementalProcessing() throws Exception{
        this.testCustomKnowledgeBuilderConfiguration(true);
    }

    @Test
    public void testUseKBaseClassLoaderForCompilingPropertyWithIncrementalProcessing() throws Exception{
        this.testUseKBaseClassLoaderForCompilingProperty(false);
    }

    @Test
    public void testUseKBaseClassLoaderForCompilingPropertyWithoutIncrementalProcessing() throws Exception{
        this.testUseKBaseClassLoaderForCompilingProperty(true);
    }

    private void testCustomKnowledgeBuilderConfiguration(boolean newInstance) throws Exception {
        this.testKagentWithCustomClassLoader(newInstance, false);
    }

    private void testUseKBaseClassLoaderForCompilingProperty(boolean newInstance) throws Exception {
        this.testKagentWithCustomClassLoader(newInstance, true);
    }

    /**
     * 
     * @param newInstance
     * @param useKBaseClassLoaderForCompiling if true, the kagent will use
     * the kbase's class loader to compile the rules. If false, it will
     * create a new KnowledgeBuilderConfiguration with the kbase's cl and pass 
     * it to the agent.
     * @throws Exception 
     */
    private void testKagentWithCustomClassLoader(boolean newInstance, boolean useKBaseClassLoaderForCompiling) throws Exception {

        //A simple rule using a class (org.drools.agent.test.KnowledgeAgentInstance)
        //that is not present in the classloader.
        String rule = this.createCustomRule(true, "org.drools.test", new String[]{"org.drools.agent.test.KnowledgeAgentInstance"}, new String[]{"rule1"}, null, "   KnowledgeAgentInstance($id: instanceId)\n","  list.add(\"Instance number \"+$id);\n");
        this.fileManager.write("rule1.drl", rule);

        //The change set to process the created resource
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

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
        try{
            applyChangeSet(kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()));
            fail( "Knowledge should fail to compile" );
        }catch (Exception e){
            assertTrue(e.getMessage().contains("Unable to compile Knowledge"));
            
        }
        //Because the compilation failed, no package was created.
        assertTrue(kagent.getKnowledgeBase().getKnowledgePackages().isEmpty());


        //Stop monitoring resources
        kagent.dispose();

        if (useKBaseClassLoaderForCompiling){
            //Create a kagent with the kbase and using useKBaseClassLoaderForCompiling.
            //The kbase's classloader should be used for compilation too
            kagent = this.createKAgent(kbase, newInstance, true);
        }else{
            //Now we create a new kagent, but passing a custom KnowledgeBuilderConfiguration
            //having the correct classloader
            KnowledgeBuilderConfiguration kbuilderConfig =
                KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, ucl);
            kagent = this.createKAgent(kbase, newInstance, false, kbuilderConfig);
        }
        
        
        //The agent processes the changeset. The rule should be compiled
        //succesfully
        this.applyChangeSet(kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        //One package should be created
        assertEquals(1, kagent.getKnowledgeBase().getKnowledgePackages().size());

        //We create a session to test if the rule runs ok
        StatefulKnowledgeSession ksession = createKnowledgeSession(kagent.getKnowledgeBase());
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

        //The global list should contain 1 element
        assertEquals(1, list.size());
        assertTrue(list.contains("Instance number 2"));
        
        
        ksession.dispose();
        kagent.dispose();
    }

}
