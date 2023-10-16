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
import java.util.UUID;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.MilestoneStatus;
import org.mockito.InOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.infinispan.protostream.MilestoneMarshaller.ID;
import static org.kie.kogito.index.infinispan.protostream.MilestoneMarshaller.NAME;
import static org.kie.kogito.index.infinispan.protostream.MilestoneMarshaller.STATUS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MilestoneMarshallerTest {

    String id = UUID.randomUUID().toString();
    String name = "SimpleMilestone";
    String status = MilestoneStatus.AVAILABLE.name();

    @Test
    void testReadFrom() throws IOException {
        MessageMarshaller.ProtoStreamReader reader = mock(MessageMarshaller.ProtoStreamReader.class);

        when(reader.readString(ID)).thenReturn(id);
        when(reader.readString(NAME)).thenReturn(name);
        when(reader.readString(STATUS)).thenReturn(status);

        Milestone milestone = new MilestoneMarshaller(null).readFrom(reader);

        assertThat(milestone)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID, id)
                .hasFieldOrPropertyWithValue(NAME, name)
                .hasFieldOrPropertyWithValue(STATUS, status);

        InOrder inOrder = inOrder(reader);
        inOrder.verify(reader).readString(ID);
        inOrder.verify(reader).readString(NAME);
        inOrder.verify(reader).readString(STATUS);
    }

    @Test
    void testWriteTo() throws IOException {
        Milestone milestone = Milestone.builder().id(id).name(name).status(status).build();

        MessageMarshaller.ProtoStreamWriter writer = mock(MessageMarshaller.ProtoStreamWriter.class);

        new MilestoneMarshaller(null).writeTo(writer, milestone);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeString(ID, id);
        inOrder.verify(writer).writeString(NAME, name);
        inOrder.verify(writer).writeString(STATUS, status);
    }

    @Test
    void testMarshaller() {
        MilestoneMarshaller marshaller = new MilestoneMarshaller(null);
        assertThat(marshaller.getJavaClass()).isEqualTo(Milestone.class);
        assertThat(marshaller.getTypeName()).isEqualTo(Milestone.class.getName());
    }
}
