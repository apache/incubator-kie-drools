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
package org.kie.kogito.persistence.protobuf;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.persistence.api.schema.EntityIndexDescriptor;
import org.kie.kogito.persistence.api.schema.ProcessDescriptor;
import org.kie.kogito.persistence.api.schema.SchemaDescriptor;
import org.kie.kogito.persistence.api.schema.SchemaRegisteredEvent;
import org.kie.kogito.persistence.api.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.kie.kogito.persistence.protobuf.ProtoIndexParser.INDEXED_ANNOTATION;
import static org.kie.kogito.persistence.protobuf.ProtoIndexParser.configureBuilder;
import static org.kie.kogito.persistence.protobuf.ProtoIndexParser.createEntityIndexDescriptors;

@ApplicationScoped
public class ProtobufService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufService.class);

    static final SchemaType SCHEMA_TYPE = new SchemaType("proto");

    static final String DOMAIN_MODEL_PROTO_NAME = "domainModel";

    @Inject
    FileDescriptorSource kogitoDescriptors;

    @Inject
    ProtobufMonitorService protobufMonitorService;

    @Inject
    Event<FileDescriptorRegisteredEvent> domainModelEvent;

    @Inject
    Event<SchemaRegisteredEvent> schemaEvent;

    void onStart(@Observes StartupEvent ev) {
        kogitoDescriptors.getFileDescriptors().forEach((name, bytes) -> {
            LOGGER.info("Registering Kogito ProtoBuffer file: {}", name);
            String content = new String(bytes);
            try {
                SerializationContext ctx = createSerializationContext(FileDescriptorSource.fromString(name, content));
                FileDescriptor desc = ctx.getFileDescriptors().get(name);
                Map<String, EntityIndexDescriptor> entityIndexes = desc.getMessageTypes().stream().map(t -> t.<EntityIndexDescriptor> getProcessedAnnotation(INDEXED_ANNOTATION))
                        .filter(Objects::nonNull).collect(toMap(EntityIndexDescriptor::getName, Function.identity()));
                Map<String, EntityIndexDescriptor> entityIndexDescriptors = createEntityIndexDescriptors(desc, entityIndexes);

                schemaEvent.fire(new SchemaRegisteredEvent(new SchemaDescriptor(name, content, entityIndexDescriptors, null), SCHEMA_TYPE));
            } catch (ProtobufValidationException e) {
                throw new ProtobufFileRegistrationException(e);
            }
        });

        protobufMonitorService.startMonitoring();
    }

    public void registerProtoBufferType(String content) throws ProtobufValidationException {
        LOGGER.debug("Registering new ProtoBuffer file with content: \n{}", content);

        SerializationContext ctx = createSerializationContext(kogitoDescriptors, FileDescriptorSource.fromString(DOMAIN_MODEL_PROTO_NAME, content));
        FileDescriptor desc = ctx.getFileDescriptors().get(DOMAIN_MODEL_PROTO_NAME);
        Option processIdOption = desc.getOption("kogito_id");
        if (processIdOption == null || processIdOption.getValue() == null) {
            throw new ProtobufValidationException("Missing marker for process id in proto file, please add option kogito_id=\"processid\"");
        }
        String processId = (String) processIdOption.getValue();

        Option model = desc.getOption("kogito_model");
        if (model == null || model.getValue() == null) {
            throw new ProtobufValidationException("Missing marker for main message type in proto file, please add option kogito_model=\"messagename\"");
        }
        String messageName = (String) model.getValue();
        String fullTypeName = desc.getPackage() == null ? messageName : desc.getPackage() + "." + messageName;

        Descriptor descriptor;
        try {
            descriptor = ctx.getMessageDescriptor(fullTypeName);
        } catch (IllegalArgumentException ex) {
            throw new ProtobufValidationException(format("Could not find message with name: %s in proto file, e, please review option kogito_model", fullTypeName));
        }

        validateDescriptorField(messageName, descriptor, Constants.KOGITO_DOMAIN_ATTRIBUTE);

        Map<String, EntityIndexDescriptor> entityIndexes = desc.getMessageTypes().stream().map(t -> t.<EntityIndexDescriptor> getProcessedAnnotation(INDEXED_ANNOTATION))
                .filter(Objects::nonNull).collect(toMap(EntityIndexDescriptor::getName, Function.identity()));
        Map<String, EntityIndexDescriptor> entityIndexedDescriptors = createEntityIndexDescriptors(desc, entityIndexes);

        try {
            schemaEvent.fire(new SchemaRegisteredEvent(new SchemaDescriptor(processId + ".proto", content, entityIndexedDescriptors, new ProcessDescriptor(processId, fullTypeName)), SCHEMA_TYPE));
        } catch (RuntimeException ex) {
            throw new ProtobufValidationException(ex.getMessage(), ex);
        }
        domainModelEvent.fire(new FileDescriptorRegisteredEvent(desc));
    }

    private void validateDescriptorField(String messageName, Descriptor descriptor, String processInstancesDomainAttribute) throws ProtobufValidationException {
        FieldDescriptor processInstances = descriptor.findFieldByName(processInstancesDomainAttribute);
        if (processInstances == null) {
            throw new ProtobufValidationException(format("Could not find %s attribute in proto message: %s", processInstancesDomainAttribute, messageName));
        }
    }

    private SerializationContext createSerializationContext(FileDescriptorSource... fileDescriptorSources) throws ProtobufValidationException {
        SerializationContext ctx = new SerializationContextImpl(configureBuilder().build());
        try {
            Arrays.stream(fileDescriptorSources).forEach(ctx::registerProtoFiles);
        } catch (Exception ex) {
            LOGGER.warn("Error trying to parse proto buffer file: {}", ex.getMessage(), ex);
            throw new ProtobufValidationException(ex.getMessage());
        }
        return ctx;
    }
}
