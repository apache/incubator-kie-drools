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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.efesto.common.utils.PackageClassNameUtils;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.kie.pmml.compiler.executor.PMMLCompiler;
import org.kie.pmml.compiler.executor.PMMLCompilerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.compiler.service.PMMLCompilerService.getEfestoFinalOutputPMML;

/**
 * Class meant to <b>compile</b> resources
 */
public class PMMLCompilerServicePMMLInputStream {

    private static final Logger logger = LoggerFactory.getLogger(PMMLCompilerServicePMMLInputStream.class.getName());

    private static final PMMLCompiler PMML_COMPILER = new PMMLCompilerImpl();

    private PMMLCompilerServicePMMLInputStream() {
        // Avoid instantiation
    }

    public static List<EfestoCompilationOutput> getEfestoCompilationOutputPMML(EfestoInputStreamResource resource,
                                                                               EfestoCompilationContext efestoCompilationContext) {
        PMMLCompilationContext pmmlContext = getPMMLCompilationContext(resource.getFileName(), efestoCompilationContext);
        return getEfestoCompilationOutputPMML(resource, pmmlContext);
    }

    public static List<EfestoCompilationOutput> getEfestoCompilationOutputPMML(EfestoInputStreamResource resource,
                                                                               PMMLCompilationContext pmmlContext) {
        List<KiePMMLModel> kiePmmlModels =
                getKiePMMLModelsFromInputStreamResourcesWithConfigurationsWithSources(pmmlContext,
                                                                                      Collections.singletonList(resource));
        return getEfestoFinalOutputPMML(kiePmmlModels, resource.getFileName(), pmmlContext);
    }

    static PMMLCompilationContext getPMMLCompilationContext(String fileName, EfestoCompilationContext compilationContext) {
        PMMLCompilationContext toReturn = new PMMLCompilationContextImpl(fileName,
                                                                                   new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader()));
        toReturn.getGeneratedResourcesMap().putAll(compilationContext.getGeneratedResourcesMap());
        return toReturn;
    }

    /**
     * @param pmmlContext
     * @param resources
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    static List<KiePMMLModel> getKiePMMLModelsFromInputStreamResourcesWithConfigurationsWithSources(PMMLCompilationContext pmmlContext,
                                                                                                    Collection<EfestoInputStreamResource> resources) {
        return resources.stream()
                .flatMap(resource -> getKiePMMLModelsFromResourceWithSources(pmmlContext, resource).stream())
                .collect(Collectors.toList());
    }

    /**
     * @param pmmlContext
     * @param resource
     * @return
     */
    static List<KiePMMLModel> getKiePMMLModelsFromResourceWithSources(PMMLCompilationContext pmmlContext,
                                                                      EfestoInputStreamResource resource) {
        String[] classNamePackageName = getFactoryClassNamePackageName(resource);
        String packageName = classNamePackageName[1];
        final List<KiePMMLModel> toReturn = PMML_COMPILER.getKiePMMLModelsWithSources(packageName,
                                                                                      resource.getContent(),
                                                                                      resource.getFileName(),
                                                                                      pmmlContext);
        return toReturn;
    }

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the
     * <b>package</b> name,
     * built starting from the given <code>Resource</code>
     * @param resource
     * @return
     */
    static String[] getFactoryClassNamePackageName(EfestoInputStreamResource resource) {
        String sourcePath = resource.getFileName();
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("Missing required sourcePath in resource " + resource + " -> " + resource.getClass().getName());
        }
        return PackageClassNameUtils.getFactoryClassNamePackageName(PMML_STRING, sourcePath);
    }
}
