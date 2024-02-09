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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInputDataException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class meant to provide static methods related to <b>pre-process</b> manipulation
 */
public class PreProcess {

    private static final Logger logger = LoggerFactory.getLogger(PreProcess.class);

    private PreProcess() {
        // Avoid instantiation
    }

    /**
     * Method to create a <code>ProcessingDTO</code> with <b>fix</b> values from the given <code>KiePMMLModel</code>
     *
     * @param model
     * @param context
     * @return
     */
    public static ProcessingDTO preProcess(final KiePMMLModel model, final PMMLRuntimeContext context) {
        final List<KiePMMLMiningField> notTargetMiningFields = model.getMiningFields() != null ?
                model.getKiePMMLMiningFields().stream().filter(miningField -> !miningField.isTarget())
                        .collect(Collectors.toList())
                : Collections.emptyList();
        final PMMLRequestData requestData = context.getRequestData();
        convertInputData(notTargetMiningFields, requestData);
        verifyFixInvalidValues(notTargetMiningFields, requestData);
        verifyAddMissingValues(notTargetMiningFields, requestData);
        final ProcessingDTO toReturn = createProcessingDTO(model, requestData.getMappedRequestParams());
        executeTransformations(toReturn, requestData);
        return toReturn;
    }

    /**
     * Try to convert input data to expected data-type, throwing exception when data are not
     * convertible
     *
     * @param notTargetMiningFields
     * @param requestData
     */
    static void convertInputData(final List<KiePMMLMiningField> notTargetMiningFields,
                                 final PMMLRequestData requestData) {
        logger.debug("convertInputData {} {}", notTargetMiningFields, requestData);
        Collection<ParameterInfo> requestParams = requestData.getRequestParams();
        notTargetMiningFields.forEach(miningField -> {
            ParameterInfo parameterInfo = requestParams.stream()
                    .filter(paramInfo -> miningField.getName().equals(paramInfo.getName()))
                    .findFirst()
                    .orElse(null);
            if (parameterInfo != null) {
                Object originalValue = parameterInfo.getValue();
                Object requiredValue = miningField.getDataType().getActualValue(originalValue);
                parameterInfo.setType(miningField.getDataType().getMappedClass());
                parameterInfo.setValue(requiredValue);
            }
        });
    }

    /**
     * Verify the invalid values if defined in original PMML as <b>invalidValueTreatment</b>,
     * eventually <b>removing</b> or <b>replacing</b> them, depending on the <b>invalidValueTreatment</b>.
     * <p>
     * invalidValueTreatment: This field specifies how invalid input values are handled.
     * returnInvalid is the default and specifies that, when an invalid input is encountered,
     * the model should return a value indicating an invalid result has been returned.
     * asIs means to use the input without modification. asMissing specifies that an invalid
     * input value should be treated as a missing value and follow the behavior specified by
     * the missingValueReplacement attribute if present (see above). If asMissing is specified
     * but there is no respective missingValueReplacement present, a missing value is passed on
     * for eventual handling by successive transformations via DerivedFields or in the actual
     * mining model. asValue specifies that an invalid input value should be replaced with the
     * value specified by attribute invalidValueReplacement which must be present in this case,
     * or the PMML is invalid.
     * </p>
     *
     * @param notTargetMiningFields
     * @param requestData
     * @see
     * <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_INVALID-VALUE-TREATMENT-METHOD">INVALID-VALUE-TREATMENT-METHOD</a>
     */
    static void verifyFixInvalidValues(final List<KiePMMLMiningField> notTargetMiningFields,
                                       final PMMLRequestData requestData) {
        logger.debug("verifyInvalidValues {} {}", notTargetMiningFields, requestData);
        final Collection<ParameterInfo> requestParams = requestData.getRequestParams();
        final List<ParameterInfo> toRemove = new ArrayList<>();
        notTargetMiningFields.forEach(miningField -> {
            ParameterInfo parameterInfo = requestParams.stream()
                    .filter(paramInfo -> miningField.getName().equals(paramInfo.getName()))
                    .findFirst()
                    .orElse(null);
            if (parameterInfo != null) {
                boolean match = isMatching(parameterInfo, miningField);
                if (!match) {
                    manageInvalidValues(miningField, parameterInfo, toRemove);
                }
                toRemove.forEach(requestData::removeRequestParam);
            }
        });
    }

    /**
     * Verify the missing values if defined in original PMML as <b>missingValueTreatment</b>,
     * <b>eventually adding default ones</b>.
     * <p>
     * missingValueTreatment: In a PMML consumer this field is for information only,
     * unless the value is returnInvalid, in which case if a missing value is encountered
     * in the given field, the model should return a value indicating an invalid result;
     * </p>
     *
     * @param notTargetMiningFields
     * @param requestData
     * @see
     * <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_MISSING-VALUE-TREATMENT-METHOD">MISSING-VALUE-TREATMENT-METHOD</a>
     */
    static void verifyAddMissingValues(final List<KiePMMLMiningField> notTargetMiningFields,
                                       final PMMLRequestData requestData) {
        logger.debug("verifyMissingValues {} {}", notTargetMiningFields, requestData);
        Collection<ParameterInfo> requestParams = requestData.getRequestParams();
        notTargetMiningFields
                .forEach(miningField -> {
                    ParameterInfo parameterInfo = requestParams.stream()
                            .filter(paramInfo -> miningField.getName().equals(paramInfo.getName()))
                            .findFirst()
                            .orElse(null);
                    if (parameterInfo == null) {
                        manageMissingValues(miningField, requestData);
                    }
                });
    }

    static ProcessingDTO createProcessingDTO(final KiePMMLModel model,
                                             final Map<String, ParameterInfo> mappedRequestParams) {
        final List<KiePMMLNameValue> kiePMMLNameValues =
                getKiePMMLNameValuesFromParameterInfos(mappedRequestParams.values());
        return new ProcessingDTO(model, kiePMMLNameValues);
    }

    /**
     * Execute <b>transformations</b> on input data.
     * @param processingDTO
     * @param requestData
     * @see <a href="http://dmg.org/pmml/v4-4/Transformations.html">Transformations</a>
     * @see
     * <a href="http://dmg.org/pmml/v4-4/Transformations.html#xsdElement_LocalTransformations">LocalTransformations</a>
     */
    static void executeTransformations(final ProcessingDTO processingDTO,
                                       final PMMLRequestData requestData) {
        logger.debug("executeTransformations {} {}", processingDTO, requestData);
        for (KiePMMLDerivedField derivedField : processingDTO.getDerivedFields()) {
            Object derivedValue = derivedField.evaluate(processingDTO);
            if (derivedValue != null) {
                requestData.addRequestParam(derivedField.getName(), derivedValue);
                processingDTO.addKiePMMLNameValue(new KiePMMLNameValue(derivedField.getName(), derivedValue));
            }
        }
    }

    static List<KiePMMLNameValue> getKiePMMLNameValuesFromParameterInfos(
            final Collection<ParameterInfo> parameterInfos) {
        return parameterInfos.stream()
                .map(parameterInfo -> new KiePMMLNameValue(parameterInfo.getName(), parameterInfo.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Manage the <b>invalid value</b> of the given <code>ParameterInfo</code> depending on the
     * <code>INVALID_VALUE_TREATMENT_METHOD</code>
     * of the given <code>MiningField</code>, <b>eventually adding the ParameterInfo to the list of the ones to be
     * removed from input data</b>
     *
     * @param miningField
     * @param parameterInfo
     * @param toRemove
     */
    static void manageInvalidValues(final KiePMMLMiningField miningField, final ParameterInfo parameterInfo,
                                    final List<ParameterInfo> toRemove) {
        INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod =
                miningField.getInvalidValueTreatmentMethod() != null ?
                        miningField.getInvalidValueTreatmentMethod()
                        : INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID;
        Object originalValue = parameterInfo.getValue();
        switch (invalidValueTreatmentMethod) {
            case RETURN_INVALID:
                throw new KiePMMLInputDataException("Invalid value " + originalValue + " for " + miningField.getName());
            case AS_MISSING:
                toRemove.add(parameterInfo);
                break;
            case AS_IS:
                break;
            case AS_VALUE:
                String invalidValueReplacement = miningField.getInvalidValueReplacement();
                if (invalidValueReplacement == null) {
                    throw new KiePMMLInputDataException("Missing required invalidValueReplacement for " + miningField.getName());
                } else {
                    Object requiredValue =
                            miningField.getDataType().getActualValue(invalidValueReplacement);
                    parameterInfo.setType(miningField.getDataType().getMappedClass());
                    parameterInfo.setValue(requiredValue);
                }
                break;
            default:
                throw new KiePMMLException("Unmanaged INVALID_VALUE_TREATMENT_METHOD " + invalidValueTreatmentMethod);
        }
    }

    /**
     * Manage the <b>missing value</b> depending on the <code>INVALID_VALUE_TREATMENT_METHOD</code>
     * of the given <code>MiningField</code>, <b>eventually adding default ont to input data</b>
     *
     * @param miningField
     * @param requestData
     */
    static void manageMissingValues(final KiePMMLMiningField miningField, final PMMLRequestData requestData) {
        MISSING_VALUE_TREATMENT_METHOD missingValueTreatmentMethod =
                miningField.getMissingValueTreatmentMethod() != null ?
                        miningField.getMissingValueTreatmentMethod()
                        : MISSING_VALUE_TREATMENT_METHOD.RETURN_INVALID;
        switch (missingValueTreatmentMethod) {
            case RETURN_INVALID:
                throw new KiePMMLInputDataException("Missing required value for " + miningField.getName());
            case AS_IS:
            case AS_MEAN:
            case AS_MODE:
            case AS_MEDIAN:
            case AS_VALUE:
                String missingValueReplacement = miningField.getMissingValueReplacement();
                if (missingValueReplacement != null) {
                    Object requiredValue =
                            miningField.getDataType().getActualValue(missingValueReplacement);
                    requestData.addRequestParam(miningField.getName(), requiredValue);
                }
                break;
            default:
                throw new KiePMMLException("Unmanaged INVALID_VALUE_TREATMENT_METHOD " + missingValueTreatmentMethod);
        }
    }

    /**
     * Verify if the value of the given <code>ParameterInfo</code> is allowed for the given <code>MiningField</code>
     *
     * @param parameterInfo
     * @param miningField
     * @return
     */
    static boolean isMatching(final ParameterInfo parameterInfo, final KiePMMLMiningField miningField) {
        Object originalValue = parameterInfo.getValue();
        return miningField.isMatching(originalValue);
    }
}
