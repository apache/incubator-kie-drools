package org.kie.dmn.validation.dtanalysis;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Contraction;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class ContractionRulesTest extends AbstractDTAnalysisTest {

    @Test
    public void testContractionRules() {
        List<DMNMessage> validate = validator.validate(getReader("Contraction.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_01d9abb9-b968-49c0-b6ab-909f3e03d8d3");
        assertThat(analysis.getGaps()).hasSize(0);
        assertThat(analysis.getOverlaps()).hasSize(0);

        // Contraction count.
        assertThat(analysis.getContractions()).hasSize(2);
        List<Contraction> results = Arrays.asList(new Contraction(4,
                                                                  List.of(5),
                                                                  2,
                                                                  List.of(new Interval(RangeBoundary.CLOSED, new BigDecimal("0.35"), Interval.POS_INF, RangeBoundary.CLOSED, 0, 0))),
                                                  new Contraction(3,
                                                                  List.of(6),
                                                                  1,
                                                                  List.of(new Interval(RangeBoundary.CLOSED, new BigDecimal("600"), Interval.POS_INF, RangeBoundary.CLOSED, 0, 0))));
        assertThat(results).hasSize(2);
        assertThat(analysis.getContractions()).containsAll(results);
        assertThat(validate.stream().filter(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_CONTRACTION_RULE)).collect(Collectors.toList()))
        	.as("It should contain 2 DMNMessage for the Contraction")
        	.hasSize(2);
    }
}
