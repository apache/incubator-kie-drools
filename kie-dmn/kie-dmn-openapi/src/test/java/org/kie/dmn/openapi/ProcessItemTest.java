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
package org.kie.dmn.openapi;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ProcessItemTest extends BaseDMNOASTest {

    @Test
    void test() throws Exception {
        final DMNRuntime runtime = createRuntime("processItem.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        checkProcessItem(runtime, result);
    }

    private void checkProcessItem(final DMNRuntime runtime, DMNOASResult result) throws IOException {
        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_4B5AD433-0A08-4D69-A91F-89ECD6C2546F", "processItem");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"items\" : [ { \"id\" : 123 }, { \"id\" : 123 }] }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"items\" : [ { \"id\" : \"abc\" }, { \"id\" : \"abc\" }] }")).isEmpty();
    }

    @Test
    void test_2() throws Exception {
        final DMNRuntime runtime = createRuntime("processItem_2.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        checkProcessItem_2(runtime, result);
    }

    private void checkProcessItem_2(final DMNRuntime runtime, DMNOASResult result) throws IOException {
        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_4B5AD433-0A08-4D69-A91F-89ECD6C2546F_2", "processItem_2");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"items\" : [ { \"id\" : 123 }, { \"id\" : 123 }] }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"items\" : [ { \"id\" : \"abc\" }, { \"id\" : \"abc\" }] }")).isNotEmpty();
    }

    @Test
    void together() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("processItem.dmn", this.getClass(), "processItem_2.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        checkProcessItem(runtime, result);
        checkProcessItem_2(runtime, result);
    }

    @Test
    void colliding() {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("processItem.dmn", this.getClass(), "processItemCollidingNS.dmn");
        assertThatExceptionOfType(IllegalStateException.class)
                  .isThrownBy(() -> DMNOASGeneratorFactory.generator(runtime.getModels()).build())
                  .withMessageContaining("processItem") // name of the 1st DMN Model
                  .withMessageContaining("processItemCollidingNS"); // name of the 2nd DMN Model
    }
}
