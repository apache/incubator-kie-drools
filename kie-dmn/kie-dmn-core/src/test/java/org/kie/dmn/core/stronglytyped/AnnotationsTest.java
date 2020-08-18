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

package org.kie.dmn.core.stronglytyped;

import java.lang.reflect.Method;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AnnotationsTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(AnnotationsTest.class);
    private boolean strongly;

    public AnnotationsTest(VariantTestConf testConfig) {
        super(testConfig);
        strongly = testConfig.isTypeSafe();
    }

    @Test
    public void testNSWE() throws Exception {
        final DMNRuntime runtime = createRuntime("NSEW.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB", "NSEW");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("direction", "East");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is("You decided to go East."));

        if (strongly) {
            Class<?> inputSetClass = getStronglyClassByName(dmnModel, "InputSet");
            Method getDirection = inputSetClass.getMethod("getDirection");
            org.eclipse.microprofile.openapi.annotations.media.Schema ann = getDirection.getDeclaredAnnotation(org.eclipse.microprofile.openapi.annotations.media.Schema.class);
            Assertions.assertThat(ann).isNotNull();
            Assertions.assertThat(ann.enumeration()).isNotNull().contains("North", "South", "East", "West");
        }
    }
}

