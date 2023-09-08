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
package org.drools.verifier.incompatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.impl.Operator;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternVariable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.report.components.Cause;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IncompatibilityRestrictionsTest extends IncompatibilityBase {

    @Test
    void testLiteralRestrictionsIncompatibilityLessOrEqual() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern1,
                "10");
        r1.setOperator(Operator.BuiltInOperator.EQUAL.getOperator());
        r1.setFieldPath("0");
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern1,
                "1");
        r2.setOperator(Operator.BuiltInOperator.LESS.getOperator());
        r2.setFieldPath("0");
        r2.setOrderNumber(2);

        /*
         * Pair that doesn't work.
         */
        LiteralRestriction r3 = LiteralRestriction.createRestriction(pattern2,
                "1");
        r3.setOperator(Operator.BuiltInOperator.GREATER_OR_EQUAL.getOperator());
        r3.setFieldPath("1");
        r3.setOrderNumber(0);

        LiteralRestriction r4 = LiteralRestriction.createRestriction(pattern2,
                "10");
        r4.setOperator(Operator.BuiltInOperator.EQUAL.getOperator());
        r4.setFieldPath("1");
        r4.setOrderNumber(1);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incompatible LiteralRestrictions with ranges in pattern possibility, impossible equality less or equal"));

        Map<Cause, Set<Cause>> map = createIncompatibilityMap(VerifierComponentType.RESTRICTION,
                (Iterator<Object>) session.getObjects().iterator());

        assertThat((TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1))).isTrue();

        if (!map.isEmpty()) {
            fail("More incompatibilities than was expected.");
        }
    }

    @Test
    void testLiteralRestrictionsIncompatibilityGreater() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern1,
                "10");
        r1.setOperator(Operator.BuiltInOperator.GREATER.getOperator());
        r1.setFieldPath("0");
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern1,
                "1");
        r2.setOperator(Operator.BuiltInOperator.EQUAL.getOperator());
        r2.setFieldPath("0");
        r2.setOrderNumber(1);

        /*
         * Pair that doesn't work.
         */
        LiteralRestriction r3 = LiteralRestriction.createRestriction(pattern2,
                "1");
        r3.setOperator(Operator.BuiltInOperator.GREATER_OR_EQUAL.getOperator());
        r3.setFieldPath("1");
        r3.setOrderNumber(0);

        LiteralRestriction r4 = LiteralRestriction.createRestriction(pattern2,
                "10");
        r4.setOperator(Operator.BuiltInOperator.EQUAL.getOperator());
        r4.setFieldPath("1");
        r4.setOrderNumber(1);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incompatible LiteralRestrictions with ranges in pattern possibility, impossible equality greater"));

        Map<Cause, Set<Cause>> map = createIncompatibilityMap(VerifierComponentType.RESTRICTION,
                (Iterator<Object>) session.getObjects().iterator());

        assertThat((TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1))).isTrue();

        if (!map.isEmpty()) {
            fail("More incompatibilities than was expected.");
        }
    }

    @Test
    void testLiteralRestrictionsIncompatibilityImpossibleRange() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern1,
                "10");
        r1.setOperator(Operator.BuiltInOperator.GREATER.getOperator());
        r1.setFieldPath("0");
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern1,
                "10");
        r2.setOperator(Operator.BuiltInOperator.LESS.getOperator());
        r2.setFieldPath("0");
        r2.setOrderNumber(1);

        /*
         * Pair that doesn't work.
         */
        LiteralRestriction r3 = LiteralRestriction.createRestriction(pattern2,
                "1");
        r3.setOperator(Operator.BuiltInOperator.GREATER_OR_EQUAL.getOperator());
        r3.setFieldPath("1");
        r3.setOrderNumber(0);

        LiteralRestriction r4 = LiteralRestriction.createRestriction(pattern2,
                "");
        r4.setOperator(Operator.BuiltInOperator.EQUAL.getOperator());
        r4.setFieldPath("1");
        r4.setOrderNumber(1);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incompatible LiteralRestrictions with ranges in pattern possibility, impossible range"));

        Map<Cause, Set<Cause>> map = createIncompatibilityMap(VerifierComponentType.RESTRICTION,
                (Iterator<Object>) session.getObjects().iterator());

        assertThat((TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1))).isTrue();

        if (!map.isEmpty()) {
            fail("More incompatibilities than was expected.");
        }
    }

    @Test
    void testVariableRestrictionsIncompatibilityImpossibleRange() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));
        objectType.setFullName("org.test.Person");

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        PatternVariable variable1 = new PatternVariable(rule);
        variable1.setParentPath("0");
        variable1.setParentType(VerifierComponentType.FIELD);
        variable1.setOrderNumber(11);

        VariableRestriction r1 = new VariableRestriction(pattern1);
        r1.setOperator(Operator.BuiltInOperator.GREATER.getOperator());
        r1.setFieldPath("0");
        r1.setVariable(variable1);
        r1.setOrderNumber(0);

        VariableRestriction r2 = new VariableRestriction(pattern1);
        r2.setOperator(Operator.BuiltInOperator.LESS.getOperator());
        r2.setFieldPath("0");
        r2.setVariable(variable1);
        r2.setOrderNumber(1);

        /*
         * Pair that doesn't work.
         */
        PatternVariable variable2 = new PatternVariable(rule);
        variable2.setParentPath("1");
        variable2.setParentType(VerifierComponentType.FIELD);
        variable2.setOrderNumber(10);

        VariableRestriction r3 = new VariableRestriction(pattern2);
        r3.setOperator(Operator.BuiltInOperator.GREATER_OR_EQUAL.getOperator());
        r3.setFieldPath("1");
        r3.setVariable(variable2);
        r3.setOrderNumber(0);

        VariableRestriction r4 = new VariableRestriction(pattern2);
        r4.setOperator(Operator.BuiltInOperator.EQUAL.getOperator());
        r4.setFieldPath("1");
        r4.setVariable(variable2);
        r4.setOrderNumber(1);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent VariableRestrictions in pattern possibility, impossible range"));

        Map<Cause, Set<Cause>> map = createIncompatibilityMap(VerifierComponentType.RESTRICTION,
                (Iterator<Object>) session.getObjects().iterator());

        assertThat((TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1))).isTrue();

        if (!map.isEmpty()) {
            fail("More incompatibilities than was expected.");
        }
    }
}
