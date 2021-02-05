/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.compiler.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.RandomStringUtils;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.Model;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.SEGMENTID_TEMPLATE;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLUtilTest {

    private static final String NO_MODELNAME_SAMPLE_NAME = "NoModelNameSample.pmml";
    private static final String NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME = "NoModelNameNoSegmentIdSample.pmml";

    @Test
    public void loadString() throws IOException, JAXBException, SAXException {
        commonLoadString(NO_MODELNAME_SAMPLE_NAME);
        commonLoadString(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
    }

    @Test
    public void loadFile() throws JAXBException, IOException, SAXException {
        commonLoadFile(NO_MODELNAME_SAMPLE_NAME);
        commonLoadFile(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
    }

    @Test
    public void populateMissingOutputFieldDataType() {
        Random random = new Random();
        List<String> fieldNames = IntStream.range(0, 6)
                .mapToObj(i -> RandomStringUtils.random(6, true, false))
                .collect(Collectors.toList());
        List<DataField> dataFields = fieldNames.stream()
                .map(fieldName -> {
                    DataField toReturn = new DataField();
                    toReturn.setName(FieldName.create(fieldName));
                    DataType dataType = DataType.values()[random.nextInt(DataType.values().length)];
                    toReturn.setDataType(dataType);
                    return toReturn;
                })
                .collect(Collectors.toList());
        List<MiningField> miningFields = IntStream.range(0, dataFields.size() - 1)
                .mapToObj(dataFields::get)
                .map(dataField -> {
                    MiningField toReturn = new MiningField();
                    toReturn.setName(FieldName.create(dataField.getName().getValue()));
                    toReturn.setUsageType(MiningField.UsageType.ACTIVE);
                    return toReturn;
                })
                .collect(Collectors.toList());
        DataField lastDataField = dataFields.get(dataFields.size() - 1);
        MiningField targetMiningField = new MiningField();
        targetMiningField.setName(FieldName.create(lastDataField.getName().getValue()));
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        miningFields.add(targetMiningField);
        // Following OutputFields should be populated based on "ResultFeature.PROBABILITY"
        List<OutputField> outputFields = IntStream.range(0, 3)
                .mapToObj(i -> {
                    OutputField toReturn = new OutputField();
                    toReturn.setName(FieldName.create(RandomStringUtils.random(6, true, false)));
                    toReturn.setResultFeature(ResultFeature.PROBABILITY);
                    return toReturn;
                })
                .collect(Collectors.toList());
        // Following OutputField should be populated based on "ResultFeature.PREDICTED_VALUE"
        OutputField targetOutputField = new OutputField();
        targetOutputField.setName(FieldName.create(RandomStringUtils.random(6, true, false)));
        targetOutputField.setResultFeature(ResultFeature.PREDICTED_VALUE);
        outputFields.add(targetOutputField);
        // Following OutputField should be populated based on "TargetField" property
        OutputField targetingOutputField = new OutputField();
        targetingOutputField.setName(FieldName.create(RandomStringUtils.random(6, true, false)));
        targetingOutputField.setTargetField(FieldName.create(targetMiningField.getName().getValue()));
        outputFields.add(targetingOutputField);
        outputFields.forEach(outputField -> assertNull(outputField.getDataType()));
        IntStream.range(0, 2)
                .forEach(i -> {
                    OutputField toAdd = new OutputField();
                    toAdd.setName(FieldName.create(RandomStringUtils.random(6, true, false)));
                    DataType dataType = DataType.values()[random.nextInt(DataType.values().length)];
                    toAdd.setDataType(dataType);
                    outputFields.add(toAdd);
                });
        KiePMMLUtil.populateMissingOutputFieldDataType(outputFields, miningFields, dataFields);
        outputFields.forEach(outputField -> assertNotNull(outputField.getDataType()));
    }

    @Test
    public void getSanitizedId() {
        final String modelName = "MODEL_NAME";
        String id = "2";
        String expected = String.format(SEGMENTID_TEMPLATE, modelName, id);
        String retrieved = KiePMMLUtil.getSanitizedId(id, modelName);
        assertEquals(expected, retrieved);
        id = "34.5";
        expected = String.format(SEGMENTID_TEMPLATE, modelName, id);
        retrieved = KiePMMLUtil.getSanitizedId(id, modelName);
        assertEquals(expected, retrieved);
        id = "3,45";
        expected = String.format(SEGMENTID_TEMPLATE, modelName, id);
        retrieved = KiePMMLUtil.getSanitizedId(id, modelName);
        assertEquals(expected, retrieved);
    }

    private void commonLoadString(String fileName) throws IOException, JAXBException, SAXException {
        InputStream inputStream = getFileInputStream(fileName);

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                                                        (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
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
        assertNotNull(toValidate);
        for (Model model : toValidate.getModels()) {
            assertNotNull(model.getModelName());
            if (model instanceof MiningModel) {
                commonValidateMiningModel((MiningModel) model);
            }
        }
    }

    private void commonValidateMiningModel(MiningModel toValidate) {
        assertNotNull(toValidate);
        for (Segment segment : toValidate.getSegmentation().getSegments()) {
            assertNotNull(segment.getId());
            Model segmentModel = segment.getModel();
            assertNotNull(segmentModel.getModelName());
            if (segmentModel instanceof MiningModel) {
                commonValidateMiningModel((MiningModel) segmentModel);
            }
        }
    }
}