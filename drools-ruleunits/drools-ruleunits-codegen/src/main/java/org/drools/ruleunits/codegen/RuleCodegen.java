/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.codegen;

import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.ruleunits.api.RuleUnitConfig;
import org.drools.ruleunits.codegen.config.NamedRuleUnitConfig;
import org.drools.ruleunits.codegen.config.RuleConfigGenerator;
import org.drools.ruleunits.codegen.context.KogitoBuildContext;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RuleCodegen {

    public static final GeneratedFileType RULE_TYPE = GeneratedFileType.of("RULE", GeneratedFileType.Category.SOURCE);
    public static final String TEMPLATE_RULE_FOLDER = "/class-templates/rules/";
    public static final String GENERATOR_NAME = "rules";
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleCodegen.class);
    private final RuleConfigGenerator configGenerator;
    private final KogitoBuildContext context;
    private final String name;
//
//    public static RuleCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
//        List<Resource> generatedRules = resources.stream()
//                .map(CollectedResource::resource)
//                .filter(r -> isRuleFile(r) || r.getResourceType() == ResourceType.PROPERTIES)
//                .collect(toList());
//        return ofResources(context, generatedRules);
//    }
//
//    public static RuleCodegen ofJavaResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
//        List<Resource> generatedRules =
//                AnnotatedClassPostProcessor.scan(
//                        resources.stream()
//                                .filter(r -> r.resource().getResourceType() == ResourceType.JAVA)
//                                .map(r -> new File(r.resource().getSourcePath()))
//                                .map(File::toPath))
//                        .generate();
//        return ofResources(context, generatedRules);
//    }

    public static RuleCodegen ofResources(KogitoBuildContext context, Collection<Resource> resources) {
        return new RuleCodegen(context, resources);
    }

    private final Collection<Resource> resources;
    private final List<RuleUnitGenerator> ruleUnitGenerators = new ArrayList<>();

    private boolean hotReloadMode = false;
    private final boolean decisionTableSupported;
    private final Map<String, RuleUnitConfig> configs;

    private RuleCodegen(KogitoBuildContext context, Collection<Resource> resources) {
        Objects.requireNonNull(context, "context cannot be null");
        this.name = GENERATOR_NAME;
        this.context = context;
        this.configGenerator = new RuleConfigGenerator(context);
        this.resources = resources;
        this.decisionTableSupported = DecisionTableFactory.getDecisionTableProvider() != null;
        this.configs = new HashMap<>();
        for (NamedRuleUnitConfig cfg : NamedRuleUnitConfig.fromContext(context)) {
            this.configs.put(cfg.getCanonicalName(), cfg.getConfig());
        }
    }

    public Optional<RuleUnitContainerGenerator> section() {
        RuleUnitContainerGenerator moduleGenerator = new RuleUnitContainerGenerator(context());
        ruleUnitGenerators.forEach(moduleGenerator::addRuleUnit);
        return Optional.of(moduleGenerator);
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    protected Collection<GeneratedFile> internalGenerate() {

        DroolsModelBuilder droolsModelBuilder =
                new DroolsModelBuilder(
                        context(), resources, decisionTableSupported, hotReloadMode);

        droolsModelBuilder.build();
        Collection<GeneratedFile> generatedFiles = droolsModelBuilder.generateCanonicalModelSources();
        this.ruleUnitGenerators.addAll(droolsModelBuilder.createRuleUnitGenerators(configs));

        boolean hasRuleUnits = !ruleUnitGenerators.isEmpty();

        if (hasRuleUnits) {
            KieModuleModelWrapper kieModuleModelWrapper = KieModuleModelWrapper.fromResourcePaths(context().getAppPaths().getResourcePaths());
            // fixme it looks like this config is never really propagated (i.e. written anywhere)
            droolsModelBuilder.packageSources()
                    .stream()
                    .flatMap(pkgSrc -> pkgSrc.getRuleUnits().stream())
                    .forEach(ru -> kieModuleModelWrapper.addRuleUnitConfig(ru, configs.get(ru.getCanonicalName())));

            // main codegen procedure (rule units, rule unit instances, queries, generated pojos)
            RuleUnitMainCodegen ruleUnitCodegen = new RuleUnitMainCodegen(context(), ruleUnitGenerators, hotReloadMode);
            generatedFiles.addAll(ruleUnitCodegen.generate());

//            // dashboard for rule unit
//            RuleUnitDashboardCodegen dashboardCodegen = new RuleUnitDashboardCodegen(context(), ruleUnitGenerators);
//            generatedFiles.addAll(dashboardCodegen.generate());

//            // "extended" procedure: REST + Event handlers + query dashboards
//            if (context().hasRESTForGenerator(this)) {
//                Collection<QueryGenerator> validQueries = ruleUnitCodegen.validQueries();
//                RuleUnitExtendedCodegen ruleUnitExtendedCodegen = new RuleUnitExtendedCodegen(context(), validQueries);
//                generatedFiles.addAll(ruleUnitExtendedCodegen.generate());
//            }

            if (!ruleUnitCodegen.errors().isEmpty()) {
                throw new RuleCodegenError(ruleUnitCodegen.errors());
            }
        } else if (context().hasClassAvailable("org.kie.kogito.legacy.rules.KieRuntimeBuilder")) {
            KieSessionModelBuilder kieSessionModelBuilder =
                    new KieSessionModelBuilder(context(), droolsModelBuilder.packageSources());
            generatedFiles.addAll(kieSessionModelBuilder.generate());

        } else if (hasRuleFiles()) { // this additional check is necessary because also properties or java files can be loaded
            throw new IllegalStateException("Found DRL files using legacy API, add org.kie.kogito:kogito-legacy-api dependency to enable it");
        }

        return generatedFiles;
    }

    public RuleCodegen withHotReloadMode() { // fixme this is currently only used in test cases. Drop?
        this.hotReloadMode = true;
        return this;
    }

    private boolean hasRuleFiles() {
        return resources.stream().anyMatch(RuleCodegen::isRuleFile);
    }

    private static boolean isRuleFile(Resource resource) {
        return resource.getResourceType() == ResourceType.DRL || resource.getResourceType() == ResourceType.DTABLE;
    }

    public int priority() {
        return 20;
    }

    public KogitoBuildContext context() {
        return this.context;
    }

    public String name() {
        return name;
    }

    protected String applicationCanonicalName() {
        return context.getPackageName() + ".Application";
    }

    public Optional<RuleConfigGenerator> configGenerator() {
        return Optional.ofNullable(configGenerator);
    }

    public final Collection<GeneratedFile> generate() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        return internalGenerate();
    }
}
