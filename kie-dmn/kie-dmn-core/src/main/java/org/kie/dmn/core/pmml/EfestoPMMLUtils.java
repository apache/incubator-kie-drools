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
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;

import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.internal.io.ResourceFactory;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.identifiers.PmmlIdFactory;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static org.kie.efesto.common.utils.PackageClassNameUtils.getSanitizedClassName;

@SuppressWarnings("rawtypes")
public class EfestoPMMLUtils {

    private static final Logger logger = LoggerFactory.getLogger(EfestoPMMLUtils.class);

    private static final CompilationManager compilationManager =
            SPIUtils.getCompilationManager(true).orElseThrow(() -> new RuntimeException("Compilation Manager not " +
                                                                                                "available"));

    private EfestoPMMLUtils() {
    }

    public static ModelLocalUriId compilePMML(File pmmlFile, ClassLoader classLoader) {
        try {
            return compilePMML(Files.readString(pmmlFile.toPath()), pmmlFile.getAbsolutePath(), classLoader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ModelLocalUriId compilePMML(String pmmlSource, String locationUri, ClassLoader classLoader) {
        KieMemoryCompiler.MemoryCompilerClassLoader toUse =
                classLoader instanceof KieMemoryCompiler.MemoryCompilerClassLoader ?
                        (KieMemoryCompiler.MemoryCompilerClassLoader) classLoader :
                        new KieMemoryCompiler.MemoryCompilerClassLoader(classLoader);
        EfestoCompilationContext pmmlCompilationContext =
                EfestoCompilationContextUtils.buildWithParentClassLoader(toUse);
        EfestoInputStreamResource toProcess =
                new EfestoInputStreamResource(new ByteArrayInputStream(pmmlSource.getBytes(StandardCharsets.UTF_8)),
                                              locationUri);
        compilationManager.processResource(pmmlCompilationContext, toProcess);
        GeneratedExecutableResource generatedExecutableResource =
                ((GeneratedResources) pmmlCompilationContext.getGeneratedResourcesMap().get("pmml"))
                        .stream()
                        .filter(GeneratedExecutableResource.class::isInstance)
                        .map(GeneratedExecutableResource.class::cast)
                        .findFirst()
                        .orElse(null);
        ModelLocalUriId toReturn = getPmmlModelLocalUriIdFromFullPath(locationUri);
        ContextStorage.putEfestoCompilationSource(toReturn, pmmlSource);
        ContextStorage.putEfestoCompilationContext(toReturn, pmmlCompilationContext);
        return toReturn;
    }

    public static ModelLocalUriId getPmmlModelLocalUriId(Import anImport, Function<String, Reader> relativeResolver) {
        if (relativeResolver != null) {
            return compilePmmlFromRelativeResolver(anImport, relativeResolver);
        } else {
            return getPmmlModelLocalUriIdFromImport(anImport);
        }
    }

    public static ModelLocalUriId compilePmmlFromRelativeResolver(Import i, Function<String, Reader> relativeResolver) {
        String pmmlSource = getStringFromRelativeResolver(i.getLocationURI(), relativeResolver);
        return compilePMML(pmmlSource, i.getLocationURI(), Thread.currentThread().getContextClassLoader());
    }

    public static ModelLocalUriId getPmmlModelLocalUriIdFromImport(Import anImport) {
        String locationURI = anImport.getLocationURI();
        logger.trace("locationURI: {}", locationURI);
        return getPmmlModelLocalUriIdFromFullPath(locationURI);
    }

    public static ModelLocalUriId getRelativePmmlModelLocalUriIdFromImport(Import anImport, Resource dmnResource) {
        String locationURI = anImport.getLocationURI();
        if (isRelative(locationURI)) {
            Path parentPath = getParentPath(dmnResource);
            if (parentPath != null) {
                locationURI = parentPath.resolve(locationURI).toString();
            }
        }
        logger.trace("locationURI: {}", locationURI);
        return getPmmlModelLocalUriIdFromFullPath(locationURI);
    }

    public static ModelLocalUriId getPmmlModelLocalUriIdFromFullPath(String fullFileNamePath) {
        String pathPart;
        String name;
        if (fullFileNamePath.contains(SLASH)) {
            pathPart = fullFileNamePath.substring(0, fullFileNamePath.lastIndexOf(SLASH));
            name = fullFileNamePath.substring(fullFileNamePath.lastIndexOf(SLASH) + 1);
        } else {
            name = pathPart = getFileNameNoSuffix(fullFileNamePath);
        }
        name = getFileNameNoSuffix(name);
        name = getSanitizedClassName(name);
        if (!pathPart.startsWith(SLASH)) {
            pathPart = SLASH + pathPart;
        }
        if (!pathPart.endsWith(SLASH)) {
            pathPart = pathPart + SLASH;
        }
        String fileName = pathPart + name;
        return new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdFactory.class)
                .get(fileName, name);
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

    public static String getFileNameNoSuffix(String fileName) {
        return fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
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