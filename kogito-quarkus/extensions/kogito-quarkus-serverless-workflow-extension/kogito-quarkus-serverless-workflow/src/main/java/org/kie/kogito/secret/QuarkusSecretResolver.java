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
package org.kie.kogito.secret;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.serverless.workflow.utils.SecretResolver;

import io.quarkus.runtime.Startup;
import io.smallrye.config.SecretKeys;

@Startup
public class QuarkusSecretResolver implements SecretResolver {

    private Config provider;

    public QuarkusSecretResolver() {
        provider = ConfigProvider.getConfig();
    }

    @Override
    public String apply(String key) {
        try {
            return getValue(key);
        } catch (SecurityException ex) {
            // see https://smallrye.io/docs/smallrye-config/config/secret-keys.html
            return SecretKeys.doUnlocked(() -> getValue(key));
        }
    }

    private String getValue(String key) {
        return provider.getValue(key, String.class);
    }
}
