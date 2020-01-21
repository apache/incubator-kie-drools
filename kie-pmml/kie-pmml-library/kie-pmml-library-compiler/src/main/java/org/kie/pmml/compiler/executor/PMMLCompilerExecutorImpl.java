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
package org.kie.pmml.compiler.executor;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.utils.KiePMMLUtil;
import org.kie.pmml.library.api.implementations.ModelImplementationProviderFinder;
import org.xml.sax.SAXException;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;

/**
 * PMMLCompilerExecutor default implementation
 */
public class PMMLCompilerExecutorImpl implements PMMLCompilerExecutor {

    private static final Logger log = Logger.getLogger(PMMLCompilerExecutorImpl.class.getName());

    private ModelImplementationProviderFinder modelImplementationProviderFinder;

    public PMMLCompilerExecutorImpl(ModelImplementationProviderFinder modelImplementationProviderFinder) {
        this.modelImplementationProviderFinder = modelImplementationProviderFinder;
    }

    @Override
    public List<KiePMMLModel> getResults(InputStream inputStream) throws JAXBException, SAXException, KiePMMLException {
        log.info("getResults " + inputStream);
        PMML commonPMMLModel = KiePMMLUtil.load(inputStream);
        return getResults(commonPMMLModel);
    }

    /**
     * Read the given <code>PMML</code> to returns a <code>List&lt;KiePMMLModel&gt;</code>
     * @param pmml
     * @return
     */
    private List<KiePMMLModel> getResults(PMML pmml) throws KiePMMLException {
        log.info("getResults " + pmml);
        DataDictionary dataDictionary = pmml.getDataDictionary();
        return pmml
                .getModels()
                .stream()
                .map(throwingFunctionWrapper(model -> getFromModel(dataDictionary, model)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Read the given <code>DataDictionary</code> and <code>Model</code>> to returns a <code>Optional&lt;KiePMMLModel&gt;</code>
     *
     *
     * @param dataDictionary
     * @param model
     * @return
     * @throws KiePMMLException
     */
    @SuppressWarnings("unchecked")
    private Optional<KiePMMLModel> getFromModel(DataDictionary dataDictionary, Model model) throws KiePMMLException {
        log.info("getFromModel " + model);
        final PMML_MODEL pmmlMODEL = PMML_MODEL.byName(model.getClass().getSimpleName());
        log.info("pmmlModelType " + pmmlMODEL);
        return modelImplementationProviderFinder.getImplementations(false)
                .stream()
                .filter(implementation -> pmmlMODEL.equals(implementation.getPMMLModelType()))
                .map(throwingFunctionWrapper(implementation -> implementation.getKiePMMLModel(dataDictionary, model)))
                .findFirst();
    }
}
