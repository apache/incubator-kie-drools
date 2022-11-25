/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.modelcompiler.operators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.BaseModelTest.RUN_TYPE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class InequalityComparisonTest extends BaseOperatorsTest {

    private static final RUN_TYPE[] RUN_TYPES = new BaseModelTest.RUN_TYPE[]{RUN_TYPE.STANDARD_FROM_DRL, RUN_TYPE.PATTERN_DSL};

    private static final Class[] TYPES = new Class[]{Integer.class, Long.class, Byte.class, Character.class, Short.class, Float.class, Double.class, BigInteger.class, BigDecimal.class};
    private static final String[] COMPARISON_OPERATORS = new String[]{"<", ">", "<=", ">="};
    private static final boolean[] PROPERTY_ON_LEFT = new boolean[]{true, false};

    @Parameters(name = "{0} {1} {2} {3}")
    public static Collection<Object[]> ruleParams() {
        List<Object[]> parameterData = new ArrayList<Object[]>();
        for (RUN_TYPE runType : RUN_TYPES) {
            for (Class type : TYPES) {
                for (String operator : COMPARISON_OPERATORS) {
                    for (boolean propertyOnLeft : PROPERTY_ON_LEFT)
                        parameterData.add(new Object[]{runType, type, operator, propertyOnLeft});
                }
            }
        }
        return parameterData;
    }

    @Parameterized.Parameter(0)
    public RUN_TYPE testRunType;

    @Parameterized.Parameter(1)
    public Class type;

    @Parameterized.Parameter(2)
    public String operator;

    @Parameterized.Parameter(3)
    public boolean propertyOnLeft;

    @Before
    public void setUp() {
        org.drools.compiler.rule.builder.util.ConstraintTestUtil.disableNormalizeConstraint();
        org.drools.modelcompiler.builder.generator.ConstraintTestUtil.disableNormalizeConstraint();
    }

    @After
    public void tearDown() {
        org.drools.compiler.rule.builder.util.ConstraintTestUtil.enableNormalizeConstraint();
        org.drools.modelcompiler.builder.generator.ConstraintTestUtil.enableNormalizeConstraint();
    }

    @Test
    public void operatorsWithNull() throws Exception {
        String propertyName = ArithmeticTest.getPropertyName(type);
        String instanceValueString = ArithmeticTest.getInstanceValueString(type);
        String drl = "import " + type.getCanonicalName() + ";\n" +
                     "import " + ValueHolder.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "when\n";
        if (propertyOnLeft) {
            drl += "  ValueHolder(" + propertyName + " " + operator + " " + instanceValueString + ")\n";
        } else {
            drl += "  ValueHolder(" + instanceValueString + " " + operator + " " + propertyName + ")\n";
        }
        drl += "then\n" +
               "end\n";

        System.out.println(drl);
        KieSession ksession = getKieSession(drl, testRunType);
        ksession.insert(new ValueHolder());
        try {
            int fired = ksession.fireAllRules();
            System.out.println("  => fired = " + fired);
            fail("Should throw NPE");
        } catch (org.drools.mvel.ConstraintEvaluationException | org.drools.modelcompiler.constraints.ConstraintEvaluationException e) {
            if (getRootCause(e) instanceof NullPointerException) {
                assertTrue(true);
            } else {
                throw new RuntimeException("Unexpected Cause", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected Exception", e);
        }
    }
}
