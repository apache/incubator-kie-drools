package org.kie.pmml.evaluator.core.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.ResultCode.OK;

/**
 * Class meant to provide static methods related to <b>post-process</b> manipulation
 */
public class PostProcess {

    private static final Logger logger = LoggerFactory.getLogger(PostProcess.class);

    private PostProcess() {
        // Avoid instantiation
    }

    public static void postProcess(final PMML4Result toReturn, final KiePMMLModel model,
                                   final PMMLRuntimeContext pmmlContext, final ProcessingDTO processingDTO) {
        executeTargets(toReturn, processingDTO);
        updateTargetValueType(model, toReturn);
        populateProcessingDTO(toReturn, pmmlContext, processingDTO);
        populateOutputFields(toReturn, processingDTO);
    }

    /**
     * Method used to populate a <code>ProcessingDTO</code> with values accumulated inside the given
     * <code>KiePMMLModel</code>
     * during evaluation
     *
     * @param pmml4Result
     * @param pmmlContext
     * @param toPopulate
     */
    static void populateProcessingDTO(final PMML4Result pmml4Result,
                                      final PMMLRuntimeContext pmmlContext,
                                      final ProcessingDTO toPopulate) {
        pmml4Result.getResultVariables().forEach((key, value) -> toPopulate.addKiePMMLNameValue(new KiePMMLNameValue(key, value)));
        final Map<String, Double> sortedByValue
                = pmmlContext.getOutputFieldsMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Double && (Double) entry.getValue() > 0)
                .map((Function<Map.Entry<String, Object>, Map.Entry<String, Double>>) entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), (Double) entry.getValue()))
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                          LinkedHashMap::new));
        final List<String> orderedReasonCodes = new ArrayList<>(sortedByValue.keySet());
        toPopulate.addOrderedReasonCodes(orderedReasonCodes);
        toPopulate.setAffinity(pmmlContext.getAffinity());
        toPopulate.setEntityId(pmmlContext.getEntityId());
        toPopulate.setPredictedDisplayValue(pmmlContext.getPredictedDisplayValue());
        toPopulate.setProbabilityMap(pmmlContext.getProbabilityMap());
    }

    /**
     * Execute <b>modifications</b> on target result.
     *
     * @param toModify
     * @param processingDTO
     * @see <a href="http://dmg.org/pmml/v4-4-1/Targets.html>Targets</a>
     */
    static void executeTargets(final PMML4Result toModify, final ProcessingDTO processingDTO) {
        logger.debug("executeTargets {} {}", toModify, processingDTO);
        if (!toModify.getResultCode().equals(OK.getName())) {
            return;
        }
        final String targetFieldName = toModify.getResultObjectName();
        final Map<String, Object> resultVariables = toModify.getResultVariables();
        processingDTO.getKiePMMLTargets()
                .stream()
                .filter(kiePMMLTarget -> kiePMMLTarget.getField() != null && kiePMMLTarget.getField().equals(targetFieldName))
                .findFirst()
                .ifPresent(kiePMMLTarget -> {
                    Object prediction = resultVariables.get(targetFieldName);
                    logger.debug("Original prediction {}", prediction);
                    Object modifiedPrediction = kiePMMLTarget.modifyPrediction(resultVariables.get(targetFieldName));
                    logger.debug("Modified prediction {}", modifiedPrediction);
                    resultVariables.put(targetFieldName, modifiedPrediction);
                });
    }

    /**
     * Verify that the returned value has the required type as defined inside <code>DataDictionary/MiningSchema</code>
     *
     * @param model
     * @param toUpdate
     */
    static void updateTargetValueType(final KiePMMLModel model, final PMML4Result toUpdate) {
        DATA_TYPE dataType = model.getMiningFields().stream()
                .filter(miningField -> model.getTargetField().equals(miningField.getName()))
                .map(MiningField::getDataType)
                .findFirst()
                .orElseThrow(() -> new KiePMMLException("Failed to find DATA_TYPE for " + model.getTargetField()));
        Object prediction = toUpdate.getResultVariables().get(model.getTargetField());
        if (prediction != null) {
            Object convertedPrediction = dataType.getActualValue(prediction);
            toUpdate.getResultVariables().put(model.getTargetField(), convertedPrediction);
        }
    }

    /**
     * Populated the <code>PMML4Result</code> with <code>OutputField</code> results
     *
     * @param toUpdate
     * @param processingDTO
     */
    static void populateOutputFields(final PMML4Result toUpdate,
                                     final ProcessingDTO processingDTO) {
        logger.debug("populateOutputFields {} {}", toUpdate, processingDTO);
        for (KiePMMLOutputField outputField : processingDTO.getOutputFields()) {
            Object variableValue = outputField.evaluate(processingDTO);
            if (variableValue != null) {
                String variableName = outputField.getName();
                toUpdate.addResultVariable(variableName, variableValue);
                processingDTO.addKiePMMLNameValue(new KiePMMLNameValue(variableName, variableValue));
            }
        }
    }

}
