/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.missingEquality;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MissingEqualityTest extends TestBaseOld {

    @Test
    public void testMissingEqualityInLiteralRestrictions() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("MissingEquality.drl"));

        session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
                "Missing restriction in LiteralRestrictions"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingEqualityTest.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        session.executeWithResults(testData);

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((LiteralRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertTrue(ruleNames.remove("Missing equality 1"));
        assertTrue(ruleNames.remove("Missing equality 2"));

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    public void testMissingEqualityInVariableRestrictions() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("MissingEquality.drl"));

        session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
                "Missing restriction in VariableRestrictions, equal operator"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingEqualityTest.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

//        for (Object o : testData) {
//            if (o instanceof VariableRestriction) {
//                System.out.println(o);
//                VariableRestriction variableRestriction = (VariableRestriction) o;
//                System.out.println(variableRestriction.getOperator());
//            }
//        }

        session.executeWithResults(testData);

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((VariableRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertTrue(ruleNames.remove("Missing equality 5"));

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    public void testMissingEqualityInVariableRestrictions2() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("MissingEquality.drl"));

        session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
                "Missing restriction in VariableRestrictions, unequal operator"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingEqualityTest.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

//        for (Object o : testData) {
//            if (o instanceof VariableRestriction) {
//                System.out.println(o);
//                VariableRestriction variableRestriction = (VariableRestriction) o;
//                System.out.println(variableRestriction.getOperator());
//            }
//        }

        session.executeWithResults(testData);

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((VariableRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertTrue(ruleNames.remove("Missing equality 7"));

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    public void testMissingEqualityInVariableRestrictions3() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("MissingEquality.drl"));

        session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
                "Missing restriction in VariableRestrictions, custom operator"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingEqualityTest.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

//        for (Object o : testData) {
//            if (o instanceof VariableRestriction) {
//                System.out.println(o);
//                VariableRestriction variableRestriction = (VariableRestriction) o;
//                System.out.println(variableRestriction.getOperator());
//            }
//        }

        session.executeWithResults(testData);

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((VariableRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertTrue(ruleNames.remove("Missing equality 3"));
        assertTrue(ruleNames.remove("Missing equality 4"));
        assertTrue(ruleNames.remove("Missing equality 6"));

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
