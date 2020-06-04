/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.protobuf;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.StartupEvent;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.index.event.SchemaRegisteredEvent;
import org.kie.kogito.index.schema.ProcessDescriptor;
import org.kie.kogito.index.schema.SchemaDescriptor;
import org.kie.kogito.index.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.kie.kogito.index.Constants.KOGITO_DOMAIN_ATTRIBUTE;

@ApplicationScoped
public class ProtobufService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufService.class);

    static final SchemaType SCHEMA_TYPE = new SchemaType("proto");

    static final String DOMAIN_MODEL_PROTO_NAME = "domainModel";

    @Inject
    FileDescriptorSource kogitoDescriptors;

    @Inject
    Event<FileDescriptorRegisteredEvent> domainModelEvent;

    @Inject
    Event<SchemaRegisteredEvent> schemaEvent;

    void onStart(@Observes StartupEvent ev) {
        kogitoDescriptors.getFileDescriptors().forEach((name, bytes) -> {
            LOGGER.info("Registering Kogito ProtoBuffer file: {}", name);
            schemaEvent.fire(new SchemaRegisteredEvent(new SchemaDescriptor(name, new String(bytes), null), SCHEMA_TYPE));
        });
    }

    public void registerProtoBufferType(String content) throws ProtobufValidationException {
        LOGGER.debug("Registering new ProtoBuffer file with content: \n{}", content);

        content = content.replaceAll("kogito.Date", "string");
        SerializationContext ctx = new SerializationContextImpl(Configuration.builder().build());
        try {
            ctx.registerProtoFiles(kogitoDescriptors);
            ctx.registerProtoFiles(FileDescriptorSource.fromString(DOMAIN_MODEL_PROTO_NAME, content));
        } catch (Exception ex) {
            LOGGER.warn("Error trying to parse proto buffer file: {}", ex.getMessage(), ex);
            throw new ProtobufValidationException(ex.getMessage());
        }

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

        validateDescriptorField(messageName, descriptor, KOGITO_DOMAIN_ATTRIBUTE);

        try {
            schemaEvent.fire(new SchemaRegisteredEvent(new SchemaDescriptor(processId + ".proto", content, new ProcessDescriptor(processId, fullTypeName)), SCHEMA_TYPE));
        } catch (RuntimeException ex) {
            throw new ProtobufValidationException(ex.getMessage());
        }
        domainModelEvent.fire(new FileDescriptorRegisteredEvent(desc));
    }

    private void validateDescriptorField(String messageName, Descriptor descriptor, String processInstancesDomainAttribute) throws ProtobufValidationException {
        FieldDescriptor processInstances = descriptor.findFieldByName(processInstancesDomainAttribute);
        if (processInstances == null) {
            throw new ProtobufValidationException(format("Could not find %s attribute in proto message: %s", processInstancesDomainAttribute, messageName));
        }
    }
}