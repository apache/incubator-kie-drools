package org.kie.pmml.models.drools.scorecard.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLScorecardModelEvaluatorTest {

    private PMMLScorecardModelEvaluator evaluator;

    @BeforeEach
    public void setUp() {
        evaluator = new PMMLScorecardModelEvaluator();
    }

    @Test
    void getPMMLModelType() {
        assertThat(evaluator.getPMMLModelType()).isEqualTo(PMML_MODEL.SCORECARD_MODEL);
    }
}
