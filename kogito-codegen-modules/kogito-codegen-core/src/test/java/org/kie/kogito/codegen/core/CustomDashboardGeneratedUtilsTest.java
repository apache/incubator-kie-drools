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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.codegen.core.CustomDashboardGeneratedUtils.domainFunction;
import static org.kie.kogito.codegen.core.CustomDashboardGeneratedUtils.operationalFunction;
import static org.kie.kogito.codegen.core.DashboardGeneratedFileUtils.DOMAIN_DASHBOARD_PREFIX;
import static org.kie.kogito.codegen.core.DashboardGeneratedFileUtils.OPERATIONAL_DASHBOARD_PREFIX;

class CustomDashboardGeneratedUtilsTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void loadCustomGrafanaDashboardsList(KogitoBuildContext.Builder contextBuilder) {
        final KogitoBuildContext context = contextBuilder
                .withAppPaths(AppPaths.fromTestDir(new File(".").toPath()))
                .build();
        Collection<GeneratedFile> retrieved = CustomDashboardGeneratedUtils.loadCustomGrafanaDashboardsList(context);
        assertThat(retrieved).hasSize(2); // 2 = valid *.json files inside src/test/META-INF/dashboards
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void addToGeneratedFiles(KogitoBuildContext.Builder contextBuilder) {
        final KogitoBuildContext context = contextBuilder
                .withAppPaths(AppPaths.fromTestDir(new File(".").toPath()))
                .build();
        Collection<CollectedResource> collectedResources =
                CollectedResourceProducer.fromPaths(context.getAppPaths().getPaths());
        Map<String, List<Resource>> dashboardJsonsMap =
                CustomDashboardGeneratedUtils.getMappedJsons(collectedResources);
        Collection<GeneratedFile> toPopulate = new ArrayList<>();
        CustomDashboardGeneratedUtils.addToGeneratedFiles(dashboardJsonsMap.get(OPERATIONAL_DASHBOARD_PREFIX),
                toPopulate, operationalFunction,
                OPERATIONAL_DASHBOARD_PREFIX);
        assertThat(toPopulate).hasSameSizeAs(dashboardJsonsMap.get(OPERATIONAL_DASHBOARD_PREFIX));
        String sourcePath = dashboardJsonsMap.get(OPERATIONAL_DASHBOARD_PREFIX).get(0).getSourcePath();
        String originalFileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
        validateGeneratedFile(toPopulate.iterator().next(),
                OPERATIONAL_DASHBOARD_PREFIX,
                originalFileName);
        toPopulate = new ArrayList<>();
        CustomDashboardGeneratedUtils.addToGeneratedFiles(dashboardJsonsMap.get(DOMAIN_DASHBOARD_PREFIX), toPopulate,
                domainFunction,
                DOMAIN_DASHBOARD_PREFIX);
        assertThat(toPopulate).hasSameSizeAs(dashboardJsonsMap.get(DOMAIN_DASHBOARD_PREFIX));
        sourcePath = dashboardJsonsMap.get(DOMAIN_DASHBOARD_PREFIX).get(0).getSourcePath();
        originalFileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
        validateGeneratedFile(toPopulate.iterator().next(),
                DOMAIN_DASHBOARD_PREFIX,
                originalFileName);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void getMappedJsons(KogitoBuildContext.Builder contextBuilder) {
        final KogitoBuildContext context = contextBuilder
                .withAppPaths(AppPaths.fromTestDir(new File(".").toPath()))
                .build();
        Collection<CollectedResource> collectedResources =
                CollectedResourceProducer.fromPaths(context.getAppPaths().getPaths());
        Map<String, List<Resource>> retrieved =
                CustomDashboardGeneratedUtils.getMappedJsons(collectedResources);
        assertThat(retrieved).hasSize(2); // 2 = valid *.json files inside src/test/META-INF/dashboards
        assertThat(retrieved.get(OPERATIONAL_DASHBOARD_PREFIX)).hasSize(1);
        assertThat(retrieved.get(DOMAIN_DASHBOARD_PREFIX)).hasSize(1);
    }

    private void validateGeneratedFile(GeneratedFile toValidate, String dashboardType, String originalFileName) {
        assertThat(toValidate.type().name()).isEqualTo("DASHBOARD");
        assertThat(toValidate.category().name()).isEqualTo("STATIC_HTTP_RESOURCE");
        String fileName =
                toValidate.relativePath().substring(toValidate.relativePath().lastIndexOf('/') + 1);
        assertThat(fileName).startsWith(dashboardType)
                .isEqualTo(originalFileName);
    }
}
