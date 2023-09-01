package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class DTinBKMTest extends AbstractDTAnalysisTest {
    @Test
    public void testDTnestedEverywhere() {
        List<DMNMessage> validate = validator.validate(getReader("dtInBKM.dmn"), ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_860A5A56-0C43-4B42-B1DB-7415984E5624");

        assertThat(analysis.getGaps()).hasSize(0);
        assertThat(analysis.getOverlaps()).hasSize(0);
        assertThat(validate).hasSize(1);
        assertThat(validate.get(0).getMessageType()).isEqualTo(DMNMessageType.DECISION_TABLE_ANALYSIS_EMPTY);
    }
}
