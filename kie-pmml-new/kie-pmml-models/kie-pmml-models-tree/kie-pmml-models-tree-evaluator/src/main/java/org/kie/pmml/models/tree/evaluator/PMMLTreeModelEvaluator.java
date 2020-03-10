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

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelExecutor;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Tree</b>
 */
public class PMMLTreeModelEvaluator implements PMMLModelExecutor {

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
        ReleaseId rel = new ReleaseIdImpl(releaseId);
        // TODO {gcardosi}: here the generate PackageDescr must be compiled by drools and inserted inside the kiebuilder/kiebase something
        final KieContainer kieContainer = kieServices.newKieContainer(rel);
        final KiePMMLTreeModel treeModel = (KiePMMLTreeModel) model;
        PMML4Result toReturn = new PMML4Result();
        StatelessKieSession kSession = kContainer.newStatelessKieSession("PMMLTreeModelSession");
        Map<String, Object> unwrappedInputParams = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        List<Object> executionParams = new ArrayList<>();
        executionParams.add(treeModel);
        executionParams.add(toReturn);
        executionParams.add(unwrappedInputParams);
        /*
        // TODO {gcardosi} Retrieve the converted datadictionary from the treemodel and use it to map input data to expected input values
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
