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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.grafana.utils.GrafanaDashboardUtils.DISABLED_DOMAIN_DASHBOARDS;
import static org.kie.kogito.grafana.utils.GrafanaDashboardUtils.DISABLED_OPERATIONAL_DASHBOARDS;

class GrafanaDashboardUtilsTest {

    @Test
    void isOperationDashboardEnabled() {
        Map<String, String> propertiesMap = new HashMap<>();
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan"));
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello"));
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, "");
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan"));
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello"));

        String values = "Hello";
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, values);
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan"));
        assertFalse(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello"));

        values = "Hello,Loan";
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, values);
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Traffic"));
        assertFalse(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan"));
        assertFalse(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello"));

        values = " Hello, Loan ";
        propertiesMap.put(DISABLED_OPERATIONAL_DASHBOARDS, values);
        assertTrue(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Traffic"));
        assertFalse(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Loan"));
        assertFalse(GrafanaDashboardUtils.isOperationDashboardEnabled(propertiesMap, "Hello"));
    }

    @Test
    void isDomainDashboardEnabled() {
        Map<String, String> propertiesMap = new HashMap<>();

        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan"));
        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello"));
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, "");
        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan"));
        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello"));

        String values = "Hello";
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, values);
        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan"));
        assertFalse(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello"));

        values = "Hello,Loan";
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, values);
        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Traffic"));
        assertFalse(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan"));
        assertFalse(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello"));

        values = " Hello, Loan ";
        propertiesMap.put(DISABLED_DOMAIN_DASHBOARDS, values);
        assertTrue(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Traffic"));
        assertFalse(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Loan"));
        assertFalse(GrafanaDashboardUtils.isDomainDashboardEnabled(propertiesMap, "Hello"));
    }
}