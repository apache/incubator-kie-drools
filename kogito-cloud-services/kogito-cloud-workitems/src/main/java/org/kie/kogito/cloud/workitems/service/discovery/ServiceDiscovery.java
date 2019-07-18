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

import java.util.Optional;

import org.kie.kogito.cloud.workitems.ServiceInfo;

/**
 * Service Discovery mechanism: tries to discover the desired endpoint based on the cluster infrastructure: Kubernetes, OpenShift, Istio/KNative or Operators. 
 */
public interface ServiceDiscovery {

    /**
     * Finds an endpoint based on a namespace and the specified label. 
     * If more than one service is found, the first one is returned.  
     * The service must reside within the namespace. 
     * 
     * @param namespace the namespace where to look for the namespace. Can't be null.
     * @param labelKey optional label key.
     * @param labelValue optional label value. Ignored if key is empty.
     * @return
     */
    public Optional<ServiceInfo> findEndpoint(final String namespace, final String labelKey, final String labelValue);

    /**
     * Finds an endpoint based on a namespace with a label key equals to the name of the service. 
     * If more than one service is found, the first one is returned. 
     * The service must reside within the namespace.   
     * 
     * @param namespace the namespace where to look for the namespace. Can't be null.
     * @param service the service name that should match with any label in the Kubernetes Service.    
     * @return
     */
    public Optional<ServiceInfo> findEndpoint(final String namespace, final String service);

}
