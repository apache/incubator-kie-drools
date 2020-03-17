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
package org.kie.pmml.models.tree.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.DrlDumper;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelExecutor;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Tree</b>
 */
public class PMMLTreeModelEvaluator implements PMMLModelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(PMMLTreeModelEvaluator.class.getName());
    private final KieServices kieServices;
    private final KieContainer kContainer;

    public PMMLTreeModelEvaluator() {
        this.kieServices = KieServices.Factory.get();
        // TODO {gcardosi} is this correct?
        this.kContainer = kieServices.getKieClasspathContainer();
    }

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext, String releaseId) {
        if (!(model instanceof KiePMMLTreeModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLTreeModel, received a " + model.getClass().getName());
        }
        final KiePMMLTreeModel treeModel = (KiePMMLTreeModel) model;
        printGeneratedRules(treeModel);
        KieSession kSession = new KieHelper()
                .addContent(treeModel.getPackageDescr())
                .build(ExecutableModelProject.class)
                .newKieSession();
        final Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        List<Object> executionParams = new ArrayList<>();
        KiePMMLStatusHolder statusHolder = new KiePMMLStatusHolder();
        executionParams.add(statusHolder);
        for (Map.Entry<String, Object> entry : unwrappedInputParams.entrySet()) {
            try {
                FactType factType = kSession.getKieBase().getFactType(treeModel.getPackageDescr().getName(), entry.getKey().toUpperCase());
                Object toAdd = factType.newInstance();
                factType.set(toAdd, "value", entry.getValue());
                executionParams.add(toAdd);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        executionParams.forEach(kSession::insert);
        setupExecutionListener(kSession);
        kSession.fireAllRules();
        PMML4Result toReturn = new PMML4Result();
        toReturn.setResultObjectName(treeModel.getTargetField());
        if (statusHolder.getResult() != null) {
            toReturn.setResultCode(StatusCode.OK.getName());
            toReturn.addResultVariable(treeModel.getTargetField(), statusHolder.getResult());
        } else {
            toReturn.setResultCode(StatusCode.FAIL.getName());
        }
        return toReturn;
    }

    private void printGeneratedRules(KiePMMLTreeModel treeModel) {
        try {
            String string = new DrlDumper().dump(treeModel.getPackageDescr());
            logger.info(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupExecutionListener(final KieSession kSession) {
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void matchCancelled(MatchCancelledEvent event) {
                logger.info(event.toString());
            }

            public void matchCreated(MatchCreatedEvent event) {
                logger.info(event.toString());
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
                logger.info(event.toString());
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                logger.info(event.toString());
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                logger.info(event.toString());
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                logger.info(event.toString());
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                logger.info(event.toString());
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                logger.info(event.toString());
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                logger.info(event.toString());
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                logger.info(event.toString());
            }
        };
        kSession.addEventListener(agendaEventListener);
    }
}
