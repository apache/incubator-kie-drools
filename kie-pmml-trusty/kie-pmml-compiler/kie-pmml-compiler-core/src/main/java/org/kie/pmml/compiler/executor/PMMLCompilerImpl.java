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
package org.kie.pmml.compiler.executor;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLFactoryFactory.getFactorySourceCode;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSources;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getSourcesMapFromCommonDataAndTransformationDictionaryAndModel;

/**
 * <code>PMMLCompiler</code> default implementation
 */
public class PMMLCompilerImpl implements PMMLCompiler {

    private static final Logger logger = LoggerFactory.getLogger(PMMLCompilerImpl.class.getName());


    @Override
    public List<KiePMMLModel> getKiePMMLModelsWithSources(final String packageName,
                                                          final InputStream inputStream,
                                                          final String fileName,
                                                          final PMMLCompilationContext pmmlContext) {
        logger.trace("getModels {} {}", inputStream, pmmlContext);
        try {
            PMML commonPMMLModel = KiePMMLUtil.load(inputStream, fileName);
            List<Model> models = commonPMMLModel.getModels();
            final List<KiePMMLModel> toReturn = getModelsWithSources(packageName, commonPMMLModel, pmmlContext,
                                                                     fileName);
            final List<KiePMMLFactoryModel> toAdd = toReturn.stream()
                    .map(kiePMMLModel -> getKiePMMLFactoryModel(kiePMMLModel, models, packageName)).collect(Collectors.toList());
            toReturn.addAll(toAdd);
            return toReturn;
        } catch (KiePMMLInternalException e) {
            throw new KiePMMLException("KiePMMLInternalException", e);
        } catch (KiePMMLException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    private KiePMMLFactoryModel getKiePMMLFactoryModel(KiePMMLModel kiePMMLModel, List<Model> models,
                                                       String packageName) {
        Set<String> expectedClasses = models
                .stream()
                .filter(model -> kiePMMLModel.getName().equals(model.getModelName()))
                .map(model -> {
                    String modelPackageName = getSanitizedPackageName(String.format(PACKAGE_CLASS_TEMPLATE,
                                                                                    packageName,
                                                                                    model.getModelName()));
                    return modelPackageName + "." + getSanitizedClassName(model.getModelName());
                })
                .collect(Collectors.toSet());
        final Set<String> generatedClasses = new HashSet<>();
        Map<String, Boolean> expectedClassModelTypeMap =
                expectedClasses
                        .stream()
                        .collect(Collectors.toMap(expectedClass -> expectedClass,
                                                  expectedClass -> {
                                                      HasSourcesMap retrieved = getHasSourceMap(kiePMMLModel,
                                                                                                expectedClass);
                                                      generatedClasses.addAll(retrieved.getSourcesMap().keySet());
                                                      return retrieved.isInterpreted();
                                                  }));
        if (!generatedClasses.containsAll(expectedClasses)) {
            expectedClasses.removeAll(generatedClasses);
            String missingClasses = String.join(", ", expectedClasses);
            throw new KiePMMLException("Expected generated class " + missingClasses + " not found");
        }
        String factoryClassName = getSanitizedClassName(kiePMMLModel.getName()) + "Factory";
        Map<String, String> factorySourceMap = getFactorySourceCode(factoryClassName, packageName,
                                                                    expectedClassModelTypeMap);
        return new KiePMMLFactoryModel(kiePMMLModel.getFileName(), factoryClassName, packageName,
                                       factorySourceMap);
    }

    /**
     * Read the given <code>PMML</code> to returns a <code>List&lt;KiePMMLModel&gt;</code>
     * @param packageName the package into which put all the generated classes out of the given <code>PMML</code>
     * @param pmml
     * @param pmmlContext
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    private List<Map<String, String>> getModelSourcesMap(final String packageName, final PMML pmml,
                                                         final PMMLCompilationContext pmmlContext,
                                                         final String fileName) {
        logger.trace("getModelSourcesMap {}", pmml);
        return pmml
                .getModels()
                .stream()
                .map(model -> {
                    final CommonCompilationDTO<?> compilationDTO =
                            CommonCompilationDTO.fromGeneratedPackageNameAndFields(packageName, pmml, model,
                                                                                   pmmlContext, fileName);
                    return getSourcesMapFromCommonDataAndTransformationDictionaryAndModel(compilationDTO);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Read the given <code>PMML</code> to returns a <code>List&lt;KiePMMLModel&gt;</code>
     * @param packageName the package into which put all the generated classes out of the given <code>PMML</code>
     * @param pmmlContext Using <code>PMMLRuntimeContext</code>
     * @param fileName
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     */
    private List<KiePMMLModel> getModelsWithSources(final String packageName, final PMML pmml,
                                                    final PMMLCompilationContext pmmlContext,
                                                    final String fileName) {
        logger.trace("getModels {}", pmml);
        return pmml
                .getModels()
                .stream()
                .map(model -> {
                    final CommonCompilationDTO<?> compilationDTO =
                            CommonCompilationDTO.fromGeneratedPackageNameAndFields(packageName, pmml, model,
                                                                                   pmmlContext, fileName);
                    return getFromCommonDataAndTransformationDictionaryAndModelWithSources(compilationDTO);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private HasSourcesMap getHasSourceMap(final KiePMMLModel kiePMMLModel, final String expectedClass) {
        String fullSourceName =
                String.format(PACKAGE_CLASS_TEMPLATE,
                              kiePMMLModel.getKModulePackageName(),
                              getSanitizedClassName(kiePMMLModel.getName()));
        if (expectedClass.equals(fullSourceName)) {
            if (!(kiePMMLModel instanceof HasSourcesMap)) {
                throw new KiePMMLException(String.format("Expecting %s at this phase",
                                                         HasSourcesMap.class.getCanonicalName()));
            }
            return (HasSourcesMap) kiePMMLModel;
        } else {
            return null;
        }
    }
}
