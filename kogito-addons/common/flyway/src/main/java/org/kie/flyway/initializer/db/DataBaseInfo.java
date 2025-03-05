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

package org.kie.flyway.initializer.db;

public class DataBaseInfo {

    private final String name;
    private final String version;
    private final String normalizedName;

    public DataBaseInfo(String name, String version) {
        this.name = name;
        this.version = version;
        this.normalizedName = normalizeName(name);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    private String normalizeName(String name) {
        final String NORMALIZATION_REGEX = "[^a-zA-Z0-9]+";
        String[] fragments = name.split(NORMALIZATION_REGEX);
        return String.join("-", fragments).toLowerCase();
    }

    public String toString() {
        return "DataBaseInfo [name=" + name + ", version=" + version + ", normalizedName=" + normalizedName + "]";
    }
}
