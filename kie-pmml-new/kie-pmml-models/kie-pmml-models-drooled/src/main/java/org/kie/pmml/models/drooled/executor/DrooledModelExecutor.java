package org.kie.pmml.models.drooled.executor;

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
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.model.KiePMMLDrooledModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

public abstract class DrooledModelExecutor implements PMMLModelExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DrooledModelExecutor.class.getName());

    private final KieServices kieServices;

    public DrooledModelExecutor() {
        this.kieServices = KieServices.Factory.get();
    }

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
        toReturn.setResultCode(StatusCode.FAIL.getName());
        toReturn.setResultObjectName(drooledModel.getTargetField());
        executionParams.add(toReturn);
        final Map<String, String> fieldTypeMap = drooledModel.getFieldTypeMap();
        for (Map.Entry<String, Object> entry : unwrappedInputParams.entrySet()) {
            if (!fieldTypeMap.containsKey(entry.getKey())) {
                throw new KiePMMLModelException(String.format("Field %s not mapped to generated type", entry.getKey()));
            }
            try {
                String generatedTypeName = fieldTypeMap.get(entry.getKey());
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
        kSession.fireAllRules();
        return toReturn;
    }

    private void printGeneratedRules(KiePMMLDrooledModel treeModel) {
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

//    @Override
//    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext, String releaseId) {
//        if (!(model instanceof KiePMMLDrooledModel)) {
//            throw new KiePMMLModelException("Expected a KiePMMLDrooledModel, received a " + model.getClass().getName());
//        }
//        final KiePMMLDrooledModel drooledModel = (KiePMMLDrooledModel) model;
//        ReleaseId rel = new ReleaseIdImpl(releaseId);
//        // TODO {gcardosi}: here the generate PackageDescr must have already been compiled by drools and inserted inside the kiebuilder/kiebase something
//        final KieContainer kieContainer = kieServices.newKieContainer(rel);
//        PMML4Result toReturn = new PMML4Result();
//        StatelessKieSession kSession = kieContainer.newStatelessKieSession("PMMLTreeModelSession");
//        Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
//        List<FactType> factTypes = getParameterFactTypes(unwrappedInputParams, kSession, drooledModel.getPackageDescr().getTypeDeclarations(), drooledModel.getPackageDescr().getEnumDeclarations());
//        List<Object> executionParams = new ArrayList<>();
//        executionParams.add(toReturn);
//        executionParams.add(factTypes);
//        kSession.execute(executionParams);
//        return toReturn;
//    }
//
//    private List<FactType> getParameterFactTypes(Map<String, Object> unwrappedInputParams, final StatelessKieSession kSession, final List<TypeDeclarationDescr> typeDeclarations, final List<EnumDeclarationDescr> enumDeclarations) throws KiePMMLException {
//        List<FactType> toReturn = new ArrayList<>();
//        for (Map.Entry<String, Object> entry : unwrappedInputParams.entrySet()) {
//            toReturn.add(getParameterFactType(entry.getKey(), entry.getValue(), kSession, typeDeclarations, enumDeclarations));
//        }
//        return toReturn;
//    }
//
//    private FactType getParameterFactType(String parameterName, Object parameterValue, final StatelessKieSession kSession, final List<TypeDeclarationDescr> typeDeclarations, final List<EnumDeclarationDescr> enumDeclarations) {
//        try {
//            Optional<FactType> toReturn = getParameterFactType(parameterName, parameterValue, kSession, typeDeclarations);
//            if (!toReturn.isPresent()) {
//                toReturn = getParameterFactType(parameterName, parameterValue, kSession, enumDeclarations);
//            }
//            if (!toReturn.isPresent()) {
//                throw new KiePMMLException(String.format("Failed to retrieve FactType for %s", parameterName));
//            }
//            return toReturn.get();
//        } catch (Exception e) {
//            throw new KiePMMLException(String.format("Failed to retrieve FactType for %s", parameterName), e);
//        }
//    }
//
//    private Optional<FactType> getParameterFactType(String parameterName, Object parameterValue, final StatelessKieSession kSession, List<? extends AbstractClassTypeDeclarationDescr> toRead) {
//        return toRead.stream()
//                .filter(typeDeclaration -> typeDeclaration.getTypeName().equals(parameterName))
//                .map(typeDeclaration -> {
//                    try {
//                        FactType factType = kSession.getKieBase().getFactType(typeDeclaration.getNamespace(), typeDeclaration.getTypeName());
//                        Object bean = factType.newInstance();
//                        factType.set(bean, "value", parameterValue);
//                        return factType;
//                    } catch (Exception e) {
//                        throw new KiePMMLException("Failed to instantiate " + parameterName);
//                    }
//                })
//                .findFirst();
//    }
}
