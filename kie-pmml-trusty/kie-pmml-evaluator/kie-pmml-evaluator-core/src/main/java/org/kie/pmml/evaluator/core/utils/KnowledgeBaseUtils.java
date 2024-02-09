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
package org.kie.pmml.evaluator.core.utils;

public class KnowledgeBaseUtils {

//    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseUtils.class);
//
//    private KnowledgeBaseUtils() {
//        // Avoid instantiation
//    }
//
//    public static List<KiePMMLModel> getModels(final KieMemoryCompiler.MemoryCompilerClassLoader classLoader) {
//        List<KiePMMLModel> models = new ArrayList<>();
//        KieMemoryCompiler.MemoryCompilerClassLoader.getSystemClassLoader();
//       runtimePackageContainer.getRuntimePackages().forEach(kpkg -> {
//            PMMLPackage pmmlPackage = (PMMLPackage) kpkg.getResourceTypePackages().get(ResourceType.PMML);
//            if (pmmlPackage != null) {
//                models.addAll(pmmlPackage.getAllModels().values());
//            }
//        });
//        return models;
//    }
//
//    public static Optional<KiePMMLModel> getPMMLModel(final RuntimePackageContainer runtimePackageContainer, String
//    modelName) {
//        logger.trace("getModels {} {}", runtimePackageContainer, modelName);
//        return getModels(runtimePackageContainer)
//                .stream()
//                .filter(model -> Objects.equals(modelName, model.getName()))
//                .findFirst();
//    }
}
