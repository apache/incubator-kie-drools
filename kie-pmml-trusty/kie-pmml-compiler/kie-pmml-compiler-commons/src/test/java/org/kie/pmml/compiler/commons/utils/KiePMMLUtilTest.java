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
package org.kie.pmml.compiler.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.xml.bind.JAXBException;

import org.apache.commons.lang3.RandomStringUtils;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MathContext;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.Target;
import org.dmg.pmml.Targets;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.MODELNAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.SEGMENTID_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.SEGMENTMODELNAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.TARGETFIELD_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.getMiningTargetFields;
import static org.drools.util.FileUtils.getFileInputStream;

public class KiePMMLUtilTest {

    private static final String NO_MODELNAME_SAMPLE_NAME = "NoModelNameSample.pmml";
    private static final String NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME = "NoModelNameNoSegmentIdSample.pmml";
    private static final String NO_MODELNAME_NO_SEGMENT_ID_NOSEGMENT_TARGET_FIELD_SAMPLE =
            "NoModelNameNoSegmentIdNoSegmentTargetFieldSample.pmml";
    private static final String NO_TARGET_FIELD_SAMPLE = "NoTargetFieldSample.pmml";
    private static final String NO_OUTPUT_FIELD_TARGET_NAME_SAMPLE = "NoOutputFieldTargetNameSample.pmml";
    private static final String MINING_WITH_SAME_NESTED_MODEL_NAMES = "MiningWithSameNestedModelNames.pmml";

    @Test
    void loadString() throws IOException, JAXBException, SAXException {
        commonLoadString(NO_MODELNAME_SAMPLE_NAME);
        commonLoadString(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
        commonLoadString(MINING_WITH_SAME_NESTED_MODEL_NAMES);
    }

    @Test
    void loadFile() throws JAXBException, IOException, SAXException {
        commonLoadFile(NO_MODELNAME_SAMPLE_NAME);
        commonLoadFile(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
        commonLoadFile(MINING_WITH_SAME_NESTED_MODEL_NAMES);
    }

    @Test
    void populateMissingModelName() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_SAMPLE_NAME);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model toPopulate = pmml.getModels().get(0);
        assertThat(toPopulate.getModelName()).isNull();
        KiePMMLUtil.populateMissingModelName(toPopulate, NO_MODELNAME_SAMPLE_NAME, 0);
        assertThat(toPopulate.getModelName()).isNotNull();
        String expected = String.format(MODELNAME_TEMPLATE,
                NO_MODELNAME_SAMPLE_NAME,
                toPopulate.getClass().getSimpleName(),
                0);
        assertThat(toPopulate.getModelName()).isEqualTo(expected);
    }

    @Test
    void populateMissingMiningTargetField() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_TARGET_FIELD_SAMPLE);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model toPopulate = pmml.getModels().get(0);
        List<MiningField> miningTargetFields = getMiningTargetFields(toPopulate.getMiningSchema().getMiningFields());
        assertThat(miningTargetFields).isEmpty();
        assertThat(toPopulate.getTargets().getTargets().get(0).getField()).isNull();
        KiePMMLUtil.populateMissingMiningTargetField(toPopulate, pmml.getDataDictionary().getDataFields());
        miningTargetFields = getMiningTargetFields(toPopulate.getMiningSchema().getMiningFields());
        assertThat(miningTargetFields).hasSize(1);
        final MiningField targetField = miningTargetFields.get(0);
        assertThat(pmml.getDataDictionary()
                .getDataFields()
                .stream()
                .anyMatch(dataField -> dataField.getName().equals(targetField.getName()))).isTrue();
        assertThat(toPopulate.getTargets().getTargets().get(0).getField()).isEqualTo(targetField.getName());
    }

    @Test
    void populateMissingPredictedOutputFieldTarget() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_OUTPUT_FIELD_TARGET_NAME_SAMPLE);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model toPopulate = pmml.getModels().get(0);
        final OutputField outputField = toPopulate.getOutput().getOutputFields().get(0);
        assertThat(outputField.getResultFeature()).isEqualTo(ResultFeature.PREDICTED_VALUE);
        assertThat(outputField.getTargetField()).isNull();
        KiePMMLUtil.populateMissingPredictedOutputFieldTarget(toPopulate);
        final MiningField targetField = getMiningTargetFields(toPopulate.getMiningSchema().getMiningFields()).get(0);
        assertThat(outputField.getTargetField()).isNotNull();
        assertThat(outputField.getTargetField()).isEqualTo(targetField.getName());
    }

    @Test
    void getTargetDataField() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_TARGET_FIELD_SAMPLE);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model model = pmml.getModels().get(0);
        Optional<DataField> optionalDataField = KiePMMLUtil.getTargetDataField(model);
        assertThat(optionalDataField).isPresent();
        DataField retrieved = optionalDataField.get();
        String expected = String.format(TARGETFIELD_TEMPLATE, "golfing");
        assertThat(retrieved.getName()).isEqualTo(expected);
    }

    @Test
    void getTargetDataType()  {
        MiningFunction miningFunction = MiningFunction.REGRESSION;
        MathContext mathContext = MathContext.DOUBLE;
        DataType retrieved = KiePMMLUtil.getTargetDataType(miningFunction, mathContext);
        assertThat(retrieved).isEqualTo(DataType.DOUBLE);
        mathContext = MathContext.FLOAT;
        retrieved = KiePMMLUtil.getTargetDataType(miningFunction, mathContext);
        assertThat(retrieved).isEqualTo(DataType.FLOAT);
        miningFunction = MiningFunction.CLASSIFICATION;
        retrieved = KiePMMLUtil.getTargetDataType(miningFunction, mathContext);
        assertThat(retrieved).isEqualTo(DataType.STRING);
        miningFunction = MiningFunction.CLUSTERING;
        retrieved = KiePMMLUtil.getTargetDataType(miningFunction, mathContext);
        assertThat(retrieved).isEqualTo(DataType.STRING);
        List<MiningFunction> notMappedMiningFunctions = Arrays.asList(MiningFunction.ASSOCIATION_RULES,
                MiningFunction.MIXED,
                MiningFunction.SEQUENCES,
                MiningFunction.TIME_SERIES);

        notMappedMiningFunctions.forEach(minFun -> assertThat(KiePMMLUtil.getTargetDataType(minFun, MathContext.DOUBLE)).isNull());
    }

    @Test
    void getTargetOpType()  {
        MiningFunction miningFunction = MiningFunction.REGRESSION;
        OpType retrieved = KiePMMLUtil.getTargetOpType(miningFunction);
        assertThat(retrieved).isEqualTo(OpType.CONTINUOUS);
        miningFunction = MiningFunction.CLASSIFICATION;
        retrieved = KiePMMLUtil.getTargetOpType(miningFunction);
        assertThat(retrieved).isEqualTo(OpType.CATEGORICAL);
        miningFunction = MiningFunction.CLUSTERING;
        retrieved = KiePMMLUtil.getTargetOpType(miningFunction);
        assertThat(retrieved).isEqualTo(OpType.CATEGORICAL);
        List<MiningFunction> notMappedMiningFunctions = Arrays.asList(MiningFunction.ASSOCIATION_RULES,
                MiningFunction.MIXED,
                MiningFunction.SEQUENCES,
                MiningFunction.TIME_SERIES);

        notMappedMiningFunctions.forEach(minFun -> assertThat(KiePMMLUtil.getTargetOpType(minFun)).isNull());
    }

    @Test
    void getTargetMiningField() {
        final DataField dataField = new DataField();
        dataField.setName("FIELD_NAME");
        final MiningField retrieved = KiePMMLUtil.getTargetMiningField(dataField);
        assertThat(retrieved.getName()).isEqualTo(dataField.getName());
        assertThat(retrieved.getUsageType()).isEqualTo(MiningField.UsageType.TARGET);
    }

    @Test
    void correctTargetFields() {
        final MiningField miningField = new MiningField("FIELD_NAME");
        final Targets targets = new Targets();
        final Target namedTarget = new Target();
        String targetName = "TARGET_NAME";
        namedTarget.setField(targetName);
        final Target unnamedTarget = new Target();
        targets.addTargets(namedTarget, unnamedTarget);
        KiePMMLUtil.correctTargetFields(miningField, targets);
        assertThat(namedTarget.getField()).isEqualTo(targetName);
        assertThat(unnamedTarget.getField()).isEqualTo(miningField.getName());
    }

    @Test
    void populateCorrectMiningModel() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_NO_SEGMENT_ID_NOSEGMENT_TARGET_FIELD_SAMPLE);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model retrieved = pmml.getModels().get(0);
        assertThat(retrieved).isInstanceOf(MiningModel.class);
        MiningModel miningModel = (MiningModel) retrieved;
        miningModel.getSegmentation().getSegments().forEach(segment -> {
            assertThat(segment.getId()).isNull();
            assertThat(segment.getModel().getModelName()).isNull();
            assertThat(getMiningTargetFields(segment.getModel().getMiningSchema())).isEmpty();
        });
        KiePMMLUtil.populateCorrectMiningModel(miningModel);
        miningModel.getSegmentation().getSegments().forEach(segment -> {
            assertThat(segment.getId()).isNotNull();
            assertThat(segment.getModel().getModelName()).isNotNull();
            assertThat(getMiningTargetFields(segment.getModel().getMiningSchema())).isNotEmpty();
        });
    }

    @Test
    void populateCorrectSegmentId() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model retrieved = pmml.getModels().get(0);
        assertThat(retrieved).isInstanceOf(MiningModel.class);
        MiningModel miningModel = (MiningModel) retrieved;
        Segment toPopulate = miningModel.getSegmentation().getSegments().get(0);
        assertThat(toPopulate.getId()).isNull();
        String modelName = "MODEL_NAME";
        int i = 0;
        KiePMMLUtil.populateCorrectSegmentId(toPopulate, modelName, i);
        assertThat(toPopulate.getId()).isNotNull();
        String expected = String.format(SEGMENTID_TEMPLATE,
                modelName,
                i);
        assertThat(toPopulate.getId()).isEqualTo(expected);
    }

    @Test
    void populateMissingSegmentModelName() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model retrieved = pmml.getModels().get(0);
        assertThat(retrieved).isInstanceOf(MiningModel.class);
        MiningModel miningModel = (MiningModel) retrieved;
        Model toPopulate = miningModel.getSegmentation().getSegments().get(0).getModel();
        assertThat(toPopulate.getModelName()).isNull();
        String segmentId = "SEG_ID";
        KiePMMLUtil.populateMissingSegmentModelName(toPopulate, segmentId);
        assertThat(toPopulate.getModelName()).isNotNull();
        String expected = String.format(SEGMENTMODELNAME_TEMPLATE,
                segmentId,
                toPopulate.getClass().getSimpleName());
        assertThat(toPopulate.getModelName()).isEqualTo(expected);
    }

    @Test
    void populateMissingTargetFieldInSegment() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_NO_SEGMENT_ID_NOSEGMENT_TARGET_FIELD_SAMPLE);
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model retrieved = pmml.getModels().get(0);
        assertThat(retrieved).isInstanceOf(MiningModel.class);
        MiningModel miningModel = (MiningModel) retrieved;
        Model toPopulate = miningModel.getSegmentation().getSegments().get(0).getModel();
        assertThat(getMiningTargetFields(toPopulate.getMiningSchema())).isEmpty();
        KiePMMLUtil.populateMissingTargetFieldInSegment(retrieved.getMiningSchema(), toPopulate);
        List<MiningField> childrenTargetFields = getMiningTargetFields(toPopulate.getMiningSchema());
        assertThat(childrenTargetFields).isNotEmpty();
        getMiningTargetFields(miningModel.getMiningSchema()).forEach(parentTargetField -> assertThat(childrenTargetFields).contains(parentTargetField));
    }


    @Test
    void populateMissingOutputFieldDataType() {
        Random random = new Random();
        List<String> fieldNames = IntStream.range(0, 6)
                .mapToObj(i -> RandomStringUtils.random(6, true, false))
                .collect(Collectors.toList());
        List<DataField> dataFields = fieldNames.stream()
                .map(fieldName -> {
                    DataField toReturn = new DataField();
                    toReturn.setName(fieldName);
                    DataType dataType = DataType.values()[random.nextInt(DataType.values().length)];
                    toReturn.setDataType(dataType);
                    return toReturn;
                })
                .collect(Collectors.toList());
        List<MiningField> miningFields = IntStream.range(0, dataFields.size() - 1)
                .mapToObj(dataFields::get)
                .map(dataField -> {
                    MiningField toReturn = new MiningField();
                    toReturn.setName(dataField.getName());
                    toReturn.setUsageType(MiningField.UsageType.ACTIVE);
                    return toReturn;
                })
                .collect(Collectors.toList());
        DataField lastDataField = dataFields.get(dataFields.size() - 1);
        MiningField targetMiningField = new MiningField();
        targetMiningField.setName(lastDataField.getName());
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        miningFields.add(targetMiningField);
        // Following OutputFields should be populated based on "ResultFeature.PROBABILITY"
        List<OutputField> outputFields = IntStream.range(0, 3)
                .mapToObj(i -> {
                    OutputField toReturn = new OutputField();
                    toReturn.setName(RandomStringUtils.random(6, true, false));
                    toReturn.setResultFeature(ResultFeature.PROBABILITY);
                    return toReturn;
                })
                .collect(Collectors.toList());
        // Following OutputField should be populated based on "ResultFeature.PREDICTED_VALUE"
        OutputField targetOutputField = new OutputField();
        targetOutputField.setName(RandomStringUtils.random(6, true, false));
        targetOutputField.setResultFeature(ResultFeature.PREDICTED_VALUE);
        outputFields.add(targetOutputField);
        // Following OutputField should be populated based on "TargetField" property
        OutputField targetingOutputField = new OutputField();
        targetingOutputField.setName(RandomStringUtils.random(6, true, false));
        targetingOutputField.setTargetField(targetMiningField.getName());
        outputFields.add(targetingOutputField);
        outputFields.forEach(outputField -> assertThat(outputField.getDataType()).isNull());
        IntStream.range(0, 2)
                .forEach(i -> {
                    OutputField toAdd = new OutputField();
                    toAdd.setName(RandomStringUtils.random(6, true, false));
                    DataType dataType = DataType.values()[random.nextInt(DataType.values().length)];
                    toAdd.setDataType(dataType);
                    outputFields.add(toAdd);
                });
        KiePMMLUtil.populateMissingOutputFieldDataType(outputFields, miningFields, dataFields);
        outputFields.forEach(outputField -> assertThat(outputField.getDataType()).isNotNull());
    }

    @Test
    void getSanitizedId() {
        final String modelName = "MODEL_NAME";
        String id = "2";
        String expected = String.format(SEGMENTID_TEMPLATE, modelName, id);
        String retrieved = KiePMMLUtil.getSanitizedId(id, modelName);
        assertThat(retrieved).isEqualTo(expected);
        id = "34.5";
        expected = String.format(SEGMENTID_TEMPLATE, modelName, id);
        retrieved = KiePMMLUtil.getSanitizedId(id, modelName);
        assertThat(retrieved).isEqualTo(expected);
        id = "3,45";
        expected = String.format(SEGMENTID_TEMPLATE, modelName, id);
        retrieved = KiePMMLUtil.getSanitizedId(id, modelName);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getMiningTargetFieldsFromMiningSchema() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_SAMPLE_NAME);
        final PMML toPopulate = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model model = toPopulate.getModels().get(0);
        List<MiningField> retrieved = KiePMMLUtil.getMiningTargetFields(model.getMiningSchema());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        MiningField targetField = retrieved.get(0);
        assertThat(targetField.getName()).isEqualTo("car_location");
        assertThat(targetField.getUsageType().value()).isEqualTo("target");
    }

    @Test
    void getMiningTargetFieldsFromMiningFields() throws Exception {
        final InputStream inputStream = getFileInputStream(NO_MODELNAME_SAMPLE_NAME);
        final PMML toPopulate = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        final Model model = toPopulate.getModels().get(0);
        List<MiningField> retrieved = KiePMMLUtil.getMiningTargetFields(model.getMiningSchema().getMiningFields());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        MiningField targetField = retrieved.get(0);
        assertThat(targetField.getName()).isEqualTo("car_location");
        assertThat(targetField.getUsageType().value()).isEqualTo("target");
    }

    private void commonLoadString(String fileName) throws IOException, JAXBException, SAXException {
        InputStream inputStream = getFileInputStream(fileName);

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                                                        (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        PMML retrieved = KiePMMLUtil.load(textBuilder.toString());
        commonValidatePMML(retrieved);
    }

    private void commonLoadFile(String fileName) throws IOException, JAXBException, SAXException {
        PMML retrieved = KiePMMLUtil.load(getFileInputStream(fileName), fileName);
        commonValidatePMML(retrieved);
    }

    private void commonValidatePMML(PMML toValidate) {
        assertThat(toValidate).isNotNull();
        for (Model model : toValidate.getModels()) {
            assertThat(model.getModelName()).isNotNull();
            if (model instanceof MiningModel) {
                commonValidateMiningModel((MiningModel) model);
            }
        }
    }

    private void commonValidateMiningModel(MiningModel toValidate) {
        assertThat(toValidate).isNotNull();
        for (Segment segment : toValidate.getSegmentation().getSegments()) {
            assertThat(segment.getId()).isNotNull();
            Model segmentModel = segment.getModel();
            assertThat(segmentModel.getModelName()).isNotNull();
            if (segmentModel instanceof MiningModel) {
                commonValidateMiningModel((MiningModel) segmentModel);
            }
        }
        List<String> modelNames = toValidate.getSegmentation().getSegments()
                .stream()
                .map(segment -> segment.getModel().getModelName())
                .collect(Collectors.toList());
        assertThat(modelNames.stream().distinct()).hasSameSizeAs(modelNames);
    }
}