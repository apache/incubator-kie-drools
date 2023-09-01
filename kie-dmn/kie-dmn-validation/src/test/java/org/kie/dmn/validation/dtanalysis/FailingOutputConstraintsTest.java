package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class FailingOutputConstraintsTest extends AbstractDTAnalysisTest {

    @Test
    public void testFailingOutputConstraints() {
        List<DMNMessage> validate = validator.validate(getReader("FailingOutputConstraints.dmn"), ANALYZE_DECISION_TABLE);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR))).as("It should contain DMNMessage for output outside of LoV").isTrue();
        debugValidatorMsg(validate);

        DTAnalysis analysis = getAnalysis(validate, "_E72BD036-C550-4992-AA6D-A8AD4666C63A");
        assertThat(analysis.isError()).isFalse();
        assertThat(analysis.getGaps()).hasSize(1);
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
    
    @Test
    public void testFailingOutputConstraintsWhenOutputIsSymbol() {
        List<DMNMessage> validate = validator.validate(getReader("FailingOutputConstraints2.dmn"), ANALYZE_DECISION_TABLE);
        debugValidatorMsg(validate);
        assertThat(validate.stream().noneMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR))).as("It should NOT contain DMNMessage for output outside of LoV (using a symbol in output)").isTrue();

        DTAnalysis analysis = getAnalysis(validate, "_E72BD036-C550-4992-AA6D-A8AD4666C63A");
        assertThat(analysis.isError()).isFalse();
        assertThat(analysis.getGaps()).hasSize(1);
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
