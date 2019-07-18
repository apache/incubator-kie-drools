/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.kubernetes.client;

import org.kie.kogito.cloud.kubernetes.client.operations.IstioGatewayOperations;
import org.kie.kogito.cloud.kubernetes.client.operations.KNativeServiceOperations;
import org.kie.kogito.cloud.kubernetes.client.operations.ServiceOperations;

/**
 * Default {@link KogitoKubeClient} implementation.
 */
public class DefaultKogitoKubeClient implements KogitoKubeClient {

    private ServiceOperations serviceOperation;
    private IstioGatewayOperations istioGatewayOperations;
    private KNativeServiceOperations kNativeServiceOperations;
    private KogitoKubeConfig clientConfig;

    public DefaultKogitoKubeClient() {
        this.clientConfig = new KogitoKubeConfig();
    }

    @Override
    public KogitoKubeConfig getConfig() {
        return this.clientConfig;
    }

    @Override
    public DefaultKogitoKubeClient withConfig(KogitoKubeConfig clientConfig) {
        this.clientConfig = clientConfig;
        return this;
    }

    /**
     * The Services Operations
     */
    @Override
    public ServiceOperations services() {
        if (serviceOperation == null) {
            this.serviceOperation = new ServiceOperations(clientConfig);
        }
        return this.serviceOperation;
    }

    @Override
    public IstioGatewayOperations istioGateway() {
        if (istioGatewayOperations == null) {
            this.istioGatewayOperations = new IstioGatewayOperations(clientConfig);
        }
        return this.istioGatewayOperations;
    }

    @Override
    public KNativeServiceOperations knativeService() {
        if (kNativeServiceOperations == null) {
            this.kNativeServiceOperations = new KNativeServiceOperations(clientConfig);
        }
        return this.kNativeServiceOperations;
    }
}
