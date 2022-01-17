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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.PMML_STEP;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;
import org.kie.pmml.evaluator.core.implementations.PMMLRuntimeStep;
import org.kie.pmml.evaluator.core.utils.KnowledgeBaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.PMML_STEP.END;
import static org.kie.pmml.api.enums.PMML_STEP.POST_EVALUATION;
import static org.kie.pmml.api.enums.PMML_STEP.PRE_EVALUATION;
import static org.kie.pmml.api.enums.PMML_STEP.START;
import static org.kie.pmml.evaluator.core.utils.PMMLListenerUtils.stepExecuted;
import static org.kie.pmml.evaluator.core.utils.PostProcess.postProcess;
import static org.kie.pmml.evaluator.core.utils.PreProcess.preProcess;

public class PMMLRuntimeInternalImpl implements PMMLRuntimeInternal {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeInternalImpl.class);

    private final KieBase knowledgeBase;
    private final PMMLModelEvaluatorFinderImpl pmmlModelExecutorFinder;
    private final Set<PMMLListener> pmmlListeners = new HashSet<>();

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

    @Override
    public void addPMMLListener(PMMLListener toAdd) {
        pmmlListeners.add(toAdd);
    }

    @Override
    public void removePMMLListener(PMMLListener toRemove) {
        pmmlListeners.remove(toRemove);
    }

    @Override
    public Set<PMMLListener> getPMMLListeners() {
        return Collections.unmodifiableSet(pmmlListeners);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected PMML4Result evaluate(final KiePMMLModel model, final PMMLContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("evaluate {} {}", model, context);
        }
        pmmlListeners.forEach(context::addPMMLListener);
        addStep(() -> getStep(START, model, context.getRequestData()), context);
        final ProcessingDTO processingDTO = preProcess(model, context);
        addStep(() -> getStep(PRE_EVALUATION, model, context.getRequestData()), context);
        PMMLModelEvaluator executor = getFromPMMLModelType(model.getPmmlMODEL())
                .orElseThrow(() -> new KiePMMLException(String.format("PMMLModelEvaluator not found for model %s",
                                                                      model.getPmmlMODEL())));
        PMML4Result toReturn = executor.evaluate(knowledgeBase, model, context);
        addStep(() -> getStep(POST_EVALUATION, model, context.getRequestData()), context);
        postProcess(toReturn, model, context, processingDTO);
        addStep(() -> getStep(END, model, context.getRequestData()), context);
        return toReturn;
    }

    /**
     * Send the given <code>PMMLStep</code>
     * to the <code>PMMLContext</code>
     * @param stepSupplier
     * @param pmmlContext
     */
    void addStep(final Supplier<PMMLStep> stepSupplier, final PMMLContext pmmlContext) {
        stepExecuted(stepSupplier, pmmlContext);
    }

    PMMLStep getStep(final PMML_STEP pmmlStep, final KiePMMLModel model, final PMMLRequestData requestData) {
        final PMMLStep toReturn = new PMMLRuntimeStep(pmmlStep);
        toReturn.addInfo("MODEL", model.getName());
        toReturn.addInfo("CORRELATION ID", requestData.getCorrelationId());
        toReturn.addInfo("REQUEST MODEL", requestData.getModelName());
        requestData.getRequestParams()
                .forEach(requestParam -> toReturn.addInfo(requestParam.getName(), requestParam.getValue()));
        return toReturn;
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
}
