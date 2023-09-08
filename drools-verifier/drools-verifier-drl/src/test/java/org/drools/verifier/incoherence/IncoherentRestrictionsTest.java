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

import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.DefaultVerifierConfiguration;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IncoherentRestrictionsTest extends TestBaseOld {

    @Test
    void testApprovedTrueAndNotTrue() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        String str = "";
        str += "package mortgages\n";
        str += "rule \"Bankruptcy history\"\n";
        str += "salience 10\n";
        str += "dialect \"mvel\"\n";
        str += "when\n";
        str += "Applicant( approved == \"true\" , approved != \"true\" )\n";
        str += "then\n";
        str += "end";

        DefaultVerifierConfiguration conf = new DefaultVerifierConfiguration();
        Verifier verifier = VerifierBuilderFactory.newVerifierBuilder().newVerifier(conf);
        verifier.addResourcesToVerify(ResourceFactory.newReaderResource(new StringReader(str)),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter(true,
                ScopesAgendaFilter.VERIFYING_SCOPE_KNOWLEDGE_PACKAGE));

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();

        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(3);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(1);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(0);

    }

    @Test
    void testIncoherentLiteralRestrictionsInSubPattern() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent LiteralRestrictions in pattern possibility"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 1")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 2")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentLiteralRestrictionsInSubPatternImpossibleRanges() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent LiteralRestrictions with ranges in pattern possibility, impossible ranges"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 8")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentLiteralRestrictionsInSubPatternImpossibleEqualityLess() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent LiteralRestrictions with ranges in pattern possibility, impossible equality less or equal"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 9")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 11")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentLiteralRestrictionsInSubPatternImpossibleEqualityGreater() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent LiteralRestrictions with ranges in pattern possibility, impossible equality greater"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 10")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentLiteralRestrictionsInSubPatternImpossibleRange() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent LiteralRestrictions with ranges in pattern possibility, impossible range"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 7")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentVariableRestrictionsInSubPattern() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent VariableRestrictions in pattern possibility"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 3")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 4")).isTrue();
        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 5")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testIncoherentVariableRestrictionsInSubPatternImpossibleRange() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Restrictions.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("RestrictionsTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incoherent VariableRestrictions in pattern possibility, impossible range"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
                rulesThatHadErrors.add(pattern.getRuleName());
            }
        }

        assertThat(rulesThatHadErrors.remove("Incoherent restrictions 6")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
