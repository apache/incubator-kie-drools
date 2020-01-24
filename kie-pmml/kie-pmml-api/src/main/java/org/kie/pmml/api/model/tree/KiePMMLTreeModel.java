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
package org.kie.pmml.api.model.tree;

import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;

public class KiePMMLTreeModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TREE_MODEL;

    private static final long serialVersionUID = 3107205976845585067L;

    public KiePMMLTreeModel(String name, PMML_MODEL pmmlMODEL) {
        super(name, pmmlMODEL);
    }
}
