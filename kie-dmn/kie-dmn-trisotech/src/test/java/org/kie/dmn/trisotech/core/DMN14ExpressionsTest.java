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
package org.kie.dmn.trisotech.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.trisotech.TrisotechDMNProfile;
import org.kie.dmn.trisotech.core.compiler.TrisotechDMNEvaluatorCompilerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMN14ExpressionsTest {

    private DMNRuntime runtime;
    private DMNModel model;
    private TestStrategy testConfig;

    public static Object[] params() {
        return new Object[]{TestStrategy.KIE_API, TestStrategy.DMNRUNTIMEBUILDER};
    }

    public static enum TestStrategy {
        KIE_API,
        DMNRUNTIMEBUILDER
    }

    public DMNRuntime createRuntime(String model, Class<?> class1) {
        if (testConfig == TestStrategy.DMNRUNTIMEBUILDER) {
            return createRuntimeUsingBuilder(model, class1);
        } else {
            return createRuntimeUsingKieAPI(model, class1);
        }
    }

    public DMNRuntime createRuntimeUsingKieAPI(String model, Class<?> class1) {
        final KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0");
        final KieFileSystem kfs = ks.newKieFileSystem();
        Resource modelResource = ks.getResources().newClassPathResource(model, class1);
        kfs.write(modelResource);
        kfs.writeKModuleXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                            "  <configuration>\n" +
                            "    <property key=\"org.kie.dmn.profiles.trisotech\" value=\"org.kie.dmn.trisotech.TrisotechDMNProfile\"/>\n" +
                            "    <property key=\"org.kie.dmn.decisionlogiccompilerfactory\" value=\"org.kie.dmn.trisotech.core.compiler.TrisotechDMNEvaluatorCompilerFactory\"/>\n" +
                            "  </configuration>\n" +
                            "</kmodule>");
        kfs.generateAndWritePomXML(releaseId);
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages()).as(kieBuilder.getResults().getMessages().toString()).isEmpty();
        final KieContainer kieContainer = ks.newKieContainer(releaseId);
        final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);
        return runtime;
    }

    public DMNRuntime createRuntimeUsingBuilder(String model, Class<?> class1) {
        return DMNRuntimeBuilder.fromDefaults()
                                .addProfile(new TrisotechDMNProfile())
                                .setDecisionLogicCompilerFactory(new TrisotechDMNEvaluatorCompilerFactory())
                                .buildConfiguration()
                                .fromClasspathResource(model, class1)
                                .getOrElseThrow(e -> new RuntimeException("Error initalizing DMNRuntime", e));
    }

    public void initDMN14ExpressionsTest(final TestStrategy testConfig) {
        this.testConfig = testConfig;
    }

    @BeforeEach
    void setup() {
        runtime = createRuntime("dmn14expressions.dmn", DMN14ExpressionsTest.class);
        assertThat(runtime).isNotNull();
        model = runtime.getModel("http://www.trisotech.com/definitions/_3404349f-5046-4ad3-ad15-7f1e27291ab5", "DMN 1.4 expressions");
        assertThat(model).isNotNull();
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void conditionalWithInput(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Boolean Input", true)), "Conditional");
        assertThat(results.getDecisionResultByName("Conditional").getResult()).isEqualTo("Conditional evaluated to TRUE");

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Boolean Input", false)), "Conditional");
        assertThat(results.getDecisionResultByName("Conditional").getResult()).isEqualTo("Conditional evaluated to FALSE");

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Boolean Input", null)), "Conditional");
        assertThat(results.getDecisionResultByName("Conditional").getResult()).isEqualTo("Conditional evaluated to FALSE");

    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void conditionalNonBooleanIf(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(), "Non boolean");
        assertThat(results.getMessages()).hasSize(1);
        assertThat(results.getMessages().iterator().next().getMessageType()).isEqualTo(DMNMessageType.ERROR_EVAL_NODE);

    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void iteratorFor(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 1)), "Addition");
        assertThat(results.getDecisionResultByName("Addition").getResult().toString()).isEqualTo(Arrays.asList(2, 3, 4, 5).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 2)), "Addition");
        assertThat(results.getDecisionResultByName("Addition").getResult().toString()).isEqualTo(Arrays.asList(3, 4, 5, 6).toString());
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void iteratorForPartial(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 0)), "Addition Partial");
        assertThat(results.getDecisionResultByName("Addition Partial").getResult().toString()).isEqualTo(Arrays.asList(1, 3, 6, 10).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Addition Partial");
        assertThat(results.getDecisionResultByName("Addition Partial").getResult().toString()).isEqualTo(Arrays.asList(1, 8, 16, 25).toString());
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void iteratorForInRangeClose(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 1)), "Addition Range Close");
        assertThat(results.getDecisionResultByName("Addition Range Close").getResult().toString()).isEqualTo(Arrays.asList(3, 4).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 2)), "Addition Range Close");
        assertThat(results.getDecisionResultByName("Addition Range Close").getResult().toString()).isEqualTo(Arrays.asList(4, 5).toString());
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void iteratorForInRangeOpen(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 1)), "Addition Range Open");
        assertThat(results.getDecisionResultByName("Addition Range Open").getResult().toString()).isEqualTo(Arrays.asList(2, 3, 4, 5).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 2)), "Addition Range Open");
        assertThat(results.getDecisionResultByName("Addition Range Open").getResult().toString()).isEqualTo(Arrays.asList(3, 4, 5, 6).toString());
    }


    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void iteratorSome(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Number Greater Exists");
        assertThat(((Boolean) results.getDecisionResultByName("Number Greater Exists").getResult()).booleanValue()).isTrue();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", -1)), "Number Greater Exists");
        assertThat(((Boolean) results.getDecisionResultByName("Number Greater Exists").getResult()).booleanValue()).isTrue();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 11)), "Number Greater Exists");
        assertThat(((Boolean) results.getDecisionResultByName("Number Greater Exists").getResult()).booleanValue()).isFalse();
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void iteratorEvery(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "All Greater");
        assertThat(((Boolean) results.getDecisionResultByName("All Greater").getResult()).booleanValue()).isFalse();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", -1)), "All Greater");
        assertThat(((Boolean) results.getDecisionResultByName("All Greater").getResult()).booleanValue()).isTrue();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 11)), "All Greater");
        assertThat(((Boolean) results.getDecisionResultByName("All Greater").getResult()).booleanValue()).isFalse();
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void filterIndex(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Match by index");
        assertThat(results.getDecisionResultByName("Match by index").getResult()).isEqualTo(new BigDecimal(5));

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", -2)), "Match by index");
        assertThat(results.getDecisionResultByName("Match by index").getResult()).isEqualTo(new BigDecimal(9));

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 0)), "Match by index");
        assertThat(results.getMessages()).hasSizeGreaterThan(0);

    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void filterExpression(final TestStrategy testConfig) throws Throwable {
        initDMN14ExpressionsTest(testConfig);
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Match by Fnct");
        assertThat(results.getDecisionResultByName("Match by Fnct").getResult().toString()).isEqualTo(Arrays.asList(6, 7, 8, 9, 10).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 11)), "Match by Fnct");
        assertThat(results.getDecisionResultByName("Match by Fnct").getResult().toString()).isEqualTo(List.of().toString());

    }
}
