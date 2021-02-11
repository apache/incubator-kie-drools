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

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.api.core.*;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.kie.dmn.core.util.DynamicTypeUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * The goal of this test case is just test that the functions are wired properly on the
 * combined decision service.
 *
 * The functions themselves are tested directly, using unit tests, and as such this
 * test case only has a single scenario for each function.
 */
public class DMNTwoValueLogicTest extends BaseInterpretedVsCompiledTest {

    private static DMNRuntime runtime;
    private static DMNContext context;
    private static DMNModel dmnModel;

    public DMNTwoValueLogicTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNTwoValueLogicTest.class);

    @BeforeClass
    public static void setup() {
        runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Two-Value Logic Tests.dmn",
                DMNTwoValueLogicTest.class,
                "/libs/Two-Value Logic.dmn");
        dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_0062b41c-61d2-43db-a575-676ed3c6d967",
                "Two-Value Logic Tests");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));
        context = DMNFactory.newContext();
    }


    private void runTest(String decisionName, Object expected) {
        final DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, decisionName);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get(decisionName), is(expected));
    }

    @Test
    public void testFunctionAll() {
        runTest("Test all", Boolean.TRUE);
    }

    @Test
    public void testFunctionAny() {
        runTest("Test any", Boolean.TRUE);
    }

    @Test
    public void testFunctionSum() {
        runTest("Test sum", new BigDecimal(6, MathContext.DECIMAL128 ));
    }

    @Test
    public void testFunctionMean() {
        runTest("Test mean", new BigDecimal(20, MathContext.DECIMAL128 ));
    }

    @Test
    public void testFunctionCount() {
        runTest("Test count", new BigDecimal(3, MathContext.DECIMAL128 ));
    }

    @Test
    public void testFunctionMax() {
        runTest("Test max", new BigDecimal(30, MathContext.DECIMAL128 ));
    }

    @Test
    public void testFunctionMin() {
        runTest("Test min", new BigDecimal(10, MathContext.DECIMAL128 ));
    }

    @Test
    public void testFunctionMedian() {
        runTest("Test median", new BigDecimal(20, MathContext.DECIMAL128 ));
    }

    @Test
    public void testFunctionMode() {
        runTest("Test mode", Arrays.asList( new BigDecimal(20, MathContext.DECIMAL128 ) ) );
    }

    @Test
    public void testFunctionStddev() {
        runTest("Test stddev", new BigDecimal("8.164965809277260327324280249019638", MathContext.DECIMAL128 ) );
    }
}

