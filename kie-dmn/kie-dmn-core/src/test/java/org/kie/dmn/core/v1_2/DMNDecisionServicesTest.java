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

package org.kie.dmn.core.v1_2;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNDecisionServicesTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTest.class);

    public DMNDecisionServicesTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDecisionServiceCompiler20180830_DMN12() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServiceABC_DMN12.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        org.kie.dmn.core.decisionservices.DMNDecisionServicesTest.testDecisionServiceCompiler20180830_testEvaluateDS(runtime, dmnModel);
        org.kie.dmn.core.decisionservices.DMNDecisionServicesTest.testDecisionServiceCompiler20180830_testEvaluateAll(runtime, dmnModel);
    }

}
