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

import org.drools.ancompiler.CompiledNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.alphanetbased.TableCell.ALPHANETWORK_STATIC_PACKAGE;

public class GeneratedSources {

    // TODO DT-ANC https://issues.redhat.com/browse/DROOLS-6620
    private static final String ANC_PACKAGE = "org.drools.ancompiler";

    private static final Logger logger = LoggerFactory.getLogger(GeneratedSources.class);

    // This is only for debugging purposes and should NEVER be enabled in production
    private static final boolean DUMP_GENERATED_CLASSES = false;

    private final Map<String, String> allGeneratedSources = new HashMap<>();

    private String alphaNetworkClassName = null;

    public void addNewSourceClass(String classNameWithPackage, String classSourceCode) {
        allGeneratedSources.put(classNameWithPackage, classSourceCode);
    }

    public void addNewSourceClasses(Map<String, String> sourceClasses) {
        allGeneratedSources.putAll(sourceClasses);
    }

    public void addNewAlphaNetworkClass(String alphaNetworkClassWithPackage, String toString) {
        addNewSourceClass(alphaNetworkClassWithPackage, toString);
        this.alphaNetworkClassName = alphaNetworkClassWithPackage;
    }

    public DMNAlphaNetworkEvaluator newInstanceOfAlphaNetwork(Map<String, Class<?>> compiledClasses,
                                                              CompiledNetwork compiledNetwork,
                                                              AlphaNetworkEvaluationContext alphaNetworkEvaluationContext) {
        Class<?> inputSetClass = compiledClasses.get(alphaNetworkClassName);
        Object inputSetInstance;
        try {
            inputSetInstance = inputSetClass
                    .getDeclaredConstructor(CompiledNetwork.class, AlphaNetworkEvaluationContext.class)
                    .newInstance(compiledNetwork, alphaNetworkEvaluationContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (DMNAlphaNetworkEvaluator) inputSetInstance;
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

                    javaSource = javaSource.replace("package " + ANC_PACKAGE, "package " + tempDirPackage + "." + ANC_PACKAGE);
                    javaSource = javaSource.replace("= " + ANC_PACKAGE, "= " + tempDirPackage + "." + ANC_PACKAGE);
                    javaSource = javaSource.replace(ALPHANETWORK_STATIC_PACKAGE, tempDirPackage + "." + ALPHANETWORK_STATIC_PACKAGE);


                    Files.write(path, javaSource.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Dumped files to: \n\n{}\n", tempDirWithPrefix);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void putAllGeneratedFEELTestClasses(Map<String, String> unaryTestClasses) {
        allGeneratedSources.putAll(unaryTestClasses);
    }
}
