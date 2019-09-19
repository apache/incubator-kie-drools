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

package org.kie.kogito.index.infinispan.protostream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.kie.kogito.index.infinispan.cache.InfinispanCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.kie.kogito.index.Constants.*;

@ApplicationScoped
public class ProtobufService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufService.class);

    @Inject
    InfinispanCacheManager manager;

    @Inject
    FileDescriptorSource kogitoDescriptors;

    @Inject
    Event<FileDescriptorRegisteredEvent> event;

    @PostConstruct
    public void init() {
        kogitoDescriptors.getFileDescriptors().forEach((name, bytes) -> {
            LOGGER.info("Registering Kogito ProtoBuffer file: {}", name);
            manager.getProtobufCache().put(name, new String(bytes));
        });
    }

    public void registerProtoBufferType(String content) throws Exception {
        LOGGER.debug("Registering new ProtoBuffer file with content: \n{}", content);

        content = content.replaceAll("kogito.Date", "string");
        SerializationContext ctx = new SerializationContextImpl(Configuration.builder().build());
        try {
            ctx.registerProtoFiles(kogitoDescriptors);
            ctx.registerProtoFiles(FileDescriptorSource.fromString("", content));
        } catch (Exception ex) {
            LOGGER.warn("Error trying to parse proto buffer file: {}", ex.getMessage(), ex);
            throw ex;
        }

        FileDescriptor desc = ctx.getFileDescriptors().get("");
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
        
        validateDescriptorField(messageName, descriptor, PROCESS_INSTANCES_DOMAIN_ATTRIBUTE);
        validateDescriptorField(messageName, descriptor, USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
        
        Map<String, String> cache = manager.getProtobufCache();
        cache.put(processId + ".proto", content);
        manager.getProcessIdModelCache().put(processId, fullTypeName);
        List<String> errors = checkSchemaErrors(cache);
        if (errors.isEmpty()) {
            event.fire(new FileDescriptorRegisteredEvent(desc));
        } else {
            String message = "Proto Schema contain errors:\n" + errors.stream().collect(Collectors.joining("\n"));
            throw new ProtobufValidationException(message);
        }

        if (LOGGER.isDebugEnabled()) {
            listProtoCacheKeys();
        }
    }

    private void validateDescriptorField(String messageName, Descriptor descriptor, String processInstancesDomainAttribute) throws Exception {
        FieldDescriptor processInstances = descriptor.findFieldByName(processInstancesDomainAttribute);
        if (processInstances == null) {
            throw new ProtobufValidationException(format("Could not find %s attribute in proto message: %s", processInstancesDomainAttribute, messageName));
        }
    }

    private void listProtoCacheKeys() {
        LOGGER.debug(">>>>>>list cache keys start");
        manager.getProtobufCache().entrySet().forEach(e -> LOGGER.debug(e.toString()));
        LOGGER.debug(">>>>>>list cache keys end");
    }

    private List<String> checkSchemaErrors(Map<String, String> metadataCache) {
        if (metadataCache.containsKey(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX)) {
            List<String> errors = new ArrayList<>();
            // The existence of this key indicates there are errors in some files
            String files = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
            for (String fname : files.split("\n")) {
                String errorKey = fname + ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX;
                final String error = metadataCache.get(errorKey);
                LOGGER.warn("Found errors in Protobuf schema file: {}\n{}\n", fname, error);
                errors.add(String.format("Protobuf schema file: %s\n%s\n", fname, error));
            }
            return errors;
        } else {
            return emptyList();
        }
    }
}