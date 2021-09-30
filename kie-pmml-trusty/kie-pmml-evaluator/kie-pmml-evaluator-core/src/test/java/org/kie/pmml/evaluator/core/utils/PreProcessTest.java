/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.evaluator.core.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreProcessTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String CUSTOM_FIELD = "CUSTOM_FIELD";
    private static final String CUSTOM_REF_FIELD = "CUSTOM_REF_FIELD";
    private static final String INPUT_FIELD = "INPUT_FIELD";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;

    @Test
    public void verifyMissingValuesNotMissingReturnInvalid() {
        List<MiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                    DATA_TYPE dataType = DATA_TYPE.values()[i];
                    return new MiningField("FIELD-" + i, null, null, dataType,
                                           MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, null, null, null);
                })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "123");
        pmmlRequestData.addRequestParam("FIELD-1", 123);
        pmmlRequestData.addRequestParam("FIELD-2", 1.23f);
        PreProcess.verifyMissingValues(miningFields, pmmlRequestData);
    }

    @Test
    public void verifyMissingValuesNotMissingNotReturnInvalidNotReplacement() {
        List<MiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                    DATA_TYPE dataType = DATA_TYPE.values()[i];
                    return new MiningField("FIELD-" + i, null, null, dataType, MISSING_VALUE_TREATMENT_METHOD.AS_IS,
                                           null, null, null, null, null);
                })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        PreProcess.verifyMissingValues(miningFields, pmmlRequestData);
    }

    @Test
    public void verifyMissingValuesNotMissingNotReturnInvalidReplacement() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING,
                                                   MISSING_VALUE_TREATMENT_METHOD.AS_IS,
                                                   null, "123", null, Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE,
                                                   MISSING_VALUE_TREATMENT_METHOD.AS_IS,
                                                   null, "1.23", null, Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.FLOAT,
                                                   MISSING_VALUE_TREATMENT_METHOD.AS_IS,
                                                   null, "12.9", null, null,
                                                   intervals);

        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        assertTrue(pmmlRequestData.getRequestParams().isEmpty());
        PreProcess.verifyMissingValues(miningFields, pmmlRequestData);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertEquals(miningFields.size(), mappedRequestParams.size());
        assertEquals("123", mappedRequestParams.get("FIELD-0").getValue());
        assertEquals(1.23, mappedRequestParams.get("FIELD-1").getValue());
        assertEquals(12.9f, mappedRequestParams.get("FIELD-2").getValue());
    }

    @Test(expected = KiePMMLException.class)
    public void verifyMissingValuesMissingReturnInvalid() {
        List<MiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                    DATA_TYPE dataType = DATA_TYPE.values()[i];
                    return new MiningField("FIELD-" + i, null, null, dataType,
                                           MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, null, null, null);
                })
                .collect(Collectors.toList());
        KiePMMLTestingModel model = KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(),
                                                                MINING_FUNCTION.REGRESSION)
                .withMiningFields(miningFields)
                .build();
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        PreProcess.verifyMissingValues(miningFields, pmmlRequestData);
    }

    @Test
    public void convertInputDataConvertibles() {
        List<MiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                    DATA_TYPE dataType = DATA_TYPE.values()[i];
                    return new MiningField("FIELD-" + i, null, null, dataType, null, null, null, null, null, null);
                })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", 123);
        pmmlRequestData.addRequestParam("FIELD-1", "123");
        pmmlRequestData.addRequestParam("FIELD-2", "1.23");
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertEquals(123, mappedRequestParams.get("FIELD-0").getValue());
        assertEquals("123", mappedRequestParams.get("FIELD-1").getValue());
        assertEquals("1.23", mappedRequestParams.get("FIELD-2").getValue());
        PreProcess.convertInputData(miningFields, pmmlRequestData);
        assertEquals("123", mappedRequestParams.get("FIELD-0").getValue());
        assertEquals(123, mappedRequestParams.get("FIELD-1").getValue());
        assertEquals(1.23f, mappedRequestParams.get("FIELD-2").getValue());
    }

    @Test(expected = KiePMMLException.class)
    public void convertInputDataNotConvertibles() {
        List<MiningField> miningFields = IntStream.range(0, 3).mapToObj(i -> {
                    DATA_TYPE dataType = DATA_TYPE.values()[i];
                    return new MiningField("FIELD-" + i, null, null, dataType, null, null, null, null, null, null);
                })
                .collect(Collectors.toList());
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", 123);
        pmmlRequestData.addRequestParam("FIELD-1", true);
        pmmlRequestData.addRequestParam("FIELD-2", "123");
        PreProcess.convertInputData(miningFields, pmmlRequestData);
    }

    @Test
    public void verifyInvalidValuesNotInvalid() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING, null, null, null, null,
                                                   Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE, null, null, null, null,
                                                   Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.DOUBLE, null, null, null, null,
                                                   null, intervals);

        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "123");
        pmmlRequestData.addRequestParam("FIELD-1", 12.4);
        pmmlRequestData.addRequestParam("FIELD-2", 9.3);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
        pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "125");
        pmmlRequestData.addRequestParam("FIELD-1", 1.25);
        pmmlRequestData.addRequestParam("FIELD-2", 13.9);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void verifyInvalidValuesInvalidReturnInvalid() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null,
                                                   Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null,
                                                   Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, null,
                                                   intervals);

        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
    }

    @Test
    public void verifyInvalidValuesInvalidAsMissing() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_MISSING, null, null,
                                                   Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_MISSING, null, null,
                                                   Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_MISSING, null, null, null,
                                                   intervals);

        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
        assertTrue(pmmlRequestData.getRequestParams().isEmpty());
    }

    @Test
    public void verifyInvalidValuesInvalidAsValueWithReplacement() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_VALUE, null, "123",
                                                   Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_VALUE, null, "1.23",
                                                   Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_VALUE, null, "12.3",
                                                   null, intervals);
        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertEquals("123", mappedRequestParams.get("FIELD-0").getValue());
        assertEquals(1.23, mappedRequestParams.get("FIELD-1").getValue());
        assertEquals(12.3, mappedRequestParams.get("FIELD-2").getValue());
    }

    @Test(expected = KiePMMLException.class)
    public void verifyInvalidValuesInvalidAsValueWithoutReplacement() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_VALUE, null, null,
                                                   Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_VALUE, null, null,
                                                   Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_VALUE, null, null,
                                                   null, intervals);
        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
    }

    @Test
    public void verifyInvalidValuesInvalidAsIs() {
        MiningField miningField0 = new MiningField("FIELD-0", null, null, DATA_TYPE.STRING, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_IS, null, "123",
                                                   Arrays.asList("123", "124", "125"), null);
        MiningField miningField1 = new MiningField("FIELD-1", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_IS, null, "1.23",
                                                   Arrays.asList("1.23", "12.4", "1.25"), null);
        List<Interval> intervals = Arrays.asList(new Interval(0.0, 12.4), new Interval(12.6, 14.5));
        MiningField miningField2 = new MiningField("FIELD-2", null, null, DATA_TYPE.DOUBLE, null,
                                                   INVALID_VALUE_TREATMENT_METHOD.AS_IS, null, "12.3",
                                                   null, intervals);
        List<MiningField> miningFields = Arrays.asList(miningField0, miningField1, miningField2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("FIELD-0", "122");
        pmmlRequestData.addRequestParam("FIELD-1", 12.5);
        pmmlRequestData.addRequestParam("FIELD-2", 14.6);
        PreProcess.verifyInvalidValues(miningFields, pmmlRequestData);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        assertEquals("122", mappedRequestParams.get("FIELD-0").getValue());
        assertEquals(12.5, mappedRequestParams.get("FIELD-1").getValue());
        assertEquals(14.6, mappedRequestParams.get("FIELD-2").getValue());
    }

    @Test
    public void executeTransformations() {
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
                .builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withTransformationDictionary(transformationDictionary)
                .build();
        //
        final PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.addRequestParam(INPUT_FIELD, value1);
        Map<String, ParameterInfo> mappedRequestParams = pmmlRequestData.getMappedRequestParams();
        final List<KiePMMLNameValue> kiePMMLNameValues =
                PreProcess.getKiePMMLNameValuesFromParameterInfos(mappedRequestParams.values());
        Optional<KiePMMLNameValue> retrieved =
                kiePMMLNameValues.stream().filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(INPUT_FIELD)).findFirst();
        assertTrue(retrieved.isPresent());
        assertEquals(value1, retrieved.get().getValue());

        ProcessingDTO processingDTO = new ProcessingDTO(kiePMMLModel, kiePMMLNameValues);
        PreProcess.executeTransformations(processingDTO, pmmlRequestData);
        mappedRequestParams = pmmlRequestData.getMappedRequestParams();

        Object expected = value1 / value2;
        assertTrue(mappedRequestParams.containsKey(CUSTOM_REF_FIELD));
        assertEquals(expected, mappedRequestParams.get(CUSTOM_REF_FIELD).getValue());
        retrieved =
                kiePMMLNameValues.stream().filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(CUSTOM_REF_FIELD)).findFirst();
        assertTrue(retrieved.isPresent());
        assertEquals(expected, retrieved.get().getValue());
    }
}