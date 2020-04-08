package org.kie.pmml.models.drooled.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.DrlDumper;
import org.drools.modelcompiler.ExecutableModelProject;
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
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.exceptions.KieEnumException;
import org.kie.pmml.commons.model.KiePMMLDrooledModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelExecutor;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

public abstract class DrooledModelExecutor implements PMMLModelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DrooledModelExecutor.class.getName());

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext, String releaseId) {
        if (!(model instanceof KiePMMLDrooledModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLDrooledModel, received a " + model.getClass().getName());
        }
        final KiePMMLDrooledModel drooledModel = (KiePMMLDrooledModel) model;
        printGeneratedRules(drooledModel);
        KieSession kSession = new KieHelper()
                .addContent(drooledModel.getPackageDescr())
                .build(ExecutableModelProject.class)
                .newKieSession();
        final Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        List<Object> executionParams = new ArrayList<>();
        KiePMMLStatusHolder statusHolder = new KiePMMLStatusHolder();
        executionParams.add(statusHolder);
        PMML4Result toReturn = new PMML4Result();
        toReturn.setResultCode(ResultCode.FAIL.getName());
        toReturn.setResultObjectName(drooledModel.getTargetField());
        executionParams.add(toReturn);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = drooledModel.getFieldTypeMap();
        for (Map.Entry<String, Object> entry : unwrappedInputParams.entrySet()) {
            if (!fieldTypeMap.containsKey(entry.getKey())) {
                throw new KiePMMLModelException(String.format("Field %s not mapped to generated type", entry.getKey()));
            }
            try {
                String generatedTypeName = fieldTypeMap.get(entry.getKey()).getGeneratedType();
                FactType factType = kSession.getKieBase().getFactType(drooledModel.getPackageDescr().getName(), generatedTypeName);
                Object toAdd = factType.newInstance();
                factType.set(toAdd, "value", entry.getValue());
                executionParams.add(toAdd);
            } catch (Exception e) {
                throw new KiePMMLModelException(e.getMessage(), e);
            }
        }
        executionParams.forEach(kSession::insert);
        setupExecutionListener(kSession);
        kSession.setGlobal("$pmml4Result", toReturn);
        kSession.fireAllRules();
        return toReturn;
    }

    private void printGeneratedRules(KiePMMLDrooledModel drooledModel) {
        if (logger.isDebugEnabled()) {
            try {
                String string = new DrlDumper().dump(drooledModel.getPackageDescr());
                logger.debug(string);
            } catch (Exception e) {
                throw new KieEnumException("Failed to dump " + drooledModel, e);
            }
        }
    }

    private void setupExecutionListener(final KieSession kSession) {
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void matchCancelled(MatchCancelledEvent event) {
                // Not used
            }

            public void matchCreated(MatchCreatedEvent event) {
                // Not used
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
                if (logger.isDebugEnabled()) {
                    logger.debug(event.toString());
                }
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                // Not used
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                if (logger.isDebugEnabled()) {
                    logger.debug(event.toString());
                }
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                // Not used
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                // Not used
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                // Not used
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                // Not used
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                // Not used
            }
        };
        kSession.addEventListener(agendaEventListener);
    }
}
