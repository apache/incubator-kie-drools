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

import java.math.BigDecimal;

import org.drools.core.common.NamedEntryPoint;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Toy;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConstraintNormalizationTest extends BaseModelTest {

    // Only supports executable-model at the moment
    @Parameters(name = "{0}")
    public static Object[] params() {
        if (Boolean.valueOf(System.getProperty("alphanetworkCompilerEnabled"))) {
            return new Object[]{RUN_TYPE.FLOW_DSL, RUN_TYPE.PATTERN_DSL, RUN_TYPE.FLOW_WITH_ALPHA_NETWORK, RUN_TYPE.PATTERN_WITH_ALPHA_NETWORK};
        } else {
            return new Object[]{RUN_TYPE.FLOW_DSL, RUN_TYPE.PATTERN_DSL};
        }
    }

    public ConstraintNormalizationTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

//    @Test
//    public void testNormalizationForPropertyReactivity() {
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "import " + Toy.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " $t : Toy($owner : owner)\n" +
//                           " $p : Person($owner == name)\n" +
//                           "then\n" +
//                           "  $p.setAge(20);" +
//                           "  update($p);" +
//                           "end\n" +
//                           "rule R2 when \n" +
//                           "  $p : Person(age == 20)\n" +
//                           "then\n" +
//                           "end\n";
//
//        final KieSession ksession = getKieSession(str);
//
//        final Toy t = new Toy("Ball");
//        t.setOwner("Toshiya");
//        final Person p = new Person("Toshiya", 45);
//        ksession.insert(t);
//        ksession.insert(p);
//        assertEquals(2, ksession.fireAllRules(10)); // no infinite loop
//    }
//
//    @Test
//    public void testNormalizationForPropertyReactivity2() {
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " $i : Integer()\n" +
//                           " $p : Person($i < age)\n" +
//                           "then\n" +
//                           "  $p.setName(\"Blaa\");" +
//                           "  update($p);" +
//                           "end\n" +
//                           "rule R2 when \n" +
//                           " $p : Person(name == \"Blaa\")\n" +
//                           "then\n" +
//                           "end\n";
//
//        final KieSession ksession = getKieSession(str);
//
//        final Person p = new Person("Toshiya", 45);
//        ksession.insert(new Integer(30));
//        ksession.insert(p);
//        assertEquals(2, ksession.fireAllRules(10)); // no infinite loop
//    }
//
//    @Test
//    public void testNormalizationForAlphaIndexing() {
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " $p : Person(\"Toshiya\" == name)\n" +
//                           "then\n" +
//                           "end\n" +
//                           "rule R2 when \n" +
//                           " $p : Person(\"Mario\" == name)\n" +
//                           "then\n" +
//                           "end\n" +
//                           "rule R3 when \n" +
//                           " $p : Person(\"Luca\" == name)\n" +
//                           "then\n" +
//                           "end\n";
//
//        final KieSession ksession = getKieSession(str);
//
//        ObjectTypeNode otn = ((NamedEntryPoint) ksession.getEntryPoint("DEFAULT")).getEntryPointNode().getObjectTypeNodes().entrySet()
//                                                                                  .stream()
//                                                                                  .filter(e -> e.getKey().getClassName().equals(Person.class.getCanonicalName()))
//                                                                                  .map(e -> e.getValue())
//                                                                                  .findFirst()
//                                                                                  .get();
//        CompositeObjectSinkAdapter sinkAdaptor = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();
//
//        assertNotNull(sinkAdaptor.getHashedSinkMap());
//        assertEquals(3, sinkAdaptor.getHashedSinkMap().size());
//
//        final Person p = new Person("Toshiya", 45);
//        ksession.insert(p);
//        assertEquals(1, ksession.fireAllRules());
//    }
//
//    @Test
//    public void testNormalizationForNodeSharing() {
//
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " $p : Person(\"Toshiya\" == name)\n" +
//                           "then\n" +
//                           "end\n" +
//                           "rule R2 when \n" +
//                           " $p : Person(name == \"Toshiya\")\n" +
//                           "then\n" +
//                           "end\n";
//
//        final KieSession ksession = getKieSession(str);
//
//        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());
//
//        final Person p = new Person("Toshiya", 45);
//        ksession.insert(p);
//        assertEquals(2, ksession.fireAllRules());
//    }
//
//    @Test
//    public void testOperators() throws Exception {
//
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " Person(20 < age, 30 > age)\n" +
//                           " Person(30 <= age, 40 >= age)\n" +
//                           "then\n" +
//                           "end\n" +
//
//                           "rule R2 when \n" +
//                           " Person(age > 20, age < 30)\n" +
//                           " Person(age >= 30, age <= 40)\n" +
//                           "then\n" +
//                           "end";
//
//        final KieSession ksession = getKieSession(str);
//
//        // Check NodeSharing to verify if normalization works expectedly
//        assertEquals(4, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());
//
//        final Person p1 = new Person("John", 21);
//        final Person p2 = new Person("Paul", 40);
//
//        ksession.insert(p1);
//        ksession.insert(p2);
//        assertEquals(2, ksession.fireAllRules());
//    }
//
//    @Test
//    public void testNestedProperty() throws Exception {
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " Person(\"ABC\" == address.city)\n" +
//                           "then\n" +
//                           "end\n" +
//
//                           "rule R2 when \n" +
//                           " Person(address.city == \"ABC\")\n" +
//                           "then\n" +
//                           "end";
//
//        final KieSession ksession = getKieSession(str);
//
//        // Check NodeSharing to verify if normalization works expectedly
//        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());
//
//        final Person p = new Person("Toshiya", 45);
//        p.setAddress(new Address("ABC"));
//
//        ksession.insert(p);
//        assertEquals(2, ksession.fireAllRules());
//    }
//
//    @Test
//    public void testComplexMethod() throws Exception {
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "import " + BigDecimal.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " Person(0 == this.money.compareTo(new BigDecimal(\"0.0\")))\n" +
//                           "then\n" +
//                           "end\n" +
//
//                           "rule R2 when \n" +
//                           " Person(this.money.compareTo(new BigDecimal(\"0.0\")) == 0)\n" +
//                           "then\n" +
//                           "end";
//
//        final KieSession ksession = getKieSession(str);
//
//        // Check NodeSharing to verify if normalization works expectedly
//        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());
//
//        final Person p = new Person("Toshiya", 45);
//        p.setMoney(new BigDecimal("0.0"));
//
//        ksession.insert(p);
//        assertEquals(2, ksession.fireAllRules());
//    }
//
//    @Test
//    public void testPropsOnBothSide() throws Exception {
//        final String str =
//                "package org.drools.test;\n" +
//                           "import " + Person.class.getCanonicalName() + ";\n" +
//                           "rule R1 when \n" +
//                           " Person($id: id == age)\n" +
//                           "then\n" +
//                           "end\n" +
//
//                           "rule R2 when \n" +
//                           " Person($id: id == age)\n" + // No normalization
//                           "then\n" +
//                           "end";
//
//        final KieSession ksession = getKieSession(str);
//
//        // Check NodeSharing to verify if normalization works expectedly
//        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());
//
//        final Person p = new Person("Toshiya", 45);
//        p.setId(45);
//
//        ksession.insert(p);
//        assertEquals(2, ksession.fireAllRules());
//    }

    @Test
    public void testExtraParentheses() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person((30 < age))\n" +
                           "then\n" +
                           "end\n" +

                           "rule R2 when \n" +
                           " Person((age > 30))\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        // Check NodeSharing to verify if normalization works expectedly
        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());

        final Person p = new Person("Toshiya", 45);

        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules());
    }

    @Ignore("Not yet implemented. Benefit is only NodeSharing")
    @Test
    public void testAnd() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(\"Toshiya\" == name && \"Bird\" == likes)\n" +
                           "then\n" +
                           "end\n" +

                           "rule R2 when \n" +
                           " Person(name == \"Toshiya\" && likes == \"Bird\")\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        // Check NodeSharing to verify if normalization works expectedly
        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());

        final Person p = new Person("Toshiya", 45);
        p.setLikes("Bird");

        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules());
    }

    @Ignore("Not yet implemented. Benefit is only NodeSharing")
    @Test
    public void testOr() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(\"XXX\" == name || \"Bird\" == likes)\n" +
                           "then\n" +
                           "end\n" +

                           "rule R2 when \n" +
                           " Person(name == \"XXX\" || likes == \"Bird\")\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        // Check NodeSharing to verify if normalization works expectedly
        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());

        final Person p = new Person("Toshiya", 45);
        p.setLikes("Bird");

        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules());
    }

    @Ignore("Not yet implemented. Benefit is only NodeSharing")
    @Test
    public void testNegate() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(!(30 > age))\n" +
                           "then\n" +
                           "end\n" +

                           "rule R2 when \n" +
                           " Person(!(age < 30))\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        // Check NodeSharing to verify if normalization works expectedly
        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());

        final Person p = new Person("Toshiya", 45);

        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules());
    }
}
