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
package org.kie.kogito.codegen.decision;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.eclipse.microprofile.openapi.spi.OASFactoryResolver;
import org.kie.api.io.ResourceType;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.dmn.core.assembler.DMNAssemblerService.DMN_PROFILE_PREFIX;
import static org.kie.kogito.codegen.decision.DecisionCodegenUtils.generateModelFromGeneratedResources;
import static org.kie.kogito.codegen.decision.DecisionCodegenUtils.generateModelsFromResources;

public class DecisionCodegen extends AbstractGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(DecisionCodegen.class);
    public static final String GENERATOR_NAME = "decisions";

    /**
     * (boolean) generate java classes to support strongly typed input (default false)
     */
    public static String STRONGLY_TYPED_CONFIGURATION_KEY = "kogito.decisions.stronglytyped";
    /**
     * model validation strategy; possible values: ENABLED, DISABLED, IGNORE; (default ENABLED)
     */
    public static String VALIDATION_CONFIGURATION_KEY = "kogito.decisions.validation";

    /**
     * (string) kafka bootstrap server address
     */
    public static final String KOGITO_ADDON_TRACING_DECISION_KAFKA_BOOTSTRAPADDRESS = "kogito.addon.tracing.decision.kafka.bootstrapAddress";
    /**
     * (string) name of the decision topic; default to kogito-tracing-decision
     */
    public static final String KOGITO_ADDON_TRACING_DECISION_KAFKA_TOPIC_NAME = "kogito.addon.tracing.decision.kafka.topic.name";
    /**
     * (integer) number of decision topic partitions; default to 1
     */
    public static final String KOGITO_ADDON_TRACING_DECISION_KAFKA_TOPIC_PARTITIONS = "kogito.addon.tracing.decision.kafka.topic.partitions";

    /**
     * (integer) number of decision topic replication factor; default to 1
     */
    public static final String KOGITO_ADDON_TRACING_DECISION_KAFKA_TOPIC_REPLICATION_FACTOR = "kogito.addon.tracing.decision.kafka.topic.replicationFactor";

    /**
     * (boolean) enable/disable asynchronous collection of decision events; default to true
     */
    public static final String KOGITO_ADDON_TRACING_DECISION_ASYNC_ENABLED = "kogito.addon.tracing.decision.asyncEnabled";

    public static DecisionCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        OASFactoryResolver.instance(); // manually invoke SPI, o/w Kogito CodeGen Kogito Quarkus extension failure at NewFileHotReloadTest due to java.util.ServiceConfigurationError: org.eclipse
        // .microprofile.openapi.spi.OASFactoryResolver: io.smallrye.openapi.spi.OASFactoryResolverImpl not a subtype
        List<CollectedResource> dmnResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.DMN)
                .collect(toList());
        return new DecisionCodegen(context, dmnResources);
    }

    public static DecisionCodegen ofPath(KogitoBuildContext context, Path... paths) {
        return ofCollectedResources(context, CollectedResourceProducer.fromPaths(context.ignoreHiddenFiles(), paths));
    }

    private final List<CollectedResource> cResources;
    private final List<String> classesForManualReflection = new ArrayList<>();
    private final Set<DMNProfile> customDMNProfiles = new HashSet<>();
    private final boolean enableRuntimeTypeCheckOption;

    public DecisionCodegen(KogitoBuildContext context, List<CollectedResource> cResources) {
        super(context, GENERATOR_NAME, new DecisionConfigGenerator(context));
        LOGGER.debug("DecisionCodegen {}", cResources);
        Set<String> customDMNProfilesProperties = getCustomDMNProfilesProperties();
        customDMNProfiles.addAll(getCustomDMNProfiles(customDMNProfilesProperties, context.getClassLoader()));
        enableRuntimeTypeCheckOption = getEnableRuntimeTypeCheckOption();
        this.cResources = cResources;
    }

    @Override
    public Optional<ApplicationSection> section() {
        LOGGER.debug("section");
        return Optional.of(new DecisionContainerGenerator(
                context(),
                applicationCanonicalName(),
                this.cResources,
                this.classesForManualReflection,
                this.customDMNProfiles,
                this.enableRuntimeTypeCheckOption));
    }

    @Override
    public boolean isEmpty() {
        return cResources.isEmpty();
    }

    @Override
    public int priority() {
        return 30;
    }

    @Override
    public String applicationCanonicalName() {
        return super.applicationCanonicalName();
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        LOGGER.debug("internalGenerate");
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        Map.Entry<String, GeneratedResources> generatedResourcesEntry = generateModelsFromResources(generatedFiles,
                classesForManualReflection,
                cResources,
                customDMNProfiles,
                new RuntimeTypeCheckOption(getEnableRuntimeTypeCheckOption()),
                this);
        generateModelFromGeneratedResources(generatedFiles, generatedResourcesEntry);
        return generatedFiles;
    }

    Set<String> getCustomDMNProfilesProperties() {
        Map<String, String> propertiesMap = this.context().getPropertiesMap();
        return propertiesMap.entrySet().stream()
                .filter(stringStringEntry -> stringStringEntry.getKey().startsWith(DMN_PROFILE_PREFIX))
                .map(Entry::getValue)
                .collect(Collectors.toSet());
    }

    boolean getEnableRuntimeTypeCheckOption() {
        Map<String, String> propertiesMap = this.context().getPropertiesMap();
        return Boolean.parseBoolean(propertiesMap.getOrDefault(RuntimeTypeCheckOption.PROPERTY_NAME, "false"));
    }

    static Set<DMNProfile> getCustomDMNProfiles(Set<String> customDMNProfiles, ClassLoader classLoader) {
        Set<DMNProfile> toReturn = new HashSet<>();
        for (String profileName : customDMNProfiles) {
            Class<? extends DMNProfile> profileClass = null;
            try {
                profileClass = classLoader.loadClass(profileName).asSubclass(DMNProfile.class);
            } catch (Exception e) {
                LOGGER.warn("Unable to load DMN profile {} from classloader.", profileName);
            }
            if (profileClass != null) {
                try {
                    toReturn.add(profileClass.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    LOGGER.warn("Unable to instantiate DMN profile {}", profileName, e);
                }
            }
        }
        return toReturn;
    }

    boolean isMPAnnotationsPresent() {
        return context().hasClassAvailable("org.eclipse.microprofile.openapi.models.OpenAPI");
    }

    boolean isIOSwaggerOASv3AnnotationsPresent() {
        return context().hasClassAvailable("io.swagger.v3.oas.annotations.media.Schema");
    }
}
