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
import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class YCombinatorTest extends BaseInterpretedVsCompiledTest {

    public YCombinatorTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(YCombinatorTest.class);

    @Test
    public void testY() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Y.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_2E160C58-B13A-4C35-B161-BB4B31E049B4",
                                                   "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext ctx = runtime.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("fac3").getResult(), is(new BigDecimal(6)));
        assertThat(dmnResult.getDecisionResultByName("fib5").getResult(), is(Arrays.asList(new BigDecimal(1),
                                                                                           new BigDecimal(1),
                                                                                           new BigDecimal(2),
                                                                                           new BigDecimal(3),
                                                                                           new BigDecimal(5))));
    }

    @Test
    public void testYboxed() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Yboxed.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_2E160C58-B13A-4C35-B161-BB4B31E049B4",
                                                   "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext ctx = runtime.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("fac3").getResult(), is(new BigDecimal(6)));
        assertThat(dmnResult.getDecisionResultByName("fib5").getResult(), is(Arrays.asList(new BigDecimal(1),
                                                                                           new BigDecimal(1),
                                                                                           new BigDecimal(2),
                                                                                           new BigDecimal(3),
                                                                                           new BigDecimal(5))));
    }

}

