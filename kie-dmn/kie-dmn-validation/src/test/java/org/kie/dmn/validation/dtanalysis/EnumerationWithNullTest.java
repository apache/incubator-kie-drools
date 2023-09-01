package org.kie.dmn.validation.dtanalysis;

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

public class EnumerationWithNullTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("EnumerationWithNull.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_5ef1ff81-621d-4c9a-9881-0aaf865758cb");

        assertThat(analysis.getGaps()).hasSize(1);

        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = List.of(new Hyperrectangle(1,
                                                               List.of(Interval.newFromBounds(new Bound("c",
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null),
                                                                                              new Bound("c",
                                                                                                        RangeBoundary.CLOSED,
                                                                                                        null)))));
        assertThat(gaps).hasSize(1);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);
    }
}
