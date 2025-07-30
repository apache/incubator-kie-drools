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
package org.kie.kogito.codegen.manager.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.model.codegen.project.RuleCodegen;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.SourceFileCodegenBindNotifier;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeGenManagerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenManagerUtil.class);
    public static final PathMatcher DRL_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.drl");

    public enum Framework {
        QUARKUS,
        SPRING,
        NONE
    }

    public record ProjectParameters(CodeGenManagerUtil.Framework framework,
            String generateDecisions,
            String generatePredictions,
            String generateProcesses,
            String generateRules,
            boolean persistence) {
    }

    public static KogitoBuildContext discoverKogitoRuntimeContext(ClassLoader projectClassLoader,
            Path projectDir,
            KogitoGAV kogitoGAV,
            ProjectParameters projectParameters,
            Predicate<String> classAvaialbilityPredicate) {
        AppPaths appPaths = AppPaths.fromProjectDir(projectDir);
        KogitoBuildContext context = contextBuilder(projectParameters.framework)
                .withClassAvailabilityResolver(classAvaialbilityPredicate)
                .withClassSubTypeAvailabilityResolver(classSubTypeAvailabilityResolver(projectClassLoader))
                .withApplicationProperties(appPaths.getResourceFiles())
                .withPackageName(DroolsModelBuildContext.DEFAULT_PACKAGE_NAME)
                .withClassLoader(projectClassLoader)
                .withAppPaths(appPaths)
                .withGAV(kogitoGAV)
                .withSourceFileProcessBindNotifier(new SourceFileCodegenBindNotifier())
                .build();

        additionalProperties(context, projectParameters);
        return context;
    }

    static KogitoBuildContext.Builder contextBuilder(CodeGenManagerUtil.Framework framework) {
        return switch (framework) {
            case QUARKUS -> QuarkusKogitoBuildContext.builder();
            case SPRING -> SpringBootKogitoBuildContext.builder();
            default -> JavaKogitoBuildContext.builder();
        };
    }

    static Optional<String> classToCheckForREST(CodeGenManagerUtil.Framework framework) {
        return switch (framework) {
            case QUARKUS -> Optional.of(QuarkusKogitoBuildContext.QUARKUS_REST);
            case SPRING -> Optional.of(SpringBootKogitoBuildContext.SPRING_REST);
            default -> Optional.empty();
        };
    }

    static Optional<String> classToCheckForDI(CodeGenManagerUtil.Framework framework) {
        return switch (framework) {
            case QUARKUS -> Optional.of(QuarkusKogitoBuildContext.QUARKUS_DI);
            case SPRING -> Optional.of(SpringBootKogitoBuildContext.SPRING_DI);
            default -> Optional.empty();
        };
    }

    static void overwritePropertiesIfNeeded(KogitoBuildContext context, ProjectParameters projectParameters) {
        overwritePropertyIfNeeded(context, RuleCodegen.GENERATOR_NAME, projectParameters.generateRules());
        overwritePropertyIfNeeded(context, ProcessCodegen.GENERATOR_NAME, projectParameters.generateProcesses());
        overwritePropertyIfNeeded(context, PredictionCodegen.GENERATOR_NAME, projectParameters.generatePredictions());
        overwritePropertyIfNeeded(context, DecisionCodegen.GENERATOR_NAME, projectParameters.generateDecisions());
        overwritePropertyIfNeeded(context, PersistenceGenerator.GENERATOR_NAME, Boolean.toString(projectParameters.persistence()));
    }

    static void overwritePropertyIfNeeded(KogitoBuildContext context, String generatorName, String propertyValue) {
        if (propertyValue != null && !propertyValue.isEmpty()) {
            context.setApplicationProperty(Generator.CONFIG_PREFIX + generatorName, propertyValue);
        }
    }

    static void additionalProperties(KogitoBuildContext context, ProjectParameters projectParameters) {
        classToCheckForREST(projectParameters.framework).ifPresent(restClass -> {
            if (!context.hasClassAvailable(restClass)) {
                LOGGER.info("Disabling REST generation because class '{}' is not available", restClass);
                context.setApplicationProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "false");
            }
        });
        classToCheckForDI(projectParameters.framework).ifPresent(diClass -> {
            if (!context.hasClassAvailable(diClass)) {
                LOGGER.info("Disabling dependency injection generation because class '{}", diClass);
                context.setApplicationProperty(DroolsModelBuildContext.KOGITO_GENERATE_DI, "false");
            }
        });

        overwritePropertiesIfNeeded(context, projectParameters);
    }

    public static Reflections getReflections(ClassLoader projectClassLoader) {
        URLClassLoader urlClassLoader = (URLClassLoader) projectClassLoader;
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addUrls(urlClassLoader.getURLs());
        builder.addClassLoaders(urlClassLoader);
        return new Reflections(builder);
    }

    static Predicate<Class<?>> classSubTypeAvailabilityResolver(ClassLoader projectClassLoader) {
        return clazz -> getReflections(projectClassLoader).getSubTypesOf(clazz).stream()
                .anyMatch(c -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()));
    }

    public static boolean isClassNameInUrlClassLoader(URL[] urls, String className) {
        try (URLClassLoader cl = new URLClassLoader(urls)) {
            cl.loadClass(className);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * It deletes all DRL files in a given Path directory
     */
    public static void deleteDrlFiles(Path directory) {
        try (final Stream<Path> drlFiles = Files.find(directory,
                Integer.MAX_VALUE,
                (p, f) -> DRL_FILE_MATCHER.matches(p))) {
            drlFiles.forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Error during .drl files deletion", e);
        }
    }
}
