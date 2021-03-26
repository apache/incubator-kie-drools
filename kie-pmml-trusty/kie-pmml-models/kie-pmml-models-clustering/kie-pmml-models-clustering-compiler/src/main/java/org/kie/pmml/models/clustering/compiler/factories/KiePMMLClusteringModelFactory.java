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
package  org.kie.pmml.models.clustering.compiler.factories;

import java.util.Collections;
import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;

public class KiePMMLClusteringModelFactory {

    private KiePMMLClusteringModelFactory(){
        // Avoid instantiation
    }

    public static KiePMMLClusteringModel getKiePMMLClusteringModel(final DataDictionary dataDictionary,
                                                                       final TransformationDictionary transformationDictionary,
                                                                       final ClusteringModel model,
                                                                       final String packageName,
                                                                       final HasClassLoader hasClassLoader) {
        // TODO
//        throw new UnsupportedOperationException();
        return new DummyPMMLClusteringModel(model.getModelName());
    }

    public static Map<String, String> getKiePMMLClusteringModelSourcesMap(final DataDictionary dataDictionary,
                                                                                 final TransformationDictionary transformationDictionary,
                                                                                 final ClusteringModel model,
                                                                                 final String packageName) {
        // TODO
//        throw new UnsupportedOperationException();
        return Collections.emptyMap();
    }
}

class DummyPMMLClusteringModel extends KiePMMLClusteringModel {

    public DummyPMMLClusteringModel(String modelName) {
        super(modelName);
        miningFunction = MINING_FUNCTION.CLUSTERING;
        pmmlMODEL = PMML_MODEL.CLUSTERING_MODEL;
        targetField = "class";
    }
}
