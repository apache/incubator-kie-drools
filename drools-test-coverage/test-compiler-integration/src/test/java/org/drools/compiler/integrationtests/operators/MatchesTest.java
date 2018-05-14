/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class MatchesTest extends CommonTestMethodBase {

    @Test
    public void testMatchesMVEL() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MatchesMVEL.drl"));
        final KieSession session = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        session.setGlobal("results", results);

        final Map map = new HashMap();
        map.put("content", "hello ;=");
        session.insert(map);

        session.fireAllRules();

        assertEquals(1, results.size());
    }

    @Test
    public void testMatchesMVEL2() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_MatchesMVEL2.drl");
        final KieSession ksession = createKnowledgeSession(kbase);

        final Map map = new HashMap();
        map.put("content", "String with . and (routine)");
        ksession.insert(map);
        final int fired = ksession.fireAllRules();

        assertEquals(2, fired);
    }

    @Test
    public void testMatchesMVEL3() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_MatchesMVEL2.drl");
        final KieSession ksession = createKnowledgeSession(kbase);

        final Map map = new HashMap();
        map.put("content", "String with . and ()");
        ksession.insert(map);
        final int fired = ksession.fireAllRules();

        assertEquals(1, fired);
    }

    @Test
    public void testMatchesNotMatchesCheese() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MatchesNotMatches.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 12);
        final Cheese stilton2 = new Cheese("stilton2", 12);
        final Cheese agedStilton = new Cheese("aged stilton", 12);
        final Cheese brie = new Cheese("brie", 10);
        final Cheese brie2 = new Cheese("brie2", 10);
        final Cheese muzzarella = new Cheese("muzzarella", 10);
        final Cheese muzzarella2 = new Cheese("muzzarella2", 10);
        final Cheese provolone = new Cheese("provolone", 10);
        final Cheese provolone2 = new Cheese("another cheese (provolone)", 10);

        ksession.insert(stilton);
        ksession.insert(stilton2);
        ksession.insert(agedStilton);
        ksession.insert(brie);
        ksession.insert(brie2);
        ksession.insert(muzzarella);
        ksession.insert(muzzarella2);
        ksession.insert(provolone);
        ksession.insert(provolone2);

        ksession.fireAllRules();

        assertEquals(4, list.size());

        assertEquals(stilton, list.get(0));
        assertEquals(brie, list.get(1));
        assertEquals(agedStilton, list.get(2));
        assertEquals(provolone, list.get(3));
    }

    @Test
    public void testNotMatchesSucceeds() throws InstantiationException, IllegalAccessException {
        // JBRULES-2914: Rule misfires due to "not matches" not working
        testMatchesSuccessFail("-..x..xrwx", 0);
    }

    @Test
    public void testNotMatchesFails() throws InstantiationException, IllegalAccessException {
        // JBRULES-2914: Rule misfires due to "not matches" not working
        testMatchesSuccessFail("d..x..xrwx", 1);
    }

    private void testMatchesSuccessFail(final String personName, final int expectedFireCount) {
        final String str = "package org.drools.compiler\n" +
                "rule NotMatches\n" +
                "when\n" +
                "    Person( name == null || (name != null && name not matches \"-.{2}x.*\" ) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Person p = new Person(personName);
        ksession.insert(p);

        final int rules = ksession.fireAllRules();
        ksession.dispose();
        assertEquals(expectedFireCount, rules);
    }
}
