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
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

import static org.kie.kogito.swf.tools.custom.dashboard.impl.CustomDashboardStorageImpl.PROJECT_CUSTOM_DASHBOARD_STORAGE_PROP;

public class CustomDashboardStorageTestProfile implements QuarkusTestProfile {

    private String storagePath;

    public CustomDashboardStorageTestProfile() throws IOException {
        File storage = Files.createTempDirectory("CustomDashboardStorageTestProfile").toFile();
        storage.deleteOnExit();
        storage.mkdir();
        storagePath = storage.getAbsolutePath();
    }

    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap(PROJECT_CUSTOM_DASHBOARD_STORAGE_PROP, storagePath);
    }
}
