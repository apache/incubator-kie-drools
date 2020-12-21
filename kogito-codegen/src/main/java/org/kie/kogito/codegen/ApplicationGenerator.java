/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.drools.core.util.StringUtils;
import org.kie.kogito.codegen.metadata.Labeler;
import org.kie.kogito.codegen.metadata.MetaDataWriter;
import org.kie.kogito.codegen.metadata.PrometheusLabeler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationGenerator.class);

    public static final String DEFAULT_GROUP_ID = "org.kie.kogito";
    public static final String DEFAULT_PACKAGE_NAME = "org.kie.kogito.app";
    public static final String APPLICATION_CLASS_NAME = "Application";

    private final String packageName;
    private final File targetDirectory;

    private final ApplicationContainerGenerator applicationMainGenerator;
    private ApplicationConfigGenerator configGenerator;
    private List<Generator> generators = new ArrayList<>();
    private Map<Class, Labeler> labelers = new HashMap<>();

    private GeneratorContext context;
    private ClassLoader classLoader;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    public ApplicationGenerator(GeneratorContext context, String packageName, File targetDirectory) {
        this.context = context;
        this.packageName = packageName;
        this.targetDirectory = targetDirectory;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.applicationMainGenerator = new ApplicationContainerGenerator(context.getBuildContext(), packageName);

        this.configGenerator = new ApplicationConfigGenerator(context.getBuildContext(), packageName);
        this.configGenerator.withAddons(loadAddonList());
    }

    public String targetCanonicalName() {
        return this.packageName + "." + APPLICATION_CLASS_NAME;
    }

    private String getFilePath(String className) {
        return (this.packageName + "." + className).replace('.', '/') + ".java";
    }

    public ApplicationGenerator withAddons(AddonsConfig addonsConfig) {
        if (addonsConfig.usePrometheusMonitoring()) {
            this.labelers.put(PrometheusLabeler.class, new PrometheusLabeler());
        }
        this.addonsConfig = addonsConfig;
        return this;
    }

    public Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = generateComponents();
        generators.forEach(gen -> gen.updateConfig(configGenerator));
        if (targetDirectory.isDirectory()) {
            generators.forEach(gen -> MetaDataWriter.writeLabelsImageMetadata(targetDirectory, gen.getLabels()));
        }
        generatedFiles.add(generateApplicationDescriptor());
        generatedFiles.addAll(generateApplicationSections());

        generatedFiles.addAll(configGenerator.generate());

        this.labelers.values().forEach(l -> MetaDataWriter.writeLabelsImageMetadata(targetDirectory, l.generateLabels()));
        logGeneratedFiles(generatedFiles);

        return generatedFiles;
    }

    public List<GeneratedFile> generateComponents() {
        return generators.stream()
                .flatMap(gen -> gen.generate().stream())
                .collect(Collectors.toList());
    }

    public GeneratedFile generateApplicationDescriptor() {
        List<String> sections = generators.stream()
                .map(Generator::section)
                .filter(Objects::nonNull)
                .map(ApplicationSection::sectionClassName)
                .collect(Collectors.toList());

        applicationMainGenerator.withSections(sections);
        return applicationMainGenerator.generate();
    }

    private List<GeneratedFile> generateApplicationSections() {
        ArrayList<GeneratedFile> generatedFiles = new ArrayList<>();

        for (Generator generator : generators) {
            ApplicationSection section = generator.section();
            if (section == null) {
                continue;
            }
            generatedFiles.add(
                    new GeneratedFile(GeneratedFile.Type.APPLICATION_SECTION,
                                      getFilePath(section.sectionClassName()),
                                      section.compilationUnit().toString()));
        }
        return generatedFiles;
    }

    /**
     * Method to wire Generator with ApplicationGenerator and initialize it with common parameters
     * @param generator
     * @param <G>
     * @return
     */
    public <G extends Generator> G setupGenerator(G generator) {
        this.generators.add(generator);
        generator.setPackageName(packageName);
        generator.setProjectDirectory(targetDirectory.getParentFile().toPath());
        generator.setContext(context);
        generator.setAddonsConfig(addonsConfig);
        return generator;
    }

    public ApplicationGenerator withClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    private Collection<String> loadAddonList() {
        ArrayList<String> addons = new ArrayList<>();
        try {
            Enumeration<URL> urls = classLoader.getResources("META-INF/kogito.addon");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream urlStream = url.openStream(); InputStreamReader isr = new InputStreamReader(urlStream)) {
                    String addon = StringUtils.readFileAsString(isr);
                    addons.add(addon);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Unexpected exception during loading of kogito.addon files", e);
        }
        return addons;
    }

    private void logGeneratedFiles(Collection<GeneratedFile> files) {
        if (LOGGER.isDebugEnabled()) {
            String separator = "=====";
            for (GeneratedFile file : files) {
                LOGGER.debug(separator);
                LOGGER.debug("{}: {}", file.getType(), file.relativePath());
                LOGGER.debug(separator);
                LOGGER.debug(new String(file.contents()));
                LOGGER.debug(separator);
            }
        }
    }
}
