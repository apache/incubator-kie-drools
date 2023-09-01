package org.drools.verifier.consequence;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class NamedConsequencesTest extends TestBaseOld {

    @Test
    void testMissingConsequence() throws Exception {

        InputStream in = getClass().getResourceAsStream("NamedConsequences.drl");

        KieSession session = getStatelessKieSession(in);

        VerifierReport result = VerifierReportFactory.newVerifierReport();

        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("ConsequenceTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("No action - possibly commented out"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.NOTE).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                VerifierMessage message = (VerifierMessage) o;
                rulesThatHadErrors.addAll(message.getImpactedRules().values());
            }
        }

        assertThat(rulesThatHadErrors.contains("This one is has an unused named consequence")).isFalse();

        if (!rulesThatHadErrors.isEmpty()) {
            for (String string : rulesThatHadErrors) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
