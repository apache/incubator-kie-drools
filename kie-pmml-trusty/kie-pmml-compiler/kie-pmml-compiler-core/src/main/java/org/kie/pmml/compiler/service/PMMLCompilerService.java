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
package org.kie.pmml.compiler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.identifiers.PmmlIdFactory;
import org.kie.pmml.commons.HasRedirectOutput;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.model.EfestoCallableOutputPMMLClassesContainer;

import static org.kie.efesto.common.api.utils.FileNameUtils.removeSuffix;

/**
 * Class meant to <b>compile</b> resources
 */
public class PMMLCompilerService {

    private PMMLCompilerService() {
        // Avoid instantiation
    }

    static List<EfestoCompilationOutput> getEfestoFinalOutputPMML(List<KiePMMLModel> kiePmmlModels, String originalFileName, PMMLCompilationContext pmmlContext) {
        List<KiePMMLModelWithSources> kiePmmlModelsWithSources = kiePmmlModels
                .stream()
                .filter(KiePMMLModelWithSources.class::isInstance)
                .map(KiePMMLModelWithSources.class::cast)
                .collect(Collectors.toList());
        String fileName = removeSuffix(originalFileName);
        Map<String, String> allSourcesMap = new HashMap<>();
        List<EfestoCompilationOutput> toReturn = new ArrayList<>();
        iterateOverKiePmmlModelsWithSources(kiePmmlModelsWithSources, toReturn, allSourcesMap);
        List<KiePMMLFactoryModel> kiePMMLFactoryModels = kiePmmlModels
                .stream()
                .filter(KiePMMLFactoryModel.class::isInstance)
                .map(KiePMMLFactoryModel.class::cast)
                .collect(Collectors.toList());
        kiePMMLFactoryModels.forEach(kiePMMLFactoryModel -> allSourcesMap.putAll(kiePMMLFactoryModel.getSourcesMap()));
        Map<String, byte[]> compiledClasses = pmmlContext.compileClasses(allSourcesMap);
        kiePMMLFactoryModels.forEach(kiePMMLFactoryModel -> {
            String modelName = kiePMMLFactoryModel.getName().substring(0, kiePMMLFactoryModel.getName().lastIndexOf(
                    "Factory"));
            LocalComponentIdPmml modelLocalUriId = new EfestoAppRoot()
                    .get(KiePmmlComponentRoot.class)
                    .get(PmmlIdFactory.class)
                    .get(fileName, modelName);

            String fullResourceClassName = kiePMMLFactoryModel.getSourcesMap().keySet().iterator().next();
            toReturn.add(new EfestoCallableOutputPMMLClassesContainer(modelLocalUriId, fullResourceClassName,
                                                                      compiledClasses));
        });
        return toReturn;
    }

    static void iterateOverKiePmmlModelsWithSources(
            List<KiePMMLModelWithSources> toIterate,
            List<EfestoCompilationOutput> efestoCompilationOutputs,
            Map<String, String> allSourcesMap) {
        toIterate.forEach(kiePMMLModelWithSources -> {
            Map<String, String> sourcesMap = kiePMMLModelWithSources.getSourcesMap();
            allSourcesMap.putAll(sourcesMap);
            if (kiePMMLModelWithSources instanceof HasRedirectOutput) {
                efestoCompilationOutputs.add(((HasRedirectOutput) kiePMMLModelWithSources).getRedirectOutput());
            }
            if (kiePMMLModelWithSources instanceof HasNestedModels) {
                List<KiePMMLModelWithSources> nestedKiePmmlModelsWithSources =
                        ((HasNestedModels) kiePMMLModelWithSources)
                        .getNestedModels()
                        .stream()
                        .filter(KiePMMLModelWithSources.class::isInstance)
                        .map(KiePMMLModelWithSources.class::cast)
                        .collect(Collectors.toList());
                iterateOverKiePmmlModelsWithSources(nestedKiePmmlModelsWithSources, efestoCompilationOutputs,
                                                    allSourcesMap);
            }
        });

    }

}
