/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.tree.compiler.executor;

import java.util.List;
import java.util.Map;

import org.dmg.pmml.Field;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
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
    public Class<KiePMMLTreeModel> getKiePMMLModelClass() {
        return KiePMMLTreeModel.class;
    }

    @Override
    public KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                final TreeModel model,
                                                final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                final List<KiePMMLDroolsType> types) {
        return KiePMMLTreeModelFactory.getKiePMMLDroolsAST(fields, model, fieldTypeMap, types);
    }

    @Override
    public Map<String, String> getKiePMMLDroolsModelSourcesMap(final DroolsCompilationDTO<TreeModel> compilationDTO) {
        return KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(compilationDTO);
    }
}
