package org.kie.pmml.models.drools.tree.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLTreeModelEvaluatorTest {

    private PMMLTreeModelEvaluator evaluator;

    @BeforeEach
    public void setUp() {
        evaluator = new PMMLTreeModelEvaluator();
    }

    @Test
    void getPMMLModelType() {
        assertThat(evaluator.getPMMLModelType()).isEqualTo(PMML_MODEL.TREE_MODEL);
    }
}
