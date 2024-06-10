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
import org.kie.dmn.core.v1_4.DMN14specificTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class CH11Test extends BaseDMNOASTest {

    @Test
    void dmn14support() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", DMN14specificTest.class, "Financial.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{\n"
                + "    \"Applicant data\": {\n"
                + "        \"ExistingCustomer\": false,\n"
                + "        \"EmploymentStatus\": \"EMPLOYED\",\n"
                + "        \"Age\": 51,\n"
                + "        \"Monthly\": {\n"
                + "            \"Income\": 100000,\n"
                + "            \"Repayments\": 2500,\n"
                + "            \"Expenses\": 10000\n"
                + "        },\n"
                + "        \"MartitalStatus\": \"M\"\n"
                + "    },\n"
                + "    \"Requested product\": {\n"
                + "        \"Amount\": 100000,\n"
                + "        \"ProductType\": \"STANDARD LOAN\",\n"
                + "        \"Rate\": 0.08,\n"
                + "        \"Term\": 36\n"
                + "    },\n"
                + "    \"Supporting documents\": null,\n"
                + "    \"Bureau data\": {\n"
                + "        \"Bankrupt\": false,\n"
                + "        \"CreditScore\": 600\n"
                + "    },\n"
                + "    \"Loan default data\": \"\"\n"
                + "}")).isEmpty();
    }
}
