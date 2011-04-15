package org.drools.agent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLClassPathResourceTest extends BaseKnowledgeAgentTest {

    @Test
    public void testModifyFileUrlIncremental() throws Exception {
        URL resource = URLClassPathResourceTest.class.getClassLoader().getResource("org/drools/agent/rules/rules1.drl");
        
        assertNotNull(resource);
        
        File resourceFile = new File(resource.getFile());
        
        fileManager.write( resourceFile,
                           createDefaultRule( "rule1" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='URLClasspath:org/drools/agent/rules/rules1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase,
                                                   false );

        kagent.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "rule1" ) );

        list.clear();

        
        fileManager.write( resourceFile,
                           createDefaultRule( "rule3" ) );

        scan( kagent );

        // Use the same session for incremental build test
        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertTrue( list.contains( "rule3" ) );

        ksession.dispose();
        kagent.dispose();
    }

    
}
