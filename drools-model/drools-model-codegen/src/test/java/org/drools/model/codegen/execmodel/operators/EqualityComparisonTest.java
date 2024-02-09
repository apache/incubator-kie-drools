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
package org.drools.model.codegen.execmodel.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.model.codegen.execmodel.BaseModelTest.RUN_TYPE;
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
public class EqualityComparisonTest extends BaseOperatorsTest {

    @Parameters(name = "{0} {1} {2} {3}")
    public static Collection<Object[]> ruleParams() {
        List<Object[]> parameterData = new ArrayList<Object[]>();
        for (RUN_TYPE runType : RUN_TYPES) {
            for (Class type : TYPES) {
                for (String operator : EQUALITY_COMPARISON_OPERATORS) {
                    for (boolean nullPropertyOnLeft : NULL_PROPERTY_ON_LEFT)
                        parameterData.add(new Object[]{runType, type, operator, nullPropertyOnLeft});
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
    public boolean nullPropertyOnLeft;

    @Before
    public void setUp() {
        org.drools.compiler.rule.builder.util.ConstraintTestUtil.disableNormalizeConstraint();
        org.drools.model.codegen.execmodel.generator.ConstraintTestUtil.disableNormalizeConstraint();
    }

    @After
    public void tearDown() {
        org.drools.compiler.rule.builder.util.ConstraintTestUtil.enableNormalizeConstraint();
        org.drools.model.codegen.execmodel.generator.ConstraintTestUtil.enableNormalizeConstraint();
    }

    @Test
    public void compareWithNullProperty() throws Exception {
        String propertyName = BaseOperatorsTest.getPropertyName(type);
        String instanceValueString = BaseOperatorsTest.getInstanceValueString(type);
        String drl = "import " + type.getCanonicalName() + ";\n" +
                     "import " + ValueHolder.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "when\n";
        if (nullPropertyOnLeft) {
            drl += "  ValueHolder(" + propertyName + " " + operator + " " + instanceValueString + ")\n";
        } else {
            drl += "  ValueHolder(" + instanceValueString + " " + operator + " " + propertyName + ")\n";
        }
        drl += "then\n" +
               "end\n";

        KieSession ksession = getKieSession(drl, testRunType);
        ksession.insert(new ValueHolder());
        try {
            int fired = ksession.fireAllRules();
            if (operator.equals("==") && fired == 0 || operator.equals("!=") && fired == 1) {
                assertTrue(true);
            } else {
                fail("Wrong result");
            }
        } catch (org.drools.mvel.ConstraintEvaluationException | org.drools.modelcompiler.constraints.ConstraintEvaluationException e) {
            throw new RuntimeException("Unexpected Exception", e);
        }
    }
}
