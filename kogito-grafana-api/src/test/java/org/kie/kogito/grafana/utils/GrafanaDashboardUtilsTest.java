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
package org.kie.kogito.grafana.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.grafana.utils.GrafanaDashboardUtils.DISABLED_DOMAIN_DASHBOARDS;
import static org.kie.kogito.grafana.utils.GrafanaDashboardUtils.DISABLED_OPERATIONAL_DASHBOARDS;

class GrafanaDashboardUtilsTest {

    @Test
    void isOperationDashboardEnabled() {
        Map<String, String> propertiesMap = new HashMap<>();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan")).isTrue();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello")).isTrue();
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, "");
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan")).isTrue();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello")).isTrue();

        String values = "Hello";
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, values);
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan")).isTrue();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello")).isFalse();

        values = "Hello,Loan";
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, values);
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Traffic")).isTrue();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan")).isFalse();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello")).isFalse();

        values = " Hello, Loan ";
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, values);
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Traffic")).isTrue();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan")).isFalse();
        assertThat(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello")).isFalse();
    }

    @Test
    void isDomainDashboardEnabled() {
        Map<String, String> propertiesMap = new HashMap<>();

        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan")).isTrue();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello")).isTrue();
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, "");
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan")).isTrue();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello")).isTrue();

        String values = "Hello";
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, values);
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan")).isTrue();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello")).isFalse();

        values = "Hello,Loan";
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, values);
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Traffic")).isTrue();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan")).isFalse();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello")).isFalse();

        values = " Hello, Loan ";
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, values);
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Traffic")).isTrue();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan")).isFalse();
        assertThat(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello")).isFalse();
    }
}