/*
* Copyright ${year} Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.naive_bayes.compiler.executor;

import java.util.Map;

import org.dmg.pmml.naive_bayes.NaiveBayesModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.naive_bayes.compiler.NaiveBayesCompilationDTO;
import org.kie.pmml.models.naive_bayes.compiler.factories.KiePMMLNaiveBayesModelFactory;
import org.kie.pmml.models.naive_bayes.model.KiePMMLNaiveBayesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>NaiveBayes</b>
 */
public class NaiveBayesModelImplementationProvider implements ModelImplementationProvider<NaiveBayesModel,KiePMMLNaiveBayesModel>{

private static final Logger logger = LoggerFactory.getLogger(NaiveBayesModelImplementationProvider.class.getName());


    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.NAIVEBAYES_MODEL;
    }

    @Override
    public KiePMMLNaiveBayesModel getKiePMMLModel(final CompilationDTO<NaiveBayesModel> compilationDTO) {
        logger.trace("getKiePMMLModel {} {} {} {}", compilationDTO.getPackageName(),
                    compilationDTO.getFields(),
                    compilationDTO.getModel(),
                    compilationDTO.getHasClassloader());
        return KiePMMLNaiveBayesModelFactory.getKiePMMLNaiveBayesModel(NaiveBayesCompilationDTO.fromCompilationDTO(compilationDTO));
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<NaiveBayesModel> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", compilationDTO.getPackageName(),
                    compilationDTO.getFields(),
                    compilationDTO.getModel(),
                    compilationDTO.getHasClassloader());
        try {
            NaiveBayesCompilationDTO naivebayesCompilationDTO =
                    NaiveBayesCompilationDTO.fromCompilationDTO(compilationDTO);
        return
            KiePMMLNaiveBayesModelFactory.getKiePMMLNaiveBayesModelSourcesMap(naivebayesCompilationDTO);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
