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

package org.kie.dmn.core.extra;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNRecursionTest extends BaseInterpretedVsCompiledTest {

    public DMNRecursionTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRecursionTest.class);

    @Test
    public void testBasicRecursion() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("basicRecursion.dmn", this.getClass());
        runtime.addListener(new DMNRuntimeEventListener() {});
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_16EF6BF4-9B59-40E8-8C99-A3A3D58B88CC", "factorial");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("My number", 3);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("compute factorial").getResult(), is(new BigDecimal(6)));
    }
}

