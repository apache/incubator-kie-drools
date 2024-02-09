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
package org.drools.verifier.missingEquality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MissingEqualityTest extends TestBaseOld {

    @Test
    void testMissingEqualityInLiteralRestrictions() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingEqualityTest.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in LiteralRestrictions"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((LiteralRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertThat(ruleNames.remove("Missing equality 1")).isTrue();
        assertThat(ruleNames.remove("Missing equality 2")).isTrue();

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testMissingEqualityInVariableRestrictions() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));

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

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in VariableRestrictions, equal operator"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((VariableRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertThat(ruleNames.remove("Missing equality 5")).isTrue();

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testMissingEqualityInVariableRestrictions2() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));

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

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in VariableRestrictions, unequal operator"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((VariableRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertThat(ruleNames.remove("Missing equality 7")).isTrue();

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testMissingEqualityInVariableRestrictions3() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));

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

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in VariableRestrictions, custom operator"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(
                Severity.WARNING).iterator();

        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Cause cause = ((VerifierMessage) o).getFaulty();
                String name = ((VariableRestriction) cause).getRuleName();

                ruleNames.add(name);
            }
        }

        assertThat(ruleNames.remove("Missing equality 3")).isTrue();
        assertThat(ruleNames.remove("Missing equality 4")).isTrue();
        assertThat(ruleNames.remove("Missing equality 6")).isTrue();

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
