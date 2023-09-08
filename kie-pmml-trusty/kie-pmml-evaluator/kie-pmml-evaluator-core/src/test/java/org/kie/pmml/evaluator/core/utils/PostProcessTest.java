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
package org.kie.pmml.evaluator.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class PostProcessTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String CUSTOM_FIELD = "CUSTOM_FIELD";
    private static final String CUSTOM_REF_FIELD = "CUSTOM_REF_FIELD";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final String OUTPUT_NAME = "OUTPUT_NAME";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;

    @Test
    void executeTargets() {
        // Build model
        String TARGET_NAME = "TARGET_NAME";
        String FIELD_NAME = "FIELD_NAME";
        TargetField targetField = new TargetField(Collections.emptyList(), null, FIELD_NAME, null, 4.34, null, null,
                null);
        KiePMMLTarget kiePMMLTarget = KiePMMLTarget.builder(TARGET_NAME, Collections.emptyList(), targetField)
                .build();
        List<KiePMMLTarget> kiePMMLTargets = Arrays.asList(kiePMMLTarget, KiePMMLTarget.builder("NEW_TARGET",
                Collections.emptyList(),
                new TargetField(Collections.emptyList(), null, "NEW_TARGET", null, null, null, null,
                        null)).build());
        KiePMMLTestingModel model = KiePMMLTestingModel.builder("FILENAME", "TESTINGMODEL", Collections.emptyList(),
                MINING_FUNCTION.REGRESSION)
                .withKiePMMLTargets(kiePMMLTargets)
                .build();
        // Build PMML4Result
        PMML4Result toModify = new PMML4Result();
        toModify.setResultCode(ResultCode.FAIL.getName());
        toModify.addResultVariable(FIELD_NAME, 4.33);
        assertThat(toModify.getResultVariables().get(FIELD_NAME)).isEqualTo(4.33);
        ProcessingDTO processingDTO = getProcessingDTO(model, new ArrayList<>());
        PostProcess.executeTargets(toModify, processingDTO);
        assertThat(toModify.getResultVariables().get(FIELD_NAME)).isEqualTo(4.33);
        toModify.setResultCode(ResultCode.OK.getName());
        PostProcess.executeTargets(toModify, processingDTO);
        assertThat(toModify.getResultVariables().get(FIELD_NAME)).isEqualTo(4.33);
        toModify.setResultObjectName(FIELD_NAME);
        PostProcess.executeTargets(toModify, processingDTO);
        assertThat(toModify.getResultVariables().get(FIELD_NAME)).isEqualTo(4.34);
    }

    @Test
    void populatePredictedOutputField1() {
        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.PREDICTED_VALUE)
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithDefaultNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isEmpty();
    }

    @Test
    void populatePredictedOutputField2() {
        KiePMMLNameValue kiePMMLNameValue = new KiePMMLNameValue("targetField", 54346.32454);

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.PREDICTED_VALUE)
                .withTargetField(kiePMMLNameValue.getName())
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithNameValues(kiePMMLModel, kiePMMLNameValue);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(kiePMMLNameValue.getValue());
    }

    @Test
    void populateTransformedOutputField1() {
        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithDefaultNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);
        assertThat(toUpdate.getResultVariables()).isEmpty();
    }

    @Test
    void populateTransformedOutputField2() {
        KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("CONSTANT_NAME", Collections.emptyList(), "String", null);

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLConstant)
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithDefaultNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(kiePMMLConstant.getValue());
    }

    @Test
    void populateTransformedOutputFieldWithApplyWithConstants() {
        //     <Apply function="/">
        //        <Constant>100.0</Constant>
        //        <Constant>5.0</Constant>
        //      </Apply>
        KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLConstant1, kiePMMLConstant2))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithEmptyNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);
    }

    @Test
    void populateTransformedOutputFieldWithApplyDerivedFieldFromConstant() {
        // <DerivedField name="CUSTOM_FIELD" optype="continuous" dataType="double">
        //        <Constant>100.0</Constant>
        // </DerivedField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLDerivedField derivedField = KiePMMLDerivedField.builder(CUSTOM_FIELD, Collections.emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                kiePMMLConstant1)
                .build();
        //     <Apply function="/">
        //        <FieldRef>CUSTOM_FIELD</FieldRef>
        //        <Constant>5.0</Constant>
        //      </Apply>
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(CUSTOM_FIELD, Collections.emptyList(), null);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef, kiePMMLConstant2))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();

        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDerivedFields(Collections.singletonList(derivedField))
                .build();
        KiePMMLTestingModel kiePMMLModel1 = testingModelBuilder(outputField)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        ProcessingDTO processingDTO1 = buildProcessingDTOWithEmptyNameValues(kiePMMLModel1);
        PMML4Result toUpdate1 = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate1, processingDTO1);

        assertThat(toUpdate1.getResultVariables()).isNotEmpty();
        assertThat(toUpdate1.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate1.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);

        // From LocalTransformations
        KiePMMLLocalTransformations localTransformations = KiePMMLLocalTransformations.builder("localTransformations"
        , Collections.emptyList())
                .withDerivedFields(Collections.singletonList(derivedField))
                .build();
        KiePMMLTestingModel kiePMMLModel2 = testingModelBuilder(outputField)
                .withKiePMMLLocalTransformations(localTransformations)
                .build();
        ProcessingDTO processingDTO2 = buildProcessingDTOWithEmptyNameValues(kiePMMLModel2);
        PMML4Result toUpdate2 = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate2, processingDTO2);

        assertThat(toUpdate2.getResultVariables()).isNotEmpty();
        assertThat(toUpdate2.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate2.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);
    }

    @Test
    void populateTransformedOutputFieldWithApplyDerivedFieldFromFieldRef() {
        // <DerivedField name="CUSTOM_REF_FIELD" optype="continuous" dataType="double">
        //        <Constant>100.0</Constant>
        // </DerivedField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLDerivedField derivedField1 = KiePMMLDerivedField.builder(CUSTOM_REF_FIELD, Collections
                        .emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                kiePMMLConstant1).build();

        // <DerivedField name="CUSTOM_FIELD" optype="continuous" dataType="double">
        //        <FieldRef>CUSTOM_REF_FIELD</FieldRef>
        // </DerivedField>
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(CUSTOM_REF_FIELD, Collections.emptyList(), null);
        final KiePMMLDerivedField derivedField2 = KiePMMLDerivedField.builder(CUSTOM_FIELD, Collections.emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                kiePMMLFieldRef).build();
        //     <Apply function="/">
        //        <FieldRef>CUSTOM_FIELD</FieldRef>
        //        <Constant>5.0</Constant>
        //      </Apply>
        final KiePMMLFieldRef kiePMMLFieldRef2 = new KiePMMLFieldRef(CUSTOM_FIELD, Collections.emptyList(), null);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef2, kiePMMLConstant2))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();

        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDerivedFields(Arrays.asList(derivedField1, derivedField2))
                .build();
        KiePMMLTestingModel kiePMMLModel1 = testingModelBuilder(outputField)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        ProcessingDTO processingDTO1 = buildProcessingDTOWithEmptyNameValues(kiePMMLModel1);
        PMML4Result toUpdate1 = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate1, processingDTO1);

        assertThat(toUpdate1.getResultVariables()).isNotEmpty();
        assertThat(toUpdate1.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate1.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);

        // From LocalTransformations
        KiePMMLLocalTransformations localTransformations = KiePMMLLocalTransformations.builder("localTransformations"
        , Collections.emptyList())
                .withDerivedFields(Arrays.asList(derivedField1, derivedField2))
                .build();
        KiePMMLTestingModel kiePMMLModel2 = testingModelBuilder(outputField)
                .withKiePMMLLocalTransformations(localTransformations)
                .build();
        ProcessingDTO processingDTO2 = buildProcessingDTOWithEmptyNameValues(kiePMMLModel2);
        PMML4Result toUpdate2 = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate2, processingDTO2);

        assertThat(toUpdate2.getResultVariables()).isNotEmpty();
        assertThat(toUpdate2.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate2.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);
    }

    @Test
    void populateTransformedOutputFieldWithApplyDerivedFieldFromApply() {
        // <DerivedField name="CUSTOM_FIELD" optype="continuous" dataType="double">
        //     <Apply function="+">
        //        <Constant>64.0</Constant>
        //        <Constant>36.0</Constant>
        //      </Apply>
        // </DerivedField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), 64.0, null);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), 36, null);
        final KiePMMLApply kiePMMLApplyRef = KiePMMLApply.builder("NAMEREF", Collections.emptyList(), "+")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLConstant1, kiePMMLConstant2))
                .build();
        final KiePMMLDerivedField derivedField = KiePMMLDerivedField.builder(CUSTOM_FIELD, Collections.emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                kiePMMLApplyRef).build();
        //     <Apply function="/">
        //        <FieldRef>CUSTOM_FIELD</FieldRef>
        //        <Constant>5.0</Constant>
        //      </Apply>
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(CUSTOM_FIELD, Collections.emptyList(), null);
        final KiePMMLConstant kiePMMLConstant3 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef, kiePMMLConstant3))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();

        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDerivedFields(Collections.singletonList(derivedField))
                .build();
        KiePMMLTestingModel kiePMMLModel1 = testingModelBuilder(outputField)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        ProcessingDTO processingDTO1 = buildProcessingDTOWithEmptyNameValues(kiePMMLModel1);
        PMML4Result toUpdate1 = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate1, processingDTO1);

        assertThat(toUpdate1.getResultVariables()).isNotEmpty();
        assertThat(toUpdate1.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate1.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);

        // From LocalTransformations
        KiePMMLLocalTransformations localTransformations = KiePMMLLocalTransformations.builder("localTransformations"
        , Collections.emptyList())
                .withDerivedFields(Collections.singletonList(derivedField))
                .build();
        KiePMMLTestingModel kiePMMLModel2 = testingModelBuilder(outputField)
                .withKiePMMLLocalTransformations(localTransformations)
                .build();
        ProcessingDTO processingDTO2 = buildProcessingDTOWithEmptyNameValues(kiePMMLModel2);
        PMML4Result toUpdate2 = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate2, processingDTO2);

        assertThat(toUpdate2.getResultVariables()).isNotEmpty();
        assertThat(toUpdate2.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate2.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);
    }

    @Test
    void populateTransformedOutputFieldWithApplyDefineFunctionFromConstant() {
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <Constant>100.0</Constant>
        // </DefineFunction>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections
                        .emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                Collections.emptyList(),
                kiePMMLConstant1);
        //     <Apply function="CUSTOM_FUNCTION">
        //        <FieldRef>CUSTOM_FIELD</FieldRef>
        //        <Constant>5.0</Constant>
        //      </Apply>
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(CUSTOM_FIELD, Collections.emptyList(), null);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), CUSTOM_FUNCTION)
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef, kiePMMLConstant2))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();

        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDefineFunctions(Collections.singletonList(defineFunction))
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        ProcessingDTO processingDTO = buildProcessingDTOWithEmptyNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1);
    }

    @Test
    void populateTransformedOutputFieldWithApplyDefineFunctionFromFieldRef() {
        // <DerivedField name="CUSTOM_REF_FIELD" optype="continuous" dataType="double">
        //        <Constant>100.0</Constant>
        // </DerivedField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLDerivedField derivedField = KiePMMLDerivedField.builder(CUSTOM_REF_FIELD, Collections
                        .emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                kiePMMLConstant1).build();
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <FieldRef>CUSTOM_REF_FIELD</FieldRef>
        // </DefineFunction>
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(CUSTOM_REF_FIELD, Collections.emptyList(), null);
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections
                        .emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                Collections.emptyList(),
                kiePMMLFieldRef1);
        //     <Apply function="CUSTOM_FUNCTION">
        //        <Constant>5.0</Constant>
        //      </Apply>
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), CUSTOM_FUNCTION)
                .withKiePMMLExpressions(Collections.singletonList(kiePMMLConstant2))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();

        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDefineFunctions(Collections.singletonList(defineFunction))
                .withDerivedFields(Collections.singletonList(derivedField))
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        ProcessingDTO processingDTO = buildProcessingDTOWithEmptyNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1);
    }

    @Test
    void populateTransformedOutputFieldWithApplyDefineFunctionFromApply() {
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <ParameterField name="PARAM_1" />
        //     <Apply function="/">
        //        <FieldRef>PARAM_1</FieldRef>
        //        <Constant>5.0</Constant>
        //      </Apply>
        // </DefineFunction>
        final KiePMMLParameterField kiePMMLParameterField = KiePMMLParameterField.builder(PARAM_1, Collections
                .emptyList()).build();
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        final KiePMMLApply kiePMMLApplyRef = KiePMMLApply.builder("NAMEREF", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef1, kiePMMLConstant2))
                .build();
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections
                        .emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                Collections.singletonList
                        (kiePMMLParameterField),
                kiePMMLApplyRef);
        //     <Apply function="CUSTOM_FUNCTION">
        //        <Constant>100.0</Constant>
        //      </Apply>
        final KiePMMLConstant kiePMMLConstant3 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value1, null);
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), CUSTOM_FUNCTION)
                .withKiePMMLExpressions(Collections.singletonList(kiePMMLConstant3))
                .build();

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLApply)
                .build();

        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDefineFunctions(Collections.singletonList(defineFunction))
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        ProcessingDTO processingDTO = buildProcessingDTOWithEmptyNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);
        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(value1 / value2);
    }

    @Test
    void populateTransformedOutputFieldWithConstant() {
        KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), "String", null);

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLConstant)
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithEmptyNameValues(kiePMMLModel);
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(kiePMMLConstant.getValue());
    }

    @Test
    void populateTransformedOutputFieldWithFieldRef() {
        final String mapMissingTo = "mapMissingTo";
        final String variableName = "variableName";
        final Object variableValue = 543.65434;
        KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(variableName, Collections.emptyList(), mapMissingTo);

        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLFieldRef)
                .build();
        KiePMMLTestingModel kiePMMLModel = testingModelBuilder(outputField).build();
        ProcessingDTO processingDTO = buildProcessingDTOWithNameValues(kiePMMLModel, new KiePMMLNameValue(variableName, variableValue));
        PMML4Result toUpdate = new PMML4Result();

        PostProcess.populateOutputFields(toUpdate, processingDTO);

        assertThat(toUpdate.getResultVariables()).isNotEmpty();
        assertThat(toUpdate.getResultVariables()).containsKey(OUTPUT_NAME);
        assertThat(toUpdate.getResultVariables().get(OUTPUT_NAME)).isEqualTo(variableValue);
    }

    private static ProcessingDTO buildProcessingDTOWithDefaultNameValues(KiePMMLModel kiePMMLModel) {
        List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3)
                .mapToObj(i -> new KiePMMLNameValue("val-" + i, i))
                .collect(Collectors.toList());
        return getProcessingDTO(kiePMMLModel, kiePMMLNameValues);
    }

    private static ProcessingDTO buildProcessingDTOWithEmptyNameValues(KiePMMLModel kiePMMLModel) {
        return getProcessingDTO(kiePMMLModel, new ArrayList<>());
    }

    private static ProcessingDTO buildProcessingDTOWithNameValues(KiePMMLModel kiePMMLModel, KiePMMLNameValue... nameValues) {
        List<KiePMMLNameValue> kiePMMLNameValues = Stream.concat(
                IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue("val-" + i, i)),
                Arrays.stream(nameValues)
        ).collect(Collectors.toList());
        return getProcessingDTO(kiePMMLModel, kiePMMLNameValues);
    }

    private static KiePMMLTestingModel.Builder testingModelBuilder(KiePMMLOutputField outputField) {
        return (KiePMMLTestingModel.Builder) KiePMMLTestingModel.builder("FILENAME", "TESTINGMODEL", Collections.emptyList(),
                                                                         MINING_FUNCTION.REGRESSION)
                .withKiePMMLOutputFields(Collections.singletonList(outputField));
    }

}