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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.kogito.event.Converter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;

public class CloudEventWrapDataEvent<T> implements DataEvent<T> {

    private final CloudEvent cloudEvent;
    private final Converter<CloudEventData, T> unmarshaller;
    private final AtomicReference<T> data;

    public CloudEventWrapDataEvent(CloudEvent cloudEvent, Converter<CloudEventData, T> unmarshaller) {
        this.cloudEvent = cloudEvent;
        this.unmarshaller = unmarshaller;
        data = new AtomicReference<>();
    }

    @Override
    public SpecVersion getSpecVersion() {
        return cloudEvent.getSpecVersion();
    }

    @Override
    public String getId() {
        return cloudEvent.getId();
    }

    @Override
    public String getType() {
        return cloudEvent.getType();
    }

    @Override
    public URI getSource() {
        return cloudEvent.getSource();
    }

    @Override
    public String getDataContentType() {
        return cloudEvent.getDataContentType();
    }

    @Override
    public URI getDataSchema() {
        return cloudEvent.getDataSchema();
    }

    @Override
    public String getSubject() {
        return cloudEvent.getSubject();
    }

    @Override
    public OffsetDateTime getTime() {
        return cloudEvent.getTime();
    }

    @Override
    public Object getAttribute(String attributeName) throws IllegalArgumentException {
        return cloudEvent.getAttribute(attributeName);
    }

    @Override
    public Object getExtension(String extensionName) {
        return cloudEvent.getExtension(extensionName);
    }

    @Override
    public Set<String> getExtensionNames() {
        return cloudEvent.getExtensionNames();
    }

    @Override
    public T getData() {
        CloudEventData cloudEventData = cloudEvent.getData();
        if (cloudEventData == null) {
            return null;
        }
        T result = data.get();
        if (result == null) {
            try {
                result = unmarshaller.convert(cloudEventData);
            } catch (IOException io) {
                throw new UncheckedIOException(io);
            }
            data.set(result);
        }
        return result;
    }

    @Override
    public String getKogitoProcessInstanceId() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_ID);
    }

    @Override
    public String getKogitoRootProcessInstanceId() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID);
    }

    @Override
    public String getKogitoProcessId() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_ID);
    }

    @Override
    public String getKogitoRootProcessId() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID);
    }

    @Override
    public String getKogitoAddons() {
        return (String) getExtension(CloudEventExtensionConstants.ADDONS);
    }

    @Override
    public String getKogitoParentProcessInstanceId() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID);
    }

    @Override
    public String getKogitoProcessInstanceState() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_STATE);
    }

    @Override
    public String getKogitoReferenceId() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID);
    }

    @Override
    public String getKogitoBusinessKey() {
        return (String) getExtension(CloudEventExtensionConstants.BUSINESS_KEY);
    }

    @Override
    public String getKogitoStartFromNode() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_START_FROM_NODE);
    }

    @Override
    public String getKogitoProcessInstanceVersion() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION);
    }

    @Override
    public String getKogitoProcessType() {
        return (String) getExtension(CloudEventExtensionConstants.PROCESS_TYPE);
    }

    @Override
    public CloudEvent asCloudEvent() {
        return cloudEvent;
    }
}
