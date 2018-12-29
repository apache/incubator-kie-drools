/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.dmn.api.core.*;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.kie.dmn.core.util.DynamicTypeUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DMNTwoValueLogicTest extends BaseInterpretedVsCompiledTest {

    public DMNTwoValueLogicTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNTwoValueLogicTest.class);

    @Test
    public void testFunctionAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Two-Value Logic Tests.dmn",
                this.getClass(),
                "/libs/Two-Value Logic.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_0062b41c-61d2-43db-a575-676ed3c6d967",
                "Two-Value Logic Tests");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, "Test All");
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Test All"), is(Boolean.TRUE));
    }

    @Test
    public void testFunctionAny() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Two-Value Logic Tests.dmn",
                this.getClass(),
                "/libs/Two-Value Logic.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_0062b41c-61d2-43db-a575-676ed3c6d967",
                "Two-Value Logic Tests");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, "Test Any");
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Test Any"), is(Boolean.TRUE));
    }
}

