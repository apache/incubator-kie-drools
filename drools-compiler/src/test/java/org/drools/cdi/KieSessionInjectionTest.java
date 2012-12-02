package org.drools.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.cdi.KSession;
import org.kie.runtime.KieSession;

@RunWith(CDITestRunner.class)
public class KieSessionInjectionTest {

    @Inject @KSession("ksession1")
    private KieSession kSession1;
    
    @Test 
    public void test1() {
        assertNotNull( kSession1 );
        assertEquals(1, kSession1.getKnowledgeBase().getKnowledgePackage( "org.kie.kbase1" ).getRules().size() );
    }
}
