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

package org.kie.dmn.core.v1_4;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.model.Person;
import org.kie.dmn.feel.model.SupportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;
import static org.kie.dmn.core.util.DMNRuntimeUtil.formatMessages;

/**
 * at the time of first creation of these tests are to be considered provisional in support of the next publication
 */
public class DMN14specificTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN14specificTest.class);

    public DMN14specificTest(final BaseVariantTest.VariantTestConf conf) {
        super(conf);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_DEFAULT_NOCL_TYPECHECK}; // only variants needed until DMNv1.4 is actually published
    }

    @Test
    public void testDMNv1_4_put() {
        final DMNRuntime runtime = createRuntime("examplePut.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("ns1", "examplePut");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).describedAs(formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Support Request", new SupportRequest("John Doe", "47", "info@redhat.com", "+1", "somewhere", "tech", "app crashed", false));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).describedAs(formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Determine Priority").getResult()).isEqualTo("Medium");
        assertThat(dmnResult.getDecisionResultByName("Processed Request").getResult()).hasFieldOrPropertyWithValue("priority", "Medium");
    }

    @Test
    public void testDMNv1_4_putAll() {
        final DMNRuntime runtime = createRuntime("examplePutAll.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_864E9A62-12E5-41DC-A7A6-7F028822A067", "examplePutAll");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).describedAs(formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Partial Person", new Person("John", "Wick"));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).describedAs(formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Complete Person").getResult()).hasFieldOrPropertyWithValue("last name", "Doe")
                                                                                    .hasFieldOrPropertyWithValue("age", new BigDecimal(47));
    }

}
