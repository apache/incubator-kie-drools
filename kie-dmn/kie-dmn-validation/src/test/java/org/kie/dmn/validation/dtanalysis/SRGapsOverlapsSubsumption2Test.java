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
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class SRGapsOverlapsSubsumption2Test extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("SRGapsOverlapsSubsumption2.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_ccd87aa2-7081-4338-bafa-3a2cbf27c44c");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(2,
                                                               Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("660"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null),
                                                                                                    new Bound(Interval.POS_INF,
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)),
                                                                             Interval.newFromBounds(new Bound("Fair",
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound("Good",
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = List.of(new Overlap(Arrays.asList(2,
                                                                   4),
                                                     new Hyperrectangle(2,
                                                                        Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("660"),
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound(new BigDecimal("660"),
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null)),
                                                                                      Interval.newFromBounds(new Bound("Bad",
                                                                                                                       RangeBoundary.CLOSED,
                                                                                                                       null),
                                                                                                             new Bound("Fair",
                                                                                                                       RangeBoundary.OPEN,
                                                                                                                       null))))));
        assertThat(overlaps).hasSize(1);

        // Assert OVERLAPs same values
        assertThat(analysis.getOverlaps()).containsAll(overlaps);
    }
}
