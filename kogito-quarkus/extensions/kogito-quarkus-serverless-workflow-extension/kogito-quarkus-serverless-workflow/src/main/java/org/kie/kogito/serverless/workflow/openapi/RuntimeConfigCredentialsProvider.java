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
package org.kie.kogito.serverless.workflow.openapi;

import java.util.Optional;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;
import io.quarkus.restclient.runtime.RestClientBuilderFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RuntimeConfigCredentialsProvider extends ConfigCredentialsProvider {

    protected String getConfigKey(CredentialsContext context) {
        return RestClientBuilderFactory.buildConfigKey(context.getOpenApiSpecId(),
                Optional.ofNullable((String) context.getRequestContext().getProperty(OpenApiWorkItemHandler.CONFIG_KEY_SUFFIX_PROP)));
    }
}
