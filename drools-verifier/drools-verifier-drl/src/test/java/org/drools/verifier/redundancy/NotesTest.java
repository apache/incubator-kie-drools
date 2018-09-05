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

package org.drools.verifier.redundancy;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.*;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class NotesTest extends TestBaseOld {

    @Test
    public void testRedundantRestrictionsInPatternPossibilities() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Notes.drl"));

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        Collection<Object> objects = new ArrayList<Object>();
        LiteralRestriction left = LiteralRestriction.createRestriction(pattern,
                                                                       "");

        LiteralRestriction right = LiteralRestriction.createRestriction(pattern,
                                                                        "");

        Redundancy redundancy = new Redundancy(left,
                                               right);

        SubPattern possibility = new SubPattern(pattern,
                                                0);
        possibility.add(left);
        possibility.add(right);

        objects.add(left);
        objects.add(right);
        objects.add(redundancy);
        objects.add(possibility);

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal("result",
                          result);

        for (Object o : objects) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Find redundant restrictions from pattern possibilities"));

        Collection<VerifierMessageBase> notes = result.getBySeverity(Severity.NOTE);

        // Has at least one item.
        assertEquals(1,
                     notes.size());

        VerifierMessageBase note = notes.iterator().next();
        Iterator<Cause> causes = note.getCauses().iterator();

        assertEquals(left,
                     causes.next());
        assertEquals(right,
                     causes.next());
    }

    @Test
    public void testRedundantPatternPossibilitiesInRulePossibilities() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Notes.drl"));


        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        Collection<Object> objects = new ArrayList<Object>();
        SubPattern left = new SubPattern(pattern,
                                         0);

        SubPattern right = new SubPattern(pattern,
                                          1);

        Redundancy redundancy = new Redundancy(left,
                                               right);

        SubRule possibility = new SubRule(rule,
                                          0);
        possibility.add(left);
        possibility.add(right);

        objects.add(left);
        objects.add(right);
        objects.add(redundancy);
        objects.add(possibility);

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal("result",
                          result);

        for (Object o : objects) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Find redundant pattern possibilities from rule possibilities"));

        Collection<VerifierMessageBase> notes = result.getBySeverity(Severity.NOTE);

        // Has at least one item.
        assertEquals(1,
                     notes.size());

        VerifierMessageBase note = notes.iterator().next();
        Iterator<Cause> causes = note.getCauses().iterator();

        assertEquals(left,
                     causes.next());
        assertEquals(right,
                     causes.next());
    }
}
