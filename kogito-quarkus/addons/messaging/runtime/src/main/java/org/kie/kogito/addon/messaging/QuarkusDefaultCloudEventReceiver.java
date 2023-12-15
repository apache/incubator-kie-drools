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
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.EventUnmarshaller;

import io.quarkus.arc.DefaultBean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@DefaultBean
@ApplicationScoped
public class QuarkusDefaultCloudEventReceiver extends AbstractQuarkusCloudEventReceiver<Object> {

    @Inject
    ConfigBean configBean;

    @Inject
    CloudEventUnmarshallerFactory<Object> cloudEventUnmarshaller;

    @Inject
    EventUnmarshaller<Object> eventUnmarshaller;

    @PostConstruct
    void init() {
        if (configBean.useCloudEvents()) {
            setCloudEventUnmarshaller(cloudEventUnmarshaller);
        } else {
            setEventDataUnmarshaller(eventUnmarshaller);
        }
    }
}
