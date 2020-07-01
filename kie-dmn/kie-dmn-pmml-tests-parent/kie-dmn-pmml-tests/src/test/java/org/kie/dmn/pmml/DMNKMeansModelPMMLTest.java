/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.pmml;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
        Assertions.assertThat(dmnModel).isNotNull();
        Assertions.assertThat(dmnModel.hasErrors()).isFalse();

        Assertions.assertThat(findCluster(5, 5)).isEqualTo("4");
        Assertions.assertThat(findCluster(5, -5)).isEqualTo("1");
        Assertions.assertThat(findCluster(-5, 5)).isEqualTo("3");
        Assertions.assertThat(findCluster(-5, -5)).isEqualTo("2");
    }

    private String findCluster(final int x, final int y) {
        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("x", x);
        dmnContext.set("y", y);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        Assertions.assertThat(dmnResult.hasErrors()).isFalse();
        Assertions.assertThat(dmnResult.getDecisionResultByName("Decision1").getResult()).isNotNull();
        final Map<String, Object> decisionResult = (Map<String, Object>) dmnResult.getDecisionResultByName("Decision1").getResult();
        final String clusterName = (String) decisionResult.get("predictedValue");

        return clusterName;
    }
}
