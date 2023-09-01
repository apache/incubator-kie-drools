package  org.kie.pmml.models.clustering.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLClusteringModelEvaluatorTest {

    private PMMLClusteringModelEvaluator evaluator;

    @BeforeEach
    public void setUp(){
        evaluator = new PMMLClusteringModelEvaluator();
    }

    @Test
    void getPMMLModelType() {
        assertThat(evaluator.getPMMLModelType()).isEqualTo(PMML_MODEL.CLUSTERING_MODEL);
    }

}
