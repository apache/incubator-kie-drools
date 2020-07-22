/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.protobuf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.persistence.api.proto.DomainDescriptor;
import org.kie.kogito.persistence.api.schema.AttributeDescriptor;
import org.kie.kogito.persistence.api.schema.EntityIndexDescriptor;
import org.kie.kogito.persistence.api.schema.IndexDescriptor;

import static org.kie.kogito.persistence.protobuf.ProtobufService.DOMAIN_MODEL_PROTO_NAME;

public class TestUtils {

    static final String PROCESS_ID = "travels";

    static final String PROCESS_TYPE = "org.acme.travels.travels.Travels";

    static final DomainDescriptor DOMAIN_DESCRIPTOR;

    static final List<DomainDescriptor> ADDITIONAL_DESCRIPTORS;

    static {
        DOMAIN_DESCRIPTOR = new DomainDescriptor();
        DOMAIN_DESCRIPTOR.setTypeName("org.acme.travels.travels.Travels");
        DOMAIN_DESCRIPTOR.setAttributes(List.of(new org.kie.kogito.persistence.api.proto.AttributeDescriptor("flight", "org.acme.travels.travels.Flight"),
                                                new org.kie.kogito.persistence.api.proto.AttributeDescriptor("hotel", "org.acme.travels.travels.Hotel"),
                                                new org.kie.kogito.persistence.api.proto.AttributeDescriptor("id", "java.lang.String"),
                                                new org.kie.kogito.persistence.api.proto.AttributeDescriptor("metadata", "java.lang.String")));

        DomainDescriptor flight = new DomainDescriptor();
        flight.setTypeName("org.acme.travels.travels.Flight");
        flight.setAttributes(List.of(new org.kie.kogito.persistence.api.proto.AttributeDescriptor("flightNumber", "java.lang.String")));

        DomainDescriptor hotel = new DomainDescriptor();
        hotel.setTypeName("org.acme.travels.travels.Hotel");
        hotel.setAttributes(List.of(new org.kie.kogito.persistence.api.proto.AttributeDescriptor("name", "java.lang.String"),
                                    new org.kie.kogito.persistence.api.proto.AttributeDescriptor("room", "java.lang.String")));

        ADDITIONAL_DESCRIPTORS = List.of(flight, hotel);
    }

    static FileDescriptor getTestFileDescriptor() {
        String content = getTestFileContent();
        SerializationContext ctx = new SerializationContextImpl(Configuration.builder().build());
        ctx.registerProtoFiles(FileDescriptorSource.fromString(DOMAIN_MODEL_PROTO_NAME, content));
        return ctx.getFileDescriptors().get(DOMAIN_MODEL_PROTO_NAME);
    }

    static String getTestFileContent() {
        return getTestFileContent("test.proto");
    }

    static String getTestFileInvalidContent() {
        return getTestFileContent("test_invalid.proto");
    }

    static String getTestFileContent(String protofile) {
        try {
            Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(protofile).toURI());
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static Map<String, EntityIndexDescriptor> getValidEntityIndexDescriptors(boolean includeUnindexedAttribute) {
        AttributeDescriptor flightNumber = new AttributeDescriptor("flightNumber", "string", true);
        IndexDescriptor flightNumberIndex = new IndexDescriptor("flightNumber", List.of("flightNumber"));
        EntityIndexDescriptor flightEntityIndexDescriptor = new EntityIndexDescriptor("org.acme.travels.travels.Flight",
                                                                                      List.of(flightNumberIndex), List.of(flightNumber));

        AttributeDescriptor hotelName = new AttributeDescriptor("name", "string", true);
        AttributeDescriptor hotelRoom = new AttributeDescriptor("room", "string", true);
        IndexDescriptor hotelNameIndex = new IndexDescriptor("name", List.of("name"));
        EntityIndexDescriptor hotelEntityIndexDescriptor = new EntityIndexDescriptor("org.acme.travels.travels.Hotel",
                                                                                     List.of(hotelNameIndex),
                                                                                     includeUnindexedAttribute ? List.of(hotelName, hotelRoom) : List.of(hotelName));

        AttributeDescriptor flight = new AttributeDescriptor("flight", "Flight", false);
        AttributeDescriptor hotel = new AttributeDescriptor("hotel", "Hotel", false);
        AttributeDescriptor id = new AttributeDescriptor("id", "string", true);
        AttributeDescriptor metadata = new AttributeDescriptor("metadata", "string", true);
        IndexDescriptor flightIndex = new IndexDescriptor("flight", List.of("flight"));
        IndexDescriptor hotelIndex = new IndexDescriptor("hotel", List.of("hotel"));
        IndexDescriptor idIndex = new IndexDescriptor("id", List.of("id"));
        IndexDescriptor metadataIndex = new IndexDescriptor("metadata", List.of("metadata"));
        EntityIndexDescriptor travelEntityIndexDescriptor = new EntityIndexDescriptor("org.acme.travels.travels.Travels",
                                                                                      List.of(flightIndex, hotelIndex, idIndex, metadataIndex),
                                                                                      List.of(flight, hotel, id, metadata));

        Map<String, EntityIndexDescriptor> entityIndexDescriptorMap = new HashMap<>();
        entityIndexDescriptorMap.put(flightEntityIndexDescriptor.getName(), flightEntityIndexDescriptor);
        entityIndexDescriptorMap.put(hotelEntityIndexDescriptor.getName(), hotelEntityIndexDescriptor);
        entityIndexDescriptorMap.put(travelEntityIndexDescriptor.getName(), travelEntityIndexDescriptor);

        return entityIndexDescriptorMap;
    }
}