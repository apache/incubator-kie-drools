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

package org.drools.verifier.alwaysFalse;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.*;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.*;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

public class AlwaysFalseTest extends TestBaseOld {

    @Test
    public void testPatterns() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal("result",
                          result);

        // This pattern has an error.
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        Restriction r1 = LiteralRestriction.createRestriction(pattern1,
                                                              "");
        Restriction r2 = LiteralRestriction.createRestriction(pattern1,
                                                              "");
        Incompatibility i1 = new Incompatibility(r1,
                                                 r2);
        SubPattern pp1 = new SubPattern(pattern1,
                                        0);
        pp1.add(r1);
        pp1.add(r2);

        Restriction r3 = new VariableRestriction(pattern1);
        Restriction r4 = new VariableRestriction(pattern1);
        Incompatibility i2 = new Incompatibility(r1,
                                                 r2);
        SubPattern pp2 = new SubPattern(pattern1,
                                        1);
        pp2.add(r1);
        pp2.add(r2);

        // This pattern does not have an error.
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        Restriction r5 = LiteralRestriction.createRestriction(pattern2,
                                                              "");
        Restriction r6 = LiteralRestriction.createRestriction(pattern2,
                                                              "");
        SubPattern pp3 = new SubPattern(pattern2,
                                        0);
        pp3.add(r5);
        pp3.add(r6);

        Restriction r7 = new VariableRestriction(pattern2);
        Restriction r8 = new VariableRestriction(pattern2);
        Incompatibility i4 = new Incompatibility(r7,
                                                 r8);
        SubPattern pp4 = new SubPattern(pattern2,
                                        1);
        pp4.add(r7);
        pp4.add(r8);

        data.add(VerifierComponentMockFactory.createRule1());

        data.add(pattern1);
        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);
        data.add(i1);
        data.add(i2);
        data.add(pp1);
        data.add(pp2);

        data.add(pattern2);
        data.add(r5);
        data.add(r6);
        data.add(r7);
        data.add(r8);
        data.add(i4);
        data.add(pp3);
        data.add(pp4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Pattern that is always false"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        boolean works = false;
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof VerifierMessage) {
                VerifierMessage message = (VerifierMessage) o;
                if (message.getFaulty().equals(pattern1)) {
                    works = true;
                } else {
                    fail("There can be only one. (And this is not the one)");
                }
            }
        }

        assertEquals(1,
                     result.getBySeverity(Severity.ERROR).size());
        assertEquals(0,
                     result.getBySeverity(Severity.WARNING).size());
        assertEquals(0,
                     result.getBySeverity(Severity.NOTE).size());
        assertTrue(works);
    }

    /**
     * rule "test"
     * when
     * TestPattern()
     * then
     * # Nothing
     * end
     * <p/>
     * Check that a pattern with out restrictions does not raise any notifications.
     *
     * @throws Exception
     */
    @Test
    public void testSinglePatternNoRestrictions() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal("result",
                          result);

        // This pattern has an error.
        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        data.add(rule1);
        data.add(pattern1);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Pattern that is always false"));

        assertEquals(0,
                     result.getBySeverity(Severity.ERROR).size());
        assertEquals(0,
                     result.getBySeverity(Severity.WARNING).size());
        assertEquals(0,
                     result.getBySeverity(Severity.NOTE).size());
    }

    @Test
    public void testRules() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Rules.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal("result",
                          result);

        // This rule has an error.
        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        SubPattern pp1 = new SubPattern(pattern1,
                                        0);
        SubPattern pp2 = new SubPattern(pattern1,
                                        1);
        Incompatibility i1 = new Incompatibility(pp1,
                                                 pp2);
        SubRule rp1 = new SubRule(rule1,
                                  0);
        rp1.add(pp1);
        rp1.add(pp2);

        SubPattern pp3 = new SubPattern(pattern1,
                                        2);
        SubPattern pp4 = new SubPattern(pattern1,
                                        3);
        Incompatibility i2 = new Incompatibility(pp1,
                                                 pp2);
        SubRule rp2 = new SubRule(rule1,
                                  1);
        rp2.add(pp1);
        rp2.add(pp2);

        // This pattern does not have an error.
        VerifierRule rule2 = VerifierComponentMockFactory.createRule2();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        SubPattern pp5 = new SubPattern(pattern2,
                                        0);
        SubPattern pp6 = new SubPattern(pattern2,
                                        1);
        SubRule rp3 = new SubRule(rule2,
                                  2);
        rp3.add(pp5);
        rp3.add(pp6);

        SubPattern pp7 = new SubPattern(pattern2,
                                        2);
        SubPattern pp8 = new SubPattern(pattern2,
                                        3);
        Incompatibility i4 = new Incompatibility(pp7,
                                                 pp8);
        SubRule rp4 = new SubRule(rule2,
                                  3);
        rp4.add(pp7);
        rp4.add(pp8);

        data.add(rule1);
        data.add(pp1);
        data.add(pp2);
        data.add(pp3);
        data.add(pp4);
        data.add(i1);
        data.add(i2);
        data.add(rp1);
        data.add(rp2);

        data.add(rule2);
        data.add(pp5);
        data.add(pp6);
        data.add(pp7);
        data.add(pp8);
        data.add(i4);
        data.add(rp3);
        data.add(rp4);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Rule that is always false"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.ERROR).iterator();

        boolean works = false;
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof VerifierMessage) {
                VerifierMessage message = (VerifierMessage) o;
                if (message.getFaulty().equals(rule1)) {
                    works = true;
                } else {
                    fail("There can be only one. (And this is not the one)");
                }
            }
        }

        assertEquals(1,
                     result.getBySeverity(Severity.ERROR).size());
        assertEquals(0,
                     result.getBySeverity(Severity.WARNING).size());
        assertEquals(0,
                     result.getBySeverity(Severity.NOTE).size());
        assertTrue(works);
    }

    @Test
    public void testAlwaysFalse() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("AlwaysFalseTest.drl",
                getClass()),
                                      ResourceType.DRL);

        assertFalse(verifier.hasErrors());

        boolean noProblems = verifier.fireAnalysis();
        assertTrue(noProblems);

        Collection<VerifierMessageBase> notes = verifier.getResult().getBySeverity(Severity.ERROR);

        assertEquals(1,
                     containsMessageType(notes,
                                         MessageType.ALWAYS_FALSE));

        verifier.dispose();
    }

    private int containsMessageType(Collection<VerifierMessageBase> notes,
                                    MessageType type) {
        int amount = 0;
        for (VerifierMessageBase note : notes) {
            if (note instanceof VerifierMessage) {
                VerifierMessage message = (VerifierMessage) note;
                if (message.getMessageType() == type) {
                    amount++;
                }
            }
        }
        return amount;
    }

}
