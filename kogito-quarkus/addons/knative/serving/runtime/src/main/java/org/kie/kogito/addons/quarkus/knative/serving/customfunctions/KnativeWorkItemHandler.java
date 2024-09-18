/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalog;
import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalogKey;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemExecutionException;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kogito.workitem.rest.RestWorkItemHandler;

import io.vertx.mutiny.ext.web.client.WebClient;

import static org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol.KNATIVE;

public final class KnativeWorkItemHandler extends RestWorkItemHandler {

    public static final String APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8 = "application/cloudevents+json; charset=UTF-8";

    public static final String NAME = "knative";

    public static final String ID = "id";

    public static final String PATH_PROPERTY_NAME = "knative_function_path";

    public static final String SERVICE_PROPERTY_NAME = "knative_function_service";

    public static final String PAYLOAD_FIELDS_PROPERTY_NAME = "knative_function_payload_fields";

    public static final String CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE = "A Knative custom function argument cannot be a CloudEvent when the 'asCloudEvent' property are not set to 'true'";

    private final KubernetesServiceCatalog kubernetesServiceCatalog;

    public KnativeWorkItemHandler(WebClient httpClient, WebClient httpsClient, KubernetesServiceCatalog kubernetesServiceCatalog) {
        super(httpClient, httpsClient);
        this.kubernetesServiceCatalog = kubernetesServiceCatalog;
    }

    @Override
    public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        Map<String, Object> parameters = workItem.getParameters();
        parameters.put(RestWorkItemHandler.URL, getUrl(parameters));

        return super.activateWorkItemHandler(manager, handler, workItem, transition);
    }

    private String getUrl(Map<String, Object> parameters) {
        return getServiceAddress(parameters) + parameters.remove(PATH_PROPERTY_NAME);
    }

    private String getServiceAddress(Map<String, Object> parameters) {
        String service = (String) parameters.remove(SERVICE_PROPERTY_NAME);

        return kubernetesServiceCatalog.getServiceAddress(new KubernetesServiceCatalogKey(KNATIVE, service))
                .map(URI::toString)
                .orElseThrow(() -> new WorkItemExecutionException("The Knative service '" + service
                        + "' could not be found."));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
