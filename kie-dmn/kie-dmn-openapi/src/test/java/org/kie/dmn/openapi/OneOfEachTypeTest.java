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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.stronglytyped.DMNRuntimeTypesTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class OneOfEachTypeTest extends BaseDMNOASTest {

    @Test
    void test() throws Exception {
        final DMNRuntime runtime = createRuntime("OneOfEachType.dmn", DMNRuntimeTypesTest.class);
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_4f5608e9-4d74-4c22-a47e-ab657257fc9c", "OneOfEachType");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{   \"InputBoolean\": true,\n" +
                                            "    \"InputDTDuration\": \"P1D\",\n" +
                                            "    \"InputDate\": \"2020-04-02\",\n" +
                                            "    \"InputDateAndTime\": \"2020-04-02T09:00:00z\",\n" +
                                            "    \"InputNumber\": 1,\n" +
                                            "    \"InputString\": \"John Doe\",\n" +
                                            "    \"InputTime\": \"09:00:00z\",\n" +
                                            "    \"InputYMDuration\": \"P1M\"}")).isEmpty();
    }
}
