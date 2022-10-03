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

package org.kie.kogito.serverless.workflow.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Default configurations for Quarkus extensions used by Serverless Workflow.
 */
public class ServerlessWorkflowConfigSource implements ConfigSource {

    private static final Map<String, String> configuration = new HashMap<>();

    /**
     * Forcing the openapi spec generation of the workflow to add the operationId in the resulting file.
     * 
     * @see <a href="https://issues.redhat.com/browse/KOGITO-7412">Openapi files generated for ServerlessWorkflow doesn't contain operationId attribute</a>
     * @see <a href="https://quarkus.io/guides/openapi-swaggerui#auto-generation-of-operation-id">Auto-generation of Operation Id</a>
     */
    private static final String OPENAPI_GEN_STRATEGY = "quarkus.smallrye-openapi.operation-id-strategy";
    private static final String DEFAULT_OPENAPI_GEN_STRATEGY = "METHOD";

    static {
        configuration.put(OPENAPI_GEN_STRATEGY, DEFAULT_OPENAPI_GEN_STRATEGY);
    }

    @Override
    public Map<String, String> getProperties() {
        return configuration;
    }

    @Override
    public Set<String> getPropertyNames() {
        return configuration.keySet();
    }

    /**
     * We only consider our config if the user has not added these properties to their project
     *
     * @see <a href="https://quarkus.io/guides/config-extending-support#example">Quarkus Config Extending Support</a>
     */
    @Override
    public int getOrdinal() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String getValue(String propertyName) {
        return configuration.get(propertyName);
    }

    @Override
    public String getName() {
        return ServerlessWorkflowConfigSource.class.getSimpleName();
    }
}
