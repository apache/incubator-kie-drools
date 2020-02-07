package org.kie.pmml.runtime.tree.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.models.tree.api.model.KiePMMLTreeModel;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.core.executor.PMMLModelExecutor;

import static org.kie.pmml.runtime.core.utils.Converter.getUnwrappedParametersMap;

public class PMMLTreeModelExecutor implements PMMLModelExecutor {

    private final KieServices kieServices;
    private final KieContainer kContainer;

    public PMMLTreeModelExecutor() {
        this.kieServices = KieServices.Factory.get();
        // TODO {gcardosi} is this correct?
        this.kContainer = kieServices.getKieClasspathContainer();
    }

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLModel model, PMMLContext pmmlContext) throws KiePMMLException {
        if (!(model instanceof KiePMMLTreeModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLTreeModel, received a " + model.getClass().getName());
        }
        final KiePMMLTreeModel treeModel = (KiePMMLTreeModel) model;
        PMML4Result toReturn = new PMML4Result();
        StatelessKieSession kSession = kContainer.newStatelessKieSession("PMMLTreeModelSession");
        Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        List<Object> executionParams = new ArrayList<>();
        executionParams.add(treeModel);
        executionParams.add(toReturn);
        executionParams.add(unwrappedInputParams);
        /*
        FactType nameType = ksession.getKieBase().getFactType("org.test", "ExtendedName");
        Object name = nameType.newInstance();
        nameType.set(name, "value", "Mario");

        ksession.insert(name);
        ksession.fireAllRules();
         */
        kSession.execute(executionParams);
        return toReturn;
    }


}
