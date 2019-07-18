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
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.net.MalformedURLException;

import org.kie.kogito.cloud.kubernetes.client.KogitoKubeConfig;

/**
 * Wrapper for service operations on Kubernetes Client that resolves the responses from the API calls to Maps.
 * @see <a href="https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.14/#service-v1-core">Kubernetes API Reference - Services</a>
 */
public class ServiceOperations extends BaseListOperations {

    public ServiceOperations(final KogitoKubeConfig clientConfig) {
        super(clientConfig);
    }

    @Override
    protected String buildBaseUrl(String namespace) throws MalformedURLException {
        return this.getClientConfig().getServiceOperationURL(namespace).toExternalForm();
    }
}
