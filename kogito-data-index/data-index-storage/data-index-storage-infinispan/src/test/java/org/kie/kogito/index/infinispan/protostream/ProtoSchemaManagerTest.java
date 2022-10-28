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

package org.kie.kogito.index.infinispan.protostream;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManagerAdmin;
import org.infinispan.commons.configuration.BasicConfiguration;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.DataIndexStorageService;
import org.kie.kogito.index.infinispan.schema.ProtoSchemaAcceptor;
import org.kie.kogito.index.infinispan.schema.ProtoSchemaManager;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.schema.ProcessDescriptor;
import org.kie.kogito.persistence.api.schema.SchemaDescriptor;
import org.kie.kogito.persistence.api.schema.SchemaRegisteredEvent;
import org.kie.kogito.persistence.api.schema.SchemaRegistrationException;
import org.kie.kogito.persistence.api.schema.SchemaType;
import org.kie.kogito.persistence.infinispan.cache.ProtobufCacheService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtoSchemaManagerTest {

    @Mock
    ProtoSchemaAcceptor protoSchemaAcceptor;

    @Mock
    DataIndexStorageService cacheManager;

    @Mock
    ProtobufCacheService protobufCacheService;

    @InjectMocks
    ProtoSchemaManager protoSchemaManager;

    @Mock
    Storage<String, String> protobufCache;

    @Mock
    Storage<String, String> processIdModelCache;

    @Mock
    Template cacheTemplate;

    @Mock
    TemplateInstance templateInstance;

    @Mock
    RemoteCacheManager manager;

    @Mock
    RemoteCacheManagerAdmin remoteCacheManagerAdmin;

    @BeforeEach
    void prepare() {
        when(protoSchemaAcceptor.accept(any())).thenReturn(true);
        when(protobufCacheService.getProtobufCache()).thenReturn(protobufCache);
        when(cacheManager.getProcessIdModelCache()).thenReturn(processIdModelCache);
        when(cacheManager.getDomainModelCacheName(any())).thenAnswer(
                (Answer<String>) inv -> inv.getArgument(0).toString() + "_domain");
        when(cacheTemplate.data(any(), any())).thenReturn(templateInstance);
        when(templateInstance.data(any(), any())).thenReturn(templateInstance);
        when(manager.administration()).thenReturn(remoteCacheManagerAdmin);
    }

    @Test
    void onSchemaRegisteredEvent() throws Exception {
        String processId = "testProcessId";
        String processType = "testProcessType";
        ProcessDescriptor processDescriptor = new ProcessDescriptor(processId, processType);
        String name = "testName";
        String content = TestUtils.getTestProtoBufferFile();
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(name, content, emptyMap(), processDescriptor);
        SchemaType schemaType = new SchemaType(ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE);
        SchemaRegisteredEvent event = new SchemaRegisteredEvent(schemaDescriptor, schemaType);

        protoSchemaManager.onSchemaRegisteredEvent(event);

        verify(protoSchemaAcceptor).accept(eq(schemaType));

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(protobufCache).put(eq(name), contentCaptor.capture());
        assertTrue(content.contains("kogito.Date"));
        assertFalse(contentCaptor.getValue().contains("kogito.Date"));
        assertTrue(content.contains("kogito.Instant"));
        assertFalse(contentCaptor.getValue().contains("kogito.Instant"));
        assertTrue(content.contains("kogito.Serializable"));
        assertFalse(contentCaptor.getValue().contains("kogito.Serializable"));

        assertThat(contentCaptor.getValue()).isEqualTo(content.replaceAll("kogito.Date", "string")
                .replaceAll("kogito.Instant", "string")
                .replaceAll("kogito.Serializable", "string"));

        verify(processIdModelCache).put(eq(processId), eq(processType));
        verify(remoteCacheManagerAdmin).getOrCreateCache(eq(processId + "_domain"), any(BasicConfiguration.class));
        verify(cacheManager).getDomainModelCacheName(processId);
        verify(cacheTemplate).data("cache_name", processId + "_domain");
        verify(templateInstance).data("indexed", emptySet());
    }

    @Test
    void onSchemaRegisteredEventWithError() {
        String processId = "testProcessId";
        String processType = "testProcessType";
        ProcessDescriptor processDescriptor = new ProcessDescriptor(processId, processType);
        String name = "testName";
        String content = "testContent";
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(name, content, emptyMap(), processDescriptor);
        SchemaType schemaType = new SchemaType(ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE);
        SchemaRegisteredEvent event = new SchemaRegisteredEvent(schemaDescriptor, schemaType);

        when(protobufCache.containsKey(eq(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX))).thenReturn(true);
        when(protobufCache.get(eq(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX))).thenReturn("testError");

        assertThrows(SchemaRegistrationException.class, () -> protoSchemaManager.onSchemaRegisteredEvent(event));

        verify(protoSchemaAcceptor).accept(eq(schemaType));
        verify(protobufCache).put(eq(name), eq(content));
        verify(processIdModelCache).put(eq(processId), eq(processType));
    }
}
