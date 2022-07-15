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

package org.kie.kogito.index.testcontainers;

/**
 * This container wraps Data Index Service container
 */
public class DataIndexOracleContainer extends AbstractDataIndexContainer {
    public static final String NAME = "data-index-service-oracle";

    public DataIndexOracleContainer() {
        super(NAME);
    }

    public void setDatabaseURL(String oracleURL, String username, String password) {
        addEnv("QUARKUS_DATASOURCE_JDBC_URL", oracleURL);
        addEnv("QUARKUS_DATASOURCE_USERNAME", username);
        addEnv("QUARKUS_DATASOURCE_PASSWORD", password);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

}
