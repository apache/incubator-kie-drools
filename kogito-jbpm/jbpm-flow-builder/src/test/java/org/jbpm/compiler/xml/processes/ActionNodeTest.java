package org.jbpm.compiler.xml.processes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.core.io.impl.ClassPathResource;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class ActionNodeTest extends TestCase {
    public void testSingleActionNode() throws Exception {                
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "ActionNodeTest.xml", ActionNodeTest.class ), ResourceType.DRF );
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        ksession.startProcess( "process name" );
        
        assertEquals( 1, list.size() );
        assertEquals( "action node was here", list.get(0) );        
    }
}
