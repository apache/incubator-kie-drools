/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import org.drools.core.common.NamedEntryPoint;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Toy;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConstraintNormalizationTest extends BaseModelTest {

    public ConstraintNormalizationTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testNormalizationForPropertyReactivity() {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "import " + Toy.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " $t : Toy($owner : owner)\n" +
                           " $p : Person($owner == name)\n" +
                           "then\n" +
                           "  $p.setAge(20);" +
                           "  update($p);" +
                           "end\n" +
                           "rule R2 when \n" +
                           "  $p : Person(age == 20)\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        final Toy t = new Toy("Ball");
        t.setOwner("Toshiya");
        final Person p = new Person("Toshiya", 45);
        ksession.insert(t);
        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules(10)); // no infinite loop
    }

    @Test
    public void testNormalizationForPropertyReactivity2() {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " $i : Integer()\n" +
                           " $p : Person($i < age)\n" +
                           "then\n" +
                           "  $p.setName(\"Blaa\");" +
                           "  update($p);" +
                           "end\n" +
                           "rule R2 when \n" +
                           " $p : Person(name == \"Blaa\")\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        final Person p = new Person("Toshiya", 45);
        ksession.insert(new Integer(30));
        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules(10)); // no infinite loop
    }

    @Test
    public void testNormalizationForAlphaIndexing() {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " $p : Person(\"Toshiya\" == name)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when \n" +
                           " $p : Person(\"Mario\" == name)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R3 when \n" +
                           " $p : Person(\"Luca\" == name)\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        ObjectTypeNode otn = ((NamedEntryPoint) ksession.getEntryPoint("DEFAULT")).getEntryPointNode().getObjectTypeNodes().entrySet()
                                                                                  .stream()
                                                                                  .filter(e -> e.getKey().getClassName().equals(Person.class.getCanonicalName()))
                                                                                  .map(e -> e.getValue())
                                                                                  .findFirst()
                                                                                  .get();
        CompositeObjectSinkAdapter sinkAdaptor = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();

        assertNotNull(sinkAdaptor.getHashedSinkMap());
        assertEquals(3, sinkAdaptor.getHashedSinkMap().size());

        final Person p = new Person("Toshiya", 45);
        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testNormalizationForNodeSharing() {

        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " $p : Person(\"Toshiya\" == name)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when \n" +
                           // Note: Trimmed white spaces around '=='. If DROOLS-5023 is resolved, we can also test with spaces
                           " $p : Person(name==\"Toshiya\")\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());

        final Person p = new Person("Toshiya", 45);
        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules());
    }
}
