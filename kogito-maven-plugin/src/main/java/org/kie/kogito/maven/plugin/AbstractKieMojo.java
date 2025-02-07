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
package org.kie.kogito.maven.plugin;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFileWriter;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.decision.DecisionCodegen;
import org.kie.kogito.codegen.prediction.PredictionCodegen;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.rules.RuleCodegen;
import org.kie.kogito.maven.plugin.util.MojoUtil;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

public abstract class AbstractKieMojo extends AbstractMojo {

    protected static final GeneratedFileWriter.Builder generatedFileWriterBuilder = GeneratedFileWriter.builder("kogito", "kogito.codegen.resources.directory", "kogito.codegen.sources.directory");

    @Parameter(required = true, defaultValue = "${project.basedir}")
    protected File projectDir;

    @Parameter
    protected Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    protected File outputDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    protected File baseDir;

    @Parameter(property = "kogito.codegen.persistence")
    protected boolean persistence;

    @Parameter(property = "kogito.codegen.rules")
    protected String generateRules;

    @Parameter(property = "kogito.codegen.processes")
    protected String generateProcesses;

    @Parameter(property = "kogito.codegen.decisions")
    protected String generateDecisions;

    @Parameter(property = "kogito.codegen.predictions")
    protected String generatePredictions;

    private Reflections reflections;

    protected void setSystemProperties(Map<String, String> properties) {

        if (properties != null) {
            getLog().debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                System.setProperty(property.getKey(), property.getValue());
            }
            getLog().debug("Configured system properties were successfully set.");
        }
    }

    protected KogitoBuildContext discoverKogitoRuntimeContext(ClassLoader classLoader) {
        AppPaths appPaths = AppPaths.fromProjectDir(projectDir.toPath());
        KogitoBuildContext context = contextBuilder()
                .withClassAvailabilityResolver(this::hasClassOnClasspath)
                .withClassSubTypeAvailabilityResolver(classSubTypeAvailabilityResolver())
                .withApplicationProperties(appPaths.getResourceFiles())
                .withPackageName(appPackageName())
                .withClassLoader(classLoader)
                .withAppPaths(appPaths)
                .withGAV(new KogitoGAV(project.getGroupId(), project.getArtifactId(), project.getVersion()))
                .build();

        additionalProperties(context);
        return context;
    }

    protected Reflections getReflections() throws MojoExecutionException {
        if (reflections == null) {
            URLClassLoader classLoader = (URLClassLoader) projectClassLoader();
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addUrls(classLoader.getURLs());
            builder.addClassLoaders(classLoader);
            reflections = new Reflections(builder);
        }
        return reflections;
    }

    protected Reflections getReflections(ClassLoader toAdd) throws MojoExecutionException {
        URLClassLoader classLoader = (URLClassLoader) projectClassLoader();
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addUrls(classLoader.getURLs());
        builder.addClassLoaders(classLoader, toAdd);
        return new Reflections(builder);
    }

    protected Predicate<Class<?>> classSubTypeAvailabilityResolver() {
        return clazz -> {
            try {
                return getReflections().getSubTypesOf(clazz).stream()
                        .anyMatch(c -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()));
            } catch (MojoExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    protected ClassLoader projectClassLoader() throws MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return MojoUtil.createProjectClassLoader(contextClassLoader,
                project,
                outputDirectory,
                null);
    }

    protected String appPackageName() {
        return DroolsModelBuildContext.DEFAULT_PACKAGE_NAME;
    }

    private void additionalProperties(KogitoBuildContext context) {

        classToCheckForREST().ifPresent(restClass -> {
            if (!context.hasClassAvailable(restClass)) {
                getLog().info("Disabling REST generation because class '" + restClass + "' is not available");
                context.setApplicationProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "false");
            }
        });
        classToCheckForDI().ifPresent(diClass -> {
            if (!context.hasClassAvailable(diClass)) {
                getLog().info("Disabling dependency injection generation because class '" + diClass + "' is not available");
                context.setApplicationProperty(DroolsModelBuildContext.KOGITO_GENERATE_DI, "false");
            }
        });

        overwritePropertiesIfNeeded(context);
    }

    void overwritePropertiesIfNeeded(KogitoBuildContext context) {
        overwritePropertyIfNeeded(context, RuleCodegen.GENERATOR_NAME, generateRules);
        overwritePropertyIfNeeded(context, ProcessCodegen.GENERATOR_NAME, generateProcesses);
        overwritePropertyIfNeeded(context, PredictionCodegen.GENERATOR_NAME, generatePredictions);
        overwritePropertyIfNeeded(context, DecisionCodegen.GENERATOR_NAME, generateDecisions);
        overwritePropertyIfNeeded(context, PersistenceGenerator.GENERATOR_NAME, Boolean.toString(persistence));
    }

    static void overwritePropertyIfNeeded(KogitoBuildContext context, String generatorName, String propertyValue) {
        if (propertyValue != null && !propertyValue.isEmpty()) {
            context.setApplicationProperty(Generator.CONFIG_PREFIX + generatorName, propertyValue);
        }
    }

    private KogitoBuildContext.Builder contextBuilder() {
        switch (discoverFramework()) {
            case QUARKUS:
                return QuarkusKogitoBuildContext.builder();
            case SPRING:
                return SpringBootKogitoBuildContext.builder();
            default:
                return JavaKogitoBuildContext.builder();
        }
    }

    private Optional<String> classToCheckForREST() {
        switch (discoverFramework()) {
            case QUARKUS:
                return Optional.of(QuarkusKogitoBuildContext.QUARKUS_REST);
            case SPRING:
                return Optional.of(SpringBootKogitoBuildContext.SPRING_REST);
            default:
                return Optional.empty();
        }
    }

    private Optional<String> classToCheckForDI() {
        switch (discoverFramework()) {
            case QUARKUS:
                return Optional.of(QuarkusKogitoBuildContext.QUARKUS_DI);
            case SPRING:
                return Optional.of(SpringBootKogitoBuildContext.SPRING_DI);
            default:
                return Optional.empty();
        }
    }

    private enum Framework {
        QUARKUS,
        SPRING,
        NONE
    }

    private Framework discoverFramework() {
        if (hasDependency("quarkus")) {
            return Framework.QUARKUS;
        }

        if (hasDependency("spring")) {
            return Framework.SPRING;
        }

        return Framework.NONE;
    }

    private boolean hasDependency(String dependency) {
        return project.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains(dependency));
    }

    private boolean hasClassOnClasspath(String className) {
        try {
            Set<Artifact> elements = project.getArtifacts();
            URL[] urls = new URL[elements.size()];

            int i = 0;
            Iterator<Artifact> it = elements.iterator();
            while (it.hasNext()) {
                Artifact artifact = it.next();

                urls[i] = artifact.getFile().toURI().toURL();
                i++;
            }
            try (URLClassLoader cl = new URLClassLoader(urls)) {
                cl.loadClass(className);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected File getSourcesPath() {
        // using runtime BT instead of static AppPaths.MAVEN to allow
        // invocation from GRADLE
        return Path.of(baseDir.getAbsolutePath(), AppPaths.BT.GENERATED_SOURCES_PATH.toString()).toFile();
    }

    protected GeneratedFileWriter getGeneratedFileWriter() {
        return generatedFileWriterBuilder
                .build(Path.of(baseDir.getAbsolutePath()));
    }
}
