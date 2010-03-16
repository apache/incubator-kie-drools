package org.drools.integrationtests;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class BrlTest extends TestCase {
   
    public void testBrl() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "BrlRule.package", getClass() ), ResourceType.DRL );        
        kbuilder.add( ResourceFactory.newClassPathResource( "BrlRule.brl", getClass() ), ResourceType.BRL );        

        // the compiled package
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.insert( new Person( "Bob" ) );

        assertEquals( 1, session.getObjects().size() );
        
        session.fireAllRules();
        // should have fired
        assertEquals( 0, session.getObjects().size() );
    }

}
