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
package org.kie.pmml.runtime.mining.executor;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.drools.core.util.StringUtils;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.mining.KiePMMLMiningModel;
import org.kie.pmml.api.model.mining.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.api.model.mining.segmentation.KiePMMLSegment;
import org.kie.pmml.api.model.tree.predicates.KiePMMLPredicate;
import org.kie.pmml.api.model.tuples.KiePMMLNameValue;
import org.kie.pmml.api.model.tuples.KiePMMLValueWeight;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.api.executor.PMMLRuntime;
import org.kie.pmml.runtime.core.executor.PMMLModelExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.runtime.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Mining</b>
 */
public class PMMLMiningModelExecutor implements PMMLModelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(PMMLMiningModelExecutor.class.getName());

    private PMMLRuntime pmmlRuntime;

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.MINING_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext context) throws KiePMMLException {
        logger.info("evaluate {}, {}", model, context);
        if (!(model instanceof KiePMMLMiningModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLMiningModel, received a " + model.getClass().getName());
        }
        return evaluateMiningModel((KiePMMLMiningModel) model, context);
    }

    private PMML4Result evaluateMiningModel(KiePMMLMiningModel toEvaluate, PMMLContext pmmlContext) throws KiePMMLException {
        final MULTIPLE_MODEL_METHOD multipleModelMethod = toEvaluate.getSegmentation().getMultipleModelMethod();
        final List<KiePMMLSegment> segments = toEvaluate.getSegmentation().getSegments();
        final LinkedHashMap<String, KiePMMLNameValue> inputData = segments.stream()
                .map(throwingFunctionWrapper(segment ->
                                                     new AbstractMap.SimpleImmutableEntry<KiePMMLSegment, Optional<PMML4Result>>(segment, evaluateSegment(segment, pmmlContext))))
                .filter(entry -> entry.getValue().isPresent())
                .map(throwingFunctionWrapper(entry -> new AbstractMap.SimpleImmutableEntry<KiePMMLSegment, KiePMMLNameValue>(entry.getKey(),
                                                                                                                   getKiePMMLNameValue(entry.getValue().get(), multipleModelMethod, entry.getKey().getWeight()))))
                .collect(Collectors.toMap(entry ->   entry.getKey().getId(),
                                          AbstractMap.SimpleImmutableEntry::getValue,
                                          (o1, o2) -> o1,
                                          (Supplier<LinkedHashMap<String, KiePMMLNameValue>>) LinkedHashMap::new));
        final Object prediction = multipleModelMethod.apply(inputData);
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(toEvaluate.getTargetField(), prediction);
        toReturn.setResultObjectName(toEvaluate.getTargetField());
        return toReturn;
    }

    private Optional<PMML4Result> evaluateSegment(KiePMMLSegment toEvaluate, PMMLContext pmmlContext) throws KiePMMLException {
        logger.info("evaluateSegment {}", toEvaluate.getId());
        final KiePMMLPredicate kiePMMLPredicate = toEvaluate.getKiePMMLPredicate();
        Optional<PMML4Result> toReturn = Optional.empty();
        Map<String, Object> values = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        if (kiePMMLPredicate != null && kiePMMLPredicate.evaluate(values)) {
            logger.info("{}: matching predicate, evaluating... ", toEvaluate.getId());
            toReturn = Optional.of(pmmlRuntime.evaluate(toEvaluate.getModel(), pmmlContext));
        }
        return toReturn;
    }

    /**
     * Returns a <code>KiePMMLNameValue</code> representation of the <code>PMML4Result</code>.
     * <b>It is based on the assumption there is only one result to be considered, defined as</b>
     * {@link PMML4Result#getResultObjectName() }
     *
     * @param result
     * @param multipleModelMethod
     * @param weight
     * @return
     * @throws KiePMMLException
     */
    private KiePMMLNameValue getKiePMMLNameValue(PMML4Result result, MULTIPLE_MODEL_METHOD multipleModelMethod, double weight) throws KiePMMLException {
        String fieldName = result.getResultObjectName();
        Object retrieved = getEventuallyWeightedResult(result.getResultVariables().get(fieldName), multipleModelMethod, weight);
        return new KiePMMLNameValue(fieldName, retrieved);
    }

    /**
     * Returns a <code>KiePMMLValueWeight</code> if the given <code>MULTIPLE_MODEL_METHOD</code> expect it;
     * the original <b>rawObject</b>, otherwise
     * @param rawObject
     * @param multipleModelMethod
     * @param weight
     * @return
     * @throws KiePMMLException
     */
    private Object getEventuallyWeightedResult(Object rawObject, MULTIPLE_MODEL_METHOD multipleModelMethod, double weight) throws KiePMMLException {
        switch (multipleModelMethod) {
            case MAX:
            case SUM:
            case MEDIAN:
            case AVERAGE:
            case SELECT_ALL:
            case MODEL_CHAIN:
            case SELECT_FIRST:
            case MAJORITY_VOTE:
                return rawObject;
            case WEIGHTED_SUM:
            case WEIGHTED_MEDIAN:
            case WEIGHTED_AVERAGE:
                if (!(rawObject instanceof Number)) {
                    throw new KiePMMLException("Expected a number, retrieved " + rawObject.getClass().getName());
                }
                return new KiePMMLValueWeight(((Number) rawObject).doubleValue(), weight);
            case WEIGHTED_MAJORITY_VOTE:
                throw new KiePMMLException("WEIGHTED_MAJORITY_VOTE not implemented, yet");
            default:
                throw new KiePMMLException("Unrecognized MULTIPLE_MODEL_METHOD " + multipleModelMethod);
        }
    }

}
