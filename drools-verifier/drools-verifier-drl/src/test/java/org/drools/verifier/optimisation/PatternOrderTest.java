package org.drools.verifier.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class PatternOrderTest extends TestBaseOld {

    @Test
    void testEvalOrderInsideOperator() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("PatternOrder.drl"));

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("OptimisationPatternOrderTest.drl"),
                result.getVerifierData());

        session.setGlobal("result",
                result);

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Optimise evals inside pattern"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity(Severity.NOTE).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof VerifierMessage) {
                String name = ((VerifierMessage) o).getCauses().toArray(new RuleComponent[2])[0].getRuleName();

                ruleNames.add(name);
            }
        }

        assertThat(ruleNames.remove("Wrong eval order 1")).isTrue();

        if (!ruleNames.isEmpty()) {
            for (String string : ruleNames) {
                fail("Rule " + string + " caused an error.");
            }
        }
    }
}
