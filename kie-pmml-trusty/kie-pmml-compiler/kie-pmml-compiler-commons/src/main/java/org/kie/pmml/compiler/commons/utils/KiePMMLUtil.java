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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
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
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.xml.sax.SAXException;

/**
 * Utility class to decouple <code>PMMLCompilerExecutor</code> from actual marshalling model/implementation.
 * Currently, it directly uses {@link org.jpmml.model.PMMLUtil} and {@link org.dmg.pmml.PMML}
 */
public class KiePMMLUtil {

    public static final String SEGMENTID_TEMPLATE = "%sSegment%s";
    static final String MODELNAME_TEMPLATE = "%s%s%s";
    static final String SEGMENTMODELNAME_TEMPLATE = "Segment%s%s";

    private KiePMMLUtil() {
        // Avoid instantiation
    }

    /**
     * @param source
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public static PMML load(String source) throws SAXException, JAXBException {
        return load(new ByteArrayInputStream(source.getBytes()), "");
    }

    /**
     * @param is
     * @return
     * @throws SAXException
     * @throws JAXBException
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    public static PMML load(final InputStream is, final String fileName) throws SAXException, JAXBException {
        PMML toReturn = org.jpmml.model.PMMLUtil.unmarshal(is);
        String cleanedFileName = fileName.contains(".") ? fileName.substring(0, fileName.indexOf('.')) : fileName;
        populateMissingNames(toReturn, cleanedFileName);
        List<DataField> dataFields = toReturn.getDataDictionary().getDataFields();
        for (Model model : toReturn.getModels()) {
            if (model.getOutput() != null &&
                    model.getOutput().getOutputFields() != null) {
                populateMissingOutputFieldDataType(model.getOutput().getOutputFields(),
                                                   model.getMiningSchema().getMiningFields(),
                                                   dataFields);
            }
        }
        return toReturn;
    }

    /**
     * Method to provide default generated <b>modelName</b> attributes for models
     * without them.
     * Generated name would be
     * <p>
     * fileName + model type + index (inside models list)
     * @param toPopulate
     * @param fileName
     */
    static void populateMissingNames(final PMML toPopulate, final String fileName) {
        final List<Model> models = toPopulate.getModels();
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            if (model.getModelName() == null || model.getModelName().isEmpty()) {
                String modelName = String.format(MODELNAME_TEMPLATE,
                                                 fileName,
                                                 model.getClass().getSimpleName(),
                                                 i);
                model.setModelName(modelName);
            }
            if (model instanceof MiningModel) {
                populateCorrectMiningModel((MiningModel) model);
            }
        }
    }

    /**
     * Recursively populate or correct <code>Segment</code>s with auto generated id,
     * if missing in original model, and auto generated model name, if missing in original model
     * @param miningModel
     */
    static void populateCorrectMiningModel(final MiningModel miningModel) {
        final List<Segment> segments = miningModel.getSegmentation().getSegments();
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            String toSet = null;
            if (segment.getId() == null || segment.getId().isEmpty()) {
                toSet = String.format(SEGMENTID_TEMPLATE,
                                      miningModel.getModelName(),
                                      i);
            } else {
                toSet = getSanitizedId(segment.getId(), miningModel.getModelName());
            }
            segment.setId(toSet);
            Model model = segment.getModel();
            if (model.getModelName() == null || model.getModelName().isEmpty()) {
                String modelName = String.format(SEGMENTMODELNAME_TEMPLATE,
                                                 segment.getId(),
                                                 model.getClass().getSimpleName());
                model.setModelName(modelName);
            }
            if (segment.getModel() instanceof MiningModel) {
                populateCorrectMiningModel((MiningModel) segment.getModel());
            }
        }
    }

    /**
     * Method to populate the <b>dataType</b> property of <code>OutputField</code>s.
     * Such property was optional until 4.4.1 spec
     * @param toPopulate
     * @param miningFields
     * @param dataFields
     */
    static void populateMissingOutputFieldDataType(List<OutputField> toPopulate, List<MiningField> miningFields,
                                                   List<DataField> dataFields) {
        // partial implementation to fix missing "dataType" inside OutputField; "dataType" became mandatory only in 4.4.1 version
        List<MiningField> targetFields = miningFields.stream()
                .filter(miningField -> MiningField.UsageType.PREDICTED.equals(miningField.getUsageType()) ||
                        MiningField.UsageType.TARGET.equals(miningField.getUsageType()))
                .collect(Collectors.toList());
        toPopulate.stream()
                .filter(outputField -> outputField.getDataType() == null)
                .forEach(outputField -> {
                    MiningField referencedField = null;
                    if (outputField.getTargetField() != null) {
                        referencedField = targetFields.stream()
                                .filter(targetField -> outputField.getTargetField().equals(targetField.getName()))
                                .findFirst()
                                .orElseThrow(() -> new KiePMMLException("Failed to find a target field for OutputField "
                                                                                + outputField.getName().getValue()));
                    }
                    if (referencedField == null && (outputField.getResultFeature() == null || outputField.getResultFeature().equals(ResultFeature.PREDICTED_VALUE))) { // default predictedValue
                        referencedField = targetFields.stream()
                                .findFirst() // To be fixed with DROOLS-5992: there could be more then one "target" field
                                .orElse(null); // It is allowed to not have any "target" field inside MiningSchema
                    }
                    if (referencedField == null && ResultFeature.PROBABILITY.equals(outputField.getResultFeature())) {
                        outputField.setDataType(DataType.DOUBLE); // we set the "dataType" to "double" because outputField is a "probability", we may return
                        return;
                    }
                    if (referencedField != null) {
                        FieldName targetFieldName = referencedField.getName();
                        DataField dataField = dataFields.stream()
                                .filter(df -> df.getName().equals(targetFieldName))
                                .findFirst()
                                .orElseThrow(() -> new KiePMMLException("Failed to find a DataField field for " +
                                                                                "MiningField " + targetFieldName.toString()));
                        outputField.setDataType(dataField.getDataType());
                    }
                });
    }

    static String getSanitizedId(String id, String modelName) {
        String toReturn = id.replace(".", "")
                .replace(",", "");
        try {
            Integer.parseInt(toReturn);
            toReturn = String.format(SEGMENTID_TEMPLATE, modelName, id);
        } catch (NumberFormatException e) {
            // ignore
        }
        return toReturn;
    }
}
