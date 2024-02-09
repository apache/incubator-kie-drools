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
package org.kie.dmn.core.v1_4;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;

public class DMN14ExpressionsTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN14ExpressionsTest.class);
    
    private DMNRuntime runtime;
    private DMNModel model;

    public DMN14ExpressionsTest(final BaseVariantTest.VariantTestConf conf) {
        super(conf);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_DEFAULT_NOCL_TYPECHECK}; // only variants needed until DMNv1.4 is actually published
    }


    @Before
    public void setup() {
        runtime = createRuntime("dmn14expressions.dmn", DMN14ExpressionsTest.class);
        assertThat(runtime).isNotNull();
        model = runtime.getModel("http://www.trisotech.com/definitions/_3404349f-5046-4ad3-ad15-7f1e27291ab5", "DMN 1.4 expressions");
        assertThat(model).isNotNull();
    }

    @Test
    public void testConditionalWithInput() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Boolean Input", true)), "Conditional");
        assertThat(results.getDecisionResultByName("Conditional").getResult()).isEqualTo("Conditional evaluated to TRUE");

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Boolean Input", false)), "Conditional");
        assertThat(results.getDecisionResultByName("Conditional").getResult()).isEqualTo("Conditional evaluated to FALSE");

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Boolean Input", null)), "Conditional");
        assertThat(results.getDecisionResultByName("Conditional").getResult()).isEqualTo("Conditional evaluated to FALSE");

    }

    @Test
    public void testConditionalNonBooleanIf() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(), "Non boolean");
        assertThat(results.getMessages()).hasSize(1);
        assertThat(results.getMessages().iterator().next().getMessageType()).isEqualTo(DMNMessageType.ERROR_EVAL_NODE);

    }

    @Test
    public void testIteratorFor() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 1)), "Addition");
        assertThat(results.getDecisionResultByName("Addition").getResult().toString()).isEqualTo(Arrays.asList(2, 3, 4, 5).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 2)), "Addition");
        assertThat(results.getDecisionResultByName("Addition").getResult().toString()).isEqualTo(Arrays.asList(3, 4, 5, 6).toString());
    }

    @Test
    public void testIteratorForPartial() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 0)), "Addition Partial");
        assertThat(results.getDecisionResultByName("Addition Partial").getResult().toString()).isEqualTo(Arrays.asList(1, 3, 6, 10).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Addition Partial");
        assertThat(results.getDecisionResultByName("Addition Partial").getResult().toString()).isEqualTo(Arrays.asList(1, 8, 16, 25).toString());
    }
    
    @Test
    public void testIteratorForInRangeClose() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 1)), "Addition Range Close");
        assertThat(results.getDecisionResultByName("Addition Range Close").getResult().toString()).isEqualTo(Arrays.asList(3, 4).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 2)), "Addition Range Close");
        assertThat(results.getDecisionResultByName("Addition Range Close").getResult().toString()).isEqualTo(Arrays.asList(4, 5).toString());
    } 

    @Test
    public void testIteratorForInRangeOpen() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 1)), "Addition Range Open");
        assertThat(results.getDecisionResultByName("Addition Range Open").getResult().toString()).isEqualTo(Arrays.asList(2, 3, 4, 5).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 2)), "Addition Range Open");
        assertThat(results.getDecisionResultByName("Addition Range Open").getResult().toString()).isEqualTo(Arrays.asList(3, 4, 5, 6).toString());
    } 

    
    @Test
    public void testIteratorSome() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Number Greater Exists");
        assertThat(((Boolean) results.getDecisionResultByName("Number Greater Exists").getResult()).booleanValue()).isTrue();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", -1)), "Number Greater Exists");
        assertThat(((Boolean) results.getDecisionResultByName("Number Greater Exists").getResult()).booleanValue()).isTrue();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 11)), "Number Greater Exists");
        assertThat(((Boolean) results.getDecisionResultByName("Number Greater Exists").getResult()).booleanValue()).isFalse();
    }

    @Test
    public void testIteratorEvery() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "All Greater");
        assertThat(((Boolean) results.getDecisionResultByName("All Greater").getResult()).booleanValue()).isFalse();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", -1)), "All Greater");
        assertThat(((Boolean) results.getDecisionResultByName("All Greater").getResult()).booleanValue()).isTrue();

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 11)), "All Greater");
        assertThat(((Boolean) results.getDecisionResultByName("All Greater").getResult()).booleanValue()).isFalse();
    }

    @Test
    public void testFilterIndex() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Match by index");
        assertThat(results.getDecisionResultByName("Match by index").getResult()).isEqualTo(new BigDecimal(5));

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", -2)), "Match by index");
        assertThat(results.getDecisionResultByName("Match by index").getResult()).isEqualTo(new BigDecimal(9));

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 0)), "Match by index");
        assertThat(results.getMessages()).hasSizeGreaterThan(0);

    }

    @Test
    public void testFilterExpression() throws Throwable {
        DMNResult results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 5)), "Match by Fnct");
        assertThat(results.getDecisionResultByName("Match by Fnct").getResult().toString()).isEqualTo(Arrays.asList(6, 7, 8, 9, 10).toString());

        results = runtime.evaluateByName(model, new DMNContextImpl(Collections.singletonMap("Number Input", 11)), "Match by Fnct");
        assertThat(results.getDecisionResultByName("Match by Fnct").getResult().toString()).isEqualTo(List.of().toString());

    }
}
