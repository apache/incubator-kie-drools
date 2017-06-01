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
import org.drools.compiler.Guess;
import org.drools.compiler.Person;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.RandomNumber;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class MathTest extends CommonTestMethodBase {

    @Test
    public void testAddition() throws Exception {
        KieBase kbase = loadKnowledgeBase("returnvalue_rule_test.drl");
        kbase = SerializationHelper.serializeObject(kbase);
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal("two", 2);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final PersonInterface peter = new Person("peter", null, 12);
        ksession.insert(peter);
        final PersonInterface jane = new Person("jane", null, 10);
        ksession.insert(jane);

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals(jane, ((List) ksession.getGlobal("list")).get(0));
        assertEquals(peter, ((List) ksession.getGlobal("list")).get(1));
    }

    @Test
    public void testNumberComparisons() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_NumberComparisons.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        // asserting the sensor object
        final RandomNumber rn = new RandomNumber();
        rn.setValue(10);
        ksession.insert(rn);

        final Guess guess = new Guess();
        guess.setValue(5);

        final FactHandle handle = ksession.insert(guess);

        ksession.fireAllRules();

        // HIGHER
        assertEquals(1, list.size());
        assertEquals("HIGHER", list.get(0));

        guess.setValue(15);
        ksession.update(handle, guess);

        ksession.fireAllRules();

        // LOWER
        assertEquals(2, list.size());
        assertEquals("LOWER", list.get(1));

        guess.setValue(10);
        ksession.update(handle, guess);

        ksession.fireAllRules();

        // CORRECT
        assertEquals(3, list.size());
        assertEquals("CORRECT", list.get(2));
    }
}
