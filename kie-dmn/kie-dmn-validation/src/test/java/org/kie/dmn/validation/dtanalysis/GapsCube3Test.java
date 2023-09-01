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

public class GapsCube3Test extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("GapsCube3.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_4b41743d-ff04-4855-bf0b-993d475a9d62");

        assertThat(analysis.getGaps()).hasSize(4);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(3,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)))),
                                                  new Hyperrectangle(3,
                                                                     List.of(Interval.newFromBounds(new Bound(new BigDecimal("1"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null),
                                                                                                    new Bound(new BigDecimal("2"),
                                                                                                              RangeBoundary.OPEN,
                                                                                                              null)))),
                                                  new Hyperrectangle(3,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))),
                                                  new Hyperrectangle(3,
                                                                     Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("2"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("3"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null)),
                                                                                   Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                    RangeBoundary.CLOSED,
                                                                                                                    null),
                                                                                                          new Bound(new BigDecimal("1"),
                                                                                                                    RangeBoundary.OPEN,
                                                                                                                    null)))));
        assertThat(gaps).hasSize(4);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);
    }
}
