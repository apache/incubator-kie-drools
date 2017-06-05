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

package org.drools.compiler.integrationtests.drl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class LiteralTest extends CommonTestMethodBase {

    @Test
    public void testLiteral() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("literal_rule_test.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 5);
        session.insert(stilton);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        session.fireAllRules();

        assertEquals("stilton", ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testLiteralWithEscapes() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_literal_with_escapes.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final String expected = "s\tti\"lto\nn";
        final Cheese stilton = new Cheese(expected, 5);
        session.insert(stilton);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        final int fired = session.fireAllRules();
        assertEquals(1, fired);

        assertEquals(expected, ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testLiteralWithBoolean() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("literal_with_boolean.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final PersonInterface bill = new Person("bill", null, 12);
        bill.setAlive(true);
        session.insert(bill);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        session.fireAllRules();

        assertEquals(bill, ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testBigLiterals() {
        final String str = "package org.drools.compiler\n" +
                "rule X\n" +
                "when\n" +
                "    Primitives( bigInteger == 10I, bigInteger < (50I), bigDecimal == 10B, bigDecimal < (50B) )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Primitives p = new Primitives();
        p.setBigDecimal(BigDecimal.valueOf(10));
        p.setBigInteger(BigInteger.valueOf(10));
        ksession.insert(p);

        final int rulesFired = ksession.fireAllRules();
        assertEquals(1, rulesFired);
    }

    @Test
    public void testBigDecimalIntegerLiteral() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("big_decimal_and_literal.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final PersonInterface bill = new Person("bill", null, 12);
        bill.setBigDecimal(new BigDecimal("42"));
        bill.setBigInteger(new BigInteger("42"));

        session.insert(bill);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        assertEquals(6, ((List) session.getGlobal("list")).size());
    }
}
