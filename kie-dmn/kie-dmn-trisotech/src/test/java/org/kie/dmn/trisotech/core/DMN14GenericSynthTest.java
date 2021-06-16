/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.trisotech.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.trisotech.TrisotechDMNProfile;
import org.kie.dmn.trisotech.core.compiler.TrisotechDMNEvaluatorCompilerFactory;
import org.kie.dmn.trisotech.validation.TrisotechValidationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void testFilterDatatype() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/filter-datatype.dmn", TrisotechValidationTest.class);
        assertNotNull(runtime);
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertNotNull(model);

        checkFilterDatatype(runtime, model);
    }

    private void checkFilterDatatype(DMNRuntime runtime, DMNModel model) {
        Map<String, Object> tc1 = prototype(entry("Value in String", "x"), entry("Value of Number", new BigDecimal(1)), entry("value", "X1"));
        Map<String, Object> tc2 = prototype(entry("Value in String", "y"), entry("Value of Number", new BigDecimal(2)), entry("value", "X2"));
        Map<String, Object> tc3 = prototype(entry("Value in String", "x"), entry("Value of Number", new BigDecimal(3)), entry("value", "X3"));
        DMNContext context = runtime.newContext();
        context.set("Input", Arrays.asList(tc1, tc2, tc3));
        DMNResult results = runtime.evaluateAll(model, context);
        assertEquals(Arrays.asList(tc1, tc3), results.getDecisionResultByName("Decision").getResult());
    }

    @Test
    public void testFilterDatatype2() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/filter-datatype2.dmn", TrisotechValidationTest.class);
        assertNotNull(runtime);
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertNotNull(model);

        checkFilterDatatype(runtime, model);
    }

    @Test
    public void testIteratorDatatype() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/iterator-datatype.dmn", TrisotechValidationTest.class);
        assertNotNull(runtime);
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertNotNull(model);

        checkIteratorDatatype(runtime, model);
    }

    private void checkIteratorDatatype(DMNRuntime runtime, DMNModel model) {
        Map<String, Object> tc1 = prototype(entry("Value in String", "x"), entry("Value of Number", new BigDecimal(1)), entry("value", "X1"));
        Map<String, Object> tc2 = prototype(entry("Value in String", "y"), entry("Value of Number", new BigDecimal(2)), entry("value", "X2"));
        Map<String, Object> tc3 = prototype(entry("Value in String", "z"), entry("Value of Number", new BigDecimal(3)), entry("value", "X3"));
        DMNContext context = runtime.newContext();
        context.set("Input", Arrays.asList(tc1, tc2, tc3));
        DMNResult results = runtime.evaluateAll(model, context);
        assertEquals(Arrays.asList("x", "y", "z"), results.getDecisionResultByName("Decision").getResult());
    }

    @Test
    public void testIteratorDatatype2() throws Throwable {
        DMNRuntime runtime = createRuntime("boxedcontextextension/iterator-datatype2.dmn", TrisotechValidationTest.class);
        assertNotNull(runtime);
        DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_caa02430-93c6-4ba7-a646-81bbcef32978", "Drawing 1");
        assertNotNull(model);

        checkIteratorDatatype(runtime, model);
    }
}
