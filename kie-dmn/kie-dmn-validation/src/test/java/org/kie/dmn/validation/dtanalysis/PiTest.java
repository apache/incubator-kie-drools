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
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

public class PiTest extends AbstractDTAnalysisTest {

    @Test
    public void testPi() {
        List<DMNMessage> validate = validator.validate(getReader("Pi.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        checkAnalysis(validate);
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis = getAnalysis(validate, "_F32338CF-6F14-4E66-95AE-8BD4276BA75F");

        assertThat(analysis.getGaps()).hasSize(1);
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound(new BigDecimal("3.1415926535897932384626433832794"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound(new BigDecimal("3.1415926535897932384626433832794"),
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);

        // assert OVERLAPs count.
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
