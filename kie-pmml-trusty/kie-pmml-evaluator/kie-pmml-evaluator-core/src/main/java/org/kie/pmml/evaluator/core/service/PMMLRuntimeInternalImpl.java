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
package org.kie.pmml.evaluator.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;
import org.kie.pmml.evaluator.core.utils.KnowledgeBaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PMMLRuntimeInternalImpl implements PMMLRuntimeInternal {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeInternalImpl.class);

    private final KieBase knowledgeBase;
    private final PMMLModelEvaluatorFinderImpl pmmlModelExecutorFinder;

    public PMMLRuntimeInternalImpl(final KieBase knowledgeBase, final PMMLModelEvaluatorFinderImpl pmmlModelExecutorFinder) {
        this.knowledgeBase = knowledgeBase;
        this.pmmlModelExecutorFinder = pmmlModelExecutorFinder;
    }

    @Override
    public KieBase getKnowledgeBase() {
        return knowledgeBase;
    }

    @Override
    public List<KiePMMLModel> getKiePMMLModels() {
        return KnowledgeBaseUtils.getModels(knowledgeBase);
    }

    @Override
    public List<PMMLModel> getPMMLModels() {
        List<KiePMMLModel> kiePMMLModels = getKiePMMLModels();
        return new ArrayList<>(kiePMMLModels);
    }

    @Override
    public Optional<KiePMMLModel> getKiePMMLModel(final String modelName) {
        return KnowledgeBaseUtils.getModel(knowledgeBase, modelName);
    }

    @Override
    public Optional<PMMLModel> getPMMLModel(String modelName) {
        return getKiePMMLModel(modelName).map(KiePMMLModel.class::cast);
    }

    @Override
    public PMML4Result evaluate(final String modelName, final PMMLContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("evaluate {} {}", modelName, context);
        }
        KiePMMLModel toEvaluate = getKiePMMLModel(modelName).orElseThrow(() -> new KiePMMLException("Failed to retrieve model with name " + modelName));
        return evaluate(toEvaluate, context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected PMML4Result evaluate(final KiePMMLModel model, final PMMLContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("evaluate {} {}", model, context);
        }
        addMissingValuesReplacements(model, context);
        executeTransformations(model, context);
        PMMLModelEvaluator executor = getFromPMMLModelType(model.getPmmlMODEL())
                .orElseThrow(() -> new KiePMMLException(String.format("PMMLModelEvaluator not found for model %s", model.getPmmlMODEL())));
        return executor.evaluate(knowledgeBase, model, context);
    }

    /**
     * Add missing input values if defined in original PMML as <b>missingValueReplacement</b>.
     * <p>
     * "missingValueReplacement: If this attribute is specified then a missing input value is automatically replaced by the given value.
     * That is, the model itself works as if the given value was found in the original input. "
     * @param model
     * @param context
     * @see <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_MISSING-VALUE-TREATMENT-METHOD">MISSING-VALUE-TREATMENT-METHOD</a>
     */
    protected void addMissingValuesReplacements(final KiePMMLModel model, final PMMLContext context) {
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
     * Execute <b>Transformations</b> on input data.
     * @param model
     * @param context
     * @see <a href="http://dmg.org/pmml/v4-4/Transformations.html">Transformations</a>
     * @see <a href="http://dmg.org/pmml/v4-4/Transformations.html#xsdElement_LocalTransformations">LocalTransformations</a>
     */
    protected void executeTransformations(final KiePMMLModel model, final PMMLContext context) {
        logger.debug("executeTransformations {} {}", model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final Map<String, ParameterInfo> mappedRequestParams = requestData.getMappedRequestParams();
        final List<KiePMMLNameValue> kiePMMLNameValues = getKiePMMLNameValuesFromParameterInfos(mappedRequestParams.values());
        final Map<String, Function<List<KiePMMLNameValue>, Object>> commonTransformationsMap = model.getCommonTransformationsMap();
        commonTransformationsMap.forEach((fieldName, transformationFunction) -> {
            // Common Transformations need to be done only once
            if (!mappedRequestParams.containsKey(fieldName)) {
                logger.debug("commonTransformation {} {}", fieldName, transformationFunction);
                Object commonTranformation = transformationFunction.apply(kiePMMLNameValues);
                requestData.addRequestParam(fieldName, commonTranformation);
                context.addCommonTranformation(fieldName, commonTranformation);
                kiePMMLNameValues.add(new KiePMMLNameValue(fieldName, commonTranformation));
            }
        });
        final Map<String, Function<List<KiePMMLNameValue>, Object>> localTransformationsMap = model.getLocalTransformationsMap();
        localTransformationsMap.forEach((fieldName, transformationFunction) -> {
            logger.debug("localTransformation {} {}", fieldName, transformationFunction);
            Object localTransformation = transformationFunction.apply(kiePMMLNameValues);
            // Local Transformations need to be done for every model, eventually replacing previous ones
            if (mappedRequestParams.containsKey(fieldName)) {
                final ParameterInfo toRemove = mappedRequestParams.get(fieldName);
                requestData.removeRequestParam(toRemove);
            }
            requestData.addRequestParam(fieldName, localTransformation);
            context.addLocalTranformation(fieldName, localTransformation);
        });
    }

    /**
     * Returns an <code>Optional&lt;PMMLModelExecutor&gt;</code> to allow
     * incremental development of different model-specific executors
     * @param pmmlMODEL
     * @return
     */
    private Optional<PMMLModelEvaluator> getFromPMMLModelType(final PMML_MODEL pmmlMODEL) {
        logger.trace("getFromPMMLModelType {}", pmmlMODEL);
        return pmmlModelExecutorFinder.getImplementations(false)
                .stream()
                .filter(implementation -> pmmlMODEL.equals(implementation.getPMMLModelType()))
                .findFirst();
    }

    private List<KiePMMLNameValue> getKiePMMLNameValuesFromParameterInfos(final Collection<ParameterInfo> parameterInfos) {
        return parameterInfos.stream()
                .map(parameterInfo -> new KiePMMLNameValue(parameterInfo.getName(), parameterInfo.getValue()))
                .collect(Collectors.toList());
    }
}
