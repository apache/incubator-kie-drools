package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class SomeProblemruleOutsideDomainTest extends AbstractDTAnalysisTest {

    @Test
    public void test() {
        List<DMNMessage> validate = validator.validate(getReader("SomeProblem-ruleOutsideDomain.dmn"), ANALYZE_DECISION_TABLE);
        
        DTAnalysis analysis = getAnalysis(validate, "_4466518e-6240-46b0-bcb4-c7ddf5560e3a");
        assertThat(analysis.isError()).isTrue();
    }
}
