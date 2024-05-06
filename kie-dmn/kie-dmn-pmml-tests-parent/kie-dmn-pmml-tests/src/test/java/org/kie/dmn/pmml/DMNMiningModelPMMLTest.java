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

public abstract class DMNMiningModelPMMLTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNMiningModelPMMLTest.class);

    @Test
    public void miningModelSum() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("SumMiningModel.dmn",
                                                                                       DMNMiningModelPMMLTest.class,
                                                                                       "test_mining_model_summed.pmml");


        assertThat(evaluateMiningModelDecision(runtime, 10, 10, 10))
                .isEqualTo(new BigDecimal(2070));
        assertThat(evaluateMiningModelDecision(runtime, 200, -1, 2))
                .isEqualTo(new BigDecimal(-299));
        assertThat(evaluateMiningModelDecision(runtime, 90, 2, 4))
                .isEqualTo(new BigDecimal(17040));
    }

    private BigDecimal evaluateMiningModelDecision(final DMNRuntime runtime, final double input1, final double input2,
                                                   final double input3) {
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_0E8EC382-BB89-4877-8D37-A59B64285F05", "MiningModelDMN");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("input1", input1);
        dmnContext.set("input2", input2);
        dmnContext.set("input3", input3);

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
