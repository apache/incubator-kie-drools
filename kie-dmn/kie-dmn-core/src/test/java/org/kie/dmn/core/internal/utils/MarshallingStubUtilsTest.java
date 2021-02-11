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

package org.kie.dmn.core.internal.utils;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MarshallingStubUtilsTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(MarshallingStubUtilsTest.class);

    public MarshallingStubUtilsTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testComparablePeriod() {
        final DMNRuntime runtime = createRuntime("comparablePeriod.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_CB283B9C-8581-447E-8625-4D1186F0B3A6", "A1B0FA02-D1C4-4386-AF36-0280AA45A7B7");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = runtime.newContext();

        final DMNResult evaluateAll = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", evaluateAll);
        assertThat(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages()), evaluateAll.hasErrors(), is(false));
        assertThat(evaluateAll.getDecisionResultByName("Decision-1").getResult(), is(ComparablePeriod.parse("P3Y")));

        final Object serialized = MarshallingStubUtils.stubDMNResult(evaluateAll.getContext().getAll(), Object::toString);
        LOG.debug("{}", serialized);
        assertThat(serialized, instanceOf(Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object> asMap = (Map<String, Object>) serialized;
        assertThat(asMap.get("BKM"), instanceOf(String.class));
        assertThat(asMap.get("Decision-1"), instanceOf(java.time.Period.class));
        assertThat(asMap.get("Decision-1"), is(java.time.Period.parse("P3Y")));
    }
}
