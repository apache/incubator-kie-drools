/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.EvaluatorOption;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CustomOperatorTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CustomOperatorTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testCustomOperatorUsingCollections() {
        final String drl =
                "import " + Address.class.getCanonicalName() + ";\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "    $alice : Person(name == \"Alice\")\n" +
                        "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "supersetOf", SupersetOfEvaluatorDefinition.class.getName());
        try {
            final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            final KieSession ksession = kbase.newKieSession();
            try {
                final Person alice = new Person("Alice", 30);
                alice.addAddress(new Address("Large Street", "BigTown", "12345"));
                final Person bob = new Person("Bob", 30);
                bob.addAddress(new Address("Large Street", "BigTown", "12345"));
                bob.addAddress(new Address("Long Street", "SmallTown", "54321"));

                ksession.insert(alice);
                ksession.insert(bob);

                assertEquals(1, ksession.fireAllRules());
            } finally {
                ksession.dispose();
            }
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "supersetOf");
        }
    }

    public static class SupersetOfEvaluatorDefinition implements EvaluatorDefinition {

        public static final Operator SUPERSET_OF = Operator.addOperatorToRegistry("supersetOf", false);
        public static final Operator NOT_SUPERSET_OF = Operator.addOperatorToRegistry("supersetOf", true);
        private static final String[] SUPPORTED_IDS = {SUPERSET_OF.getOperatorString()};

        private Evaluator[] evaluator;

        public String[] getEvaluatorIds() {
            return SupersetOfEvaluatorDefinition.SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText, final Target leftTarget, final Target rightTarget) {
            return new SupersetOfEvaluator(type, isNegated);
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText) {
            return getEvaluator(type, operatorId, isNegated, parameterText, Target.FACT, Target.FACT);
        }

        public Evaluator getEvaluator(final ValueType type, final Operator operator, final String parameterText) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), parameterText);
        }

        public Evaluator getEvaluator(final ValueType type, final Operator operator) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), null);
        }

        public boolean supportsType(final ValueType vt) {
            return true;
        }

        public Target getTarget() {
            return Target.FACT;
        }

        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(evaluator);
        }

        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            evaluator = (Evaluator[]) in.readObject();
        }
    }

    public static class SupersetOfEvaluator extends BaseEvaluator {

        public SupersetOfEvaluator(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? SupersetOfEvaluatorDefinition.NOT_SUPERSET_OF : SupersetOfEvaluatorDefinition.SUPERSET_OF);
        }

        public boolean evaluate(final InternalWorkingMemory workingMemory, final InternalReadAccessor extractor, final InternalFactHandle factHandle, final FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle);
            return evaluateAll((Collection) value.getValue(), (Collection) objectValue);
        }

        public boolean evaluate(final InternalWorkingMemory iwm, final InternalReadAccessor ira, final InternalFactHandle left, final InternalReadAccessor ira1, final InternalFactHandle right) {
            return evaluateAll((Collection) left.getObject(), (Collection) right.getObject());
        }

        public boolean evaluateCachedLeft(final InternalWorkingMemory workingMemory, final VariableContextEntry context, final InternalFactHandle right) {
            final Object valRight = context.extractor.getValue(workingMemory, right.getObject());
            return evaluateAll((Collection) ((ObjectVariableContextEntry) context).left, (Collection) valRight);
        }

        public boolean evaluateCachedRight(final InternalWorkingMemory workingMemory, final VariableContextEntry context, final InternalFactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(workingMemory, left);
            return evaluateAll((Collection) varLeft, (Collection) ((ObjectVariableContextEntry) context).right);
        }

        public boolean evaluateAll(final Collection leftCollection, final Collection rightCollection) {
            return rightCollection.containsAll(leftCollection);
        }
    }

    @Test
    public void testCustomOperatorOnKieModule() {
        final String drl = "import " + Address.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $alice : Person(name == \"Alice\")\n" +
                "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n" +
                "then\n" +
                "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "supersetOf", SupersetOfEvaluatorDefinition.class.getName());
        try {
            final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            final KieSession ksession = kbase.newKieSession();
            try {
                final Person alice = new Person("Alice", 30);
                alice.addAddress(new Address("Large Street", "BigTown", "12345"));
                final Person bob = new Person("Bob", 30);
                bob.addAddress(new Address("Large Street", "BigTown", "12345"));
                bob.addAddress(new Address("Long Street", "SmallTown", "54321"));

                ksession.insert(alice);
                ksession.insert(bob);

                assertEquals(1, ksession.fireAllRules());
            } finally {
                ksession.dispose();
            }
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "supersetOf");
        }
    }
}
