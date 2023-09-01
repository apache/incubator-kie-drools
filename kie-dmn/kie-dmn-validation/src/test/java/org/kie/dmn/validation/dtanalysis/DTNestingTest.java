package org.kie.dmn.validation.dtanalysis;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class DTNestingTest extends AbstractDTAnalysisTest {

    @Test
    public void testDTnestedEverywhere() {
        List<DMNMessage> validate = validator.validate(getReader("DTnestedEverywhere.dmn"), ANALYZE_DECISION_TABLE);
        checkPositiveTableNestedInSubcontextOfDecision(validate);
        checkNegativeTableInBKM(validate);
    }

    private void checkPositiveTableNestedInSubcontextOfDecision(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_83a8fbd6-ffcb-4068-ab6a-4a19086ce9c7");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

    private void checkNegativeTableInBKM(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_68839ac9-1d1b-4e12-9c4f-6b9048b860e1");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(Interval.POS_INF,
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
