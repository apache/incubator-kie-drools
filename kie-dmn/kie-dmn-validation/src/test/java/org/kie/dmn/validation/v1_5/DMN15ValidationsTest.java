/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.v1_5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

public class DMN15ValidationsTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN15ValidationsTest.class);

    static final DMNValidator validator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));
    static final DMNValidator.ValidatorBuilder validatorBuilder = validator.validateUsing(DMNValidator.Validation.VALIDATE_SCHEMA, DMNValidator.Validation.VALIDATE_MODEL);


    @Test
    void overridingUnnamedImportValidation() {
        String importedModelFileName = "valid_models/DMNv1_5/Imported_Model_Unamed.dmn";
        String importingModelFileName = "valid_models/DMNv1_5/Importing_OverridingEmptyNamed_Model.dmn";
        String modelName = "Importing empty-named Model";
        String modelNamespace = "http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
        validate(importedModelFileName, importingModelFileName);
        Map<String, Object> inputData = Map.of("A Person", prototype(entry("name", "Hugh"), entry("age", 32)));
        evaluate(modelNamespace, modelName, importingModelFileName, inputData, importedModelFileName);
    }

    @Test
    void namedImportValidation() {
        String importedModelFileName = "valid_models/DMNv1_5/Imported_Model_Unamed.dmn";
        String importingModelFileName = "valid_models/DMNv1_5/Importing_Named_Model.dmn";
        String modelName = "Importing named Model";
        String modelNamespace = "http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
        validate(importedModelFileName, importingModelFileName);
        Map<String, Object> inputData = Map.of("A Person", prototype(entry("name", "Hugh"), entry("age", 32)));
        evaluate(modelNamespace, modelName, importingModelFileName, inputData, importedModelFileName);
    }

    @Test
    void unnamedImportValidationWithHrefNamespace() {
        commonUnnamedImportValidation("valid_models/DMNv1_5/Importing_EmptyNamed_Model_With_Href_Namespace.dmn",
                                      "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    void unnamedImportValidationWithoutHrefNamespace() {
        commonUnnamedImportValidation("valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
                                      "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    void forLoopDatesEvaluateValidation() {
        String modelFileName = "valid_models/DMNv1_5/ForLoopDatesEvaluate.dmn";
        String modelName = "For Loop Dates Evaluate";
        String modelNamespace = "http://www.trisotech.com/dmn/definitions/_09E8A38A-AD24-4C3D-8307-029C0C4D373F";
        validate(modelFileName);
        evaluate(modelNamespace, modelName, modelFileName, Collections.EMPTY_MAP);
    }

    @Test
    void listReplaceEvaluateValidation() {
        String modelFileName = "valid_models/DMNv1_5/ListReplaceEvaluate.dmn";
        String modelName = "List Replace Evaluate";
        String modelNamespace = "http://www.trisotech.com/dmn/definitions/_09E8A38A-AD24-4C3D-8307-029C0C4D373F";
        validate(modelFileName);
        evaluate(modelNamespace, modelName, modelFileName, Collections.EMPTY_MAP);
    }

    @Test
    void negationOfDurationEvaluateValidation() {
        String modelFileName = "valid_models/DMNv1_5/NegationOfDurationEvaluate.dmn";
        String modelName = "Negation of Duration Evaluate";
        String modelNamespace = "http://www.trisotech.com/dmn/definitions/_09E8A38A-AD24-4C3D-8307-029C0C4D373F";
        validate(modelFileName);
        evaluate(modelNamespace, modelName, modelFileName, Collections.EMPTY_MAP);
    }

    @Test
    void dateToDateTimeFunctionValidation() {
        String modelFileName = "valid_models/DMNv1_5/DateToDateTimeFunction.dmn";
        String modelName = "new-file";
        String modelNamespace = "https://kiegroup.org/dmn/_A7F17D7B-F0AB-4C0B-B521-02EA26C2FBEE";
        validate(modelFileName);
        evaluate(modelNamespace, modelName, modelFileName, Collections.EMPTY_MAP);
    }

    @Test
    void typeConstraintsChecksValidation() {
        String modelFileName = "valid_models/DMNv1_5/TypeConstraintsChecks.dmn";
        String modelName = "TypeConstraintsChecks";
        String modelNamespace = "http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442";
        validate(modelFileName);
        Map<String, Object> inputData = Map.of("p1", prototype(entry("Name", "P1"), entry("Interests", Collections.singletonList("Golf"))));
        evaluate(modelNamespace, modelName, modelFileName, inputData);
    }

    private void commonUnnamedImportValidation(String importingModelRef, String importedModelRef) {
        String modelName = "Importing empty-named Model";
        String modelNamespace = "http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
        validate(importingModelRef, importedModelRef);
        Map<String, Object> inputData = Map.of("A Person", prototype(entry("name", "Hugh"), entry("age", 32)));
        evaluate(modelNamespace, modelName, importingModelRef, inputData, importedModelRef);
    }

    private void validate(String modelFileName, String... otherFileNames) {
        List<String> allModelsFileNames = new ArrayList<>();
        allModelsFileNames.add(modelFileName);
        allModelsFileNames.addAll(List.of(otherFileNames));
        Resource[] resources = allModelsFileNames.stream()
                .map(fileName -> new ClassPathResource(fileName,
                                                       this.getClass()))
                .toArray(value -> new Resource[allModelsFileNames.size()]);
        List<DMNMessage> dmnMessages = validatorBuilder.theseModels(resources);
        assertNotNull(dmnMessages);
        dmnMessages.forEach(dmnMessage -> LOG.error(dmnMessage.toString()));
        assertTrue(dmnMessages.isEmpty());
    }

    private void evaluate(String modelNamespace, String modelName, String modelFileName,
                          Map<String, Object> inputData, String... otherFileNames) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(modelFileName,
                                                                                       this.getClass(),
                                                                                       otherFileNames);
        final DMNModel dmnModel = runtime.getModel(modelNamespace,
                                                   modelName);
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        final DMNContext ctx = runtime.newContext();
        inputData.forEach(ctx::set);
        DMNResult toReturn = runtime.evaluateAll(dmnModel, ctx);
        assertThat(toReturn.hasErrors()).as(DMNRuntimeUtil.formatMessages(toReturn.getMessages())).isFalse();
    }
}
