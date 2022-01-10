/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.addon.quarkus.messaging.common;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.addon.cloudevents.AbstractTopicsInformationResource;
import org.kie.kogito.event.TopicDiscovery;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

@Path("/messaging/topics")
@ApplicationScoped()
public class QuarkusTopicsInformationResource extends AbstractTopicsInformationResource {

    @Inject
    private TopicDiscovery topicDiscovery;

    @Inject
    private Instance<CloudEventMeta> cloudEventMetaIterable;

    @PostConstruct
    private void onPostConstruct() {
        setup(topicDiscovery, cloudEventMetaIterable);
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response getTopics() {
        return javax.ws.rs.core.Response.ok(getTopicList()).build();
    }
}
