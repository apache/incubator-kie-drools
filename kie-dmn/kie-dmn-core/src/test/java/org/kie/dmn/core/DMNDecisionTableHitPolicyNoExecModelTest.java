/*
* Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core;

import java.math.BigDecimal;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDecisionTableHitPolicyNoExecModelTest extends BaseVariantNonTypeSafeTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionTableHitPolicyNoExecModelTest.class);

    public DMNDecisionTableHitPolicyNoExecModelTest(VariantTestConf testConfig) {
        super(testConfig);
    }
    
    @Test
    public void testShortCircuitFIRST() {
        final DMNRuntime runtime = createRuntime("First DT not stopping.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_e56151c4-d522-4974-88e8-f6c88ffaaba4", "Drawing 1");
        assertThat(dmnModel).isNotNull();

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).extracting("First Decision Table.nn abs").isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void testShortCircuitFIRST_withNullResults() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("First DT not stopping - null result.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_e56151c4-d522-4974-88e8-f6c88ffaaba4", "Drawing 1");
        assertThat(dmnModel).isNotNull();

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getDecisionResultByName("First Decision Table").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("First Decision Table").getResult()).asInstanceOf(InstanceOfAssertFactories.map(String.class, Object.class)).containsEntry("nn abs", null);
    }
}
