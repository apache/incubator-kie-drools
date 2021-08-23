/*
* Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.tree.compiler.executor;

import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.kie.pmml.models.tree.model.KiePMMLTreeModelWithSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Tree</b>
 */
public class TreeModelImplementationProvider implements ModelImplementationProvider<TreeModel,KiePMMLTreeModel>{

private static final Logger logger = LoggerFactory.getLogger(TreeModelImplementationProvider.class.getName());


    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public KiePMMLTreeModel getKiePMMLModel(final String packageName,
                                                    final DataDictionary dataDictionary,
                                                    final TransformationDictionary transformationDictionary,
                                                    final TreeModel model,
                                                    final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModel {} {} {} {}", packageName, dataDictionary, model, hasClassloader);
        return KiePMMLTreeModelFactory.getKiePMMLTreeModel(dataDictionary, transformationDictionary, model, packageName, hasClassloader);
    }

    @Override
    public KiePMMLTreeModel getKiePMMLModelWithSources(final String packageName,
                                                             final DataDictionary dataDictionary,
                                                             final TransformationDictionary transformationDictionary,
                                                             final TreeModel  model,
                                                             final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", packageName, dataDictionary, model, hasClassloader);
        try {
            final Map<String, String> sourcesMap = KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(dataDictionary, transformationDictionary, model, packageName);
            return new KiePMMLTreeModelWithSources(model.getModelName(), packageName, sourcesMap);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
