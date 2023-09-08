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
package org.drools.verifier.incoherence;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IncoherentPatternsTest extends TestBaseOld {

    @Test
    void testIncoherentPatternsInSubRule() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 1")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 2")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 7")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleVariables() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, variables"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 3")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 4")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 5")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 6")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleRangesLess() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, ranges when not conflicts with lesser value"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 8")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 12")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleRangesGreater() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, ranges when not conflicts with greater value"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 9")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 14")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleRangesEqualOrUnequal() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, ranges when not conflicts with equal or unequal value"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 10")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent patterns 15")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleRangesEqualOrUnequalVariables() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, ranges when not conflicts with equal or unequal variables"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 11")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleRangesEqualValue() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, ranges when not conflicts with equal value"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 16")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentPatternsInSubRuleRangesEqualVariable() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("PatternsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent Patterns in rule possibility, ranges when not conflicts with equal variable"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.WARNING).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierRule rule = (VerifierRule) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(rule.getName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent patterns 13")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
