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
package org.kie.kogito.codegen.manager.springboot;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.manager.exceptions.KogitoCodegenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.manager.CompilerHelper.RESOURCES;
import static org.kie.kogito.codegen.manager.CompilerHelper.SOURCES;

public class SpringBootKieConfigurationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootKieConfigurationHelper.class);

    private static final String KIE_SB_CONFIG_FILE_NAME = "kie-spring-boot-config.properties";
    private static final String SPRING_KIE_SB_CONFIGURATION_CLASS_NAME = "SpringBootKieConfiguration";

    private SpringBootKieConfigurationHelper() {
        // Utility class it should not be initialized
    }

    public static Map<String, Collection<GeneratedFile>> generateKieSpringBootConfiguration(KogitoBuildContext context) {
        if (!SpringBootKogitoBuildContext.CONTEXT_NAME.equals(context.name())) {
            throw new KogitoCodegenException("Attempting to add Spring Boot KIE Configuration outside of a Spring Boot Project");
        }

        return Map.of(SOURCES, List.of(generateKieSpringBootConfigurationClass(context)), RESOURCES, List.of(generateKieSpringBootConfigProperties(context)));
    }

    static GeneratedFile generateKieSpringBootConfigurationClass(KogitoBuildContext context) {
        CompilationUnit compilationUnit = parse(context.getClassLoader().getResourceAsStream("class-templates/" + SPRING_KIE_SB_CONFIGURATION_CLASS_NAME + "Template.java"));
        compilationUnit.setPackageDeclaration(context.getPackageName());

        String generatedPath = context.getPackageName().replace(".", "/") + "/" + SPRING_KIE_SB_CONFIGURATION_CLASS_NAME + ".java";

        return new GeneratedFile(GeneratedFileType.SOURCE, generatedPath, compilationUnit.toString());
    }

    static GeneratedFile generateKieSpringBootConfigProperties(KogitoBuildContext context) {
        try (InputStream stream = context.getClassLoader().getResourceAsStream(KIE_SB_CONFIG_FILE_NAME)) {
            if (stream == null) {
                LOGGER.error("Could not find Spring Boot KIE configuration properties file");
                throw new KogitoCodegenException("Could not find Spring Boot KIE configuration properties file");
            }

            return new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE, KIE_SB_CONFIG_FILE_NAME, stream.readAllBytes());
        } catch (Exception ex) {
            if (ex instanceof KogitoCodegenException kogitoCodegenException) {
                throw kogitoCodegenException;
            }
            LOGGER.error("Failed to load KIE Spring Boot Config Properties", ex);
            throw new KogitoCodegenException("Failed to load KIE Spring Boot Config Properties", ex);
        }
    }
}
