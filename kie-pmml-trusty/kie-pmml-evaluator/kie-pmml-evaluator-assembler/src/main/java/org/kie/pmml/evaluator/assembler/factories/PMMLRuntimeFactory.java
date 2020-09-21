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
package org.kie.pmml.evaluator.assembler.factories;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.FileSystemResource;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Factory</b> class to hide implementation details to end user
 */
public class PMMLRuntimeFactory {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeFactory.class);

    private PMMLRuntimeFactory() {
        // Avoid instantiation
    }

    public static PMMLRuntime getPMMLRuntime(String kbaseName, String fileName) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder();
        File file = getFile(fileName);
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        new PMMLAssemblerService().addResource(kbuilderImpl, fileSystemResource, ResourceType.PMML, null);
        InternalKnowledgeBase kieBase = KnowledgeBaseFactory.newKnowledgeBase(kbaseName, new RuleBaseConfiguration());
        kieBase.addPackages( kbuilderImpl.getKnowledgePackages() );
        return getPMMLRuntime(kieBase);
    }


    private static PMMLRuntime getPMMLRuntime(KieBase kieBase) {
        final KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieBase);
        return kieRuntimeFactory.get(PMMLRuntime.class);
    }

    /**
     * Retrieve the <code>File</code> with the given <b>fileName</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    private static File getFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return getResourcesByExtension(extension)
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Scan into classpath folders to find resources with the required extension
     * @param extension to find
     * @return stream of matching resources
     */
    private static Stream<File> getResourcesByExtension(String extension) {
        return Arrays.stream(getClassPathElements())
                .flatMap(elem -> internalGetResources(elem, Pattern.compile(".*\\." + extension + "$")));
    }

    /**
     * Scan folder to find resources that match with pattern
     * @param directory where to start the search
     * @param pattern to find
     * @return stream of matching resources
     * @throws IOException
     */
    private static Stream<File> getResourcesFromDirectory(File directory, Pattern pattern) {
        if (directory == null || directory.listFiles() == null) {
            return Stream.empty();
        }
        return Arrays.stream(Objects.requireNonNull(directory.listFiles())).flatMap(
                elem -> {
                    if (elem.isDirectory()) {
                        return getResourcesFromDirectory(elem, pattern);
                    } else {
                        try {
                            if (pattern.matcher(elem.getCanonicalPath()).matches()) {
                                return Stream.of(elem);
                            }
                        } catch (final IOException e) {
                            logger.error("Failed top retrieve resources from directory " + directory.getAbsolutePath() + " with pattern " + pattern.pattern(), e);
                        }
                    }
                    return Stream.empty();
                });
    }

    private static String[] getClassPathElements() {
        return System.getProperty("java.class.path", ".").split(System.getProperty("path.separator"));
    }

    /**
     * This method is internal because it works only with folder to explore (classPath folder) and not with exact paths
     * @param path to folder or jar
     * @param pattern to find
     * @return stream of matching resources
     */
    private static Stream<File> internalGetResources(String path, Pattern pattern) {
        final File file = new File(path);
        if (!file.isDirectory()) {
            return Stream.empty();
        }
        return getResourcesFromDirectory(file, pattern);
    }

}
