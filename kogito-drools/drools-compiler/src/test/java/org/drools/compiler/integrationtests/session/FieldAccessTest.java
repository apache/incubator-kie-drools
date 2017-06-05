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

package org.drools.compiler.integrationtests.session;

import org.drools.compiler.Address;
import org.drools.compiler.Cat;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class FieldAccessTest extends CommonTestMethodBase {

    @Test
    // this isn't possible, we can only narrow with type safety, not widen.
    // unless typesafe=false is used
    public void testAccessFieldsFromSubClass() throws Exception {
        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "import org.drools.compiler.Person;\n";
        rule += "import org.drools.compiler.Pet;\n";
        rule += "import org.drools.compiler.Cat;\n";
        rule += "declare Person @typesafe(false) end\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    Person(\n";
        rule += "      pet.breed == \"Siamise\"\n";
        rule += "    )\n";
        rule += "then\n";
        rule += "    System.out.println(\"hello person\");\n";
        rule += "end";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        final KieSession session = createKnowledgeSession(kbase);

        final Person person = new Person();
        person.setPet(new Cat("Mittens"));
        session.insert(person);
        session.fireAllRules();
    }

    @Test
    public void testAccessClassTypeField() {
        final String str = "package org.drools.compiler\n" +
                "rule r1\n" +
                "when\n" +
                "    Primitives( classAttr == null )" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Primitives());
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testGenericsOption() throws Exception {
        // JBRULES-3579
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   $c : Cheese( $type: type )\n" +
                "   $p : Person( $name : name, addressOption.get.street == $type )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person p = new Person("x");
        p.setAddress(new Address("x", "x", "x"));
        ksession.insert(p);

        ksession.insert(new Cheese("x"));
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

}
