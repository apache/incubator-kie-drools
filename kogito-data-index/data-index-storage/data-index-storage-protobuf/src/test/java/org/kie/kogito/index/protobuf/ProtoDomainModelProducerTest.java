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

import javax.enterprise.event.Event;

import org.infinispan.protostream.descriptors.FileDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.DomainModelRegisteredEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.kie.kogito.index.protobuf.TestUtils.ADDITIONAL_DESCRIPTORS;
import static org.kie.kogito.index.protobuf.TestUtils.DOMAIN_DESCRIPTOR;
import static org.kie.kogito.index.protobuf.TestUtils.PROCESS_ID;
import static org.kie.kogito.index.protobuf.TestUtils.getTestFileDescriptor;
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

        verify(domainEvent).fire(eq(new DomainModelRegisteredEvent(PROCESS_ID, DOMAIN_DESCRIPTOR, ADDITIONAL_DESCRIPTORS)));
    }
}