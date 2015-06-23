/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.example.api.multiplekbases;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultipleKbasesExampleTest {

    @Test
    public void testSimpleKieBase() {
        List<Integer> list = useKieSession("ksession1");
        // no packages imported means import everything
        assertEquals(4, list.size());
        assertTrue( list.containsAll( asList(0, 1, 2, 3) ) );
    }

    @Test
    public void testKieBaseWithPackage() {
        List<Integer> list = useKieSession("ksession2");
        // import package org.some.pkg
        assertEquals(1, list.size());
        assertTrue(list.containsAll(asList(1)));
    }

    @Test
    public void testKieBaseWithInclusion() {
        List<Integer> list = useKieSession("ksession3");
        // include ksession2 + import package org.some.pkg2
        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList(1, 2)));
    }

    @Test
    public void testKieBaseWith2Packages() {
        List<Integer> list = useKieSession("ksession4");
        // import package org.some.pkg, org.other.pkg
        assertEquals(2, list.size());
        assertTrue( list.containsAll( asList(1, 3) ) );
    }

    @Test
    public void testKieBaseWithPackageAndTransitiveInclusion() {
        List<Integer> list = useKieSession("ksession5");
        // import package org.*
        assertEquals(3, list.size());
        assertTrue(list.containsAll(asList(1, 2, 3)));
    }

    @Test
    public void testKieBaseWithAllPackages() {
        List<Integer> list = useKieSession("ksession6");
        // import package org.some.*
        assertEquals(2, list.size());
        assertTrue(list.containsAll(asList(1, 2)));
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