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
import org.kie.dmn.core.decisionservices.DMNDecisionServicesTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class DS004decisionservicesTest extends BaseDMNOASTest {

    @Test
    void test0004decisionservices_DS1() throws Exception {
        final DMNRuntime runtime = createRuntime("0004-decision-services.dmn", DMNDecisionServicesTest.class);
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        ObjectNode syntheticJSONSchema = synthesizeSchemaForDS(result, modelUnderTest, "A only as output knowing D and E");
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"D\":123, \"E\":456 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"D\":\"d\", \"E\":\"e\" }")).isEmpty();
    }

    @Test
    void test0004decisionservices_DS2() throws Exception {
        final DMNRuntime runtime = createRuntime("0004-decision-services.dmn", DMNDecisionServicesTest.class);
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        ObjectNode syntheticJSONSchema = synthesizeSchemaForDS(result, modelUnderTest, "A Only Knowing B and C");
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"D\":123, \"E\":456 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"D\":\"d\", \"E\":\"e\" }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"D\":\"d\", \"E\":\"e\", \"B\":123, \"C\":123 }")).isEmpty(); // B and C have FEEL:Any type.
        assertThat(validateUsing(validator, "{ \"D\":\"d\", \"E\":\"e\", \"B\":\"b\", \"C\":\"c\" }")).isEmpty();
    }

}
