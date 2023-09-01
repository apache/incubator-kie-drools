package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;

public class SimpleStringNoGapTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("simpleStringNoGap.dmn"), VALIDATE_COMPILATION, VALIDATE_MODEL, ANALYZE_DECISION_TABLE);
        assertThat(validate).hasSize(1); // Gap Analysis skipped because of free string.
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DECISION_TABLE_GAP))).as("It should contain DMNMessage for the skipped gap analysis").isTrue();
        debugValidatorMsg(validate);

        DTAnalysis analysis = getAnalysis(validate, "_3D5BDDEF-8B71-4797-8662-5026A9C2A112");

        assertThat(analysis.getGaps()).hasSize(0);
        assertThat(analysis.getOverlaps()).hasSize(0);
    }
}
