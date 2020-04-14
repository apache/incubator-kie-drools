package org.kie.pmml.models.drools.executor;

import java.util.Map;

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
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelExecutor;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.drools.utils.KiePMMLSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

public abstract class DroolsModelExecutor implements PMMLModelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelExecutor.class.getName());

    private static final AgendaEventListener agendaEventListener = new AgendaEventListener() {
        public void matchCancelled(MatchCancelledEvent event) {/*Not used */}

        public void matchCreated(MatchCreatedEvent event) {/*Not used */}

        public void afterMatchFired(AfterMatchFiredEvent event) {
            if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }
        }

        public void agendaGroupPopped(AgendaGroupPoppedEvent event) {/*Not used */}

        public void agendaGroupPushed(AgendaGroupPushedEvent event) {
            if (logger.isDebugEnabled()) {
                logger.debug(event.toString());
            }
        }

        public void beforeMatchFired(BeforeMatchFiredEvent event) {/*Not used */}

        public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {/*Not used */}

        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {/*Not used */}

        public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {/*Not used */}

        public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {/*Not used */}
    };

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext, String releaseId) {
        if (!(model instanceof KiePMMLDroolsModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLDroolsModel, received a " + model.getClass().getName());
        }
        final KiePMMLDroolsModel drooledModel = (KiePMMLDroolsModel) model;
        final PMML4Result toReturn = getPMML4Result(drooledModel.getTargetField());
        final Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = drooledModel.getFieldTypeMap();
        final KiePMMLSessionUtils kiePMMLSessionUtils = KiePMMLSessionUtils.builder(drooledModel.getPackageDescr(), toReturn)
                .withAgendaEventListener(agendaEventListener)
                .withObjectsInSession(unwrappedInputParams, fieldTypeMap)
                .build();
        kiePMMLSessionUtils.fireAllRules();
        return toReturn;
    }

    protected PMML4Result getPMML4Result(final String targetField) {
        PMML4Result toReturn = new PMML4Result();
        toReturn.setResultCode(ResultCode.FAIL.getName());
        toReturn.setResultObjectName(targetField);
        return toReturn;
    }
}
