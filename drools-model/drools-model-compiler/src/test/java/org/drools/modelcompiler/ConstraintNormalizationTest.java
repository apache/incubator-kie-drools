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
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
                           " $p : Person(name == \"Toshiya\")\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        assertEquals(1, ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count());

        final Person p = new Person("Toshiya", 45);
        ksession.insert(p);
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testOperators() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(20 < age, 30 > age)\n" +
                           " Person(30 <= age, 40 >= age)\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId1 = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                          "greaterThanNumbers(_this.getAge(), 20)");
            assertThat(getConstraintString(ksession, 0, 0), containsString(exprId1));

            String exprId2 = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                          "lessThanNumbers(_this.getAge(), 30)");
            assertThat(getConstraintString(ksession, 0, 1), containsString(exprId2));

            String exprId3 = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                          "greaterOrEqualNumbers(_this.getAge(), 30)");
            assertThat(getConstraintString(ksession, 1, 0), containsString(exprId3));

            String exprId4 = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                          "lessOrEqualNumbers(_this.getAge(), 40)");
            assertThat(getConstraintString(ksession, 1, 1), containsString(exprId4));
        }

        final Person p1 = new Person("John", 21);
        final Person p2 = new Person("Paul", 40);

        ksession.insert(p1);
        ksession.insert(p2);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testNestedProperty() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(\"ABC\" == address.city)\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNullSafeEquals(_this.getAddress().getCity(), \"ABC\")");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Person p = new Person("Toshiya", 45);
        p.setAddress(new Address("ABC"));

        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testComplexMethod() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(0 == this.money.compareTo(new BigDecimal(\"0.0\")))\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNumbersNullSafeEquals(_this.getMoney().compareTo(new BigDecimal(\"0.0\")), 0)");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Person p = new Person("Toshiya", 45);
        p.setMoney(new BigDecimal("0.0"));

        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testPropsOnBothSide() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person($id: id == age)\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Person.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNumbersNullSafeEquals(_this.getId(), _this.getAge())");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Person p = new Person("Toshiya", 45);
        p.setAddress(new Address("ABC"));
        p.setMoney(new BigDecimal("0.0"));
        p.setId(45);
        p.setLikes("Bird");

        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Ignore
    @Test
    public void testAnd() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(\"Toshiya\" == name && \"Bird\" == likes)\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Person.class,
                                                           "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNullSafeEquals(_this.getName(), \"Toshiya\")" +
                                                                         " && org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNullSafeEquals(_this.getLikes(), \"Bird\")");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Person p = new Person("Toshiya", 45);
        p.setLikes("Bird");

        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Ignore
    @Test
    public void testOr() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(\"XXX\" == name || \"Bird\" == likes)\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Person.class,
                                                           "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNullSafeEquals(_this.getName(), \"XXX\")" +
                                                                         " || org.drools.modelcompiler.util.EvaluationUtil." +
                                                                         "areNullSafeEquals(_this.getLikes(), \"Bird\")");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Person p = new Person("Toshiya", 45);
        p.setLikes("Bird");

        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Ignore
    @Test
    public void testNegate() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           " Person(!(30 > age))\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Person.class, "!(_this.getAge() < 30)");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Person p = new Person("Toshiya", 45);

        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Ignore
    @Test
    public void testOOPath() throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Man.class.getCanonicalName() + ";\n" +
                           "import " + Woman.class.getCanonicalName() + ";\n" +
                           "import " + Child.class.getCanonicalName() + ";\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R1 when\n" +
                           " $man: Man( /wife/children[10 < age] )\n" +
                           "then\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        if (testRunType == RUN_TYPE.FLOW_DSL || testRunType == RUN_TYPE.PATTERN_DSL) {
            String exprId = new DRLIdGenerator().getExprId(Child.class, "org.drools.modelcompiler.util.EvaluationUtil." +
                                                                        "greaterThanNumbers(_this.getAge(), 10)");
            assertThat(getConstraintString(ksession), containsString(exprId));
        }

        final Woman alice = new Woman("Alice", 38);
        final Man bob = new Man("Bob", 40);
        bob.setWife(alice);
        final Child charlie = new Child("Charles", 12);
        alice.addChild(charlie);

        ksession.insert(bob);

        assertEquals(1, ksession.fireAllRules());
    }

    private String getConstraintString(final KieSession ksession) throws Exception {
        return getConstraintString(ksession, 0, 0);
    }

    private String getConstraintString(final KieSession ksession, int petternIdx, int constraintIdx) throws Exception {
        RuleImpl rule = (RuleImpl) ksession.getKieBase().getRule("org.drools.test", "R1");
        GroupElement lhs = rule.getLhs();
        LambdaConstraint lambdaConstraint = (LambdaConstraint) ((Pattern) lhs.getChildren().get(petternIdx)).getConstraints().get(constraintIdx);
        return lambdaConstraint.toString();
    }
}
