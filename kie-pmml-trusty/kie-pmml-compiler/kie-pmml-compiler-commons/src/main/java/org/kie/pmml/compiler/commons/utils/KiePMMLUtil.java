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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MathContext;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.Target;
import org.dmg.pmml.Targets;
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
    static final String TARGETFIELD_TEMPLATE = "target%s";

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
        List<DataField> dataFields = toReturn.getDataDictionary().getDataFields();
        List<Model> models =  toReturn.getModels();
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            populateMissingModelName(model, cleanedFileName, i);
            populateMissingOutputFieldDataType(model, dataFields);
            populateMissingMiningTargetField(model, dataFields);
            if (model instanceof MiningModel) {
                populateCorrectMiningModel((MiningModel) model);
            }
        }
        return toReturn;
    }

    /**
     * Method to add an autogenerated model name if the given <code>Model</code>
     * does not define one
     * @param model
     * @param fileName
     */
    static void populateMissingModelName(final Model model, final String fileName, int i) {
        if (model.getModelName() == null || model.getModelName().isEmpty()) {
            String modelName = String.format(MODELNAME_TEMPLATE,
                                             fileName,
                                             model.getClass().getSimpleName(),
                                             i);
            model.setModelName(modelName);
        }
    }

    /**
     * Method to populate <code>MiningSchema</code> with a n ad-hoc created target <code>MiningField</code>.
     * It also populate the given <code>List&lt;DataField&gt;</code> with the relative <code>DataField</code>.
     * This method has to be called <b>after</b> the model name has been set
     * @param model
     * @param dataFields
     */
    static void populateMissingMiningTargetField(final Model model, final  List<DataField> dataFields) {
        List<MiningField> miningTargetFields = getMiningTargetFields(model.getMiningSchema().getMiningFields());
        if (miningTargetFields.isEmpty()) {
            Optional<DataField> targetDataField = getTargetDataField(model);
            targetDataField.ifPresent(dataField -> {
                dataFields.add(dataField);
                MiningField targetMiningField = getTargetMiningField(dataField);
                model.getMiningSchema().addMiningFields(targetMiningField);
                correctTargetFields(targetMiningField, model.getTargets());
            });
         }
    }

    /**
     * Returns a model-specific <b>target</b> <code>DataField</code>
     * @param model
     * @return
     */
    static Optional<DataField> getTargetDataField(final Model model) {
        DataType targetDataType = getTargetDataType(model.getMiningFunction(), model.getMathContext());
        OpType targetOpType = getTargetOpType(model.getMiningFunction());
        if (targetDataType == null || targetOpType == null) {
            return  Optional.empty();
        }
        String cleanedName = model.getModelName().replaceAll("[^A-Za-z0-9]", "");
        String fieldName = String.format(TARGETFIELD_TEMPLATE, cleanedName);
        DataField toReturn = new DataField();
        toReturn.setName(FieldName.create(fieldName));
        toReturn.setOpType(targetOpType);
        toReturn.setDataType(targetDataType);
        return Optional.of(toReturn);
    }

    /**
     * Returns the <code>DataType</code> to be set in the target field
     * @param miningFunction
     * @param mathContext
     * @return
     */
    static DataType getTargetDataType(final MiningFunction miningFunction, final MathContext mathContext) {
        switch(miningFunction){
            case REGRESSION:
                return  DataType.fromValue(mathContext.value());
            case CLASSIFICATION:
            case CLUSTERING:
                return DataType.STRING;
            default:
                return null;
        }
    }

    /**
     * Returns the <code>DataType</code> to be set in the target field
     * @param miningFunction
     * @return
     */
    static OpType getTargetOpType(final MiningFunction miningFunction) {
        switch(miningFunction){
            case REGRESSION:
                return  OpType.CONTINUOUS;
            case CLASSIFICATION:
            case CLUSTERING:
                return OpType.CATEGORICAL;
            default:
                return null;
        }
    }

    /**
     * Returns a model-specific <b>target</b> <code>MiningField</code>
     * @param dataField
     * @return
     */
    static MiningField getTargetMiningField(final DataField dataField) {
        MiningField toReturn = new MiningField();
        toReturn.setName(dataField.getName());
        toReturn.setUsageType(MiningField.UsageType.TARGET);
        return toReturn;
    }

    /**
     * Add the newly generated <code>MiningField</code> name to anonymous <code>Target</code>s
     *
     * @param targetMiningField
     * @param targets
     */
    static void correctTargetFields(MiningField targetMiningField, Targets targets) {
        if (targets != null && !targets.getTargets().isEmpty()) {
            final List<Target> targetsFields = targets.getTargets();
            targetsFields.stream()
                    .filter(targetField -> targetField.getField() == null)
                    .forEach(targetField -> targetField.setField(targetMiningField.getName()));
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
            populateCorrectSegmentId(segment,  miningModel.getModelName(), i);
            Model model = segment.getModel();
            populateMissingSegmentModelName(model, segment.getId());
            populateMissingTargetFieldInSegment(miningModel.getMiningSchema(), model);
            if (model instanceof MiningModel) {
                populateCorrectMiningModel((MiningModel) segment.getModel());
            }
        }
    }

    static void populateCorrectSegmentId(final Segment segment, final String modelName, final int i) {
        String toSet;
        if (segment.getId() == null || segment.getId().isEmpty()) {
            toSet = String.format(SEGMENTID_TEMPLATE,
                                  modelName,
                                  i);
        } else {
            toSet = getSanitizedId(segment.getId(), modelName);
        }
        segment.setId(toSet);
    }

    /**
     * Method to add an autogenerated model name if the given <b>Segment</b> <code>Model</code>
     * does not define one
     * @param model
     * @param segmentId
     */
    static void populateMissingSegmentModelName(final Model model, final String segmentId) {
        if (model.getModelName() == null || model.getModelName().isEmpty()) {
            String modelName = String.format(SEGMENTMODELNAME_TEMPLATE,
                                             segmentId,
                                             model.getClass().getSimpleName());
            model.setModelName(modelName);
        }
    }

    /**
     * Method to add a <b>target</b> field to the <code>MiningSchema</code> of a segment model if the parent <code>MiningModel</code>
     * defines one: it copies the target field from the parent' <code>MiningSchema</code> to the children' one
     *
     * @param parentMiningSchema
     * @param childrenModel
     */
    static void populateMissingTargetFieldInSegment(final MiningSchema parentMiningSchema, final Model childrenModel) {
        List<MiningField> parentTargetFields = getMiningTargetFields(parentMiningSchema.getMiningFields());
        List<MiningField> childrenTargetFields = getMiningTargetFields(childrenModel.getMiningSchema().getMiningFields());
        if (childrenTargetFields.isEmpty()) {
            childrenModel.getMiningSchema().addMiningFields(parentTargetFields.toArray(new MiningField[parentTargetFields.size()]));
        }
    }

    /**
     * Method to populate the <b>dataType</b> property of <code>OutputField</code>s.
     * Such property was optional until 4.4.1 spec
     * @param model
     * @param dataFields
     */
    static void populateMissingOutputFieldDataType(final Model model, final List<DataField> dataFields) {
        if (model.getOutput() != null &&
                model.getOutput().getOutputFields() != null) {
            populateMissingOutputFieldDataType(model.getOutput().getOutputFields(),
                                               model.getMiningSchema().getMiningFields(),
                                               dataFields);
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
        List<MiningField> targetFields = getMiningTargetFields(miningFields);
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

    static List<MiningField> getMiningTargetFields(final MiningSchema miningSchema ) {
        return getMiningTargetFields(miningSchema.getMiningFields());
    }

    static List<MiningField> getMiningTargetFields(final List<MiningField> miningFields ) {
        return miningFields.stream()
                .filter(miningField -> MiningField.UsageType.PREDICTED.equals(miningField.getUsageType()) ||
                        MiningField.UsageType.TARGET.equals(miningField.getUsageType()))
                .collect(Collectors.toList());
    }
}
