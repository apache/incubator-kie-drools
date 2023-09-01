package org.drools.verifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Gap;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RangeCheckIntegersTest extends TestBaseOld {

    @Test
    void testSmallerOrEqual() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("rangeChecks/Integers.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Range check for integers, if smaller than or equal is missing"));

        Set<String> rulesThatHadErrors = new HashSet<String>();
        for (Object o : session.getObjects()) {
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertThat(rulesThatHadErrors.remove("Integer gap rule 4a")).isTrue();
        assertThat(rulesThatHadErrors.remove("Integer gap rule 5a")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testGreaterOrEqual() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("rangeChecks/Integers.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Range check for integers, if greater than or equal is missing"));

        Set<String> rulesThatHadErrors = new HashSet<String>();
        for (Object o : session.getObjects()) {
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertThat(rulesThatHadErrors.remove("Integer gap rule 4b")).isTrue();
        assertThat(rulesThatHadErrors.remove("Integer gap rule 5b")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testEqualAndGreaterThan() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("rangeChecks/Integers.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Range check for integers, equal and greater than"));

        Set<String> rulesThatHadErrors = new HashSet<String>();
        for (Object o : session.getObjects()) {
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertThat(rulesThatHadErrors.remove("Integer gap rule 1")).isTrue();
        assertThat(rulesThatHadErrors.remove("Integer gap rule 7b")).isTrue();
        assertThat(rulesThatHadErrors.remove("Integer gap rule 3")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }

    @Test
    void testEqualAndSmallerThan() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("rangeChecks/Integers.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass()
                .getResourceAsStream("MissingRangesForInts.drl"), result
                .getVerifierData());

        session.setGlobal("result", result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Range check for integers, equal and smaller than"));

        Set<String> rulesThatHadErrors = new HashSet<String>();
        for (Object o : session.getObjects()) {
            if (o instanceof Gap) {
                rulesThatHadErrors.add(((Gap) o).getRuleName());
            }
            // System.out.println(o);
        }

        assertThat(rulesThatHadErrors.remove("Integer gap rule 1")).isTrue();
        assertThat(rulesThatHadErrors.remove("Integer gap rule 6b")).isTrue();
        assertThat(rulesThatHadErrors.remove("Integer gap rule 2")).isTrue();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
