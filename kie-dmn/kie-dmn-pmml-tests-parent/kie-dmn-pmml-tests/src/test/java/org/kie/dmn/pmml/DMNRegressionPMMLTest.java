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

import java.math.BigDecimal;

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

public abstract class DMNRegressionPMMLTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRegressionPMMLTest.class);

    @Test
    public void regression() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLRegression.dmn",
                                                                                       DMNRegressionPMMLTest.class,
                                                                                       "test_regression.pmml");
        assertThat(runtime).isNotNull();
        assertThat(evaluateRegressionDecision(runtime, 0.0,0.0, "x")).isEqualTo(BigDecimal.valueOf(-2.5));
        assertThat(evaluateRegressionDecision(runtime, 0.0,0.0, "y")).isEqualTo(BigDecimal.valueOf(3.5));
        assertThat(evaluateRegressionDecision(runtime, 3.0,2.0, "y")).isEqualTo(BigDecimal.valueOf(52.5));
        assertThat(evaluateRegressionDecision(runtime, 5.0,-1.0, "x")).isEqualTo(BigDecimal.valueOf(120.5));
    }

    private BigDecimal evaluateRegressionDecision(final DMNRuntime runtime, final Double fld1, final Double fld2, final String fld3) {
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_51A1FD67-8A67-4332-9889-B718BE8B7456", "TestRegressionDMN");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("fld1", fld1);
        dmnContext.set("fld2", fld2);
        dmnContext.set("fld3", fld3);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(resultContext).isNotNull();
        assertThat(resultContext.get("Decision")).isInstanceOf(BigDecimal.class);
        final BigDecimal result = (BigDecimal) resultContext.get("Decision");

        return result;
    }
}
