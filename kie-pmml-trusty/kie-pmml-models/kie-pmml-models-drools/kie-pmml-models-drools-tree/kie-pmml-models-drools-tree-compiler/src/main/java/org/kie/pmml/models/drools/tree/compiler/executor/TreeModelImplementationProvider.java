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
package org.kie.pmml.models.drools.tree.compiler.executor;

import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.provider.DroolsModelProvider;
import org.kie.pmml.models.drools.tree.compiler.factories.KiePMMLTreeModelFactory;
import org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel.PMML_MODEL_TYPE;

/**
 * Default <code>DroolsModelProvider</code> for <b>Tree</b>
 */
public class TreeModelImplementationProvider extends DroolsModelProvider<TreeModel, KiePMMLTreeModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLTreeModel getKiePMMLDroolsModel(final DataDictionary dataDictionary,
                                                  final TransformationDictionary transformationDictionary,
                                                  final TreeModel model,
                                                  final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                  final String packageName,
                                                  final HasClassLoader hasClassLoader) {
        try {
            return KiePMMLTreeModelFactory.getKiePMMLTreeModel(dataDictionary,
                                                               transformationDictionary,
                                                               model,
                                                               fieldTypeMap,
                                                               packageName,
                                                               hasClassLoader);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new KiePMMLException(e.getMessage(), e);
        }
    }

    @Override
    public KiePMMLDroolsAST getKiePMMLDroolsAST(final DataDictionary dataDictionary,
                                                final TreeModel model,
                                                final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                final List<KiePMMLDroolsType> types){
        return KiePMMLTreeModelFactory.getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap, types);
    }

    @Override
    public Map<String, String> getKiePMMLDroolsModelSourcesMap(final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final TreeModel model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final String packageName) {
        return KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(dataDictionary, transformationDictionary, model, fieldTypeMap, packageName);
    }
}
