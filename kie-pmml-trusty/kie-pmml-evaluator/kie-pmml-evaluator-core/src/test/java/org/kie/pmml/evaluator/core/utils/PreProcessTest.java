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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.CLOSURE;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class PreProcessTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String CUSTOM_REF_FIELD = "CUSTOM_REF_FIELD";
    private static final String INPUT_FIELD = "INPUT_FIELD";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;

    @Test
    void convertInputDataConvertibles() {
        List<KiePMMLMiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
            DATA_TYPE dataType = DATA_TYPE.values()[i];
            return KiePMMLMiningField.builder("FIELD-" + i, null)
                    .withDataType(dataType)
                    .build();
        })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", 123);
        pmmlRequestData.addRequestParam("FIELD-1", "123");
        pmmlRequestData.addRequestParam("FIELD-2", "1.23");
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertThat(mappedRequestParams.get("FIELD-0").getValue()).isEqualTo(123);
        assertThat(mappedRequestParams.get("FIELD-1").getValue()).isEqualTo("123");
        assertThat(mappedRequestParams.get("FIELD-2").getValue()).isEqualTo("1.23");
        PreProcess.convertInputData(miningFields, pmmlRequestData);
        assertThat(mappedRequestParams.get("FIELD-0").getValue()).isEqualTo("123");
        assertThat(mappedRequestParams.get("FIELD-1").getValue()).isEqualTo(123);
        assertThat(mappedRequestParams.get("FIELD-2").getValue()).isEqualTo(1.23f);
    }

    @Test
    void convertInputDataNotConvertibles() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            List<KiePMMLMiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                DATA_TYPE dataType = DATA_TYPE.values()[i];
                new MiningField("FIELD-" + i, null, null, dataType, null, null, null, null, null, null);
                return KiePMMLMiningField.builder("FIELD-" + i, null)
                        .withDataType(dataType)
                        .build();
            })
                    .collect(Collectors.toList());
            PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
            pmmlRequestData.addRequestParam("FIELD-0", 123);
            pmmlRequestData.addRequestParam("FIELD-1", true);
            pmmlRequestData.addRequestParam("FIELD-2", "123");
            PreProcess.convertInputData(miningFields, pmmlRequestData);
        });
    }

    @Test
    void verifyFixInvalidValuesNotInvalid() {
        KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                .withDataType(DATA_TYPE.STRING)
                .withAllowedValues(Arrays.asList("123", "124", "125"))
                .build();
        KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                .build();
        List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
        KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withIntervals(intervals)
                .build();
        List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);

        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "123");
        pmmlRequestData.addRequestParam("FIELD-1", 12.4);
        pmmlRequestData.addRequestParam("FIELD-2", 9.3);
        PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
        pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "125");
        pmmlRequestData.addRequestParam("FIELD-1", 1.25);
        pmmlRequestData.addRequestParam("FIELD-2", 13.9);
        PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
    }

    @Test
    void verifyFixInvalidValuesInvalidReturnInvalid() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                    .withDataType(DATA_TYPE.STRING)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                    .withAllowedValues(Arrays.asList("123", "124", "125"))
                    .build();
            KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                    .withDataType(DATA_TYPE.DOUBLE)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                    .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                    .build();
            List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                    new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
            KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                    .withDataType(DATA_TYPE.DOUBLE)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                    .withIntervals(intervals)
                    .build();
            List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);

            PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
            pmmlRequestData.addRequestParam("FIELD-0", "122");
            pmmlRequestData.addRequestParam("FIELD-1", 12.5);
            pmmlRequestData.addRequestParam("FIELD-2", 14.6);
            PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
        });
    }

    @Test
    void verifyFixInvalidValuesInvalidAsMissing() {
        KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                .withDataType(DATA_TYPE.STRING)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_MISSING)
                .withAllowedValues(Arrays.asList("123", "124", "125"))
                .build();
        KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_MISSING)
                .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                .build();
        List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
        KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_MISSING)
                .withIntervals(intervals)
                .build();
        List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);

        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
        assertThat(pmmlRequestData.getRequestParams()).isEmpty();
    }

    @Test
    void verifyFixInvalidValuesInvalidAsValueWithReplacement() {
        KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                .withDataType(DATA_TYPE.STRING)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                .withInvalidValueReplacement("123")
                .withAllowedValues(Arrays.asList("123", "124", "125"))
                .build();
        KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                .withInvalidValueReplacement("1.23")
                .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                .build();
        List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
        KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                .withInvalidValueReplacement("12.3")
                .withIntervals(intervals)
                .build();
        List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertThat(mappedRequestParams.get("FIELD-0").getValue()).isEqualTo("123");
        assertThat(mappedRequestParams.get("FIELD-1").getValue()).isEqualTo(1.23);
        assertThat(mappedRequestParams.get("FIELD-2").getValue()).isEqualTo(12.3);
    }

    @Test
    void verifyFixInvalidValuesInvalidAsValueWithoutReplacement() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                    .withDataType(DATA_TYPE.STRING)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                    .withAllowedValues(Arrays.asList("123", "124", "125"))
                    .build();
            KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                    .withDataType(DATA_TYPE.DOUBLE)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                    .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                    .build();
            List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                    new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
            KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                    .withDataType(DATA_TYPE.DOUBLE)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                    .withIntervals(intervals)
                    .build();
            List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);

            PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
            pmmlRequestData.addRequestParam("FIELD-0", "122");
            pmmlRequestData.addRequestParam("FIELD-1", 12.5);
            pmmlRequestData.addRequestParam("FIELD-2", 14.6);
            PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
        });
    }

    @Test
    void verifyFixInvalidValuesInvalidAsIs() {
        KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                .withDataType(DATA_TYPE.STRING)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_IS)
                .withInvalidValueReplacement("123")
                .withAllowedValues(Arrays.asList("123", "124", "125"))
                .build();
        KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_IS)
                .withInvalidValueReplacement("1.23")
                .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                .build();
        List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
        KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_IS)
                .withInvalidValueReplacement("12.3")
                .withIntervals(intervals)
                .build();
        List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyFixInvalidValues(miningFields, pmmlRequestData);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertThat(mappedRequestParams.get("FIELD-0").getValue()).isEqualTo("122");
        assertThat(mappedRequestParams.get("FIELD-1").getValue()).isEqualTo(12.5);
        assertThat(mappedRequestParams.get("FIELD-2").getValue()).isEqualTo(14.6);
    }

    @Test
    void verifyAddMissingValuesNotMissingReturnInvalid() {
        List<KiePMMLMiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
            DATA_TYPE dataType = DATA_TYPE.values()[i];
            return KiePMMLMiningField.builder("FIELD-" + i, null)
                    .withDataType(dataType)
                    .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                    .build();
        })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "123");
        pmmlRequestData.addRequestParam("FIELD-1", 123);
        pmmlRequestData.addRequestParam("FIELD-2", 1.23f);
        PreProcess.verifyAddMissingValues(miningFields, pmmlRequestData);
    }

    @Test
    void verifyAddMissingValuesNotMissingNotReturnInvalidNotReplacement() {
        List<KiePMMLMiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
            DATA_TYPE dataType = DATA_TYPE.values()[i];
            return KiePMMLMiningField.builder("FIELD-" + i, null)
                    .withDataType(dataType)
                    .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.AS_IS)
                    .build();
        })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        PreProcess.verifyAddMissingValues(miningFields, pmmlRequestData);
    }

    @Test
    void verifyAddMissingValuesNotMissingNotReturnInvalidReplacement() {
        KiePMMLMiningField miningField0 = KiePMMLMiningField.builder("FIELD-0", null)
                .withDataType(DATA_TYPE.STRING)
                .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.AS_IS)
                .withMissingValueReplacement("123")
                .withAllowedValues(Arrays.asList("123", "124", "125"))
                .build();
        KiePMMLMiningField miningField1 = KiePMMLMiningField.builder("FIELD-1", null)
                .withDataType(DATA_TYPE.DOUBLE)
                .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.AS_IS)
                .withMissingValueReplacement("1.23")
                .withAllowedValues(Arrays.asList("1.23", "12.4", "1.25"))
                .build();
        List<KiePMMLInterval> intervals = Arrays.asList(new KiePMMLInterval(0.0, 12.4, CLOSURE.CLOSED_CLOSED),
                new KiePMMLInterval(12.6, 14.5, CLOSURE.OPEN_CLOSED));
        KiePMMLMiningField miningField2 = KiePMMLMiningField.builder("FIELD-2", null)
                .withDataType(DATA_TYPE.FLOAT)
                .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.AS_IS)
                .withMissingValueReplacement("12.9")
                .withIntervals(intervals)
                .build();

        List<KiePMMLMiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        assertThat(pmmlRequestData.getRequestParams()).isEmpty();
        PreProcess.verifyAddMissingValues(miningFields, pmmlRequestData);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertThat(mappedRequestParams).hasSameSizeAs(miningFields);
        assertThat(mappedRequestParams.get("FIELD-0").getValue()).isEqualTo("123");
        assertThat(mappedRequestParams.get("FIELD-1").getValue()).isEqualTo(1.23);
        assertThat(mappedRequestParams.get("FIELD-2").getValue()).isEqualTo(12.9f);
    }

    @Test
    void verifyAddMissingValuesMissingReturnInvalid() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            List<KiePMMLMiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                DATA_TYPE dataType = DATA_TYPE.values()[i];

                return KiePMMLMiningField.builder("FIELD-" + i, null)
                        .withDataType(dataType)
                        .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                        .build();
            })
                    .collect(Collectors.toList());
            PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
            PreProcess.verifyAddMissingValues(miningFields, pmmlRequestData);
        });
    }

    @Test
    void executeTransformations() {
        // <DefineFunction name="CUSTOM_FUNCTION" optype="continuous" dataType="double">
        //     <ParameterField name="PARAM_1" />
        //     <ParameterField name="PARAM_2" />
        //     <Apply function="/">
        //        <FieldRef>PARAM_1</FieldRef>
        //        <FieldRef>PARAM_2</FieldRef>
        //      </Apply>
        // </DefineFunction>
        final KiePMMLParameterField kiePMMLParameterField1 = KiePMMLParameterField.builder(PARAM_1,
                Collections.emptyList()).build();
        final KiePMMLParameterField kiePMMLParameterField2 = KiePMMLParameterField.builder(PARAM_2,
                Collections.emptyList()).build();
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLFieldRef kiePMMLFieldRef2 = new KiePMMLFieldRef(PARAM_2, Collections.emptyList(), null);
        final KiePMMLApply kiePMMLApplyRef = KiePMMLApply.builder("NAMEREF", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef1, kiePMMLFieldRef2))
                .build();
        final KiePMMLDefineFunction defineFunction = new KiePMMLDefineFunction(CUSTOM_FUNCTION, Collections.emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                Arrays.asList(kiePMMLParameterField1,
                        kiePMMLParameterField2),
                kiePMMLApplyRef);

        // <DerivedField name="CUSTOM_REF_FIELD" optype="continuous" dataType="double">
        //     <Apply function="CUSTOM_FUNCTION">
        //        <FieldRef>INPUT_FIELD</FieldRef>
        //        <Constant>5.0</Constant>
        //      </Apply>
        // </DerivedField>
        final KiePMMLFieldRef kiePMMLFieldRef3 = new KiePMMLFieldRef(INPUT_FIELD, Collections.emptyList(), null);
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        final KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), CUSTOM_FUNCTION)
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef3, kiePMMLConstant1))
                .build();
        final KiePMMLDerivedField derivedField = KiePMMLDerivedField.builder(CUSTOM_REF_FIELD, Collections.emptyList(),
                DATA_TYPE.DOUBLE,
                OP_TYPE.CONTINUOUS,
                kiePMMLApply).build();
        // From TransformationDictionary
        KiePMMLTransformationDictionary transformationDictionary = KiePMMLTransformationDictionary.builder(
                "transformationDictionary", Collections.emptyList())
                .withDefineFunctions(Collections.singletonList(defineFunction))
                .withDerivedFields(Collections.singletonList(derivedField))
                .build();
        KiePMMLTestingModel kiePMMLModel = KiePMMLTestingModel
                .builder("FILENAME", "TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withKiePMMLTransformationDictionary(transformationDictionary)
                .build();
        //
        final PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.addRequestParam(INPUT_FIELD, value1);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        final List<KiePMMLNameValue> kiePMMLNameValues =
                PreProcess.getKiePMMLNameValuesFromParameterInfos(mappedRequestParams.values());
        Optional<KiePMMLNameValue> retrieved =
                kiePMMLNameValues.stream().filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(INPUT_FIELD)).findFirst();
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getValue()).isEqualTo(value1);

        ProcessingDTO processingDTO = new ProcessingDTO(kiePMMLModel, kiePMMLNameValues);
        PreProcess.executeTransformations(processingDTO, pmmlRequestData);
        mappedRequestParams = pmmlRequestData.getMappedRequestParams();

        Object expected = value1 / value2;
        assertThat(mappedRequestParams).containsKey(CUSTOM_REF_FIELD);
        assertThat(mappedRequestParams.get(CUSTOM_REF_FIELD).getValue()).isEqualTo(expected);
        retrieved =
                kiePMMLNameValues.stream().filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(CUSTOM_REF_FIELD)).findFirst();
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getValue()).isEqualTo(expected);
    }

    @Test
    void manageInvalidValuesNotReturnInvalid() {
        final ParameterInfo parameterInfo = new ParameterInfo();
        // AS_MISSING
        KiePMMLMiningField miningField = KiePMMLMiningField.builder("FIELD", null)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_MISSING)
                .build();
        List<ParameterInfo> toRemove = new ArrayList<>();
        PreProcess.manageInvalidValues(miningField, parameterInfo, toRemove);
        assertThat(toRemove).hasSize(1);
        assertThat(toRemove).contains(parameterInfo);
        // AS_IS
        miningField = KiePMMLMiningField.builder("FIELD", null)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_IS)
                .build();
        toRemove = new ArrayList<>();
        PreProcess.manageInvalidValues(miningField, parameterInfo, toRemove);
        assertThat(toRemove).isEmpty();
        // AS_VALUE with replacement
        String invalidValueReplacement = "REPLACEMENT";
        miningField = KiePMMLMiningField.builder("FIELD", null)
                .withDataType(DATA_TYPE.STRING)
                .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                .withInvalidValueReplacement(invalidValueReplacement)
                .build();
        toRemove = new ArrayList<>();
        assertThat(parameterInfo.getValue()).isNull();
        assertThat(parameterInfo.getType()).isNull();
        PreProcess.manageInvalidValues(miningField, parameterInfo, toRemove);
        assertThat(toRemove).isEmpty();
        assertThat(parameterInfo.getValue()).isEqualTo(invalidValueReplacement);
        assertThat(parameterInfo.getType()).isEqualTo(String.class);
    }

    @Test
    void manageInvalidValuesAsValueNoReplacement() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final ParameterInfo parameterInfo = new ParameterInfo();
            // AS_VALUE without replacement
            KiePMMLMiningField miningField = KiePMMLMiningField.builder("FIELD", null)
                    .withDataType(DATA_TYPE.STRING)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.AS_VALUE)
                    .build();
            List<ParameterInfo> toRemove = new ArrayList<>();
            assertThat(parameterInfo.getValue()).isNull();
            assertThat(parameterInfo.getType()).isNull();
            PreProcess.manageInvalidValues(miningField, parameterInfo, toRemove);
        });
    }

    @Test
    void manageInvalidValuesReturnInvalid() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final ParameterInfo parameterInfo = new ParameterInfo();
            // RETURN_INVALID
            KiePMMLMiningField miningField = KiePMMLMiningField.builder("FIELD", null)
                    .withDataType(DATA_TYPE.STRING)
                    .withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                    .build();
            List<ParameterInfo> toRemove = new ArrayList<>();
            PreProcess.manageInvalidValues(miningField, parameterInfo, toRemove);
        });
    }

    @Test
    void manageMissingValuesNotReturnInvalid() {
        List<MISSING_VALUE_TREATMENT_METHOD> missingValueTreatmentMethods =
                Arrays.stream(MISSING_VALUE_TREATMENT_METHOD.values())
                        .filter(treatmentMethod -> !treatmentMethod.equals(MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID))
                        .collect(Collectors.toList());
        final String fieldName = "FIELD";
        missingValueTreatmentMethods.forEach(missingValueTreatmentMethod -> {
            KiePMMLMiningField miningField = KiePMMLMiningField.builder(fieldName, null)
                    .withMissingValueTreatmentMethod(missingValueTreatmentMethod)
                    .build();
            PMMLRequestData pmmlRequestData = new PMMLRequestData();
            PreProcess.manageMissingValues(miningField, pmmlRequestData);
            assertThat(pmmlRequestData.getRequestParams()).isEmpty();
            String missingValueReplacement = "REPLACEMENT";
            miningField = KiePMMLMiningField.builder(fieldName, null)
                    .withDataType(DATA_TYPE.STRING)
                    .withMissingValueTreatmentMethod(missingValueTreatmentMethod)
                    .withMissingValueReplacement(missingValueReplacement)
                    .build();
            pmmlRequestData = new PMMLRequestData();
            PreProcess.manageMissingValues(miningField, pmmlRequestData);
            assertThat(pmmlRequestData.getRequestParams()).hasSize(1);
            assertThat(pmmlRequestData.getMappedRequestParams()).containsKey(fieldName);
            ParameterInfo parameterInfo = pmmlRequestData.getMappedRequestParams().get(fieldName);
            assertThat(parameterInfo.getValue()).isEqualTo(missingValueReplacement);
            assertThat(parameterInfo.getType()).isEqualTo(String.class);
        });
    }

    @Test
    void manageMissingValuesReturnInvalid() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            KiePMMLMiningField miningField = KiePMMLMiningField.builder("FIELD", null)
                    .withDataType(DATA_TYPE.STRING)
                    .withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID)
                    .build();
            PreProcess.manageMissingValues(miningField, new PMMLRequestData());
        });
    }
}