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
package org.kie.pmml.models.tree.compiler.executor;

import java.util.Map;

import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.tree.compiler.dto.TreeCompilationDTO;
import org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Tree</b>
 */
public class TreeModelImplementationProvider implements ModelImplementationProvider<TreeModel, KiePMMLTreeModel> {

    private static final Logger logger = LoggerFactory.getLogger(TreeModelImplementationProvider.class.getName());

    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public Class<KiePMMLTreeModel> getKiePMMLModelClass() {
        return KiePMMLTreeModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<TreeModel> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(),
                     compilationDTO.getModel(),
                     compilationDTO.getPmmlContext());
        try {
            return KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(TreeCompilationDTO.fromCompilationDTO(compilationDTO));
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
