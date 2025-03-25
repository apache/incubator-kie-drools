/*
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
package org.kie.dmn.core.pmml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;
import org.kie.api.io.Resource;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Import;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;

import org.kie.internal.io.ResourceFactory;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static org.kie.efesto.common.utils.PackageClassNameUtils.getSanitizedClassName;

public class EfestoPMMLUtils {


    private static final Logger logger = LoggerFactory.getLogger(EfestoPMMLUtils.class );

    private static final CompilationManager compilationManager =
            SPIUtils.getCompilationManager(true).orElseThrow(() -> new RuntimeException("Compilation Manager not " +
                                                                                                "available"));

    private EfestoPMMLUtils() {
    }

    @SuppressWarnings("rawtypes")
    public static ModelLocalUriId compilePMML(File pmmlFile, ClassLoader classLoader) {
        KieMemoryCompiler.MemoryCompilerClassLoader toUse =
                classLoader instanceof KieMemoryCompiler.MemoryCompilerClassLoader ? (KieMemoryCompiler.MemoryCompilerClassLoader) classLoader : new KieMemoryCompiler.MemoryCompilerClassLoader(classLoader);
        EfestoCompilationContext pmmlCompilationContext = new PMMLCompilationContextImpl(pmmlFile.getName(),
                                                                                         toUse);
        compilationManager.processResource(pmmlCompilationContext, new EfestoFileResource(pmmlFile));
        GeneratedExecutableResource generatedExecutableResource =
                ((GeneratedResources) pmmlCompilationContext.getGeneratedResourcesMap().get("pmml"))
                        .stream()
                        .filter(GeneratedExecutableResource.class::isInstance)
                        .map(GeneratedExecutableResource.class::cast)
                        .findFirst()
                        .orElse(null);
        try {
            // TODO gcardosi fix
            ModelLocalUriId toReturn = getModelLocalUriId(pmmlFile.getName(), pmmlFile.getName());
            ContextStorage.putEfestoCompilationSource(toReturn, Files.readString(pmmlFile.toPath()));
            ContextStorage.putEfestoCompilationContext(toReturn, pmmlCompilationContext);
            System.out.println(pmmlCompilationContext.getGeneratedResourcesMap().get("pmml"));
            return toReturn;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ModelLocalUriId resolveRelativeResource(DMNModelImpl model, Import i, DMNModelInstrumentedBase node, Function<String, Reader> relativeResolver) {
        if (relativeResolver != null) {
            return compilePmmlResource(i, relativeResolver);
        } else if (model.getResource() != null) {
            return pmmlImportResource(model, i, node);
        }
        throw new UnsupportedOperationException("Unable to determine relative Resource for import named: " + i.getName());
    }

    public static String getFileNameNoSuffix(String fileName) {
        return fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
    }

    public static ModelLocalUriId compilePmmlResource(Import i,  Function<String, Reader> relativeResolver) {
        throw new UnsupportedOperationException("Not supported yet.");
//        Reader reader = relativeResolver.apply(i.getLocationURI());
//        return ResourceFactory.newReaderResource(reader);
    }

    public static ModelLocalUriId pmmlImportResource(DMNModelImpl model, Import i, DMNModelInstrumentedBase node) {
        String locationURI = i.getLocationURI();
        logger.trace("locationURI: {}", locationURI);
        return getModelLocalUriId(locationURI, locationURI);
    }

    public static ModelLocalUriId getModelLocalUriId(String fileName, String modelName) {
        String path = "/pmml/" + getFileNameNoSuffix(fileName) + SLASH + getSanitizedClassName(modelName);
        LocalUri parsed = LocalUri.parse(path);
        return new ModelLocalUriId(parsed);
    }

    public static Resource getPmmlResource(ModelLocalUriId pmmlModelLocalUriID) {
        String pmmlSource = getPmmlSource(pmmlModelLocalUriID);
        return  ResourceFactory.newByteArrayResource(pmmlSource.getBytes(StandardCharsets.UTF_8));
    }

    public static String getPmmlSource(ModelLocalUriId pmmlModelLocalUriID) {
        return  ContextStorage.getEfestoCompilationSource(pmmlModelLocalUriID);
    }
}