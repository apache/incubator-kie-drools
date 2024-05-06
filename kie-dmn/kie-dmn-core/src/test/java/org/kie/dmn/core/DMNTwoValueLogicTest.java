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
package org.kie.dmn.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

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

    public static final Logger LOG = LoggerFactory.getLogger(DMNTwoValueLogicTest.class);

    @BeforeAll
    static void setup() {
        runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Two-Value Logic Tests.dmn",
                DMNTwoValueLogicTest.class,
                "/libs/Two-Value Logic.dmn");
        dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_0062b41c-61d2-43db-a575-676ed3c6d967",
                "Two-Value Logic Tests");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        context = DMNFactory.newContext();
    }


    private void runTest(String decisionName, Object expected) {
        final DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, decisionName);
        LOG.debug("{}", dmnResult);
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get(decisionName)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionAll(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test all", Boolean.TRUE);
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionAny(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test any", Boolean.TRUE);
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionSum(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test sum", new BigDecimal(6, MathContext.DECIMAL128 ));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionMean(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test mean", new BigDecimal(20, MathContext.DECIMAL128 ));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionCount(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test count", new BigDecimal(3, MathContext.DECIMAL128 ));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionMax(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test max", new BigDecimal(30, MathContext.DECIMAL128 ));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionMin(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test min", new BigDecimal(10, MathContext.DECIMAL128 ));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionMedian(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test median", new BigDecimal(20, MathContext.DECIMAL128 ));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionMode(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test mode", List.of(new BigDecimal(20, MathContext.DECIMAL128)));
    }

    @ParameterizedTest
    @MethodSource("params")
    void functionStddev(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        runTest("Test stddev", new BigDecimal("8.164965809277260327324280249019638", MathContext.DECIMAL128 ) );
    }
}

