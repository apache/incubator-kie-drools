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

package org.drools.compiler.integrationtests.operators.memberof;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.Pet;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class MemberOfTest extends CommonTestMethodBase {

    @Test
    public void testMemberOfAndNotMemberOf() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_memberOf.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 12);
        final Cheese muzzarela = new Cheese("muzzarela", 10);
        final Cheese brie = new Cheese("brie", 15);
        ksession.insert(stilton);
        ksession.insert(muzzarela);

        final Cheesery cheesery = new Cheesery();
        cheesery.getCheeses().add(stilton.getType());
        cheesery.getCheeses().add(brie.getType());
        ksession.insert(cheesery);

        ksession.fireAllRules();

        assertEquals(2, list.size());

        assertEquals(stilton, list.get(0));
        assertEquals(muzzarela, list.get(1));
    }

    @Test
    public void testMemberOfWithOr() throws Exception {

        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "import java.util.ArrayList;\n";
        rule += "import org.drools.compiler.Person;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $list: ArrayList()                                   \n";
        rule += "    ArrayList()                                          \n";
        rule += "            from collect(                                \n";
        rule += "                  Person(                                \n";
        rule += "                      (                                  \n";
        rule += "                          pet memberOf $list             \n";
        rule += "                      ) || (                             \n";
        rule += "                          pet == null                    \n";
        rule += "                      )                                  \n";
        rule += "                  )                                      \n";
        rule += "            )\n";
        rule += "then\n";
        rule += "  System.out.println(\"hello person\");\n";
        rule += "end";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        final KieSession session = createKnowledgeSession(kbase);

        final Person toni = new Person("Toni", 12);
        toni.setPet(new Pet("Mittens"));

        session.insert(new ArrayList());
        session.insert(toni);

        session.fireAllRules();
    }
}
