package org.drools.verifier;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import org.drools.verifier.components.SubPattern;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.junit.jupiter.api.Test;

public class PatternSolverDRLTest extends TestBaseOld {

    @Test
    void testOrInsidePattern() throws Exception {

        StringBuffer rule = new StringBuffer();
        rule.append("rule \"Test rule\" ");
        rule.append("   when ");
        rule.append("       customer : Customer( status > 30 && < 50 ) ");
        rule.append("       order : OrderHeader( customer == customer , orderPriority == 3 || == 4 ) ");
        rule.append("   then ");
        rule.append("       order.setOrderDiscount( 6.0 ); ");
        rule.append("end");

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(new ByteArrayInputStream( rule.toString().getBytes() ),
                result.getVerifierData());

        int patternCount = 0;

        // Check that there is three pattern possibilities and that they contain
        // the right amount of items.
        for (Object o : testData) {
            if (o instanceof SubPattern) {
                SubPattern pp = (SubPattern) o;
                if (pp.getItems().size() == 2) {

                    patternCount++;
                }
            }
        }

        assertThat(patternCount).isEqualTo(3);
    }
}
