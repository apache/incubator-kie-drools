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

public class OverlapsMsgTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("improveHF.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_CA4B40F8-2354-48B6-B323-5C4E4B2CE467");

        // Assert GAPs
        assertThat(analysis.getGaps()).hasSize(0);

        // assert OVERLAPs
        assertThat(analysis.getOverlaps()).hasSize(2);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Overlap> overlaps = Arrays.asList(new Overlap(Arrays.asList(3,
                                                                         1),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))),
                                               new Overlap(Arrays.asList(2,
                                                                         3),
                                                           new Hyperrectangle(2,
                                                                              Arrays.asList(Interval.newFromBounds(new Bound(new BigDecimal("47"),
                                                                                                                             RangeBoundary.OPEN,
                                                                                                                             null),
                                                                                                                   new Bound(Interval.POS_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null)),
                                                                                            Interval.newFromBounds(new Bound(Interval.NEG_INF,
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null),
                                                                                                                   new Bound(new BigDecimal("0"),
                                                                                                                             RangeBoundary.CLOSED,
                                                                                                                             null))))));
        assertThat(overlaps).hasSize(2);

        assertThat(analysis.getOverlaps()).containsAll(overlaps);

        assertThat(analysis.getOverlaps().get(0).getOverlap().asHumanFriendly(analysis.getDdtaTable())).isEqualTo("[ -, >0 ]");
        assertThat(analysis.getOverlaps().get(1).getOverlap().asHumanFriendly(analysis.getDdtaTable())).isEqualTo("[ >47, <=0 ]");
    }
}
