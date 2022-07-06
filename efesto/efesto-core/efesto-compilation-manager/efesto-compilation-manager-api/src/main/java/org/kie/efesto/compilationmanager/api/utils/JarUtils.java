/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.compilationmanager.api.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.api.utils.JSONUtils;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;

public class JarUtils {

    private JarUtils() {}

    public static ClassLoader createStoredJarClassLoader(String modelType) {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        IndexFile toSearch = new IndexFile(modelType);
        Optional<IndexFile> optIndexFile = getFileFromFileName(toSearch.getName()).map(IndexFile::new);
        if (!optIndexFile.isPresent()) {
            return parentClassLoader;
        }
        IndexFile indexFile = optIndexFile.get();
        Path jarPath = getJarPath(indexFile);
        try {
            return new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, parentClassLoader);
        } catch (MalformedURLException e) {
            throw new KieEfestoCommonException(e);
        }
    }

    public static void createJarFiles(Collection<IndexFile> indexFiles, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        for (IndexFile indexFile : indexFiles) {
            Path jarPath = getJarPath(indexFile);
            JarUtils.createJarFile(jarPath, indexFile, memoryCompilerClassLoader);
        }
    }

    private static Path getJarPath(IndexFile indexFile) {
        return Paths.get(indexFile.getParent(), "IndexFile." + indexFile.getModel() + ".jar");
    }

    public static void createJarFile(Path path, IndexFile indexFile, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        Manifest manifest = new Manifest();
        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(path.toFile()), manifest)) {
            Map<String, byte[]> classMap = getCodeFromIndexFile(indexFile, memoryCompilerClassLoader);
            classMap.forEach((className, byteCode) -> {
                String entryName = className.replace(".", "/") + ".class";
                JarEntry entry = new JarEntry(entryName);
                try {
                    out.putNextEntry(entry);
                    out.write(byteCode);
                    out.closeEntry();
                } catch (IOException e) {
                    throw new KieEfestoCommonException(String.format("Failed to create jar %s", path), e);
                }
            });
        } catch (IOException e) {
            throw new KieEfestoCommonException(String.format("Failed to create jar %s", path), e);
        }

    }

    private static Map<String, byte[]> getCodeFromIndexFile(IndexFile indexFile,
                                                            KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        List<String> generatedClasses = getGeneratedClassesFromIndexFile(indexFile);
        return generatedClasses.stream()
                               .filter(memoryCompilerClassLoader::contains)
                               .collect(Collectors.toMap(fullClassName -> fullClassName,
                                                         fullClassName -> getMappedCode(fullClassName, memoryCompilerClassLoader)));
    }

    private static byte[] getMappedCode(String fullClassName, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        byte[] toReturn = memoryCompilerClassLoader.getCode(fullClassName);
        if (toReturn == null) {
            throw new KieEfestoCommonException(String.format("Failed to found %s in %s", fullClassName, memoryCompilerClassLoader));
            //            LOGGER.info(String.format("Failed to found %s in %s", fullClassName, memoryCompilerClassLoader));
        }
        return toReturn;
    }

    private static List<String> getGeneratedClassesFromIndexFile(IndexFile indexFile) {
        GeneratedResources generatedResources = getGeneratedResources(indexFile);
        return generatedResources.stream()
                                 .filter(GeneratedClassResource.class::isInstance)
                                 .map(GeneratedClassResource.class::cast)
                                 .map(GeneratedClassResource::getFullClassName)
                                 .collect(Collectors.toList());
    }

    private static GeneratedResources getGeneratedResources(IndexFile indexFile) {
        try {
            return JSONUtils.getGeneratedResourcesObject(indexFile);
        } catch (IOException e) {
            throw new KieEfestoCommonException("Failed to get GeneratedResources from index file " + indexFile, e);
        }
    }
}
