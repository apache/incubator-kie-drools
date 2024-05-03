/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.pmml;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DMNTreePMMLTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTreePMMLTest.class);
    private static final String SUNGLASSES = "sunglasses";
    private static final String UMBRELLA = "umbrella";
    private static final String NOTHING = "nothing";

    @Test
    public void treeWithOutput() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLTree.dmn",
                DMNTreePMMLTest.class,
                "test_tree.pmml");
        assertThat(runtime).isNotNull();
        assertThat(evaluateWeatherDecision(runtime, 30, 10)).isEqualTo(SUNGLASSES);
        assertThat(evaluateWeatherDecision(runtime, 5, 70)).isEqualTo(UMBRELLA);
        assertThat(evaluateWeatherDecision(runtime, 10, 15)).isEqualTo(NOTHING);
    }

    @Test
    public void treeWithoutOutput() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLTree_no_output.dmn",
                DMNTreePMMLTest.class,
                "test_tree_no_output.pmml");
        assertThat(runtime).isNotNull();
        assertThat(evaluateWeatherDecision(runtime, 30, 10)).isEqualTo(SUNGLASSES);
        assertThat(evaluateWeatherDecision(runtime, 5, 70)).isEqualTo(UMBRELLA);
        assertThat(evaluateWeatherDecision(runtime, 10, 15)).isEqualTo(NOTHING);
    }

    private String evaluateWeatherDecision(final DMNRuntime runtime, final Integer temperature, final Integer humidity) {
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_FAA4232D-9D61-4089-BB05-5F5D7C1AECE1", "TestTreeDMN");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("temperature", temperature);
        dmnContext.set("humidity", humidity);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(resultContext).isNotNull();
        assertThat(resultContext.get("Decision")).isInstanceOf(String.class);
        final String weatherDecision = (String) resultContext.get("Decision");
        assertThat(weatherDecision).isNotNull();

        return weatherDecision;
    }
}
