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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLContext;
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
     * @param model
     * @param context
     * @return
     */
    public static ProcessingDTO preProcess(final KiePMMLModel model, final PMMLContext context) {
        verifyMissingValues(model, context);
        convertInputData(model, context);
        verifyInvalidValues(model, context);
        addMissingValuesReplacements(model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final ProcessingDTO toReturn = createProcessingDTO(model, requestData.getMappedRequestParams());
        executeTransformations(toReturn, requestData);
        return toReturn;
    }

    static ProcessingDTO createProcessingDTO(final KiePMMLModel model,
                                             final Map<String, ParameterInfo> mappedRequestParams) {
        final List<KiePMMLNameValue> kiePMMLNameValues =
                getKiePMMLNameValuesFromParameterInfos(mappedRequestParams.values());
        return new ProcessingDTO(model, kiePMMLNameValues);
    }

    /**
     * Verify the missing values if defined in original PMML as <b>missingValueTreatment</b>.
     * <p>
     * missingValueTreatment: In a PMML consumer this field is for information only,
     * unless the value is returnInvalid, in which case if a missing value is encountered
     * in the given field, the model should return a value indicating an invalid result;
     * </p>
     * @param model
     * @param context
     * @see
     * <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_MISSING-VALUE-TREATMENT-METHOD">MISSING-VALUE-TREATMENT-METHOD</a>
     */
    static void verifyMissingValues(final KiePMMLModel model, final PMMLContext context) {
        logger.debug("verifyMissingValues {} {}", model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final Map<String, ParameterInfo> mappedRequestParams = requestData.getMappedRequestParams();
        final List<String> requiredFieldsList = model.getRequiredFieldsList();
        final List<String> missingFields = requiredFieldsList.stream()
                .filter(fieldName -> !mappedRequestParams.containsKey(fieldName))
                .collect(Collectors.toList());
        if (!missingFields.isEmpty()) {
            String error = String.format("Missing required field(s): %s", String.join(", ", missingFields));
            logger.error(error);
            throw new KiePMMLException(error);
        }
    }

    /**
     * Try to convert input data to expected data-type, throwing exception when data are not convertible
     * @param model
     * @param context
     */
    static void convertInputData(final KiePMMLModel model, final PMMLContext context) {
        logger.debug("convertInputData {} {}", model, context);
        final PMMLRequestData requestData = context.getRequestData();
        Collection<ParameterInfo> requestParams = requestData.getRequestParams();
        model.getMiningFields().forEach(miningField -> {
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
     * @param model
     * @param context
     * @see
     * <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_INVALID-VALUE-TREATMENT-METHOD">INVALID-VALUE-TREATMENT-METHOD</a>
     */
    static void verifyInvalidValues(final KiePMMLModel model, final PMMLContext context) {
        logger.debug("verifyInvalidValues {} {}", model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final Collection<ParameterInfo> requestParams = requestData.getRequestParams();
        final List<ParameterInfo> toRemove = new ArrayList<>();
        model.getMiningFields().forEach(miningField -> {
            ParameterInfo parameterInfo = requestParams.stream()
                    .filter(paramInfo -> miningField.getName().equals(paramInfo.getName()))
                    .findFirst()
                    .orElse(null);
            if (parameterInfo != null) {
                boolean match = true;
                Object originalValue = parameterInfo.getValue();
                if (miningField.getAllowedValues() != null) {
                    String originalValueString = originalValue.toString();
                    match = miningField.getAllowedValues().stream()
                            .anyMatch(originalValueString::equals);
                } else if (miningField.getIntervals() != null) {
                    double originalValueNumber = ((Number) originalValue).doubleValue();
                    match = miningField.getIntervals().stream()
                            .anyMatch(interval -> originalValueNumber >= interval.getLeftMargin().doubleValue() &&
                                    originalValueNumber <= interval.getRightMargin().doubleValue());
                }
                if (!match) {
                    INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod = miningField.getInvalidValueTreatmentMethod() != null ? miningField.getInvalidValueTreatmentMethod()
                            : INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID;
                    switch (invalidValueTreatmentMethod) {
                        case RETURN_INVALID:
                            throw new KiePMMLException("Invalid value " + originalValue + " for " + miningField.getName());
                        case AS_MISSING:
                            toRemove.add(parameterInfo);
                            break;
                        case AS_VALUE:
                            String invalidValueReplacement = miningField.getInvalidValueReplacement();
                            if (invalidValueReplacement == null) {
                                throw new KiePMMLException("Missing required invalidValueReplacement for " + miningField.getName());
                            } else {
                                Object requiredValue = miningField.getDataType().getActualValue(invalidValueReplacement);
                                parameterInfo.setType(miningField.getDataType().getMappedClass());
                                parameterInfo.setValue(requiredValue);
                            }
                            break;
                        case AS_IS:
                            break;
                        default:
                            throw new KiePMMLException("Unmanaged INVALID_VALUE_TREATMENT_METHOD " + invalidValueTreatmentMethod);
                    }
                }
                toRemove.forEach(requestData::removeRequestParam);
            }
        });
    }

    /**
     * Add missing input values if defined in original PMML as <b>missingValueReplacement</b>.
     * <p>
     * "missingValueReplacement: If this attribute is specified then a missing input value is automatically replaced
     * by the given value.
     * That is, the model itself works as if the given value was found in the original input. "
     * @param model
     * @param context
     * @see
     * <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_MISSING-VALUE-TREATMENT-METHOD">MISSING-VALUE-TREATMENT-METHOD</a>
     */
    static void addMissingValuesReplacements(final KiePMMLModel model, final PMMLContext context) {
        logger.debug("addMissingValuesReplacements {} {}", model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final Map<String, ParameterInfo> mappedRequestParams = requestData.getMappedRequestParams();
        final Map<String, Object> missingValueReplacementMap = model.getMissingValueReplacementMap();
        missingValueReplacementMap.forEach((fieldName, missingValueReplacement) -> {
            if (!mappedRequestParams.containsKey(fieldName)) {
                logger.debug("missingValueReplacement {} {}", fieldName, missingValueReplacement);
                requestData.addRequestParam(fieldName, missingValueReplacement);
                context.addMissingValueReplaced(fieldName, missingValueReplacement);
            }
        });
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

    static List<KiePMMLNameValue> getKiePMMLNameValuesFromParameterInfos(final Collection<ParameterInfo> parameterInfos) {
        return parameterInfos.stream()
                .map(parameterInfo -> new KiePMMLNameValue(parameterInfo.getName(), parameterInfo.getValue()))
                .collect(Collectors.toList());
    }
}
