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

package org.kie.kogito.addon.cloudevents;

import java.util.List;
import java.util.stream.StreamSupport;

import org.kie.kogito.event.Topic;
import org.kie.kogito.event.TopicDiscovery;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

import static java.util.stream.Collectors.toList;

public class AbstractTopicsInformationResource {

    private TopicDiscovery topicDiscovery;
    private List<CloudEventMeta> cloudEventMetaList;

    protected void setup(TopicDiscovery topicDiscovery, Iterable<CloudEventMeta> cloudEventMetaIterable) {
        this.topicDiscovery = topicDiscovery;
        this.cloudEventMetaList = StreamSupport.stream(cloudEventMetaIterable.spliterator(), false).collect(toList());
    }

    protected List<Topic> getTopicList() {
        return topicDiscovery.getTopics(cloudEventMetaList);
    }
}
