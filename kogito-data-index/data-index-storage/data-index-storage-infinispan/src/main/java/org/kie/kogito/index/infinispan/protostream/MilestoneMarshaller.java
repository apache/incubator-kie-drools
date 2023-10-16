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

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MilestoneMarshaller extends AbstractMarshaller implements MessageMarshaller<Milestone> {

    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String STATUS = "status";

    public MilestoneMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Milestone readFrom(ProtoStreamReader reader) throws IOException {
        return Milestone.builder()
                .id(reader.readString(ID))
                .name(reader.readString(NAME))
                .status(reader.readString(STATUS))
                .build();
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Milestone milestone) throws IOException {
        writer.writeString(ID, milestone.getId());
        writer.writeString(NAME, milestone.getName());
        writer.writeString(STATUS, milestone.getStatus());
    }

    @Override
    public Class<? extends Milestone> getJavaClass() {
        return Milestone.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
