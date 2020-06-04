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

import io.quarkus.runtime.StartupEvent;
import org.infinispan.protostream.FileDescriptorSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.SchemaRegisteredEvent;
import org.kie.kogito.index.schema.ProcessDescriptor;
import org.kie.kogito.index.schema.SchemaDescriptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.kogito.index.protobuf.ProtobufService.SCHEMA_TYPE;
import static org.kie.kogito.index.protobuf.TestUtils.PROCESS_ID;
import static org.kie.kogito.index.protobuf.TestUtils.PROCESS_TYPE;
import static org.kie.kogito.index.protobuf.TestUtils.getTestFileContent;
import static org.kie.kogito.index.protobuf.TestUtils.getTestFileInvalidContent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProtobufServiceTest {

    FileDescriptorSource kogitoDescriptors;

    @Mock
    Event<FileDescriptorRegisteredEvent> domainModelEvent;

    @Mock
    Event<SchemaRegisteredEvent> schemaEvent;

    @InjectMocks
    ProtobufService protobufService;

    @BeforeEach
    void prepare() {
        kogitoDescriptors = new FileDescriptorSource();
        protobufService.kogitoDescriptors = kogitoDescriptors;
    }

    @Test
    void onStart() {
        kogitoDescriptors.addProtoFile("test1", "test1");
        kogitoDescriptors.addProtoFile("test2", "test2");

        StartupEvent event = mock(StartupEvent.class);
        protobufService.onStart(event);

        verify(schemaEvent, times(1)).fire(eq(new SchemaRegisteredEvent(new SchemaDescriptor("test1", "test1", null), SCHEMA_TYPE)));
        verify(schemaEvent, times(1)).fire(eq(new SchemaRegisteredEvent(new SchemaDescriptor("test2", "test2", null), SCHEMA_TYPE)));
    }

    @Test
    void registerProtoBufferTypeWithInvalidKogitoDescriptors() {
        kogitoDescriptors.addProtoFile("test", "test");
        String content = getTestFileContent();

        String exceptionMessage = "";

        try {
            protobufService.registerProtoBufferType(content);
        } catch (ProtobufValidationException e) {
            exceptionMessage = e.getMessage();
        }

        assertTrue(exceptionMessage.contains("java.lang.IllegalStateException"));

        verify(schemaEvent, never()).fire(any(SchemaRegisteredEvent.class));
        verify(domainModelEvent, never()).fire(any(FileDescriptorRegisteredEvent.class));
    }

    @Test
    void registerProtoBufferTypeWithInvalidProtoFile() {
        String content = getTestFileInvalidContent();

        String exceptionMessage = "";

        try {
            protobufService.registerProtoBufferType(content);
        } catch (ProtobufValidationException e) {
            exceptionMessage = e.getMessage();
        }

        assertTrue(exceptionMessage.contains("Could not find metadata attribute in proto message"));

        verify(schemaEvent, never()).fire(any(SchemaRegisteredEvent.class));
        verify(domainModelEvent, never()).fire(any(FileDescriptorRegisteredEvent.class));
    }

    @Test
    void registerProtoBufferTypeSchemaRegistrationFailed() {
        String testExceptionMessage = "test schema registration fail";
        doThrow(new RuntimeException(testExceptionMessage)).when(schemaEvent).fire(any(SchemaRegisteredEvent.class));
        String content = getTestFileContent();

        String exceptionMessage = "";

        try {
            protobufService.registerProtoBufferType(content);
        } catch (ProtobufValidationException e) {
            exceptionMessage = e.getMessage();
        }

        assertEquals(testExceptionMessage, exceptionMessage);

        verify(schemaEvent, times(1)).fire(new SchemaRegisteredEvent(new SchemaDescriptor(PROCESS_ID + ".proto", content, new ProcessDescriptor(PROCESS_ID, PROCESS_TYPE)), SCHEMA_TYPE));
        verify(domainModelEvent, never()).fire(any(FileDescriptorRegisteredEvent.class));
    }

    @Test
    void registerProtoBufferType() {
        String content = getTestFileContent();

        try {
            protobufService.registerProtoBufferType(content);
        } catch (ProtobufValidationException e) {
            fail("RegisterProtoBufferType failed", e);
        }

        verify(schemaEvent, times(1)).fire(new SchemaRegisteredEvent(new SchemaDescriptor(PROCESS_ID + ".proto", content, new ProcessDescriptor(PROCESS_ID, PROCESS_TYPE)), SCHEMA_TYPE));
        verify(domainModelEvent, times(1)).fire(any(FileDescriptorRegisteredEvent.class));
    }
}