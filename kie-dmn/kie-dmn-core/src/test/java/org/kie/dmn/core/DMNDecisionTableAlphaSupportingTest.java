/*
* Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNDecisionTableAlphaSupportingTest extends BaseInterpretedVsCompiledTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DMNDecisionTableAlphaSupportingTest.class);

    public DMNDecisionTableAlphaSupportingTest(final boolean useExecModelCompiler ) {
        super( useExecModelCompiler );
    }

    @Test
    public void testSimpleDecision() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("alphasupport.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c0cf6e20-0b43-43ce-9def-c759a5f86df2", "DMN Specification Chapter 11 Example Reduced");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = runtime.newContext();
        context.set("Existing Customer", "false");
        context.set("Application Risk Score", new BigDecimal("123"));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.info("{}", dmnResult);
        assertThat(dmnResult.getContext().get("Pre-bureau risk category table"), is("LOW"));
    }
}
