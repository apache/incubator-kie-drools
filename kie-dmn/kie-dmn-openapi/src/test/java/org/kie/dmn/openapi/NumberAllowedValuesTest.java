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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class NumberAllowedValuesTest extends BaseDMNOASTest {

    @Test
    void allowedValues() throws Exception {
        final DMNRuntime runtime = createRuntime("numberAllowedValues.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "numberAllowedValues");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"gt0\": 123, \"lteq47gt0\": 47, \"r0100\": 0}")).isEmpty();
    }

    @Test
    void numberList() throws Exception {
        final DMNRuntime runtime = createRuntime("numberList.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "numberList");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"in1\": 47 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"in1\": 0 }")).isEmpty();
    }

    @Test
    void gt() throws Exception {
        final DMNRuntime runtime = createRuntime("numberGT.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "numberGT");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"age\": 47, \"negative\" : -47 }")).isEmpty();

        JsonNode tAge = syntheticJSONSchema.get("definitions").get("tAge");
        assertThat(tAge.fieldNames()).toIterable().doesNotContain("exclusiveMaximum", "maximum");
        JsonNode tNegative = syntheticJSONSchema.get("definitions").get("tNegative");
        assertThat(tNegative.fieldNames()).toIterable().doesNotContain("exclusiveMinumum", "minumum");
    }
}
