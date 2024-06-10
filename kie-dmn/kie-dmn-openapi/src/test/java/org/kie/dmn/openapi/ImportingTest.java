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
import org.kie.dmn.core.imports.ImportsTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class ImportingTest extends BaseDMNOASTest {

    @Test
    void basicImports() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("baseSum.dmn",
                                                                        ImportsTest.class,
                                                                        "importingSum.dmn");
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        final DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_1D35A3BF-1DBD-4CD0-882A-CA068C6F2A67",
                                                   "importingSum");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"x\": 1, \"y\": 2 }")).isEmpty();

        String mutOutputSetName = result.getNamingPolicy().getName(result.lookupIOSetsByModel(modelUnderTest).getOutputSet());
        JsonNode mutOutputSet = syntheticJSONSchema.get("definitions").get(mutOutputSetName);
        assertThat((Iterable<String>) () -> mutOutputSet.get("properties").fieldNames()).doesNotContain("baseSum Decision");

        final DMNModel importedModel = runtime.getModel("https://kiegroup.org/dmn/_FCC62740-4998-47A2-B5F2-CB3E15C98419",
                                                        "baseSum");
        String importedOutputSetName = result.getNamingPolicy().getName(result.lookupIOSetsByModel(importedModel).getOutputSet());
        JsonNode importedOutputSet = syntheticJSONSchema.get("definitions").get(importedOutputSetName);
        assertThat((Iterable<String>) () -> importedOutputSet.get("properties").fieldNames()).contains("baseSum Decision");
    }

}
