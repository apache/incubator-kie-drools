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
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.PersonWithEquals;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class EqualsTest extends CommonTestMethodBase {

    @Test
    public void testEqualitySupport() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_equalitySupport.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        PersonWithEquals person = new PersonWithEquals("bob", 30);
        ksession.insert(person);
        ksession.fireAllRules();

        assertEquals(1, results.size());
        assertEquals("mark", results.get(0));

    }

    @Test
    public void testNotEqualsOperator() {
        // JBRULES-3003: restriction evaluation returns 'false' for "trueField != falseField"

        final String str = "package org.drools.compiler\n" +
                "rule NotEquals\n" +
                "when\n" +
                "    Primitives( booleanPrimitive != booleanWrapper )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Primitives p = new Primitives();
        p.setBooleanPrimitive(true);
        p.setBooleanWrapper(Boolean.FALSE);

        ksession.insert(p);

        final int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, rules);
    }

    @Test
    public void testCharComparisons() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_charComparisons.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        Primitives p1 = new Primitives();
        p1.setCharPrimitive('a');
        p1.setStringAttribute("b");
        Primitives p2 = new Primitives();
        p2.setCharPrimitive('b');
        p2.setStringAttribute("a");

        ksession.insert(p1);
        ksession.insert(p2);

        ksession.fireAllRules();

        assertEquals(3, results.size());
        assertEquals("1", results.get(0));
        assertEquals("2", results.get(1));
        assertEquals("3", results.get(2));

    }
}
