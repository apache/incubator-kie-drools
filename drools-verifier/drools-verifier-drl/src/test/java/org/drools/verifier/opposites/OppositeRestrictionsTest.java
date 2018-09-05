/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier.opposites;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.evaluators.Operator;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.*;
import org.drools.verifier.report.components.Cause;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OppositeRestrictionsTest extends OppositesBase {

    @Test
    public void testLiteralRestrictionOpposite() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r1.setFieldPath("0");
        r1.setOperator(Operator.EQUAL);
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r2.setFieldPath("0");
        r2.setOperator(Operator.NOT_EQUAL);
        r2.setOrderNumber(1);

        LiteralRestriction r3 = LiteralRestriction.createRestriction(pattern,
                                                                     "1.0");
        r3.setFieldPath("0");
        r3.setOperator(Operator.EQUAL);
        r3.setOrderNumber(2);

        LiteralRestriction r4 = LiteralRestriction.createRestriction(pattern,
                                                                     "1.0");
        r4.setFieldPath("0");
        r4.setOperator(Operator.NOT_EQUAL);
        r4.setOrderNumber(3);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Opposite LiteralRestrictions"));

        Map<Cause, Set<Cause>> map = createOppositesMap(VerifierComponentType.RESTRICTION,
                                                        (Iterator<Object>)session.getObjects().iterator());

        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r1,
                                                 r2) ^ TestBaseOld.causeMapContains(map,
                                                                                    r2,
                                                                                    r1)));
        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r3,
                                                 r4) ^ TestBaseOld.causeMapContains(map,
                                                                                    r4,
                                                                                    r3)));

        if (!map.isEmpty()) {
            fail("More opposites than was expected.");
        }
    }

    @Test
    public void testLiteralRestrictionOppositeWithRangesGreaterOrEqualAndLess() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r1.setFieldPath("0");
        r1.setOperator(Operator.GREATER_OR_EQUAL);
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r2.setFieldPath("0");
        r2.setOperator(Operator.LESS);
        r2.setOrderNumber(1);

        data.add(r1);
        data.add(r2);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Opposite LiteralRestrictions with ranges, greater or equal - less"));

        Map<Cause, Set<Cause>> map = createOppositesMap(VerifierComponentType.RESTRICTION,
                                                        (Iterator<Object>)session.getObjects().iterator());

        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r1,
                                                 r2) ^ TestBaseOld.causeMapContains(map,
                                                                                    r2,
                                                                                    r1)));

        if (!map.isEmpty()) {
            fail("More opposites than was expected.");
        }
    }

    @Test
    public void testLiteralRestrictionOppositeWithRangesGreaterAndLessOrEqual() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r1.setFieldPath("0");
        r1.setOperator(Operator.GREATER);
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r2.setFieldPath("0");
        r2.setOperator(Operator.LESS_OR_EQUAL);
        r2.setOrderNumber(1);

        data.add(r1);
        data.add(r2);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Opposite LiteralRestrictions with ranges, greater - less or equal"));

        Map<Cause, Set<Cause>> map = createOppositesMap(VerifierComponentType.RESTRICTION,
                                                        (Iterator<Object>)session.getObjects().iterator());

        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r1,
                                                 r2) ^ TestBaseOld.causeMapContains(map,
                                                                                    r2,
                                                                                    r1)));

        if (!map.isEmpty()) {
            fail("More opposites than was expected.");
        }
    }


    @Test
    public void testLiteralRestrictionOppositeWithRangesLessOrEqualAndGreaterOrEqualForIntsAndDates() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction(pattern,
                                                                     "1");
        r1.setFieldPath("0");
        r1.setOperator(Operator.GREATER_OR_EQUAL);
        r1.setOrderNumber(0);

        LiteralRestriction r2 = LiteralRestriction.createRestriction(pattern,
                                                                     "0");
        r2.setFieldPath("0");
        r2.setOperator(Operator.LESS_OR_EQUAL);
        r2.setOrderNumber(1);

        data.add(r1);
        data.add(r2);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Opposite LiteralRestrictions with ranges, less or equal - greater or equal for ints and dates"));

        Map<Cause, Set<Cause>> map = createOppositesMap(VerifierComponentType.RESTRICTION,
                                                        (Iterator<Object>)session.getObjects().iterator());

        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r1,
                                                 r2) ^ TestBaseOld.causeMapContains(map,
                                                                                    r2,
                                                                                    r1)));

        if (!map.isEmpty()) {
            fail("More opposites than was expected.");
        }
    }

    @Test
    public void testVariableRestrictionOpposite() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        Collection<Object> data = new ArrayList<Object>();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern(1);
        Pattern pattern2 = VerifierComponentMockFactory.createPattern(2);
        Pattern pattern3 = VerifierComponentMockFactory.createPattern(3);

        /*
         * Working pair
         */
        PatternVariable variable1 = new PatternVariable(rule);
        variable1.setParentPath("1");
        variable1.setParentType(VerifierComponentType.FIELD);
        variable1.setOrderNumber(-1);

        VariableRestriction r1 = new VariableRestriction(pattern1);
        r1.setFieldPath("0");
        r1.setOperator(Operator.GREATER_OR_EQUAL);
        r1.setVariable(variable1);
        r1.setOrderNumber(0);

        VariableRestriction r2 = new VariableRestriction(pattern1);
        r2.setFieldPath("0");
        r2.setOperator(Operator.LESS);
        r2.setVariable(variable1);
        r2.setOrderNumber(1);

        String containsOperator = "contains";

        PatternVariable variable2 = new PatternVariable(rule);
        variable2.setParentPath("2");
        variable2.setParentType(VerifierComponentType.FIELD);
        variable2.setOrderNumber(3);

        VariableRestriction r3 = new VariableRestriction(pattern2);
        r3.setFieldPath("1");
        r3.setOperator(Operator.determineOperator(containsOperator,
                                                  false));
        r3.setVariable(variable2);
        r3.setOrderNumber(4);

        VariableRestriction r4 = new VariableRestriction(pattern2);
        r4.setFieldPath("1");
        r4.setOperator(Operator.determineOperator(containsOperator,
                                                  true));
        r4.setVariable(variable2);
        r4.setOrderNumber(5);

        /*
         * Pair that doesn't work.
         */
        PatternVariable variable3 = new PatternVariable(rule);
        variable3.setParentPath("3");
        variable3.setParentType(VerifierComponentType.FIELD);
        variable3.setOrderNumber(6);

        VariableRestriction r5 = new VariableRestriction(pattern3);
        r5.setFieldPath("1");
        r5.setOperator(Operator.GREATER_OR_EQUAL);
        r5.setVariable(variable3);
        r5.setOrderNumber(7);

        VariableRestriction r6 = new VariableRestriction(pattern3);
        r6.setFieldPath("1");
        r6.setOperator(Operator.EQUAL);
        r6.setVariable(variable3);
        r6.setOrderNumber(8);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);
        data.add(r5);
        data.add(r6);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Opposite VariableRestrictions"));

        Map<Cause, Set<Cause>> map = createOppositesMap(VerifierComponentType.RESTRICTION,
                                                        (Iterator<Object>)session.getObjects().iterator());

        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r1,
                                                 r2) ^ TestBaseOld.causeMapContains(map,
                                                                                    r2,
                                                                                    r1)));
        assertTrue((TestBaseOld.causeMapContains(map,
                                                 r3,
                                                 r4) ^ TestBaseOld.causeMapContains(map,
                                                                                    r4,
                                                                                    r3)));

        if (!map.isEmpty()) {
            fail("More opposites than was expected.");
        }
    }
}
