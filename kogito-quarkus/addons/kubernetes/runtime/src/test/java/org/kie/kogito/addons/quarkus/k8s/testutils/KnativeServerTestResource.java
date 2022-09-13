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
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.client.NamespacedKnativeClient;
import io.fabric8.knative.mock.KnativeServer;
import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;

public class KnativeServerTestResource extends AbstractKnativeTestResource<KnativeServer, NamespacedKnativeClient>
        implements QuarkusTestResourceConfigurableLifecycleManager<WithKnativeTestServer> {

    private boolean https = false;
    private boolean crud = true;
    private Consumer<KnativeServer> setup;

    public void init(WithKnativeTestServer annotation) {
        this.https = annotation.https();
        this.crud = annotation.crud();
        try {
            this.setup = annotation.setup().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected KnativeClient getClient() {
        return server.getKnativeClient();
    }

    protected void initServer() {
        server.before();
    }

    protected void configureServer() {
        if (setup != null)
            setup.accept(server);
    }

    protected KnativeServer createServer() {
        return new KnativeServer(https, crud);
    }

    public void stop() {
        if (server != null) {
            server.after();
            server = null;
        }
    }

    protected Class<?> getInjectedClass() {
        return KnativeServer.class;
    }

    protected Class<? extends Annotation> getInjectionAnnotation() {
        return KnativeTestServer.class;
    }
}
