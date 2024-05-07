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
import org.kie.dmn.core.DMNRuntimeTest;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class AllowNullTest extends BaseDMNOASTest {

    @Test
    void vowels() throws Exception {
        final DMNRuntime runtime = createRuntime("vowels.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_0885BF04-027C-4743-9427-2668DA3AD472", "vowels");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"my input\": \"#FF0000\" }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"my input\": null }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"my input\": \"a\"}")).isEmpty();
    }
    
    @Test
    void vowelsAllowNull() throws Exception {
        final DMNRuntime runtime = createRuntime("vowelsAllowNull.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_0885BF04-027C-4743-9427-2668DA3AD472", "vowels");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"my input\": \"#FF0000\" }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"my input\": null }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"my input\": \"a\" }")).isEmpty();
    }
    
    @Test
    void soundItemDefAllowNull() throws Exception {
        final DMNRuntime runtime = createRuntime("RecommenderHitPolicy1_allowNull_itemDef.dmn", DMNRuntimeTest.class);
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_50aea0bb-4482-48f6-acfe-4abc1a1bd0d6", "Drawing 1");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Level\": \"#FF0000\" }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"Level\": null }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"Level\": 47 }")).isEmpty();
        assertThat(validateUsing(validator, "{ \"Level\": -999 }")).isNotEmpty();
    }
}
