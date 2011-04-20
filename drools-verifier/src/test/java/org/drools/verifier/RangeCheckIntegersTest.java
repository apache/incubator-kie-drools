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

package org.drools.verifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Gap;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class RangeCheckIntegersTest extends TestBaseOld {

    @Test
    public void testSmallerOrEqual() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("rangeChecks/Integers.drl"));

        session
                .setAgendaFilter(new RuleNameMatchesAgendaFilter(
                        "Range check for integers, if smaller than or equal is missing"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        StatelessSessionResult sessionResult = session
                .executeWithResults(testData);

        Iterator<Object> iter = sessionResult.iterateObjects();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertTrue(rulesThatHadErrors.remove("Integer gap rule 4a"));
        assertTrue(rulesThatHadErrors.remove("Integer gap rule 5a"));

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    public void testGreaterOrEqual() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("rangeChecks/Integers.drl"));

        session
                .setAgendaFilter(new RuleNameMatchesAgendaFilter(
                        "Range check for integers, if greater than or equal is missing"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        StatelessSessionResult sessionResult = session
                .executeWithResults(testData);

        Iterator<Object> iter = sessionResult.iterateObjects();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertTrue(rulesThatHadErrors.remove("Integer gap rule 4b"));
        assertTrue(rulesThatHadErrors.remove("Integer gap rule 5b"));

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    public void testEqualAndGreaterThan() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("rangeChecks/Integers.drl"));

        session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
                "Range check for integers, equal and greater than"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        StatelessSessionResult sessionResult = session
                .executeWithResults(testData);

        Iterator<Object> iter = sessionResult.iterateObjects();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertTrue(rulesThatHadErrors.remove("Integer gap rule 1"));
        assertTrue(rulesThatHadErrors.remove("Integer gap rule 7b"));
        assertTrue(rulesThatHadErrors.remove("Integer gap rule 3"));

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    public void testEqualAndSmallerThan() throws Exception {
        StatelessSession session = getStatelessSession(this.getClass()
                .getResourceAsStream("rangeChecks/Integers.drl"));

        session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
                "Range check for integers, equal and smaller than"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        StatelessSessionResult sessionResult = session
                .executeWithResults(testData);

        Iterator<Object> iter = sessionResult.iterateObjects();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertTrue(rulesThatHadErrors.remove("Integer gap rule 1"));
        assertTrue(rulesThatHadErrors.remove("Integer gap rule 6b"));
        assertTrue(rulesThatHadErrors.remove("Integer gap rule 2"));

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
