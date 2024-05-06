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
package org.kie.dmn.core.internal.utils;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class MarshallingStubUtilsTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(MarshallingStubUtilsTest.class);

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void comparablePeriod(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("comparablePeriod.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_CB283B9C-8581-447E-8625-4D1186F0B3A6", "A1B0FA02-D1C4-4386-AF36-0280AA45A7B7");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = runtime.newContext();

        final DMNResult evaluateAll = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();
        assertThat(evaluateAll.getDecisionResultByName("Decision-1").getResult()).isEqualTo(ComparablePeriod.parse("P3Y"));

        final Object serialized = MarshallingStubUtils.stubDMNResult(evaluateAll.getContext().getAll(), Object::toString);
        LOG.debug("{}", serialized);
        assertThat(serialized).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> asMap = (Map<String, Object>) serialized;
        assertThat(asMap.get("BKM")).isInstanceOf(String.class);
        assertThat(asMap.get("Decision-1")).isInstanceOf(java.time.Period.class);
        assertThat(asMap.get("Decision-1")).isEqualTo(java.time.Period.parse("P3Y"));
    }
}
