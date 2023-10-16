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
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AttachmentMarshaller extends AbstractMarshaller implements MessageMarshaller<Attachment> {
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String CONTENT = "content";
    protected static final String UPDATED_BY = "updatedBy";
    protected static final String UPDATED_AT = "updatedAt";

    public AttachmentMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Attachment readFrom(ProtoStreamReader reader) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setId(reader.readString(ID));
        attachment.setName(reader.readString(NAME));
        attachment.setContent(reader.readString(CONTENT));
        attachment.setUpdatedBy(reader.readString(UPDATED_BY));
        attachment.setUpdatedAt(dateToZonedDateTime(reader.readDate(UPDATED_AT)));
        return attachment;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Attachment attachment) throws IOException {
        writer.writeString(ID, attachment.getId());
        writer.writeString(NAME, attachment.getName());
        writer.writeString(CONTENT, attachment.getContent());
        writer.writeString(UPDATED_BY, attachment.getUpdatedBy());
        writer.writeDate(UPDATED_AT, zonedDateTimeToDate(attachment.getUpdatedAt()));
    }

    @Override
    public Class<? extends Attachment> getJavaClass() {
        return Attachment.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
