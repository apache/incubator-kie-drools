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

public class KubernetesServiceDiscovery extends BaseServiceDiscovery {

    private static final String KEY_PORTS = "ports";
    private static final String KEY_PORT = "port";

    public KubernetesServiceDiscovery(final KogitoKubeClient kubeClient) {
        super(kubeClient);
    }

    @Override
    protected List<Map<String, Object>> query(String namespace, Map<String, String> labels) {
        return kubeClient.services()
                         .listNamespaced(namespace, labels)
                         .asMapWalker()
                         .mapToListMap(KEY_ITEMS)
                         .asList();
    }

    @Override
    protected ServiceInfo buildService(List<Map<String, Object>> services, String service) {
        final StringBuilder url = new StringBuilder();
        url
           .append(DEFAULT_PROTOCOL)
           .append(new MapWalker(services.get(0)).mapToMap(KEY_SPEC).asMap().get(KEY_CLUSTER_IP))
           .append(":")
           .append(new MapWalker(services.get(0)).mapToMap(KEY_SPEC)
                                                 .mapToListMap(KEY_PORTS)
                                                 .listToMap(0)
                                                 .asMap().get(KEY_PORT))
           .append("/")
           .append(service);

        return new ServiceInfo(url.toString(), new HashMap<>());
    }

}
