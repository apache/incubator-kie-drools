import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.junit.Assert.assertEquals;

public class PMML${modelName}ModelEvaluatorTest {

    private PMML${modelName}ModelEvaluator evaluator;

    @Before
    public void setUp(){
        evaluator = new PMML${modelName}ModelEvaluator();
    }

    @Test
    public void getPMMLModelType(){
        assertEquals(PMML_MODEL.${modelNameUppercase}_MODEL, evaluator.getPMMLModelType());
    }

}
