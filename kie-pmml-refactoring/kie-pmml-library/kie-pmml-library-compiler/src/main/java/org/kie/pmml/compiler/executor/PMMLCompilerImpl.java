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
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.compiler.utils.KiePMMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.library.commons.implementations.KiePMMLModelRetriever.getFromDataDictionaryAndModel;

/**
 * <code>PMMLCompiler</code> default implementation
 */
public class PMMLCompilerImpl implements PMMLCompiler {

    private static final Logger logger = LoggerFactory.getLogger(PMMLCompilerImpl.class.getName());

    @Override
    public List<KiePMMLModel> getResults(InputStream inputStream, Object kbuilder) throws KiePMMLException {
        logger.info("getResults {}", inputStream);
        try {
            PMML commonPMMLModel = KiePMMLUtil.load(inputStream);
            return getResults(commonPMMLModel, kbuilder);
        } catch (JAXBException | SAXException e) {
            throw  new KiePMMLException("Failed to get results", e);
        }
    }

    /**
     * Read the given <code>PMML</code> to returns a <code>List&lt;KiePMMLModel&gt;</code>
     * @param pmml
     * @param kbuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     */
    private List<KiePMMLModel> getResults(PMML pmml, Object kbuilder) throws KiePMMLException {
        logger.info("getResults {}", pmml);
        DataDictionary dataDictionary = pmml.getDataDictionary();
        return pmml
                .getModels()
                .stream()
                .map(throwingFunctionWrapper(model -> getFromDataDictionaryAndModel(dataDictionary, model, kbuilder)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
