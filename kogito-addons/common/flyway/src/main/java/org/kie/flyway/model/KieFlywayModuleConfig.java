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

package org.kie.flyway.model;

import java.util.Map;
import java.util.TreeMap;

public class KieFlywayModuleConfig {

    public static final String DEFAULT_DB = "default";

    private final String module;
    private final Map<String, String[]> locations = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public KieFlywayModuleConfig(String module, Map<String, String[]> locations) {
        this.module = module;
        this.locations.putAll(locations);
    }

    public String getModule() {
        return module;
    }

    public String[] getDBScriptLocations(String dbType) {
        return this.locations.getOrDefault(dbType, locations.get(DEFAULT_DB));
    }

}
