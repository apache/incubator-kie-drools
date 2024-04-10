/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.Evaluator;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import org.drools.drl.parser.impl.Operator;
import org.drools.mvel.evaluators.BaseEvaluator;
import org.drools.mvel.evaluators.VariableRestriction;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.EvaluatorOption;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.common.InternalFactHandle.dummyFactHandleOf;

@RunWith(Parameterized.class)
public class CustomOperatorExecutableTest {

    private static final String OPNAME = "supersetOf";
    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    private final String evaluatorClassName;

    public CustomOperatorExecutableTest(final String evaluator) {
        Collection<Object[]> parameters = TestParametersUtil.getKieBaseCloudOnlyExecModelConfiguration();
        assertThat(parameters.size()).isEqualTo(1);
        this.kieBaseTestConfiguration = (KieBaseTestConfiguration) parameters.iterator().next()[0];
        this.evaluatorClassName = evaluator;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        List<Object[]> results = new ArrayList<>();
        final String[] evaluatorsclassNames = { SupersetOfEvaluatorDefinitionCaseThree.class.getName(),
                SupersetOfEvaluatorDefinitionCaseTwo.class.getName() ,
                SupersetOfEvaluatorDefinitionCaseOne.class.getName(),};
        for (String evaluatorsclassName : evaluatorsclassNames) {
            results.add(new Object[]{evaluatorsclassName});
        }
        return results;
    }

    @Test
    public void testCustomOperatorUsingCollections() {
        String constraints =
                "    $alice : Person(name == \"Alice\")\n" +
                "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n";
        customOperatorUsingCollections(constraints, evaluatorClassName);
    }

    @Test
    public void testCustomOperatorUsingCollectionsInverted() {
        String constraints =
                "    $bob : Person(name == \"Bob\")\n" +
                "    $alice : Person(name == \"Alice\", $bob.addresses supersetOf this.addresses)\n";
        customOperatorUsingCollections(constraints, evaluatorClassName);
    }

    @AfterClass
    public static void instanceCount() {
        //System.out.println("instance count case 3= " + SupersetOfEvaluatorDefinitionCaseThree.newInstanceCount);
        //System.out.println("instance count case 2= " + SupersetOfEvaluatorDefinitionCaseTwo.newInstanceCount);
        //System.out.println("instance count case 1= " + SupersetOfEvaluatorDefinitionCaseOne.newInstanceCount);

        // case 2 and case 1 should have the same instance creation count
        assertThat(SupersetOfEvaluatorDefinitionCaseOne.newInstanceCount).isEqualTo(SupersetOfEvaluatorDefinitionCaseTwo.newInstanceCount);

        // case 1 & 2 must have fewer instances than case 3
        assertThat(SupersetOfEvaluatorDefinitionCaseOne.newInstanceCount).isLessThan(SupersetOfEvaluatorDefinitionCaseThree.newInstanceCount);

        // 7 instances for case 3
        // 4 instances for case 2 and 1
    }

    private void customOperatorUsingCollections(String constraints, String evaluatorClassName) {
        final String drl =
                "import " + Address.class.getCanonicalName() + ";\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        constraints +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + OPNAME, evaluatorClassName);
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

                assertThat(ksession.fireAllRules()).isEqualTo(1);
            } finally {
                ksession.dispose();
            }
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + OPNAME);
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

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "supersetOf", evaluatorClassName);
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

                assertThat(ksession.fireAllRules()).isEqualTo(1);
            } finally {
                ksession.dispose();
            }
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "supersetOf");
        }
    }

    // Case 3 new CustomOperatorWrapper + new EvaluatorDefnition

    public static class SupersetOfEvaluatorDefinitionCaseThree implements EvaluatorDefinition {
        public static final Operator SUPERSET_OF = Operator.addOperatorToRegistry(OPNAME, false);
        public static final Operator NOT_SUPERSET_OF = Operator.addOperatorToRegistry(OPNAME, true);
        private static final String[] SUPPORTED_IDS = {SUPERSET_OF.getOperatorString()};

        private Evaluator[] evaluator;

        public static int newInstanceCount =0;

        public SupersetOfEvaluatorDefinitionCaseThree() {
            newInstanceCount++;
        }

        public String[] getEvaluatorIds() {
            return SupersetOfEvaluatorDefinitionCaseThree.SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText, final Target leftTarget, final Target rightTarget) {
            return new SupersetOfEvaluatorCaseThree(type, isNegated);
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

    public static class SupersetOfEvaluatorCaseThree extends BaseEvaluator {

        public SupersetOfEvaluatorCaseThree(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? SupersetOfEvaluatorDefinitionCaseThree.NOT_SUPERSET_OF : SupersetOfEvaluatorDefinitionCaseThree.SUPERSET_OF);
        }

        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor extractor, final FactHandle factHandle, final FieldValue value) {
            final Object objectValue = extractor.getValue(valueResolver, factHandle);
            return evaluateAll((Collection) value.getValue(), (Collection) objectValue);
        }

        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor ira, final FactHandle left, final ReadAccessor ira1, final FactHandle right) {
            return evaluateAll((Collection) left.getObject(), (Collection) right.getObject());
        }

        public boolean evaluateCachedLeft(final ValueResolver valueResolver, final VariableRestriction.VariableContextEntry context, final FactHandle right) {
            final Object valRight = context.extractor.getValue(valueResolver, right.getObject());
            return evaluateAll((Collection) ((VariableRestriction.ObjectVariableContextEntry) context).left, (Collection) valRight);
        }

        public boolean evaluateCachedRight(final ValueResolver reteEvaluator, final VariableRestriction.VariableContextEntry context, final FactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(reteEvaluator, left);
            return evaluateAll((Collection) varLeft, (Collection) ((VariableRestriction.ObjectVariableContextEntry) context).right);
        }

        public boolean evaluateAll(final Collection leftCollection, final Collection rightCollection) {
            return rightCollection.containsAll(leftCollection);
        }
    }


    // Case 2 new CustomOperatorWrapper + getInstance(opName) for EvaluatorDefnition


    public static class SupersetOfEvaluatorDefinitionCaseTwo  implements EvaluatorDefinition {
        public static final Operator SUPERSET_OF = Operator.addOperatorToRegistry(OPNAME, false);
        public static final Operator NOT_SUPERSET_OF = Operator.addOperatorToRegistry(OPNAME, true);
        private static final String[] SUPPORTED_IDS = {SUPERSET_OF.getOperatorString()};

        private Evaluator[] evaluator;

        public static int newInstanceCount =0;

        private static final SupersetOfEvaluatorDefinitionCaseTwo INSTANCE = new SupersetOfEvaluatorDefinitionCaseTwo();


        public SupersetOfEvaluatorDefinitionCaseTwo() {
            newInstanceCount++;
        }

        public static SupersetOfEvaluatorDefinitionCaseTwo getInstance(String opName) {
            if (OPNAME.equals(opName)) {
                return INSTANCE;
            }
            return null;
        }

        public String[] getEvaluatorIds() {
            return SupersetOfEvaluatorDefinitionCaseTwo.SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText, final Target leftTarget, final Target rightTarget) {
            return new SupersetOfEvaluatorCaseTwo(type, isNegated);
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

    public static class SupersetOfEvaluatorCaseTwo extends BaseEvaluator {

        public SupersetOfEvaluatorCaseTwo(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? SupersetOfEvaluatorDefinitionCaseTwo.NOT_SUPERSET_OF : SupersetOfEvaluatorDefinitionCaseTwo.SUPERSET_OF);
        }

        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor extractor, final FactHandle factHandle, final FieldValue value) {
            final Object objectValue = extractor.getValue(valueResolver, factHandle);
            return evaluateAll((Collection) value.getValue(), (Collection) objectValue);
        }

        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor ira, final FactHandle left, final ReadAccessor ira1, final FactHandle right) {
            return evaluateAll((Collection) left.getObject(), (Collection) right.getObject());
        }

        public boolean evaluateCachedLeft(final ValueResolver valueResolver, final VariableRestriction.VariableContextEntry context, final FactHandle right) {
            final Object valRight = context.extractor.getValue(valueResolver, right.getObject());
            return evaluateAll((Collection) ((VariableRestriction.ObjectVariableContextEntry) context).left, (Collection) valRight);
        }

        public boolean evaluateCachedRight(final ValueResolver reteEvaluator, final VariableRestriction.VariableContextEntry context, final FactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(reteEvaluator, left);
            return evaluateAll((Collection) varLeft, (Collection) ((VariableRestriction.ObjectVariableContextEntry) context).right);
        }

        public boolean evaluateAll(final Collection leftCollection, final Collection rightCollection) {
            return rightCollection.containsAll(leftCollection);
        }
    }

    // Case 1 No CustomOperatorWrapper +  getInstance(opName) for EvaluatorDefnition

    public static class SupersetOfEvaluatorDefinitionCaseOne  implements EvaluatorDefinition, org.drools.model.functions.Operator.SingleValue<Object, Object>  {
        public static final Operator SUPERSET_OF = Operator.addOperatorToRegistry(OPNAME, false);
        public static final Operator NOT_SUPERSET_OF = Operator.addOperatorToRegistry(OPNAME, true);
        private static final String[] SUPPORTED_IDS = {SUPERSET_OF.getOperatorString()};

        private Evaluator[] evaluator;

        public static int newInstanceCount =0;

        private static final SupersetOfEvaluatorDefinitionCaseOne INSTANCE = new SupersetOfEvaluatorDefinitionCaseOne();


        public SupersetOfEvaluatorDefinitionCaseOne() {
            newInstanceCount++;
        }

        public static SupersetOfEvaluatorDefinitionCaseOne getInstance(String opName) {
            if (OPNAME.equals(opName)) {
                return INSTANCE;
            }
            return null;
        }

        public String[] getEvaluatorIds() {
            return SupersetOfEvaluatorDefinitionCaseOne.SUPPORTED_IDS;
        }

        public boolean isNegatable() {
            return true;
        }

        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText, final Target leftTarget, final Target rightTarget) {
            return new SupersetOfEvaluatorCaseOne(type, isNegated);
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

        // not used since there is no wrapper
        @Override
        public String getOperatorName() {
            return OPNAME;
        }
        @Override
        public boolean eval(Object o1, Object o2) {
             return getEvaluator(ValueType.OBJECT_TYPE, OPNAME, false, null).
                    evaluate(null, null, dummyFactHandleOf(o2), null, dummyFactHandleOf(o1));
        }

    }

    public static class SupersetOfEvaluatorCaseOne extends BaseEvaluator {

        public SupersetOfEvaluatorCaseOne(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? SupersetOfEvaluatorDefinitionCaseOne.NOT_SUPERSET_OF : SupersetOfEvaluatorDefinitionCaseOne.SUPERSET_OF);
        }

        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor extractor, final FactHandle factHandle, final FieldValue value) {
            final Object objectValue = extractor.getValue(valueResolver, factHandle);
            return evaluateAll((Collection) value.getValue(), (Collection) objectValue);
        }

        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor ira, final FactHandle left, final ReadAccessor ira1, final FactHandle right) {
            return evaluateAll((Collection) left.getObject(), (Collection) right.getObject());
        }

        public boolean evaluateCachedLeft(final ValueResolver valueResolver, final VariableRestriction.VariableContextEntry context, final FactHandle right) {
            final Object valRight = context.extractor.getValue(valueResolver, right.getObject());
            return evaluateAll((Collection) ((VariableRestriction.ObjectVariableContextEntry) context).left, (Collection) valRight);
        }

        public boolean evaluateCachedRight(final ValueResolver reteEvaluator, final VariableRestriction.VariableContextEntry context, final FactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(reteEvaluator, left);
            return evaluateAll((Collection) varLeft, (Collection) ((VariableRestriction.ObjectVariableContextEntry) context).right);
        }

        public boolean evaluateAll(final Collection leftCollection, final Collection rightCollection) {
            return rightCollection.containsAll(leftCollection);
        }
    }

}
