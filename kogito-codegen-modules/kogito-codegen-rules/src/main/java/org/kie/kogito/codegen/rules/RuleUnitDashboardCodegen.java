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

import java.util.*;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;

public class RuleUnitDashboardCodegen {

    private static final String domainDashboardDrlTemplate = "/grafana-dashboard-template/domain-dashboard-template.json";

    private final KogitoBuildContext context;
    private final Collection<RuleUnitGenerator> ruleUnitGenerators;

    public RuleUnitDashboardCodegen(KogitoBuildContext context, Collection<RuleUnitGenerator> ruleUnitGenerators) {
        this.context = context;
        this.ruleUnitGenerators = ruleUnitGenerators;
    }

    Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();

        for (RuleUnitGenerator ruleUnit : ruleUnitGenerators) {
            generatedFiles.addAll(generateRuleUnitDashboard(ruleUnit));
        }

        return generatedFiles;
    }

    private List<GeneratedFile> generateRuleUnitDashboard(RuleUnitGenerator ruleUnit) {
        Optional<String> domainDashboard = GrafanaConfigurationWriter.generateDomainSpecificDrlDashboard(
                domainDashboardDrlTemplate,
                ruleUnit.typeName(),
                context.getPropertiesMap(),
                ruleUnit.typeName(),
                context.getGAV().orElse(KogitoGAV.EMPTY_GAV),
                context.getAddonsConfig().useTracing());
        String dashboardName = GrafanaConfigurationWriter.buildDashboardName(context.getGAV(), ruleUnit.typeName());
        return domainDashboard.stream()
                .flatMap(dashboard -> DashboardGeneratedFileUtils.domain(dashboard, dashboardName + ".json").stream())
                .collect(Collectors.toUnmodifiableList());
    }

}
