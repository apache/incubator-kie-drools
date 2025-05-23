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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.stream.Stream;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.Evaluator;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.impl.Operator;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.evaluators.BaseEvaluator;
import org.drools.mvel.evaluators.VariableRestriction;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.EvaluatorOption;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomOperatorWithBracketsTest {

    private static final String F_STR = DrlParser.ANTLR4_PARSER_ENABLED ? "##F_str" : "F_str";

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void customOperatorLiteral(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "rule R1\n" +
                        "when\n" +
                        "   Person( name " + F_STR + "[startsWith] \"J\")\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "F_str", F_StrEvaluatorDefinition.class.getName());
        try {
            KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            KieSession kieSession = kieBase.newKieSession();
            kieSession.insert(new Person("John", 35));
            int fired = kieSession.fireAllRules();
            assertThat(fired).isEqualTo(1);
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "F_str");
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void tmpBuiltin(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "rule R1\n" +
                        "when\n" +
                        "   Person( name str[startsWith] \"J\")\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "F_str", F_StrEvaluatorDefinition.class.getName());
        try {
            KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            KieSession kieSession = kieBase.newKieSession();
            kieSession.insert(new Person("John", 35));
            int fired = kieSession.fireAllRules();
            assertThat(fired).isEqualTo(1);
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "F_str");
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void customOperatorJoin(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "rule R1\n" +
                        "when\n" +
                        "   $s : String()\n" +
                        "   Person( name " + F_STR + "[startsWith] $s)\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "F_str", F_StrEvaluatorDefinition.class.getName());
        try {
            KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            KieSession kieSession = kieBase.newKieSession();
            kieSession.insert("J");
            kieSession.insert(new Person("John", 35));
            int fired = kieSession.fireAllRules();
            assertThat(fired).isEqualTo(1);
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "F_str");
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void customOperatorJoinRightInsert(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "rule R1\n" +
                        "when\n" +
                        "   $s : String()\n" +
                        "   Person( name " + F_STR + "[startsWith] $s)\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "F_str", F_StrEvaluatorDefinition.class.getName());
        try {
            KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            KieSession kieSession = kieBase.newKieSession();
            kieSession.insert("J");
            kieSession.insert(new Person("Paul", 32));
            kieSession.fireAllRules();
            kieSession.insert(new Person("John", 35));
            int fired = kieSession.fireAllRules();
            assertThat(fired).isEqualTo(1);
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "F_str");
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void customOperatorJoinRightInsertInverted(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // `$s F_str[startsWith] name` becomes an MVELConstraint
        // evaluate(ValueResolver, ReadAccessor, FactHandle, ReadAccessor, FactHandle) will be eventually called
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "rule R1\n" +
                        "when\n" +
                        "   $s : String()\n" +
                        "   Person( $s " + F_STR + "[startsWith] name)\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "F_str", F_StrEvaluatorDefinition.class.getName());
        try {
            KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
            KieSession kieSession = kieBase.newKieSession();
            kieSession.insert("John");
            kieSession.insert(new Person("P", 32));
            kieSession.fireAllRules();
            kieSession.insert(new Person("J", 35));
            int fired = kieSession.fireAllRules();
            assertThat(fired).isEqualTo(1);
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "F_str");
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testCustomOperatorCombiningConstraints(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // JBRULES-3517
        final String drl =
                "declare GN\n" +
                        "   gNo : Double\n" +
                        "end\n" +
                        "\n" +
                        "declare t547147\n" +
                        "   c547148 : String\n" +
                        "   c547149 : String\n" +
                        "end\n" +
                        "\n" +
                        "declare Tra48\n" +
                        "   gNo : Double\n" +
                        "   postCode : String\n" +
                        "   name : String\n" +
                        "   cnt : String\n" +
                        "end\n" +
                        "\n" +
                        "rule \"r548695.1\"\n" +
                        "no-loop true\n" +
                        "dialect \"mvel\"\n" +
                        "when\n" +
                        "   gnId : GN()\n" +
                        "   la : t547147( )\n" +
                        "   v1717 : Tra48( gnId.gNo == gNo, name " + F_STR + "[startsWith] la.c547148 || postCode " + F_STR + "[contains] la.c547149 )\n" +
                        "then\n" +
                        "end\n";

        System.setProperty(EvaluatorOption.PROPERTY_NAME + "F_str", F_StrEvaluatorDefinition.class.getName());
        try {
            KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
        } finally {
            System.clearProperty(EvaluatorOption.PROPERTY_NAME + "F_str");
        }
    }

    public static class F_StrEvaluatorDefinition implements EvaluatorDefinition {

        public static final Operator STR_COMPARE = Operator.addOperatorToRegistry("F_str", false);
        public static final Operator NOT_STR_COMPARE = Operator.addOperatorToRegistry("F_str", true);
        private static final String[] SUPPORTED_IDS = {STR_COMPARE.getOperatorString()};

        public enum Operations {
            startsWith,
            endsWith,
            contains
        }

        private Evaluator[] evaluator;

        @Override
        public String[] getEvaluatorIds() {
            return F_StrEvaluatorDefinition.SUPPORTED_IDS;
        }

        @Override
        public boolean isNegatable() {
            return true;
        }

        @Override
        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText, final Target leftTarget, final Target rightTarget) {
            final F_StrEvaluator evaluatorLocal = new F_StrEvaluator(type, isNegated);
            evaluatorLocal.setParameterText(parameterText);
            return evaluatorLocal;
        }

        @Override
        public Evaluator getEvaluator(final ValueType type, final String operatorId, final boolean isNegated, final String parameterText) {
            return getEvaluator(type, operatorId, isNegated, parameterText, Target.FACT, Target.FACT);
        }

        @Override
        public Evaluator getEvaluator(final ValueType type, final Operator operator, final String parameterText) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), parameterText);
        }

        @Override
        public Evaluator getEvaluator(final ValueType type, final Operator operator) {
            return this.getEvaluator(type, operator.getOperatorString(), operator.isNegated(), null);
        }

        @Override
        public boolean supportsType(final ValueType vt) {
            return true;
        }

        @Override
        public Target getTarget() {
            return Target.FACT;
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(evaluator);
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            evaluator = (Evaluator[]) in.readObject();
        }
    }

    public static class F_StrEvaluator extends BaseEvaluator {

        private F_StrEvaluatorDefinition.Operations parameter;

        public void setParameterText(final String parameterText) {
            this.parameter = F_StrEvaluatorDefinition.Operations.valueOf(parameterText);
        }

        public F_StrEvaluator(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? F_StrEvaluatorDefinition.NOT_STR_COMPARE : F_StrEvaluatorDefinition.STR_COMPARE);
        }

        // In this method, 'factHandle' is the left operand of the expression. 'value' is the right operand.
        @Override
        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor extractor, final FactHandle factHandle, final FieldValue value) {
            final Object objectValue = extractor.getValue(valueResolver, factHandle.getObject());
            final String objectValueString = (String) objectValue;
            return evaluateExpression(objectValueString, (String) value.getValue());
        }

        // In this method, 'left' and 'right' literally mean the left and right operands of the expression. No need to invert.
        // for example, [$s F_str[startsWith] name]
        //     left.getObject() is $s
        //     right.getObject() is name
        @Override
        public boolean evaluate(final ValueResolver valueResolver, final ReadAccessor ira, final FactHandle left, final ReadAccessor ira1, final FactHandle right) {
            return evaluateExpression((String) left.getObject(), (String) right.getObject());
        }

        // In this method, 'left' means leftInput to the JoinNode. 'right' means RightInput to the JoinNode.
        // To evaluate the expression, RightInput is the left operand and leftInput is the right operand. So, need to invert.
        // for example, [name F_str[startsWith] $s]
        //     valRight is name
        //     context.left is $s
        @Override
        public boolean evaluateCachedLeft(final ValueResolver valueResolver, final VariableRestriction.VariableContextEntry context, final FactHandle right) {
            final Object valRight = context.extractor.getValue(valueResolver, right.getObject());
            return evaluateExpression((String) valRight, (String) ((VariableRestriction.ObjectVariableContextEntry) context).left);
        }

        // In this method, 'left' means leftInput to the JoinNode. 'right' means RightInput to the JoinNode.
        // To evaluate the expression, RightInput is the left operand and leftInput is the right operand. So, need to invert.
        // for example, [name F_str[startsWith] $s]
        //     context.right is name
        //     varLeft is $s
        @Override
        public boolean evaluateCachedRight(final ValueResolver valueResolver, final VariableRestriction.VariableContextEntry context, final FactHandle left) {
            final Object varLeft = context.declaration.getExtractor().getValue(valueResolver, left.getObject());
            return evaluateExpression((String) ((VariableRestriction.ObjectVariableContextEntry) context).right, (String) varLeft);
        }

        // In this method, 'left' and 'right' literally mean the left and right operands of the expression.
        // for example, [name F_str[startsWith] $s]
        //     leftOperandString is name
        //     rightOperandString is $s
        private boolean evaluateExpression(final String leftOperandString, final String rightOperandString) {
            boolean result = ((leftOperandString != null) && (rightOperandString != null));

            if (result) {
                switch (parameter) {
                    case startsWith:
                        result = this.getOperator().isNegated() ^ (leftOperandString.startsWith(rightOperandString));
                        return result;
                    case endsWith:
                        result = this.getOperator().isNegated() ^ (leftOperandString.endsWith(rightOperandString));
                        return result;
                }
            }
            return result;
        }
    }
}
