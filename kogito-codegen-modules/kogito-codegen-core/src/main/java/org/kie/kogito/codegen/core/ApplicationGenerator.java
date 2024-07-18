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
package org.kie.kogito.codegen.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.util.StringUtils;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.codegen.api.Generator.REST_TYPE;
import static org.kie.kogito.codegen.core.CustomDashboardGeneratedUtils.loadCustomGrafanaDashboardsList;

public class ApplicationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationGenerator.class);

    public static final String APPLICATION_CLASS_NAME = "Application";
    private static final GeneratedFileType APPLICATION_SECTION_TYPE = GeneratedFileType.of("APPLICATION_SECTION",
            GeneratedFileType.Category.SOURCE);

    private final ApplicationContainerGenerator applicationMainGenerator;
    private ApplicationConfigGenerator applicationConfigGenerator;
    private Collection<Generator> generators = new ArrayList<>();

    private KogitoBuildContext context;

    public ApplicationGenerator(KogitoBuildContext context) {
        this.context = context;
        this.applicationMainGenerator = new ApplicationContainerGenerator(context);

        this.applicationConfigGenerator = new ApplicationConfigGenerator(context);
        this.applicationConfigGenerator.withAddons(loadAddonList());
    }

    public String targetCanonicalName() {
        return context.getPackageName() + "." + APPLICATION_CLASS_NAME;
    }

    private String getFilePath(String className) {
        return (context.getPackageName() + "." + className).replace('.', '/') + ".java";
    }

    public Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = generateComponents();
        for (Generator generator : generators) {
            generator.configGenerator().ifPresent(applicationConfigGenerator::addConfigGenerator);
        }

        generatedFiles.add(generateApplicationDescriptor());
        generatedFiles.addAll(generateApplicationSections());

        generatedFiles.addAll(applicationConfigGenerator.generate());

        generatedFiles.addAll(loadCustomGrafanaDashboardsList(context));

        DashboardGeneratedFileUtils.list(generatedFiles).ifPresent(generatedFiles::add);

        logGeneratedFiles(generatedFiles);

        return generatedFiles;
    }

    public List<GeneratedFile> generateComponents() {
        return generators.stream()
                .flatMap(gen -> {
                    boolean keepRestFile = keepRestFile(gen);
                    return gen.generate().stream()
                            .filter(generatedFile -> filterGeneratedFile(generatedFile, keepRestFile));
                })
                .collect(Collectors.toList());
    }

    public GeneratedFile generateApplicationDescriptor() {
        List<String> sections = generators.stream()
                .map(Generator::section)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ApplicationSection::sectionClassName)
                .collect(Collectors.toList());

        applicationMainGenerator.withSections(sections);
        return applicationMainGenerator.generate();
    }

    boolean keepRestFile(Generator generator) {
        return context.hasRESTForGenerator(generator);
    }

    private boolean filterGeneratedFile(GeneratedFile generatedFile, boolean keepRestFile) {
        boolean keepFile = keepRestFile || !REST_TYPE.equals(generatedFile.type());
        if (!keepFile) {
            LOGGER.warn("Skipping file because REST is disabled: " + generatedFile.relativePath());
        }
        return keepFile;
    }

    private Collection<GeneratedFile> generateApplicationSections() {
        return generators.stream()
                .map(Generator::section)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(section -> this.context.addApplicationSection(section))
                .map(section -> new GeneratedFile(APPLICATION_SECTION_TYPE,
                        getFilePath(section.sectionClassName()),
                        section.compilationUnit().toString()))
                .collect(Collectors.toList());
    }

    /**
     * Method to wire Generator with ApplicationGenerator if enabled
     * 
     * @param generator
     * @param <G>
     * @return
     */
    public <G extends Generator> Optional<G> registerGeneratorIfEnabled(G generator) {
        if (!generator.isEnabled()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Skipping generator '{}' because disabled", generator.name());
            }
            return Optional.empty();
        }
        this.generators.add(generator);
        return Optional.of(generator);
    }

    protected Collection<Generator> getGenerators() {
        return Collections.unmodifiableCollection(generators);
    }

    protected Set<String> loadAddonList() {
        Set<String> addons = new HashSet<>();
        try {
            Enumeration<URL> urls = context.getClassLoader().getResources("META-INF/kogito.addon");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream urlStream = url.openStream();
                        InputStreamReader isr =
                                new InputStreamReader(urlStream)) {
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
                LOGGER.debug("{} {}: {}", file.category().name(), file.type().name(), file.relativePath());
                LOGGER.debug(separator);
                LOGGER.debug(new String(file.contents()));
                LOGGER.debug(separator);
            }
        }
    }
}
