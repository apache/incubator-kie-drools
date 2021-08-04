/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.rules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KogitoKieModuleModelImpl;
import org.drools.compiler.builder.impl.KogitoKnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.ModelSourceClass;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.rules.config.NamedRuleUnitConfig;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;
import org.kie.kogito.conf.ClockType;
import org.kie.kogito.conf.EventProcessingType;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;
import org.kie.kogito.rules.RuleUnitConfig;
import org.kie.kogito.rules.units.AbstractRuleUnitDescription;
import org.kie.kogito.rules.units.AssignableChecker;
import org.kie.kogito.rules.units.ReflectiveRuleUnitDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.addDTableToCompiler;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.loadResourceConfiguration;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

public class IncrementalRuleCodegen extends AbstractGenerator {

    public static final GeneratedFileType RULE_TYPE = GeneratedFileType.of("RULE", GeneratedFileType.Category.SOURCE);
    public static final String TEMPLATE_RULE_FOLDER = "/class-templates/rules/";
    public static final String GENERATOR_NAME = "rules";
    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementalRuleCodegen.class);
    private static final GeneratedFileType JSON_MAPPER_TYPE = GeneratedFileType.of("JSON_MAPPER", GeneratedFileType.Category.SOURCE);

    public static IncrementalRuleCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        List<Resource> generatedRules = resources.stream()
                .map(CollectedResource::resource)
                .filter(r -> r.getResourceType() == ResourceType.DRL || r.getResourceType() == ResourceType.DTABLE || r.getResourceType() == ResourceType.PROPERTIES)
                .collect(toList());
        return ofResources(context, generatedRules);
    }

    public static IncrementalRuleCodegen ofJavaResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        List<Resource> generatedRules =
                AnnotatedClassPostProcessor.scan(
                        resources.stream()
                                .filter(r -> r.resource().getResourceType() == ResourceType.JAVA)
                                .map(r -> new File(r.resource().getSourcePath()))
                                .map(File::toPath))
                        .generate();
        return ofResources(context, generatedRules);
    }

    public static IncrementalRuleCodegen ofResources(KogitoBuildContext context, Collection<Resource> resources) {
        return new IncrementalRuleCodegen(context, resources);
    }

    private static final String operationalDashboardDrlTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private static final String domainDashboardDrlTemplate = "/grafana-dashboard-template/domain-dashboard-template.json";
    private final Collection<Resource> resources;
    private final List<RuleUnitGenerator> ruleUnitGenerators = new ArrayList<>();

    private KieModuleModel kieModuleModel;
    private boolean hotReloadMode = false;
    private final boolean decisionTableSupported;
    private final Map<String, RuleUnitConfig> configs;

    private IncrementalRuleCodegen(KogitoBuildContext context, Collection<Resource> resources) {
        super(context, GENERATOR_NAME, new RuleConfigGenerator(context));
        this.resources = resources;
        this.kieModuleModel = findKieModuleModel(context.getAppPaths().getResourcePaths());
        setDefaultsforEmptyKieModule(kieModuleModel);
        this.decisionTableSupported = DecisionTableFactory.getDecisionTableProvider() != null;
        this.configs = new HashMap<>();
        for (NamedRuleUnitConfig cfg : NamedRuleUnitConfig.fromContext(context)) {
            this.configs.put(cfg.getCanonicalName(), cfg.getConfig());
        }
    }

    @Override
    public Optional<ApplicationSection> section() {
        RuleUnitContainerGenerator moduleGenerator = new RuleUnitContainerGenerator(context());
        ruleUnitGenerators.forEach(moduleGenerator::addRuleUnit);
        return Optional.of(moduleGenerator);
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        ReleaseIdImpl dummyReleaseId = new ReleaseIdImpl("dummy:dummy:0.0.0");
        if (!decisionTableSupported &&
                resources.stream().anyMatch(r -> r.getResourceType() == ResourceType.DTABLE)) {
            throw new MissingDecisionTableDependencyError();
        }

        ModelBuilderImpl<KogitoPackageSources> modelBuilder = new ModelBuilderImpl<>(KogitoPackageSources::dumpSources, createBuilderConfiguration(), dummyReleaseId, hotReloadMode);

        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        resources.forEach(f -> addResource(batch, f));

        try {
            batch.build();
        } catch (RuntimeException e) {
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                LOGGER.error(error.toString());
            }
            LOGGER.error(e.getMessage());
            throw new RuleCodegenError(e, modelBuilder.getErrors().getErrors());
        }

        if (modelBuilder.hasErrors()) {
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                LOGGER.error(error.toString());
            }
            throw new RuleCodegenError(modelBuilder.getErrors().getErrors());
        }

        Map<String, String> modelsByUnit = new HashMap<>();

        List<GeneratedFile> generatedFiles = new ArrayList<>(generateModels(modelBuilder, modelsByUnit));

        boolean hasRuleUnits = !ruleUnitGenerators.isEmpty();

        List<DroolsError> errors = new ArrayList<>();

        if (hasRuleUnits) {
            generateRuleUnits(errors, generatedFiles);
        } else if (context().hasClassAvailable("org.kie.kogito.legacy.rules.KieRuntimeBuilder")) {
            generateProject(dummyReleaseId, modelsByUnit, generatedFiles);
        }

        if (!errors.isEmpty()) {
            throw new RuleCodegenError(errors);
        }

        return generatedFiles;
    }

    private KogitoKnowledgeBuilderConfigurationImpl createBuilderConfiguration() {
        KogitoBuildContext buildContext = context();
        KogitoKnowledgeBuilderConfigurationImpl conf = new KogitoKnowledgeBuilderConfigurationImpl(buildContext.getClassLoader());
        for (String prop : buildContext.getApplicationProperties()) {
            if (prop.startsWith("drools")) {
                conf.setProperty(prop, buildContext.getApplicationProperty(prop).get());
            }
        }
        return conf;
    }

    private void addResource(CompositeKnowledgeBuilder batch, Resource resource) {
        if (resource.getResourceType() == ResourceType.PROPERTIES) {
            return;
        }
        if (resource.getResourceType() == ResourceType.DTABLE) {
            Resource resourceProps = findPropertiesResource(resource);
            if (resourceProps != null) {
                ResourceConfiguration conf = loadResourceConfiguration(resource.getSourcePath(), x -> true, x -> {
                    try {
                        return resourceProps.getInputStream();
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                if (conf instanceof DecisionTableConfiguration) {
                    addDTableToCompiler(batch, resource, ((DecisionTableConfiguration) conf));
                    return;
                }
            }
        }
        batch.add(resource, resource.getResourceType());
    }

    private Resource findPropertiesResource(Resource resource) {
        return resources.stream().filter(r -> r.getSourcePath().equals(resource.getSourcePath() + ".properties")).findFirst().orElse(null);
    }

    private List<GeneratedFile> generateModels(ModelBuilderImpl<KogitoPackageSources> modelBuilder, Map<String, String> modelsByUnit) {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        List<org.drools.modelcompiler.builder.GeneratedFile> legacyModelFiles = new ArrayList<>();

        for (KogitoPackageSources pkgSources : modelBuilder.getPackageSources()) {
            pkgSources.getModelsByUnit().forEach((unit, model) -> modelsByUnit.put(ruleUnit2KieBaseName(unit), model));

            pkgSources.collectGeneratedFiles(legacyModelFiles);

            org.drools.modelcompiler.builder.GeneratedFile reflectConfigSource = pkgSources.getReflectConfigSource();
            if (reflectConfigSource != null) {
                modelFiles.add(new GeneratedFile(GeneratedFileType.RESOURCE,
                        reflectConfigSource.getPath(),
                        reflectConfigSource.getData()));
            }

            Collection<RuleUnitDescription> ruleUnits = pkgSources.getRuleUnits();
            for (RuleUnitDescription ruleUnit : ruleUnits) {
                String canonicalName = ruleUnit.getCanonicalName();
                RuleUnitGenerator ruSource = new RuleUnitGenerator(context(), ruleUnit, pkgSources.getRulesFileName())
                        .withQueries(pkgSources.getQueriesInRuleUnit(canonicalName))
                        .mergeConfig(configs.get(canonicalName));

                ruleUnitGenerators.add(ruSource);
                // only Class<?> has config for now
                addUnitConfToKieModule(ruleUnit);
            }
        }

        modelFiles.addAll(convertGeneratedRuleFile(legacyModelFiles));
        return modelFiles;
    }

    private Collection<GeneratedFile> convertGeneratedRuleFile(Collection<org.drools.modelcompiler.builder.GeneratedFile> legacyModelFiles) {
        return legacyModelFiles.stream().map(f -> new GeneratedFile(
                IncrementalRuleCodegen.RULE_TYPE,
                f.getPath(), f.getData()))
                .collect(toList());
    }

    private void generateProject(ReleaseIdImpl dummyReleaseId, Map<String, String> modelsByUnit, List<GeneratedFile> generatedFiles) {
        Map<String, List<String>> modelsByKBase = new HashMap<>();
        for (Map.Entry<String, String> entry : modelsByUnit.entrySet()) {
            modelsByKBase.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }

        ModelSourceClass modelSourceClass = new ModelSourceClass(dummyReleaseId, kieModuleModel.getKieBaseModels(), modelsByKBase);

        generatedFiles.add(new GeneratedFile(
                RULE_TYPE,
                modelSourceClass.getName(),
                modelSourceClass.generate()));

        ProjectRuntimeGenerator projectRuntimeGenerator = new ProjectRuntimeGenerator(modelSourceClass.getModelMethod(), context());

        generatedFiles.add(new GeneratedFile(
                RULE_TYPE,
                projectRuntimeGenerator.getName(),
                projectRuntimeGenerator.generate()));
    }

    private void generateRuleUnits(List<DroolsError> errors, List<GeneratedFile> generatedFiles) {
        RuleUnitHelper ruleUnitHelper = new RuleUnitHelper();

        if (context().hasDI()) {
            TemplatedGenerator generator = TemplatedGenerator.builder()
                    .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                    .build(context(), "KogitoObjectMapper");

            generatedFiles.add(new GeneratedFile(JSON_MAPPER_TYPE,
                    generator.generatedFilePath(),
                    generator.compilationUnitOrThrow().toString()));
        }

        for (RuleUnitGenerator ruleUnit : ruleUnitGenerators) {
            initRuleUnitHelper(ruleUnitHelper, ruleUnit.getRuleUnitDescription());

            List<String> queryClasses = generateQueriesEndpoint(errors, generatedFiles, ruleUnitHelper, ruleUnit);

            generatedFiles.add(ruleUnit.generate());

            RuleUnitInstanceGenerator ruleUnitInstance = ruleUnit.instance(ruleUnitHelper, queryClasses);
            generatedFiles.add(ruleUnitInstance.generate());

            ruleUnit.pojo(ruleUnitHelper).ifPresent(p -> generatedFiles.add(p.generate()));
        }
    }

    private List<String> generateQueriesEndpoint(List<DroolsError> errors, List<GeneratedFile> generatedFiles, RuleUnitHelper ruleUnitHelper, RuleUnitGenerator ruleUnit) {

        List<QueryEndpointGenerator> queries = ruleUnit.queries();
        if (queries.isEmpty()) {
            return Collections.emptyList();
        }

        if (!context().hasDI()) {
            generatedFiles.add(new RuleUnitDTOSourceClass(ruleUnit.getRuleUnitDescription(), ruleUnitHelper).generate());
        }
        if (context().getAddonsConfig().useMonitoring()) {
            String dashboardName = GrafanaConfigurationWriter.buildDashboardName(context().getGAV(), ruleUnit.typeName());
            String dashboard = GrafanaConfigurationWriter.generateDomainSpecificDrlDashboard(
                    domainDashboardDrlTemplate,
                    dashboardName,
                    ruleUnit.typeName(),
                    context().getGAV().orElse(KogitoGAV.EMPTY_GAV),
                    context().getAddonsConfig().useTracing());
            generatedFiles.addAll(DashboardGeneratedFileUtils.domain(dashboard, dashboardName + ".json"));
        }

        return queries.stream().map(q -> generateQueryEndpoint(errors, generatedFiles, q))
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty)).collect(toList());
    }

    private void initRuleUnitHelper(RuleUnitHelper ruleUnitHelper, RuleUnitDescription ruleUnitDesc) {
        if (ruleUnitDesc instanceof ReflectiveRuleUnitDescription) {
            ruleUnitHelper.setAssignableChecker(((ReflectiveRuleUnitDescription) ruleUnitDesc).getAssignableChecker());
        } else {
            if (ruleUnitHelper.getAssignableChecker() == null) {
                ruleUnitHelper.setAssignableChecker(AssignableChecker.create(context().getClassLoader(), hotReloadMode));
            }
        }
    }

    private Optional<String> generateQueryEndpoint(List<DroolsError> errors, List<GeneratedFile> generatedFiles, QueryEndpointGenerator query) {
        if (!query.validate()) {
            errors.add(query.getError());
            return Optional.empty();
        }

        if (context().hasRESTForGenerator(this)) {
            if (context().getAddonsConfig().usePrometheusMonitoring()) {
                String dashboardName = GrafanaConfigurationWriter.buildDashboardName(context().getGAV(), query.getEndpointName());
                String dashboard = GrafanaConfigurationWriter.generateOperationalDashboard(
                        operationalDashboardDrlTemplate,
                        dashboardName,
                        query.getEndpointName(),
                        context().getGAV().orElse(KogitoGAV.EMPTY_GAV),
                        context().getAddonsConfig().useTracing());
                generatedFiles.addAll(DashboardGeneratedFileUtils.operational(dashboard, dashboardName + ".json"));
            }

            generatedFiles.add(query.generate());
        }

        QueryGenerator queryGenerator = query.getQueryGenerator();
        generatedFiles.add(queryGenerator.generate());
        return Optional.of(queryGenerator.getQueryClassName());
    }

    private void addUnitConfToKieModule(RuleUnitDescription ruleUnitDescription) {
        KieBaseModel unitKieBaseModel = kieModuleModel.newKieBaseModel(ruleUnit2KieBaseName(ruleUnitDescription.getCanonicalName()));
        unitKieBaseModel.setEventProcessingMode(org.kie.api.conf.EventProcessingOption.CLOUD);
        unitKieBaseModel.addPackage(ruleUnitDescription.getPackageName());

        // merge config from the descriptor with configs from application.conf
        // application.conf overrides any other config
        RuleUnitConfig config =
                ((AbstractRuleUnitDescription) ruleUnitDescription).getConfig()
                        .merged(configs.get(ruleUnitDescription.getCanonicalName()));

        OptionalInt sessionsPool = config.getSessionPool();
        if (sessionsPool.isPresent()) {
            unitKieBaseModel.setSessionsPool(SessionsPoolOption.get(sessionsPool.getAsInt()));
        }
        EventProcessingType eventProcessingType = config.getDefaultedEventProcessingType();
        if (eventProcessingType == EventProcessingType.STREAM) {
            unitKieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
        }

        KieSessionModel unitKieSessionModel = unitKieBaseModel.newKieSessionModel(ruleUnit2KieSessionName(ruleUnitDescription.getCanonicalName()));
        unitKieSessionModel.setType(KieSessionModel.KieSessionType.STATEFUL);
        ClockType clockType = config.getDefaultedClockType();
        if (clockType == ClockType.PSEUDO) {
            unitKieSessionModel.setClockType(ClockTypeOption.PSEUDO);
        }
    }

    private String ruleUnit2KieBaseName(String ruleUnit) {
        return ruleUnit.replace('.', '$') + "KieBase";
    }

    private String ruleUnit2KieSessionName(String ruleUnit) {
        return ruleUnit.replace('.', '$') + "KieSession";
    }

    public IncrementalRuleCodegen withHotReloadMode() {
        this.hotReloadMode = true;
        return this;
    }

    private static KieModuleModel findKieModuleModel(Path[] resourcePaths) {
        for (Path resourcePath : resourcePaths) {
            Path moduleXmlPath = resourcePath.resolve(KogitoKieModuleModelImpl.KMODULE_JAR_PATH);
            if (Files.exists(moduleXmlPath)) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(moduleXmlPath))) {
                    return KogitoKieModuleModelImpl.fromXML(bais);
                } catch (IOException e) {
                    throw new UncheckedIOException("Impossible to open " + moduleXmlPath, e);
                }
            }
        }

        return new KogitoKieModuleModelImpl();
    }

    @Override
    public int priority() {
        return 20;
    }
}
