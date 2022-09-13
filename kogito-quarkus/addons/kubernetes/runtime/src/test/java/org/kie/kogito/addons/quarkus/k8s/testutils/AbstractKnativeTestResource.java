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
package org.kie.kogito.addons.quarkus.k8s.testutils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.client.Config;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public abstract class AbstractKnativeTestResource<T, C extends KnativeClient>
        implements QuarkusTestResourceLifecycleManager {
    protected T server;

    @Override
    public Map<String, String> start() {
        final Map<String, String> systemProps = new HashMap<>();
        systemProps.put(Config.KUBERNETES_TRUST_CERT_SYSTEM_PROPERTY, "true");
        systemProps.put("quarkus.tls.trust-all", "true");
        systemProps.put(Config.KUBERNETES_AUTH_TRYKUBECONFIG_SYSTEM_PROPERTY, "false");
        systemProps.put(Config.KUBERNETES_AUTH_TRYSERVICEACCOUNT_SYSTEM_PROPERTY, "false");
        systemProps.put(Config.KUBERNETES_NAMESPACE_SYSTEM_PROPERTY, "test");
        systemProps.put(Config.KUBERNETES_HTTP2_DISABLE, "true");

        server = createServer();
        initServer();

        try (KnativeClient client = getClient()) {
            systemProps.put(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY, client.getConfiguration().getMasterUrl());
        }

        configureServer();
        // these actually need to be system properties
        // as they are read directly as system props, and not from Quarkus config
        for (Map.Entry<String, String> entry : systemProps.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }

        return systemProps;
    }

    protected abstract KnativeClient getClient();

    /**
     * Can be used by subclasses in order to
     * set up the mock server before the Quarkus application starts
     */
    protected void configureServer() {
    }

    protected void initServer() {
    }

    protected abstract T createServer();

    protected boolean useHttps() {
        return Boolean.getBoolean("quarkus.kubernetes-client.test.https");
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(server,
                new TestInjector.AnnotatedAndMatchesType(getInjectionAnnotation(), getInjectedClass()));
    }

    protected abstract Class<?> getInjectedClass();

    protected abstract Class<? extends Annotation> getInjectionAnnotation();

}