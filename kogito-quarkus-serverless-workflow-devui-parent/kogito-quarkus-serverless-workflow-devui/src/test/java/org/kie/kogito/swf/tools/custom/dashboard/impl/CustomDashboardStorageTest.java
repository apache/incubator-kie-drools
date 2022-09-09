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

package org.kie.kogito.swf.tools.custom.dashboard.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.swf.tools.custom.dashboard.CustomDashboardStorage;
import org.kie.kogito.swf.tools.custom.dashboard.model.CustomDashboardFilter;
import org.kie.kogito.swf.tools.custom.dashboard.model.CustomDashboardInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomDashboardStorageTest {

    private static String[] DASHBOARD_NAMES = { "age.dash.yaml", "products.dash.yaml" };
    private static String DASHBOARD_NAME = "age.dash.yaml";

    private CustomDashboardStorage customDashboardStorage;

    @BeforeAll
    public void init() {
        URL tempFolder = Thread.currentThread().getContextClassLoader().getResource("custom/dashboard/");

        customDashboardStorage = new CustomDashboardStorageImpl(tempFolder);
    }

    @Test
    public void testGetFormInfoList() {
        Collection<CustomDashboardInfo> customDashboardInfoFilterAll = customDashboardStorage.getCustomDashboardFiles(null);
        assertEquals(2, customDashboardInfoFilterAll.size());

        CustomDashboardFilter filterEmpty = new CustomDashboardFilter();
        filterEmpty.setNames(Collections.emptyList());
        Collection<CustomDashboardInfo> customDashboardInfoAllEmptyFilter = customDashboardStorage.getCustomDashboardFiles(filterEmpty);
        assertEquals(2, customDashboardInfoAllEmptyFilter.size());

        CustomDashboardFilter filter = new CustomDashboardFilter();
        filter.setNames(Arrays.asList(DASHBOARD_NAMES));

        Collection<CustomDashboardInfo> formInfos = customDashboardStorage.getCustomDashboardFiles(filter);
        assertEquals(2, formInfos.size());
    }

    @Test
    public void testGetFormContent() throws IOException {
        String content = customDashboardStorage.getCustomDashboardFileContent(DASHBOARD_NAME);
        assertNotNull(content);
    }
}
