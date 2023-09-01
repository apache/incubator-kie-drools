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

public class PosDoubleNegHalfTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("posDoubleNegHalf.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_d18aa93e-3f67-4dda-9b36-93ae75835bdf");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(new BigDecimal("0"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // Overlaps:
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
