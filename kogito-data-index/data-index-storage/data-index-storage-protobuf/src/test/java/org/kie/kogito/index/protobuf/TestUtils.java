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

package org.kie.kogito.index.protobuf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.index.domain.AttributeDescriptor;
import org.kie.kogito.index.domain.DomainDescriptor;

import static org.kie.kogito.index.protobuf.ProtobufService.DOMAIN_MODEL_PROTO_NAME;

public class TestUtils {

    static final String PROCESS_ID = "travels";

    static final String PROCESS_TYPE = "org.acme.travels.travels.Travels";

    static final DomainDescriptor DOMAIN_DESCRIPTOR;

    static final List<DomainDescriptor> ADDITIONAL_DESCRIPTORS;

    static {
        DOMAIN_DESCRIPTOR = new DomainDescriptor();
        DOMAIN_DESCRIPTOR.setTypeName("org.acme.travels.travels.Travels");
        DOMAIN_DESCRIPTOR.setAttributes(List.of(new AttributeDescriptor("flight", "org.acme.travels.travels.Flight"),
                                                new AttributeDescriptor("hotel", "org.acme.travels.travels.Hotel"),
                                                new AttributeDescriptor("id", "java.lang.String"),
                                                new AttributeDescriptor("metadata", "java.lang.String")));

        DomainDescriptor address = new DomainDescriptor();
        address.setTypeName("org.acme.travels.travels.Address");
        address.setAttributes(List.of(new AttributeDescriptor("city", "java.lang.String"),
                                      new AttributeDescriptor("country", "java.lang.String"),
                                      new AttributeDescriptor("street", "java.lang.String"),
                                      new AttributeDescriptor("zipCode", "java.lang.String")));

        DomainDescriptor flight = new DomainDescriptor();
        flight.setTypeName("org.acme.travels.travels.Flight");
        flight.setAttributes(List.of(new AttributeDescriptor("arrival", "java.lang.String"),
                                     new AttributeDescriptor("departure", "java.lang.String"),
                                     new AttributeDescriptor("flightNumber", "java.lang.String"),
                                     new AttributeDescriptor("gate", "java.lang.String"),
                                     new AttributeDescriptor("seat", "java.lang.String")));

        DomainDescriptor hotel = new DomainDescriptor();
        hotel.setTypeName("org.acme.travels.travels.Hotel");
        hotel.setAttributes(List.of(new AttributeDescriptor("address", "org.acme.travels.travels.Address"),
                                    new AttributeDescriptor("bookingNumber", "java.lang.String"),
                                    new AttributeDescriptor("name", "java.lang.String"),
                                    new AttributeDescriptor("phone", "java.lang.String"),
                                    new AttributeDescriptor("room", "java.lang.String")));

        ADDITIONAL_DESCRIPTORS = List.of(address, flight, hotel);
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
}
