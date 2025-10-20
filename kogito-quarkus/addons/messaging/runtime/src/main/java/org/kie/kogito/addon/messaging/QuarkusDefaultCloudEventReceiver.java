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
package org.kie.kogito.addon.messaging;

import org.kie.kogito.addon.quarkus.messaging.common.AbstractQuarkusCloudEventReceiver;
import org.kie.kogito.addon.quarkus.messaging.common.KogitoMessaging;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.EventUnmarshaller;

import io.quarkus.arc.DefaultBean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@DefaultBean
@ApplicationScoped
@Named("Receiver-default")
public class QuarkusDefaultCloudEventReceiver extends AbstractQuarkusCloudEventReceiver<Object> {

    @Inject
    @KogitoMessaging
    CloudEventUnmarshallerFactory<Object> cloudEventUnmarshaller;

    @Inject
    @KogitoMessaging
    EventUnmarshaller<Object> eventUnmarshaller;

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "kogito.messaging.as-cloudevents", defaultValue = "true")
    protected Boolean useCloudEvents;

    @Override
    protected CloudEventUnmarshallerFactory<Object> getCloudEventUnmarshallerFactory() {
        return cloudEventUnmarshaller;
    }

    @Override
    protected EventUnmarshaller<Object> getEventUnmarshaller() {
        return eventUnmarshaller;
    }

}
