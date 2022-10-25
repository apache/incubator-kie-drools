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
package org.kie.kogito.codegen.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.model.codegen.execmodel.PackageModelWriter;
import org.drools.model.codegen.project.CodegenPackageSources;
import org.drools.model.codegen.project.DroolsModelBuilder;
import org.drools.model.codegen.project.KieSessionModelBuilder;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.rules.config.NamedRuleUnitConfig;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;
import org.kie.kogito.rules.RuleUnitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

public class RuleCodegen extends AbstractGenerator {

    public static final GeneratedFileType RULE_TYPE = GeneratedFileType.of("RULE", GeneratedFileType.Category.SOURCE);
    public static final String TEMPLATE_RULE_FOLDER = "/class-templates/rules/";
    public static final String GENERATOR_NAME = "rules";
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleCodegen.class);

    public static RuleCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        List<Resource> generatedRules = resources.stream()
                .map(CollectedResource::resource)
                .filter(r -> isRuleFile(r) || r.getResourceType() == ResourceType.PROPERTIES)
                .collect(toList());
        return ofResources(context, generatedRules);
    }

    public static RuleCodegen ofJavaResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        List<Resource> generatedRules =
                AnnotatedClassPostProcessor.scan(
                        resources.stream()
                                .filter(r -> r.resource().getResourceType() == ResourceType.JAVA)
                                .map(r -> new File(r.resource().getSourcePath()))
                                .map(File::toPath))
                        .generate();
        return ofResources(context, generatedRules);
    }

    public static RuleCodegen ofResources(KogitoBuildContext context, Collection<Resource> resources) {
        return new RuleCodegen(context, resources);
    }

    private final Collection<Resource> resources;
    private final List<RuleUnitGenerator> ruleUnitGenerators = new ArrayList<>();

    private boolean hotReloadMode = false;
    private final boolean decisionTableSupported;
    private final Map<String, RuleUnitConfig> configs;

    private RuleCodegen(KogitoBuildContext context, Collection<Resource> resources) {
        super(context, GENERATOR_NAME, new RuleConfigGenerator(context));
        this.resources = resources;
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

        DroolsModelBuilder droolsModelBuilder = new DroolsModelBuilder(context(), resources, decisionTableSupported, PackageModelWriter::new);

        try {
            droolsModelBuilder.build();
        } catch (RuntimeException e) {
            throw new RuleCodegenError(e);
        }

        Collection<GeneratedFile> generatedFiles = droolsModelBuilder.generateCanonicalModelSources();
        for (CodegenPackageSources pkgSources : droolsModelBuilder.packageSources()) {
            Collection<RuleUnitDescription> ruleUnits = pkgSources.getRuleUnits();
            for (RuleUnitDescription ruleUnit : ruleUnits) {
                String canonicalName = ruleUnit.getCanonicalName();
                String rulesFileName = pkgSources.getRulesFileName();
                this.ruleUnitGenerators.add(new RuleUnitGenerator(context(), ruleUnit, rulesFileName)
                        .withQueries(pkgSources.getQueriesInRuleUnit(canonicalName))
                        .mergeConfig(configs.get(canonicalName)));
            }
        }

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

            // dashboard for rule unit
            RuleUnitDashboardCodegen dashboardCodegen = new RuleUnitDashboardCodegen(context(), ruleUnitGenerators);
            generatedFiles.addAll(dashboardCodegen.generate());

            // "extended" procedure: REST + Event handlers + query dashboards
            if (context().hasRESTForGenerator(this)) {
                Collection<QueryGenerator> validQueries = ruleUnitCodegen.validQueries();
                RuleUnitExtendedCodegen ruleUnitExtendedCodegen = new RuleUnitExtendedCodegen(context(), validQueries);
                generatedFiles.addAll(ruleUnitExtendedCodegen.generate());
            }

            if (!ruleUnitCodegen.errors().isEmpty()) {
                throw new RuleCodegenError(ruleUnitCodegen.errors());
            }
        } else {
            LOGGER.info("No rule unit is present: generate KieRuntimeBuilder implementation.");
            KieSessionModelBuilder kieSessionModelBuilder =
                    new KieSessionModelBuilder(context(), droolsModelBuilder.packageSources());
            generatedFiles.addAll(kieSessionModelBuilder.generate());
        }

        return generatedFiles;
    }

    public RuleCodegen withHotReloadMode() { // fixme this is currently only used in test cases. Drop?
        this.hotReloadMode = true;
        return this;
    }

    private static boolean isRuleFile(Resource resource) {
        return resource.getResourceType() == ResourceType.DRL || resource.getResourceType() == ResourceType.DTABLE;
    }

    @Override
    public int priority() {
        return 20;
    }
}
