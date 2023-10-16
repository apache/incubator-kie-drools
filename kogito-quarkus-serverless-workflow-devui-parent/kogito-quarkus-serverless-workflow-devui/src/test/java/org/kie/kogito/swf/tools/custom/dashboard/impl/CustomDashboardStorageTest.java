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
package org.kie.kogito.swf.tools.custom.dashboard.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.swf.tools.custom.dashboard.CustomDashboardStorage;
import org.kie.kogito.swf.tools.custom.dashboard.model.CustomDashboardFilter;
import org.kie.kogito.swf.tools.custom.dashboard.model.CustomDashboardInfo;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomDashboardStorageTest {

    private static String[] DASHBOARD_NAMES = { "age.dash.yml", "products.dash.yaml" };
    private static String DASHBOARD_NAME = "age.dash.yml";

    private CustomDashboardStorage customDashboardStorage;
    private URL tempFolder;

    @BeforeAll
    public void init() {
        tempFolder = Thread.currentThread().getContextClassLoader().getResource("custom/dashboards/");

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
    public void testHotReloading() throws IOException {
        String storageUrl = Thread.currentThread().getContextClassLoader().getResource("custom/dashboards/").getFile();
        File srcFile = new File(storageUrl + "products.dash.yaml");
        File targetFile = new File(storageUrl + "copy.dash.yml");

        assertEquals(false, targetFile.exists());
        FileUtils.copyFile(srcFile, targetFile);
        assertEquals(true, targetFile.exists());
        await().atMost(20, TimeUnit.SECONDS).until(() -> testBeforeDelete());
        Collection<CustomDashboardInfo> customDashboardInfoFilterAllBeforeDelete = customDashboardStorage.getCustomDashboardFiles(null);
        assertEquals(3, customDashboardInfoFilterAllBeforeDelete.size());

        assertEquals(true, targetFile.exists());
        FileUtils.delete(targetFile);
        assertEquals(false, targetFile.exists());
        await().atMost(20, TimeUnit.SECONDS).until(() -> testAfterDelete());

        Collection<CustomDashboardInfo> customDashboardInfoFilterAllAfterDelete = customDashboardStorage.getCustomDashboardFiles(null);
        assertEquals(2, customDashboardInfoFilterAllAfterDelete.size());
    }

    private boolean testBeforeDelete() {
        if (customDashboardStorage.getCustomDashboardFiles(null).size() == 3) {
            return true;
        }
        return false;
    }

    private boolean testAfterDelete() {
        if (customDashboardStorage.getCustomDashboardFiles(null).size() == 2) {
            return true;
        }
        return false;
    }

    @Test
    public void testGetFormContent() throws IOException {
        String content = customDashboardStorage.getCustomDashboardFileContent(DASHBOARD_NAME);
        assertNotNull(content);
    }
}
