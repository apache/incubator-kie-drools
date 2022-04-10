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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;

public class RuleUnitQueryDashboardCodegen {

    private static final String operationalDashboardDrlTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";

    private final KogitoBuildContext context;
    private final Collection<QueryEndpointGenerator> validQueries;

    public RuleUnitQueryDashboardCodegen(KogitoBuildContext context, Collection<QueryEndpointGenerator> validQueries) {
        this.context = context;
        this.validQueries = validQueries;
    }

    Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();

        for (QueryEndpointGenerator queryEndpoint : validQueries) {
            generatedFiles.addAll(generateQueryDashboard(queryEndpoint));
        }

        return generatedFiles;
    }

    private List<GeneratedFile> generateQueryDashboard(QueryEndpointGenerator query) {
        if (context.getAddonsConfig().usePrometheusMonitoring()) {
            String dashboardName = GrafanaConfigurationWriter.buildDashboardName(context.getGAV(), query.getEndpointName());
            Optional<String> operationalDashboard = GrafanaConfigurationWriter.generateOperationalDashboard(
                    operationalDashboardDrlTemplate,
                    query.getEndpointName(),
                    context.getPropertiesMap(),
                    query.getEndpointName(),
                    context.getGAV().orElse(KogitoGAV.EMPTY_GAV),
                    context.getAddonsConfig().useTracing());
            return operationalDashboard.stream()
                    .flatMap(dashboard -> DashboardGeneratedFileUtils.operational(dashboard, dashboardName + ".json").stream())
                    .collect(Collectors.toUnmodifiableList());
        }
        return Collections.emptyList();
    }

}
