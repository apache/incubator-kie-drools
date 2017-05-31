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

package org.drools.compiler.integrationtests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.conf.ShareAlphaNodesOption;

public class AlphaTest extends CommonTestMethodBase {

    @Test
    public void testAlphaExpression() {
        final String text = "package org.drools.compiler\n" +
                "rule \"alpha\"\n" +
                "when\n" +
                "    Person( 5 < 6 )\n" +
                "then\n" +
                "end";
        final KieBase kbase = loadKnowledgeBaseFromString(text);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Person("mark", 50));
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testAlphaNodeSharing() throws IOException, ClassNotFoundException {
        final KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption(ShareAlphaNodesOption.YES);
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(kbc, "test_alphaNodeSharing.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Person p1 = new Person("bob", 5);
        ksession.insert(p1);

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals("1", results.get(0));
        assertEquals("2", results.get(1));

    }

    @Test
    public void testAlphaCompositeConstraints() throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_AlphaCompositeConstraints.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("bob", 30);

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testAlphaHashingWithConstants() {
        // JBRULES-3658
        final String str = "import " + Person.class.getName() + ";\n" +
                "import " + MiscTest.class.getName() + ";\n" +
                "rule R1 when\n" +
                "   $p : Person( age == 38 )\n" +
                "then end\n" +
                "rule R2 when\n" +
                "   $p : Person( age == 37+1 )\n" +
                "then end\n" +
                "rule R3 when\n" +
                "   $p : Person( age == 36+2 )\n" +
                "then end\n";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario", 38 ) );
        assertEquals( 3, ksession.fireAllRules() );
    }

    @Test
    public void testNPEOnMVELAlphaPredicates() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_NPEOnMVELPredicate.drl"));
        final KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("results", list);

        final Cheese cheese = new Cheese("stilton", 10);
        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese(cheese);
        final Person bob = new Person("bob", "stilton");
        final Cheese cheese2 = new Cheese();
        bob.setCheese(cheese2);

        final FactHandle p = session.insert(bob);
        final FactHandle c = session.insert(cheesery);

        session.fireAllRules();

        assertEquals("should not have fired", 0, list.size());

        cheese2.setType("stilton");
        session.update(p, bob);
        session.fireAllRules();

        assertEquals(1, list.size());

    }
}
