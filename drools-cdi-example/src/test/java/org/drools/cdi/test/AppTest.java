package org.drools.cdi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.drools.KnowledgeBase;
import org.drools.cdi.KBase;
import org.drools.cdi.KSession;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CDITestRunner.class)
public class AppTest {

    private @Inject @KBase("fol4.test1.KBase1")  KnowledgeBase kBase1;
    private @Inject @KBase("fol4.test1.KBase1")  KnowledgeBase kBase2;
    private @Inject @KBase("fol4.test1.KBase1")  KnowledgeBase kBase3;
            
    
    private @Inject @KSession("fol4.test1.KSession1") StatelessKnowledgeSession kBase1kSession1;

    private @Inject @KSession("fol4.test1.KSession2") StatefulKnowledgeSession kBase1kSession2;

    private @Inject @KSession("fol4.test2.KSession3") StatefulKnowledgeSession kBase2kSession3;

    private @Inject @KSession("fol4.test3.KSession4") StatelessKnowledgeSession kBase3kSession4;

    public AppTest() {

    }
    
    @Test
    public void testWiringAndExecution() {
        List<String> list = new ArrayList<String>();

        kBase1kSession1.setGlobal( "list", list );
        kBase1kSession1.execute( "dummy" );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( "fol4.test1:rule1" ) );
        assertTrue( list.contains( "fol4.test1:rule2" ) );

        list.clear();
        kBase1kSession2.setGlobal( "list", list );
        kBase1kSession2.fireAllRules();
        assertEquals( 2, list.size() );
        assertTrue( list.contains( "fol4.test1:rule1" ) );
        assertTrue( list.contains( "fol4.test1:rule2" ) );

        list.clear();
        kBase2kSession3.setGlobal( "list", list );
        kBase2kSession3.fireAllRules();
        assertEquals( 2, list.size() );

        assertTrue( list.contains( "fol4.test2:rule1" ) );
        assertTrue( list.contains( "fol4.test2:rule2" ) );
        
        // This tests kbase includes
        list.clear();
        kBase3kSession4.setGlobal( "list", list );
        kBase3kSession4.execute( "dummy" );
        assertEquals( 4, list.size() );
        assertTrue( list.contains( "fol4.test1:rule1" ) );
        assertTrue( list.contains( "fol4.test1:rule2" ) );
        assertTrue( list.contains( "fol4.test2:rule1" ) );
        assertTrue( list.contains( "fol4.test2:rule2" ) );        
    }
}
