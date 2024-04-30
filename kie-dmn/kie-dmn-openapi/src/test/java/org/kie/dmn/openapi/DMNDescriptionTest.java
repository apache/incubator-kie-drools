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

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.openapi.impl.DMNOASConstants.X_DMN_DESCRIPTIONS;

class DMNDescriptionTest extends BaseDMNOASTest {

    @Test
    void test() throws Exception {
        final DMNRuntime runtime = createRuntime("dmnDescription.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();
        JacksonUtils.printoutJSON(result.getJsonSchemaNode());

        JsonNode definitions = result.getJsonSchemaNode().get("definitions");
        assertThat(convertToMap((definitions.get("InputSet").get(X_DMN_DESCRIPTIONS)))).hasSize(1)
                                                                                       .containsEntry("in1", "description of in1");
        assertThat(convertToMap((definitions.get("OutputSet").get(X_DMN_DESCRIPTIONS)))).hasSize(3)
                                                                                        .containsEntry("in1", "description of in1")
                                                                                        .containsEntry("out1", "description of out1")
                                                                                        .containsEntry("d1", "description of d1");
        assertThat(convertToMap((definitions.get("InputSetDSmyDS1").get(X_DMN_DESCRIPTIONS)))).hasSize(2);
        assertThat(convertToMap((definitions.get("OutputSetDSmyDS1").get(X_DMN_DESCRIPTIONS)))).hasSize(1);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(JsonNode node) {
        return new ObjectMapper().convertValue(node, Map.class);
    }
}
