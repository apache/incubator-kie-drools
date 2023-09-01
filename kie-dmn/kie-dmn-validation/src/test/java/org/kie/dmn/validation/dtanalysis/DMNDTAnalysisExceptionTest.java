package org.kie.dmn.validation.dtanalysis;

import org.junit.Test;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.v1_3.TDecisionTable;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDTAnalysisExceptionTest {

    @Test
    public void smokeTest() {
        DecisionTable dtRef = new TDecisionTable();
        DMNDTAnalysisException ut = new DMNDTAnalysisException("smoke test", dtRef);
        assertThat(ut.getDt()).isEqualTo(dtRef);
    }
}
