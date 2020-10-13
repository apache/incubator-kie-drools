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

package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.stronglytyped.DMNRuntimeTypesTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

public class MultipleModelsTest extends BaseDMNOASTest {

    @Test
    public void testNSEW() throws Exception {
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
    public void testTF() throws Exception {
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
}
