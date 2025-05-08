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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import org.drools.io.ClassPathResource;
import org.drools.io.FileSystemResource;
import org.kie.api.io.Resource;
import org.kie.dmn.model.api.Import;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;

import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.internal.io.ResourceFactory;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.FileNameUtils.getFileName;
import static org.kie.efesto.common.api.utils.FileNameUtils.removeSuffix;
import static org.kie.efesto.common.utils.PackageClassNameUtils.getSanitizedClassName;

@SuppressWarnings("rawtypes")
public class EfestoPMMLUtils {

    private static final Logger logger = LoggerFactory.getLogger(EfestoPMMLUtils.class);

    private static final CompilationManager compilationManager =
            SPIUtils.getCompilationManager(true).orElseThrow(() -> new RuntimeException("Compilation Manager not " +
                                                                                                "available"));

    private EfestoPMMLUtils() {
    }

    /**
     * Compile the given <code>File</code> as PMML model and store it under automatically-generated
     * <code>LocalComponentIdPmml</code>
     * @param pmmlFile
     * @param modelName
     * @param classLoader
     * @return
     * @see EfestoPMMLUtils#getPmmlModelLocalUriId(String, String) for information about
     * <code>LocalComponentIdPmml</code> generation
     */
    public static ModelLocalUriId compilePMML(File pmmlFile, String modelName, ClassLoader classLoader) {
        ModelLocalUriId toReturn = getPmmlModelLocalUriId(pmmlFile.getName(), modelName);
        compilePMMLAtGivenLocalComponentIdPmml(pmmlFile, toReturn, classLoader);
        return toReturn;
    }

    /**
     * Compile the given <b>pmml source</b> as PMML model and store it under automatically-generated
     * <code>LocalComponentIdPmml</code>
     *
     * @param pmmlSource
     * @param fileName
     * @param modelName
     * @param classLoader
     * @return
     * @see EfestoPMMLUtils#getPmmlModelLocalUriId(String, String) for information about
     * <code>ModelLocalUriId</code> generation
     */
    public static ModelLocalUriId compilePMML(String pmmlSource, String fileName, String modelName,
                                                   ClassLoader classLoader) {
        ModelLocalUriId toReturn = getPmmlModelLocalUriId(fileName, modelName);
        compilePMMLAtGivenLocalComponentIdPmml(pmmlSource, fileName, toReturn, classLoader);
        return toReturn;
    }

    /**
     * Compile the given <code>File</code> as PMML model and store it under the provided
     * <code>ModelLocalUriId</code>
     * @param pmmlFile
     * @param pmmlModelLocalUriId
     * @param classLoader
     */
    public static void compilePMMLAtGivenLocalComponentIdPmml(File pmmlFile, ModelLocalUriId pmmlModelLocalUriId, ClassLoader classLoader) {
        try {
            compilePMMLAtGivenLocalComponentIdPmml(Files.readString(pmmlFile.toPath()), pmmlFile.getName(),
                                                   pmmlModelLocalUriId, classLoader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compile the given <b>pmml source</b> as PMML model and store it under the provided
     * <code>LocalComponentIdPmml</code>
     *
     * @param pmmlSource
     * @param fileName
     * @param pmmlModelLocalUriId
     * @param classLoader
     */
    public static void compilePMMLAtGivenLocalComponentIdPmml(String pmmlSource, String fileName,
                                                              ModelLocalUriId pmmlModelLocalUriId,
                                                              ClassLoader classLoader) {
        KieMemoryCompiler.MemoryCompilerClassLoader toUse =
                classLoader instanceof KieMemoryCompiler.MemoryCompilerClassLoader ?
                        (KieMemoryCompiler.MemoryCompilerClassLoader) classLoader :
                        new KieMemoryCompiler.MemoryCompilerClassLoader(classLoader);
        EfestoCompilationContext pmmlCompilationContext =
                EfestoCompilationContextUtils.buildWithParentClassLoader(toUse);
        EfestoInputStreamResource toProcess =
                new EfestoInputStreamResource(new ByteArrayInputStream(pmmlSource.getBytes(StandardCharsets.UTF_8)),
                                              fileName);
        compilationManager.processResource(pmmlCompilationContext, toProcess);
        GeneratedExecutableResource generatedExecutableResource =
                ((GeneratedResources) pmmlCompilationContext.getGeneratedResourcesMap().get("pmml"))
                        .stream()
                        .filter(GeneratedExecutableResource.class::isInstance)
                        .map(GeneratedExecutableResource.class::cast)
                        .findFirst()
                        .orElse(null);
        ContextStorage.putEfestoCompilationSource(pmmlModelLocalUriId, pmmlSource);
        ContextStorage.putEfestoCompilationContext(pmmlModelLocalUriId, pmmlCompilationContext);
    }

    /**
     * Retrieve a <code>LocalComponentIdPmml</code>
     * @param anImport
     * @param modelName
     * @param relativeResolver
     * @return
     */
    public static ModelLocalUriId getPmmlModelLocalUriId(Import anImport, String modelName, Function<String,
            Reader> relativeResolver) {
        ModelLocalUriId toReturn = getPmmlModelLocalUriIdFromImport(anImport, modelName);
        if (relativeResolver != null && !isModelLocalUriIdPresent(toReturn)) {
            toReturn = compilePmmlFromRelativeResolver(anImport, modelName, relativeResolver);
        }
        return toReturn;
    }

    public static ModelLocalUriId compilePmmlFromRelativeResolver(Import i, String modelName, Function<String,
            Reader> relativeResolver) {
        String pmmlSource = getStringFromRelativeResolver(i.getLocationURI(), relativeResolver);
        return compilePMML(pmmlSource, i.getLocationURI(), modelName, Thread.currentThread().getContextClassLoader());
    }

    public static ModelLocalUriId getPmmlModelLocalUriIdFromImport(Import anImport, String modelName) {
        String locationURI = anImport.getLocationURI();
        logger.trace("locationURI: {}", locationURI);
        return getPmmlModelLocalUriId(locationURI, modelName);
    }

    public static ModelLocalUriId getRelativePmmlModelLocalUriIdFromImport(Import anImport, String pmmlModelName, Resource dmnResource) {
        String locationURI = anImport.getLocationURI();
        if (isRelative(locationURI)) {
            Path parentPath = getParentPath(dmnResource);
            if (parentPath != null) {
                locationURI = parentPath.resolve(locationURI).toString();
            }
        }
        logger.trace("locationURI: {}", locationURI);
        return getPmmlModelLocalUriId(locationURI, pmmlModelName);
    }

    /**
     * This method consider the actual file name and the model name for <code>ModelLocalUriId</code> instantiation
     * @param pathPart
     * @return
     */
    public static ModelLocalUriId getPmmlModelLocalUriId(String pathPart, String modelName) {
        String fileName = getFileName(pathPart);
        fileName = removeSuffix(fileName);
        String name = getFileName(modelName);
        name = getSanitizedClassName(name);
        LocalUri localUri = LocalUri.Root.append("pmml").append(fileName).append(name);
        return new ModelLocalUriId(localUri);
    }

    /**
     * Retrieves a <code>ByteArrayResource</code> of the <code>source</code> stored with the given
     * <code>ModelLocalUriId</code>
     * @param pmmlModelLocalUriID
     * @return
     */
    public static Resource getPmmlResourceFromContextStorage(ModelLocalUriId pmmlModelLocalUriID) {
        String pmmlSource = getPmmlSourceFromContextStorage(pmmlModelLocalUriID);
        return ResourceFactory.newByteArrayResource(pmmlSource.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Retrieves the <code>source</code> stored with the given <code>ModelLocalUriId</code>
     * @param pmmlModelLocalUriID
     * @return
     */
    public static String getPmmlSourceFromContextStorage(ModelLocalUriId pmmlModelLocalUriID) {
        return ContextStorage.getEfestoCompilationSource(pmmlModelLocalUriID);
    }

    static boolean isModelLocalUriIdPresent(ModelLocalUriId toCheck) {
        return ContextStorage.getAllModelLocalUriIdFromCompilationContext().contains(toCheck);
    }

    static Path getParentPath(Resource dmnResource) {
        File parentDir = null;
        if (dmnResource instanceof FileSystemResource fileSystemResource && fileSystemResource.getFile() != null) {
            parentDir = fileSystemResource.getFile().getParentFile();
        } else if (dmnResource instanceof ClassPathResource classPathResource) {
            try {
                URL resourceUrl = classPathResource.getURL();
                if (resourceUrl != null && resourceUrl.getFile() != null) {
                    parentDir = new File(resourceUrl.getFile()).getParentFile();
                }
            } catch (Exception e) {
                logger.warn("Failed to retrieve the URL from ClassPathResource {}", classPathResource, e);
            }
        }
        return parentDir != null ? parentDir.toPath() : null;
    }

    /**
     * Return <code>true</code> if the given <code>String</code> represent a relative path, <code>false</code> otherwise
     * @param toParse
     * @return
     */
    static boolean isRelative(String toParse) {
        try {
            URI uri = new URI(toParse);
            return uri.getScheme() == null && !Paths.get(toParse).isAbsolute();
        } catch (URISyntaxException e) {
            return !Paths.get(toParse).isAbsolute();
        }
    }

    static String getStringFromRelativeResolver(String key, Function<String, Reader> relativeResolver) {
        try (Reader toRead = relativeResolver.apply(key)) {
            char[] arr = new char[8 * 1024];
            StringBuilder toReturn = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = toRead.read(arr, 0, arr.length)) != -1) {
                toReturn.append(arr, 0, numCharsRead);
            }
            return toReturn.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}