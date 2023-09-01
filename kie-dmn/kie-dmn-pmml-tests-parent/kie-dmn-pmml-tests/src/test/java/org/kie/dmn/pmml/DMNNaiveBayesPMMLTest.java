package org.kie.dmn.pmml;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DMNNaiveBayesPMMLTest {
    private static final Logger LOG = LoggerFactory.getLogger(DMNNaiveBayesPMMLTest.class);

    private DMNRuntime runtime;
    private DMNModel dmnModel;

    @Test
    public void testKMeans() {
        runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("NaiveBayes.dmn",
                                                                      DMNNaiveBayesPMMLTest.class,
                                                                      "test_naive_bayes.pmml");

        dmnModel = runtime.getModel("https://kiegroup.org/dmn/_51A1FD67-8A67-4332-9889-B718BE8B7456", "NaiveBayesDMN");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        assertThat(evaluateNaiveBayes(5.7, 3.8, 1.7, 0.3))
                .isEqualTo("setosa");
        assertThat(evaluateNaiveBayes(6.4, 2.8, 5.6, 2.1))
                .isEqualTo("virginica");
        assertThat(evaluateNaiveBayes(5.7, 2.9, 4.2, 1.3))
                .isEqualTo("versicolor");
    }

    private String evaluateNaiveBayes(final double sepalLength, final double sepalWidth, final double petalLength,
                                      final double petalWidth) {
        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("Sepal.Length", sepalLength);
        dmnContext.set("Sepal.Width", sepalWidth);
        dmnContext.set("Petal.Length", petalLength);
        dmnContext.set("Petal.Width", petalWidth);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision1").getResult()).isNotNull();
        final Map<String, Object> decisionResult = (Map<String, Object>) dmnResult.getDecisionResultByName("Decision1").getResult();
        final String predictedClassName = (String) decisionResult.get("Predicted_Species");

        return predictedClassName;
    }
}
