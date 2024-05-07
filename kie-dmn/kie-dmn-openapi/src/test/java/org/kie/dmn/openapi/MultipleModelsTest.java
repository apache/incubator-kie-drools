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
import org.kie.dmn.core.v1_3.DMN13specificTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class MultipleModelsTest extends BaseDMNOASTest {

    @Test
    void nsew() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("NSEW.dmn",
                                                                        DMNRuntimeTypesTest.class,
                                                                        "Traffic Violation.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB", "NSEW");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"asd\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"direction\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"direction\":\"Nord\" }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"direction\":\"North\" }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"direction\":\"North\", \"asd\":123 }")).isEmpty();
    }

    @Test
    void tf() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("NSEW.dmn",
                                                                        DMNRuntimeTypesTest.class,
                                                                        "Traffic Violation.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"asd\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\": {}, \"Violation\" : {} }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\": { \"Points\": false}, \"Violation\" : {} }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\": { \"Points\": 10 }, \"Violation\" : {} }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\": { \"Points\": 10 }, \"Violation\" : {\"Type\": 123} }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\": { \"Points\": 10 }, \"Violation\" : {\"Type\": \"string\"} }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Driver\": { \"Points\": 10 }, \"Violation\" : {\"Type\": \"speed\",\"Actual Speed\":140, \"Speed Limit\":100} }")).isEmpty();
    }
    
    @Test
    void ch11() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn",
                                                                        DMN13specificTest.class,
                                                                        "Financial.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"asd\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Applicant data\": {}, \"Requested product\": {}, \"Bureau data\": {}, \"Supporting documents\": null, \"Loan default data\": \"data...\" }")).isEmpty();
        
    }
    
    @Test
    void sameName() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("sameNameNS1.dmn",
                                                                        this.getClass(),
                                                                        "sameNameNS2.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel ns1_modelUnderTest = runtime.getModel("ns1", "sameName");
        ObjectNode ns1_syntheticJSONSchema = synthesizeSchema(result, ns1_modelUnderTest);
        JsonSchema ns1_validator = getJSONSchema(ns1_syntheticJSONSchema);

        assertThat(validateUsing(ns1_validator, "{ \"asd\":123 }")).isNotEmpty();
        assertThat(validateUsing(ns1_validator, "{ \"in1\":\"John Doe\" }")).isNotEmpty();
        assertThat(validateUsing(ns1_validator, "{ \"in1\":123 }")).isEmpty();
        
        DMNModel ns2_modelUnderTest = runtime.getModel("ns2", "sameName");
        ObjectNode ns2_syntheticJSONSchema = synthesizeSchema(result, ns2_modelUnderTest);
        JsonSchema ns2_validator = getJSONSchema(ns2_syntheticJSONSchema);

        assertThat(validateUsing(ns2_validator, "{ \"asd\":123 }")).isNotEmpty();
        assertThat(validateUsing(ns2_validator, "{ \"in1\":123 }")).isNotEmpty();
        assertThat(validateUsing(ns2_validator, "{ \"in1\":\"John Doe\" }")).isEmpty();
    }
}
