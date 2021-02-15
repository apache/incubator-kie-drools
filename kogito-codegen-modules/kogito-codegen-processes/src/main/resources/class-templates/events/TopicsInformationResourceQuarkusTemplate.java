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
package org.kie.kogito.app;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.event.CloudEventMeta;
import org.kie.kogito.event.TopicDiscovery;

@Path("/messaging/topics")
public class TopicsInformationResource {

    TopicDiscovery discovery;

    private List<CloudEventMeta> eventsMeta;

    public TopicsInformationResource() {
        eventsMeta = new ArrayList<>();
        /*
         * $repeat$
         * eventsMeta.add(new CloudEventMeta("$type$", "$source$", $kind$));
         * $end_repeat$
         */
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response getTopics() {
        return javax.ws.rs.core.Response.ok(discovery.getTopics(eventsMeta)).build();
    }
}