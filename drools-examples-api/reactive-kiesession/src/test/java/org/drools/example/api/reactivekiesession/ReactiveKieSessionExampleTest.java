package org.drools.example.api.reactivekiesession;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReactiveKieSessionExampleTest {

    @Test
    public void testGo() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();

        KieSession ksession = kContainer.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );

        list.clear();

        ksession.insert("Debbie");
        ksession.fireAllRules();
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "doll" ) );
    }
}
