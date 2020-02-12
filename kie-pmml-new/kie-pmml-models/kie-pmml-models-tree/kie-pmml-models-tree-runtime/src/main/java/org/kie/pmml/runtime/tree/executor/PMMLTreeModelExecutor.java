package org.kie.pmml.runtime.tree.executor;

import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.models.drooled.executor.DrooledModelExecutor;

public class PMMLTreeModelExecutor extends DrooledModelExecutor {

//    private final KieServices kieServices;
//    private final KieContainer kContainer;

    public PMMLTreeModelExecutor() {
//        this.kieServices = KieServices.Factory.get();
//        // TODO {gcardosi} is this correct?
//        this.kContainer = kieServices.getKieClasspathContainer();
    }

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TREE_MODEL;
    }

//    @Override
//    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext, String releaseId) throws KiePMMLException {
//        if (!(model instanceof KiePMMLTreeModel)) {
//            throw new KiePMMLModelException("Expected a KiePMMLTreeModel, received a " + model.getClass().getName());
//        }
//        ReleaseId rel = new ReleaseIdImpl(releaseId);
//        // TODO {gcardosi}: here the generate PackageDescr must be compiled by droosl and inserted inside the kiebuilder/kiebase something
//        final KieContainer kieContainer = kieServices.newKieContainer(rel);
//        final KiePMMLTreeModel treeModel = (KiePMMLTreeModel) model;
//        PMML4Result toReturn = new PMML4Result();
//        StatelessKieSession kSession = kContainer.newStatelessKieSession("PMMLTreeModelSession");
//        Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
//        List<Object> executionParams = new ArrayList<>();
//        executionParams.add(treeModel);
//        executionParams.add(toReturn);
//        executionParams.add(unwrappedInputParams);
//        /*
//        // TODO {gcardosi} Retrieve the converted datadictionary from the treemodel and use it to map input data to expected input values
//        FactType nameType = ksession.getKieBase().getFactType("org.test", "ExtendedName");
//        Object name = nameType.newInstance();
//        nameType.set(name, "value", "Mario");
//
//        ksession.insert(name);
//        ksession.fireAllRules();
//         */
//        kSession.execute(executionParams);
//        return toReturn;
//    }
}
