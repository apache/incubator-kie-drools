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

import javax.enterprise.event.Event;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.persistence.api.proto.DomainModelRegisteredEvent;
import org.kie.kogito.persistence.protobuf.domain.ProtoDomainModelProducer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.kie.kogito.persistence.protobuf.ProtobufService.DOMAIN_MODEL_PROTO_NAME;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProtoDomainModelProducerTest {

    @Mock
    Event<DomainModelRegisteredEvent> domainEvent;

    @InjectMocks
    ProtoDomainModelProducer protoDomainModelProducer;

    @Test
    void onFileDescriptorRegistered() {
        FileDescriptor fileDescriptor = getTestFileDescriptor();
        FileDescriptorRegisteredEvent event = new FileDescriptorRegisteredEvent(fileDescriptor);
        protoDomainModelProducer.onFileDescriptorRegistered(event);

        verify(domainEvent).fire(eq(new DomainModelRegisteredEvent(TestUtils.PROCESS_ID, TestUtils.DOMAIN_DESCRIPTOR, TestUtils.ADDITIONAL_DESCRIPTORS)));
    }

    static FileDescriptor getTestFileDescriptor() {
        String content = TestUtils.getTestFileContent();
        SerializationContext ctx = new SerializationContextImpl(Configuration.builder().build());
        ctx.registerProtoFiles(FileDescriptorSource.fromString(DOMAIN_MODEL_PROTO_NAME, content));
        return ctx.getFileDescriptors().get(DOMAIN_MODEL_PROTO_NAME);
    }
}