package org.kie.pmml.models.drooled.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.FactType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.pmml.commons.model.KiePMMLDrooledModel;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.core.executor.PMMLModelExecutor;

import static org.kie.pmml.commons.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.runtime.core.utils.Converter.getUnwrappedParametersMap;

public class DrooledModelExecutor implements PMMLModelExecutor {

    private final KieServices kieServices;

    public DrooledModelExecutor() {
        this.kieServices = KieServices.Factory.get();
    }

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext, String releaseId) throws KiePMMLException {
        if (!(model instanceof KiePMMLDrooledModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLDrooledModel, received a " + model.getClass().getName());
        }
        final KiePMMLDrooledModel drooledModel = (KiePMMLDrooledModel) model;
        ReleaseId rel = new ReleaseIdImpl(releaseId);
        // TODO {gcardosi}: here the generate PackageDescr must have already been compiled by drools and inserted inside the kiebuilder/kiebase something
        final KieContainer kieContainer = kieServices.newKieContainer(rel);
        PMML4Result toReturn = new PMML4Result();
        StatelessKieSession kSession = kieContainer.newStatelessKieSession("PMMLTreeModelSession");
        Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        List<FactType> factTypes = getParameterFactTypes(unwrappedInputParams, kSession, drooledModel.getPackageDescr().getTypeDeclarations(), drooledModel.getPackageDescr().getEnumDeclarations());
        List<Object> executionParams = new ArrayList<>();
        executionParams.add(toReturn);
        executionParams.add(factTypes);
        kSession.execute(executionParams);
        return toReturn;
    }

    private List<FactType> getParameterFactTypes(Map<String, Object> unwrappedInputParams, final StatelessKieSession kSession, final List<TypeDeclarationDescr> typeDeclarations, final List<EnumDeclarationDescr> enumDeclarations) throws KiePMMLException {
        return unwrappedInputParams.entrySet().stream().map(throwingFunctionWrapper(entry -> getParameterFactType(entry.getKey(), entry.getValue(), kSession, typeDeclarations, enumDeclarations))).collect(Collectors.toList());
    }

    private FactType getParameterFactType(String parameterName, Object parameterValue, final StatelessKieSession kSession, final List<TypeDeclarationDescr> typeDeclarations, final List<EnumDeclarationDescr> enumDeclarations) throws KiePMMLException {
        try {
            Optional<FactType> toReturn = typeDeclarations.stream()
                    .filter(typeDeclaration -> typeDeclaration.getTypeName().equals(parameterName))
                    .map(throwingFunctionWrapper(typeDeclaration -> {
                        FactType factType = kSession.getKieBase().getFactType(typeDeclaration.getNamespace(), typeDeclaration.getTypeName());
                        Object bean = factType.newInstance();
                        factType.set(bean, "value", parameterValue);
                        return factType;
                    }))
                    .findFirst();
            if (!toReturn.isPresent()) {
                toReturn = enumDeclarations.stream()
                        .filter(enumDeclaration -> enumDeclaration.getTypeName().equals(parameterName))
                        .map(throwingFunctionWrapper(enumDeclaration -> {
                            FactType factType = kSession.getKieBase().getFactType(enumDeclaration.getNamespace(), enumDeclaration.getTypeName());
                            Object bean = factType.newInstance();
                            factType.set(bean, "value", parameterValue);
                            return factType;
                        }))
                        .findFirst();
            }
            if (!toReturn.isPresent()) {
                throw new KiePMMLException(String.format("Failed to retrieve FactType for %s", parameterName));
            }
            return toReturn.get();
        } catch (Exception e) {
            throw new KiePMMLException(String.format("Failed to retrieve FactType for %s", parameterName), e);
        }
    }
}
