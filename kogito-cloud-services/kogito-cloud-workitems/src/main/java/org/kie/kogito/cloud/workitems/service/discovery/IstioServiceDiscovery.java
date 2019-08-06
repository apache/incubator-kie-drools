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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClient;
import org.kie.kogito.cloud.kubernetes.client.operations.MapWalker;
import org.kie.kogito.cloud.workitems.ServiceInfo;

public class IstioServiceDiscovery extends BaseServiceDiscovery {

    private static final String SERVICE_KEY_HOST = "HOST";
    private static final String KEY_STATUS = "status";
    /**
     * Key field for service endpoint on KNative Serving 0.6.x and bellow
     */
    private static final String KEY_DOMAIN = "domain";
    /**
     * Key field for service endpoint on KNative Serving 0.7+
     * 
     * @see <a href="https://github.com/knative/serving/blob/master/docs/spec/spec.md#service">KNative Serving Service spec</a>
     */
    private static final String KEY_URL = "url";
    private static final String PROTOCOL_REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)";

    private final String istioGatewayUrl;

    public IstioServiceDiscovery(final KogitoKubeClient kubeClient, final String istioGatewayUrl) {
        super(kubeClient);
        this.istioGatewayUrl = istioGatewayUrl;
    }

    @Override
    protected List<Map<String, Object>> query(String namespace, Map<String, String> labels) {
        return kubeClient.knativeService()
                         .listNamespaced(namespace, labels)
                         .asMapWalker()
                         .mapToListMap(KEY_ITEMS)
                         .asList();
    }

    @Override
    protected ServiceInfo buildService(List<Map<String, Object>> services, final String service) {
        final Map<String, String> headers = new HashMap<>();
        final Map<String, Object> response = new MapWalker(services.get(0)).mapToMap(KEY_STATUS).asMap();
        String endpoint = "";
        if (response.containsKey(KEY_URL)) {
            endpoint = response.get(KEY_URL).toString().replaceAll(PROTOCOL_REGEX, "").replaceAll(PROTOCOL_REGEX, "");
            LOGGER.debug("Found key {} using endpoint: {}", KEY_URL, endpoint);
        } else {
            endpoint = response.get(KEY_DOMAIN).toString();
            LOGGER.debug("Found key {} using endpoint: {}", KEY_DOMAIN, endpoint);
        }
        headers.put(SERVICE_KEY_HOST, endpoint);
        LOGGER.debug("Headers to be used for requests {}", headers);
        return new ServiceInfo(String.format("%s%s", this.istioGatewayUrl, service), headers);
    }

}
