package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class NoGapsDomainOnTypeRefTest extends AbstractDTAnalysisTest {

    @Test
    public void test_NoGapsDomainOnTypeRef() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDomainOnTypeRef.dmn"), ANALYZE_DECISION_TABLE);

        checkAnalysis(validate);
    }

    @Test
    public void test_NoGapsDomainOnTypeRefv2() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDomainOnTypeRefv2.dmn"), ANALYZE_DECISION_TABLE);

        checkAnalysis(validate);
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis1 = getAnalysis(validate, "_E064FD38-56EA-40EB-97B4-F061ACD6F58F");
        assertThat(analysis1.isError()).isFalse();
        assertThat(analysis1.getGaps()).hasSize(0);
        assertThat(analysis1.getOverlaps()).hasSize(0);
    }

}
