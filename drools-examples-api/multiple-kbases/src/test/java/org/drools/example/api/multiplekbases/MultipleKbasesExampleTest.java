package org.drools.example.api.multiplekbases;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class MultipleKbasesExampleTest {

    @Test
    public void testSimpleKieBase() {
        List<Integer> list = useKieSession("ksession1");
        // rule1.drl is in a folder with the same name of kase1 (default folder)
        assertTrue( list.containsAll( asList(1) ) );
    }

    @Test
    public void testKieBaseWithPackage() {
        List<Integer> list = useKieSession("ksession2");
        // default folder + imported package
        assertTrue( list.containsAll( asList(2, 3) ) );
    }

    @Test
    public void testKieBaseWithInclusion() {
        List<Integer> list = useKieSession("ksession3");
        // no default folder, but add default folder of included kbase
        assertTrue( list.containsAll( asList(1) ) );
    }

    @Test
    public void testKieBaseWith2Packages() {
        List<Integer> list = useKieSession("ksession4");
        // no default folder, 2 imported packages
        assertTrue( list.containsAll( asList(3, 4) ) );
    }

    @Test
    public void testKieBaseWithPackageAndTransitiveInclusion() {
        List<Integer> list = useKieSession("ksession5");
        // no default folder, but add default folder of transitively included kbase + imported package
        assertTrue( list.containsAll( asList(1, 4) ) );
    }

    @Test
    public void testKieBaseWithAllPackages() {
        List<Integer> list = useKieSession("ksession6");
        // import * package
        assertTrue( list.containsAll( asList(0, 1, 2, 3, 4) ) );
    }

    @Test
    public void testKieBaseWithWildcardPackage() {
        List<Integer> list = useKieSession("ksession7");
        // import org.* package
        assertTrue( list.containsAll( asList(3, 4) ) );
    }

    private List<Integer> useKieSession(String name) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession(name);

        List<Integer> list = new ArrayList<Integer>();
        kSession.setGlobal("list", list);
        kSession.insert(1);
        kSession.fireAllRules();

        return list;
    }
}