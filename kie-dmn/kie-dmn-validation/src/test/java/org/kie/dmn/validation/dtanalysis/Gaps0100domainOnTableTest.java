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

public class Gaps0100domainOnTableTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("Gaps0100-domainOnTable.dmn"), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_70a95e62-8f5b-4b75-8cb9-9a9f781077da");

        assertThat(analysis.getGaps()).hasSize(2);
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(new BigDecimal("0"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(new BigDecimal("0"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)))),
                                                  new Hyperrectangle(1,
                                                                     List.of(Interval.newFromBounds(new Bound(new BigDecimal("100"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null),
                                                                                                    new Bound(new BigDecimal("100"),
                                                                                                              RangeBoundary.CLOSED,
                                                                                                              null)))));
        assertThat(gaps).hasSize(2);

        // Assert GAPS
        assertThat(analysis.getGaps()).containsAll(gaps);
    }
}
