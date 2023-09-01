package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class NoGapsDateAdjacentTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDateAdjacent.dmn"), ANALYZE_DECISION_TABLE);

        DTAnalysis analysis = getAnalysis(validate, "_322732ef-01be-40fb-abd7-ec599c2efa47");
        assertThat(analysis.isError()).isFalse();
        assertThat(analysis.getGaps()).hasSize(0);
        assertThat(analysis.getOverlaps()).hasSize(0);
    }

}
