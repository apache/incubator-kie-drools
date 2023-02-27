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
package org.kie.kogito.addons.quarkus.k8s;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Optional;

import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;
import org.kie.kogito.addons.quarkus.k8s.utils.DeploymentUtils;
import org.kie.kogito.addons.quarkus.k8s.utils.IngressUtils;
import org.kie.kogito.addons.quarkus.k8s.utils.PodUtils;
import org.kie.kogito.addons.quarkus.k8s.utils.ServiceUtils;
import org.kie.kogito.addons.quarkus.k8s.utils.StatefulSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;

public class KubeResourceDiscovery implements Serializable {

    private static final long serialVersionUID = 2346322442543423434L;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private KubernetesClient client;

    public KubeResourceDiscovery(KubernetesClient client) {
        this.client = client;
    }

    public Optional<String> query(KubeURI kubeURI) {

        logger.info("Connected to kubernetes cluster {}, current namespace is {}. Resource name for discovery is {}",
                client.getKubernetesVersion().getGitVersion(),
                client.getNamespace(),
                kubeURI.getResourceName());

        if (kubeURI.getNamespace() == null) {
            logger.warn("Namespace is not set, setting namespace to the current context [{}].", client.getNamespace());
            kubeURI.setNamespace(client.getNamespace());
        }

        switch (kubeURI.getGvk().getGVK().toLowerCase(Locale.ROOT)) {
            case KubeConstants.KIND_SERVICE:
                if (isKnative(kubeURI)) {
                    KnativeResourceDiscovery knativeResourceDiscovery = new KnativeResourceDiscovery(client);
                    return knativeResourceDiscovery.queryService(kubeURI.getNamespace(), kubeURI.getResourceName()).map(Object::toString);
                } else {
                    return ServiceUtils.queryServiceByName(client, kubeURI).map(Object::toString);
                }

            case KubeConstants.KIND_KNATIVE_SERVICE:
                KnativeResourceDiscovery knativeResourceDiscovery = new KnativeResourceDiscovery(client);
                return knativeResourceDiscovery.queryService(kubeURI.getNamespace(), kubeURI.getResourceName()).map(Object::toString);

            case KubeConstants.KIND_POD:
                return PodUtils.queryPodByName(client, kubeURI).map(Object::toString);

            case KubeConstants.KIND_DEPLOYMENT:
                return DeploymentUtils.queryDeploymentByName(client, kubeURI).map(Object::toString);

            case KubeConstants.KIND_DEPLOYMENT_CONFIG:
                OpenShiftResourceDiscovery ocpDc = new OpenShiftResourceDiscovery(client);
                return ocpDc.queryDeploymentConfigByName(kubeURI).map(Object::toString);

            case KubeConstants.KIND_STATEFUL_SET:
                return StatefulSetUtils.queryStatefulSetByName(client, kubeURI).map(Object::toString);

            case KubeConstants.KIND_INGRESS:
                return IngressUtils.queryIngressByName(client, kubeURI).map(Object::toString);

            case KubeConstants.KIND_ROUTE:
                OpenShiftResourceDiscovery ocpR = new OpenShiftResourceDiscovery(client);
                return ocpR.queryRouteByName(kubeURI).map(Object::toString);

            default:
                logger.debug("Resource kind {} is not supported yet.", kubeURI.getGvk().getGVK());
                return Optional.empty();
        }
    }

    private boolean isKnative(KubeURI kubeURI) {
        return kubeURI.getProtocol().equals("knative");
    }

}
