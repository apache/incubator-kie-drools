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
package org.kie.kogito.persistence.protobuf.domain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.infinispan.protostream.impl.AnnotatedDescriptorImpl;
import org.kie.kogito.persistence.api.proto.AttributeDescriptor;
import org.kie.kogito.persistence.api.proto.DomainDescriptor;
import org.kie.kogito.persistence.api.proto.DomainModelRegisteredEvent;
import org.kie.kogito.persistence.protobuf.FileDescriptorRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class ProtoDomainModelProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoDomainModelProducer.class);

    @Inject
    Event<DomainModelRegisteredEvent> domainEvent;

    public void onFileDescriptorRegistered(@Observes FileDescriptorRegisteredEvent event) {
        FileDescriptor descriptor = event.getDescriptor();
        String rootMessage = (String) descriptor.getOption("kogito_model").getValue();
        String processId = (String) descriptor.getOption("kogito_id").getValue();

        Map<String, Descriptor> map = descriptor.getMessageTypes().stream().collect(toMap(AnnotatedDescriptorImpl::getName, desc -> desc));

        Descriptor rootDescriptor = map.remove(rootMessage);

        DomainDescriptor domain = new DomainDescriptorMapper().apply(rootDescriptor);

        List<DomainDescriptor> additionalTypes = map.values().stream().map(desc -> new DomainDescriptorMapper().apply(desc)).collect(toList());

        domainEvent.fire(new DomainModelRegisteredEvent(processId, domain, additionalTypes));
    }

    private class FieldDescriptorMapper implements Function<FieldDescriptor, AttributeDescriptor> {

        @Override
        public AttributeDescriptor apply(FieldDescriptor field) {
            return new AttributeDescriptor(field.getName(), new FieldTypeMapper().apply(field), field.getLabel().toString());
        }
    }

    private class DomainDescriptorMapper implements Function<Descriptor, DomainDescriptor> {

        @Override
        public DomainDescriptor apply(Descriptor descriptor) {
            DomainDescriptor domain = new DomainDescriptor();
            LOGGER.debug("Mapping domain from message, type: {}", descriptor.getFullName());
            domain.setTypeName(descriptor.getFullName());
            domain.setAttributes(descriptor.getFields().stream().map(fd -> new FieldDescriptorMapper().apply(fd)).collect(toList()));
            return domain;
        }
    }

    private class FieldTypeMapper implements Function<FieldDescriptor, String> {

        @Override
        public String apply(FieldDescriptor fd) {
            switch (fd.getJavaType()) {
                case INT:
                    return Integer.class.getName();
                case LONG:
                    return Long.class.getName();
                case FLOAT:
                    return Float.class.getName();
                case DOUBLE:
                    return Double.class.getName();
                case BOOLEAN:
                    return Boolean.class.getName();
                case MESSAGE:
                    Option option = fd.getOptions().stream()
                            .filter(o -> "kogito_java_class".equals(o.getName()))
                            .findAny().orElse(null);
                    if (option != null) {
                        return option.getValue().toString();
                    }
                    return fd.getMessageType().getFullName();
                default:
                    return String.class.getName();
            }
        }
    }

}
