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
package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Entry;
import org.kie.kogito.index.model.ProcessDefinition;
import org.mockito.InOrder;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ADDONS;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ANNOTATIONS;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.DESCRIPTION;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ID;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.METADATA;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.NAME;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.ROLES;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.TYPE;
import static org.kie.kogito.index.infinispan.protostream.ProcessDefinitionMarshaller.VERSION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProcessDefinitionMarshallerTest {

    private static final String metaKey = "key1";
    private final String metaValue = "value1";

    @Test
    void testReadFrom() throws IOException {
        MessageMarshaller.ProtoStreamReader reader = mock(MessageMarshaller.ProtoStreamReader.class);
        when(reader.readString(ID)).thenReturn("processId");
        when(reader.readString(VERSION)).thenReturn("1.0");
        when(reader.readString(NAME)).thenReturn("processName");
        when(reader.readString(DESCRIPTION)).thenReturn("descr");
        when(reader.readCollection(eq(ANNOTATIONS), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("tag1")));
        when(reader.readCollection(eq(METADATA), any(), eq(Entry.class))).thenReturn(new HashSet<>(singleton(new Entry(metaKey, metaValue))));
        when(reader.readCollection(eq(ROLES), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("admin")));
        when(reader.readCollection(eq(ADDONS), any(), eq(String.class))).thenReturn(new HashSet<>(singleton("process-management")));
        when(reader.readString(TYPE)).thenReturn("processType");

        ProcessDefinitionMarshaller marshaller = new ProcessDefinitionMarshaller(null);
        ProcessDefinition pd = marshaller.readFrom(reader);

        assertThat(pd)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID, "processId")
                .hasFieldOrPropertyWithValue(VERSION, "1.0")
                .hasFieldOrPropertyWithValue(NAME, "processName")
                .hasFieldOrPropertyWithValue(DESCRIPTION, "descr")
                .hasFieldOrPropertyWithValue(ANNOTATIONS, singleton("tag1"))
                .hasFieldOrPropertyWithValue(METADATA, Map.of(metaKey, metaValue))
                .hasFieldOrPropertyWithValue(ROLES, singleton("admin"))
                .hasFieldOrPropertyWithValue(ADDONS, singleton("process-management"))
                .hasFieldOrPropertyWithValue(TYPE, "processType");

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).readString(ID);
        inOrder.verify(reader).readString(VERSION);
        inOrder.verify(reader).readString(NAME);
        inOrder.verify(reader).readString(DESCRIPTION);
        inOrder.verify(reader).readCollection(ANNOTATIONS, new HashSet<>(), String.class);
        inOrder.verify(reader).readCollection(METADATA, new HashSet<>(), Entry.class);
        inOrder.verify(reader).readCollection(ROLES, new HashSet<>(), String.class);
        inOrder.verify(reader).readCollection(ADDONS, new HashSet<>(), String.class);
        inOrder.verify(reader).readString(TYPE);
    }

    @Test
    void testWriteTo() throws IOException {

        ProcessDefinition pd = new ProcessDefinition();
        pd.setId("processId");
        pd.setVersion("1.0");
        pd.setName("processName");
        pd.setDescription("descr");
        pd.setAnnotations(singleton("tag1"));
        pd.setMetadata(Map.of(metaKey, metaValue));
        pd.setRoles(singleton("admin"));
        pd.setAddons(singleton("process-management"));
        pd.setType("processType");

        MessageMarshaller.ProtoStreamWriter writer = mock(MessageMarshaller.ProtoStreamWriter.class);

        ProcessDefinitionMarshaller marshaller = new ProcessDefinitionMarshaller(null);
        marshaller.writeTo(writer, pd);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeString(ID, pd.getId());
        inOrder.verify(writer).writeString(VERSION, pd.getVersion());
        inOrder.verify(writer).writeString(NAME, pd.getName());
        inOrder.verify(writer).writeString(DESCRIPTION, pd.getDescription());
        inOrder.verify(writer).writeCollection(ANNOTATIONS, pd.getAnnotations(), String.class);
        inOrder.verify(writer).writeCollection(METADATA, Set.of(new Entry(metaKey, metaValue)), Entry.class);
        inOrder.verify(writer).writeCollection(ROLES, pd.getRoles(), String.class);
        inOrder.verify(writer).writeCollection(ADDONS, pd.getAddons(), String.class);
        inOrder.verify(writer).writeString(TYPE, pd.getType());
    }
}
