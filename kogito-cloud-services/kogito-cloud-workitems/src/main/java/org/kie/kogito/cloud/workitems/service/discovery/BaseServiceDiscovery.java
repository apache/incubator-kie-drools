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
package org.kie.kogito.cloud.workitems.service.discovery;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClient;
import org.kie.kogito.cloud.workitems.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServiceDiscovery implements ServiceDiscovery {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceDiscovery.class);
    static final String KEY_ITEMS = "items";
    static final String KEY_SPEC = "spec";
    static final String KEY_CLUSTER_IP = "clusterIP";
    static final String DEFAULT_PROTOCOL = "http://";
    static final int DEFAULT_PORT = 80;

    protected KogitoKubeClient kubeClient;

    public BaseServiceDiscovery(final KogitoKubeClient kubeClient) {
        this.kubeClient = kubeClient;
    }

    /**
     * Should implement the Services API query logic according to the infrastructure.   
     * 
     * @see {@link KubernetesServiceDiscovery} for reference
     * @param namespace the namespace where to query the service from
     * @param labelKey an optional label key specified by the service
     * @param labelValue an optional label value specified by the service
     * @return a list of <a href="https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.10/#service-v1-core">Service specification</a> in a map structure based on the JSON server response
     */
    protected abstract List<Map<String, Object>> query(final String namespace, Map<String, String> labels);

    /**
     * Should build the {@link ServiceInfo} object based on the {@link #query(String, Map)} returned value.
     * 
     * @param services
     * @return
     */
    protected abstract ServiceInfo buildService(final List<Map<String, Object>> services, final String service);

    private Map<String, String> buildLabelMap(final String labelKey, final String labelValue) {
        if (labelKey == null || labelKey.isEmpty()) {
            return null;
        } else {
            return Collections.singletonMap(labelKey, labelValue);
        }
    }

    public final Optional<ServiceInfo> findEndpoint(String namespace, String labelKey, String labelValue) {
        LOGGER.debug("About to query for endpoints in namespace {} with labels {}:{}", namespace, labelKey, labelValue);
        final List<Map<String, Object>> services = query(namespace, this.buildLabelMap(labelKey, labelValue));
        LOGGER.debug("Result of services query: {}", services);

        if (services.size() > 1) {
            LOGGER.warn("Found more than one endpoint using labels {}:{}. Returning the first one in the list. Try to be more specific in the query search.",
                        labelKey,
                        labelValue);
        } else if (services.isEmpty()) {
            LOGGER.warn("Haven't found any endpoint in the namespace {} with labels {}:{}", namespace, labelKey, labelValue);
            return Optional.empty();
        }

        return Optional.of(this.buildService(services, labelValue == null || labelValue.isEmpty() ? labelKey : labelValue));
    }

    public final Optional<ServiceInfo> findEndpoint(String namespace, String service) {
        return this.findEndpoint(namespace, service, null);
    }

}
