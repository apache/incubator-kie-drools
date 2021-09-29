/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.alphanetbased;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.ancompiler.CompiledNetworkSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.of;
import static org.kie.dmn.core.compiler.alphanetbased.TableCell.ALPHANETWORK_STATIC_PACKAGE;

public class GeneratedSources {

    // TODO DT-ANC support specific packages for both DMN and Alpha Network classes
    private static final String ANC_PACKAGE = "org.drools.ancompiler";

    private static final Logger logger = LoggerFactory.getLogger(GeneratedSources.class);

    // This is only for debugging purposes and should NEVER be enabled in production
    private static final boolean DUMP_GENERATED_CLASSES = false;

    private final Map<String, String> allGeneratedSources = new HashMap<>();

    private String alphaNetworkClassName = null;
    private Optional<Path> optionalDumpFolder = Optional.empty();

    public void addNewSourceClass(String classNameWithPackage, String classSourceCode) {
        allGeneratedSources.put(classNameWithPackage, classSourceCode);
    }

    public void addNewAlphaNetworkClass(String alphaNetworkClassWithPackage, String toString) {
        addNewSourceClass(alphaNetworkClassWithPackage, toString);
        this.alphaNetworkClassName = alphaNetworkClassWithPackage;
    }

    public DMNCompiledAlphaNetwork newInstanceOfAlphaNetwork(Map<String, Class<?>> compiledClasses) {
        Class<?> inputSetClass = compiledClasses.get(alphaNetworkClassName);
        Object inputSetInstance;
        try {
            inputSetInstance = inputSetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (DMNCompiledAlphaNetwork) inputSetInstance;
    }

    public Map<String, String> getAllGeneratedSources() {
        return Collections.unmodifiableMap(allGeneratedSources);
    }

    public void logGeneratedClasses() {
        if (logger.isDebugEnabled()) {
            for (Map.Entry<String, String> kv : allGeneratedSources.entrySet()) {
                logger.debug("Generated class {}", kv.getKey());
                logger.debug(kv.getValue());
            }
        }
    }

    public void dumpGeneratedClasses() {
        if (DUMP_GENERATED_CLASSES) {
            try {

                Path generatedSources = Paths.get("target", "generated-sources");
                Path currentDirectory = Paths.get("").toAbsolutePath().resolve(generatedSources);
                Path tempDirWithPrefix = Files.createTempDirectory(currentDirectory, alphaNetworkClassName);

                for (Map.Entry<String, String> kv : allGeneratedSources.entrySet()) {

                    Path path = tempDirWithPrefix.resolve(kv.getKey().replace(".", "/") + ".java");
                    Files.createDirectories(path.getParent());

                    final String tempDirPackage = tempDirWithPrefix.getName(8).toString();
                    String javaSource = kv.getValue();

                    // each run will have its own package
                    String withUniquePackageDMN = javaSource.replace(ALPHANETWORK_STATIC_PACKAGE, tempDirPackage + "." + ALPHANETWORK_STATIC_PACKAGE);

                    Files.write(path, withUniquePackageDMN.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Dumped files to: \n\n{}\n", tempDirWithPrefix);
                }

                optionalDumpFolder = of(tempDirWithPrefix);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void dumpGeneratedAlphaNetwork(CompiledNetworkSource compiledNetworkSource) {
        if (DUMP_GENERATED_CLASSES) {
            try {
                Path tempDirWithPrefix = this.optionalDumpFolder.orElseThrow(RuntimeException::new);

                Path path = tempDirWithPrefix.resolve(compiledNetworkSource.getName().replace(".", "/") + ".java");

                Files.createDirectories(path.getParent());

                final String tempDirPackage = tempDirWithPrefix.getName(8).toString();

                String javaSource = compiledNetworkSource.getSource();
                String withUniquePackageANC = javaSource.replace("package " + ANC_PACKAGE, "package " + tempDirPackage + "." + ANC_PACKAGE);

                Files.write(path, withUniquePackageANC.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void putAllGeneratedFEELTestClasses(Map<String, String> unaryTestClasses) {
        allGeneratedSources.putAll(unaryTestClasses);
    }
}
