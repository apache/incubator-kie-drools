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
package org.kie.kogito.index.test.quarkus.http;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.test.resources.ConditionHolder;
import org.kie.kogito.test.resources.ConditionalQuarkusTestResource;
import org.kie.kogito.test.resources.TestResource;

import static org.kie.kogito.index.test.Constants.KOGITO_DATA_INDEX_SERVICE_URL;

public abstract class AbstractDataIndexHttpQuarkusTestResource<T extends TestResource> extends ConditionalQuarkusTestResource<T> {

    public AbstractDataIndexHttpQuarkusTestResource(T testResource) {
        super(testResource);
    }

    public AbstractDataIndexHttpQuarkusTestResource(T testResource, ConditionHolder condition) {
        super(testResource, condition);
    }

    protected Map<String, String> getDataIndexConnectionProperties() {
        Map<String, String> properties = new HashMap<>();
        String dataIndexUrl = "http://localhost:" + getTestResource().getMappedPort();
        properties.put(KOGITO_DATA_INDEX_SERVICE_URL, dataIndexUrl);
        properties.put("mp.messaging.outgoing.kogito-processinstances-events.connector", "quarkus-http");
        properties.put("mp.messaging.outgoing.kogito-processinstances-events.url", dataIndexUrl + "/processes");
        properties.put("mp.messaging.outgoing.kogito-usertaskinstances-events.connector", "quarkus-http");
        properties.put("mp.messaging.outgoing.kogito-usertaskinstances-events.url", dataIndexUrl + "/tasks");
        properties.put("mp.messaging.outgoing.kogito-variables-events.connector", "quarkus-http");
        properties.put("mp.messaging.outgoing.kogito-variables-events.url", dataIndexUrl);
        properties.put("mp.messaging.outgoing.kogito-jobs-events.connector", "quarkus-http");
        properties.put("mp.messaging.outgoing.kogito-jobs-events.url", dataIndexUrl + "/jobs");
        properties.put("kogito.events.variables.enabled", "false");
        return properties;
    }

}
