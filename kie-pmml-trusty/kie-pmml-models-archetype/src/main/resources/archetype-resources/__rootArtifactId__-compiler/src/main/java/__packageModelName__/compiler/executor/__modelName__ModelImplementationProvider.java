#set($str="")
#set($dt=$str.getClass().forName("java.util.Date").newInstance())
#set($year=$dt.getYear()+1900)
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
package ${package}.${packageModelName}.compiler.executor;

import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import ${package}.${packageModelName}.compiler.factories.KiePMML${modelName}ModelFactory;
import ${package}.${packageModelName}.model.KiePMML${modelName}Model;
import ${package}.${packageModelName}.model.KiePMML${modelName}ModelWithSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>${modelName}</b>
 */
public class ${modelName}ModelImplementationProvider implements ModelImplementationProvider<${modelName}Model,KiePMML${modelName}Model>{

private static final Logger logger = LoggerFactory.getLogger(${modelName}ModelImplementationProvider.class.getName());


    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.${modelNameUppercase}_MODEL;
    }

    @Override
    public KiePMML${modelName}Model getKiePMMLModel(final String packageName,
                                                    final DataDictionary dataDictionary,
                                                    final TransformationDictionary transformationDictionary,
                                                    final ${modelName}Model model,
                                                    final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModel {} {} {} {}", packageName, dataDictionary, model, hasClassloader);
        return KiePMML${modelName}ModelFactory.getKiePMML${modelName}Model(dataDictionary, transformationDictionary, model, packageName, hasClassloader);
    }

    @Override
    public KiePMML${modelName}Model getKiePMMLModelWithSources(final String packageName,
                                                             final DataDictionary dataDictionary,
                                                             final TransformationDictionary transformationDictionary,
                                                             final ${modelName}Model  model,
                                                             final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", packageName, dataDictionary, model, hasClassloader);
        try {
            final Map<String, String> sourcesMap = KiePMML${modelName}ModelFactory.getKiePMML${modelName}ModelSourcesMap(dataDictionary, transformationDictionary, model, packageName);
            return new KiePMML${modelName}ModelWithSources(model.getModelName(), packageName, sourcesMap);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
