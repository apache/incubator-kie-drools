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

public abstract class DMNKMeansModelPMMLTest {
    private static final Logger LOG = LoggerFactory.getLogger(DMNKMeansModelPMMLTest.class);

    private DMNRuntime runtime;
    private DMNModel dmnModel;

    @Test
    public void testKMeans() {
        runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KMeans.dmn",
                                                                      DMNKMeansModelPMMLTest.class,
                                                                      "test_kmeans.pmml");

        dmnModel = runtime.getModel("https://kiegroup.org/dmn/_51A1FD67-8A67-4332-9889-B718BE8B7456", "KMeansDMN");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        assertThat(findCluster(5, 5)).isEqualTo("4");
        assertThat(findCluster(5, -5)).isEqualTo("1");
        assertThat(findCluster(-5, 5)).isEqualTo("3");
        assertThat(findCluster(-5, -5)).isEqualTo("2");
    }

    private String findCluster(final int x, final int y) {
        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("x", x);
        dmnContext.set("y", y);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision1").getResult()).isNotNull();
        final Map<String, Object> decisionResult = (Map<String, Object>) dmnResult.getDecisionResultByName("Decision1").getResult();
        final String clusterName = (String) decisionResult.get("predictedValue");

        return clusterName;
    }
}
