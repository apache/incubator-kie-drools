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
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.trisotech.TrisotechDMNProfile;
import org.kie.dmn.trisotech.core.compiler.TrisotechDMNEvaluatorCompilerFactory;
import org.kie.dmn.trisotech.validation.TrisotechValidationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

public class DMN14GenericSynthTest {

    public DMNRuntime createRuntime(String model, Class<?> class1) {
        return DMNRuntimeBuilder.fromDefaults()
                                .addProfile(new TrisotechDMNProfile())
                                .setDecisionLogicCompilerFactory(new TrisotechDMNEvaluatorCompilerFactory())
                                .buildConfiguration()
                                .fromClasspathResource(model, class1)
                                .getOrElseThrow(e -> new RuntimeException("Error initalizing DMNRuntime", e));
    }

    @Test
    void filterDatatype() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/filter-datatype.dmn", TrisotechValidationTest.class);
        assertThat(runtime).isNotNull();
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertThat(model).isNotNull();

        checkFilterDatatype(runtime, model);
    }

    private void checkFilterDatatype(DMNRuntime runtime, DMNModel model) {
        Map<String, Object> tc1 = prototype(entry("Value in String", "x"), entry("Value of Number", new BigDecimal(1)), entry("value", "X1"));
        Map<String, Object> tc2 = prototype(entry("Value in String", "y"), entry("Value of Number", new BigDecimal(2)), entry("value", "X2"));
        Map<String, Object> tc3 = prototype(entry("Value in String", "x"), entry("Value of Number", new BigDecimal(3)), entry("value", "X3"));
        DMNContext context = runtime.newContext();
        context.set("Input", Arrays.asList(tc1, tc2, tc3));
        DMNResult results = runtime.evaluateAll(model, context);
        assertThat(results.getDecisionResultByName("Decision").getResult()).isEqualTo(Arrays.asList(tc1, tc3));
    }

    @Test
    void filterDatatype2() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/filter-datatype2.dmn", TrisotechValidationTest.class);
        assertThat(runtime).isNotNull();
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertThat(model).isNotNull();

        checkFilterDatatype(runtime, model);
    }

    @Test
    void iteratorDatatype() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/iterator-datatype.dmn", TrisotechValidationTest.class);
        assertThat(runtime).isNotNull();
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertThat(model).isNotNull();

        checkIteratorDatatype(runtime, model);
    }

    private void checkIteratorDatatype(DMNRuntime runtime, DMNModel model) {
        Map<String, Object> tc1 = prototype(entry("Value in String", "x"), entry("Value of Number", new BigDecimal(1)), entry("value", "X1"));
        Map<String, Object> tc2 = prototype(entry("Value in String", "y"), entry("Value of Number", new BigDecimal(2)), entry("value", "X2"));
        Map<String, Object> tc3 = prototype(entry("Value in String", "z"), entry("Value of Number", new BigDecimal(3)), entry("value", "X3"));
        DMNContext context = runtime.newContext();
        context.set("Input", Arrays.asList(tc1, tc2, tc3));
        DMNResult results = runtime.evaluateAll(model, context);
        assertThat(results.getDecisionResultByName("Decision").getResult()).isEqualTo(Arrays.asList("x", "y", "z"));
    }

    @Test
    void iteratorDatatype2() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/iterator-datatype2.dmn", TrisotechValidationTest.class);
        assertThat(runtime).isNotNull();
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertThat(model).isNotNull();

        checkIteratorDatatype(runtime, model);
    }
}
