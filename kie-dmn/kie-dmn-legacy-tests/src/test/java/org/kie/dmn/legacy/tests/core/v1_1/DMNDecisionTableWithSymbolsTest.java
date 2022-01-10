/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.legacy.tests.core.v1_1;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNDecisionTableWithSymbolsTest extends BaseDMN1_1VariantTest {

    public DMNDecisionTableWithSymbolsTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testDecisionWithArgumentsOnOutput() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decide with symbols.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_79b16a68-013b-484c-98f5-49ff77808800", "Decide with symbols");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = DMNFactory.newContext();
        context.set("Person age", 44);
        context.set("Person name", "Mario");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide with symbol"), is("Hello, Mario"));
    }
}
