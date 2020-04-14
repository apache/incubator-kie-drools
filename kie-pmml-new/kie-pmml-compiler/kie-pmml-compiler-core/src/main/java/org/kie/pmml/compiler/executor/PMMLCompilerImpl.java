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

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.kie.pmml.commons.exceptions.ExternalException;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromDataDictionaryAndModel;

/**
 * <code>PMMLCompiler</code> default implementation
 */
public class PMMLCompilerImpl implements PMMLCompiler {

    private static final Logger logger = LoggerFactory.getLogger(PMMLCompilerImpl.class.getName());

    @Override
    public List<KiePMMLModel> getModels(InputStream inputStream, Object kbuilder) {
        logger.trace("getModels {}", inputStream);
        try {
            PMML commonPMMLModel = KiePMMLUtil.load(inputStream);
            return getModels(commonPMMLModel, kbuilder);
        } catch (KiePMMLInternalException e) {
            throw new KiePMMLException("KiePMMLInternalException", e);
        } catch (KiePMMLException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    /**
     * Read the given <code>PMML</code> to returns a <code>List&lt;KiePMMLModel&gt;</code>
     * @param pmml
     * @param kbuilder Using <code>Object</code> to avoid coupling with drools
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    private List<KiePMMLModel> getModels(PMML pmml, Object kbuilder) {
        logger.trace("getModels {}", pmml);
        DataDictionary dataDictionary = pmml.getDataDictionary();
        return pmml
                .getModels()
                .stream()
                .map(model -> getFromDataDictionaryAndModel(dataDictionary, model, kbuilder))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
