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
package org.kie.kogito.event.impl;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.event.CloudEventFactory;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData.ToBytes;

public class JacksonCloudEventFactory implements CloudEventFactory {

    private ToBytes<Object> toBytes;

    public JacksonCloudEventFactory(ToBytes<Object> toBytes) {
        this.toBytes = toBytes;
    }

    private void withExtension(CloudEventBuilder builder, String name, String value) {
        if (value != null) {
            builder.withExtension(name, value);
        }
    }

    @Override
    public CloudEvent build(Object data, String trigger, KogitoProcessInstance pi) {
        CloudEventBuilder builder = CloudEventBuilder.v1().withType(trigger).withSource(URI.create("/process/" + pi.getProcessId()))
                .withTime(OffsetDateTime.now()).withId(UUID.randomUUID().toString());

        withExtension(builder, CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID, pi.getParentProcessInstanceId());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID, pi.getRootProcessId());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID, pi.getRootProcessInstanceId());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_REFERENCE_ID, pi.getReferenceId());
        withExtension(builder, CloudEventExtensionConstants.BUSINESS_KEY, pi.getBusinessKey());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_INSTANCE_ID, pi.getId());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_ID, pi.getProcessId());
        builder.withExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_STATE, pi.getState());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_TYPE, pi.getProcess().getType());
        withExtension(builder, CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION, pi.getProcess().getVersion());

        builder.withData(CloudEventUtils.fromObject(data, toBytes));
        //setting correlation as extension attributes
        pi.unwrap().correlation()
                .stream()
                .map(c -> CompositeCorrelation.class.isInstance(c) ? CompositeCorrelation.class.cast(c).getValue() : Collections.singleton(c))
                .flatMap(Set::stream)
                .forEach(c -> builder.withExtension(c.getKey(), c.asString()));
        return builder.build();
    }
}
