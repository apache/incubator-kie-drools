/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
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

import java.io.IOException;
import java.util.HashSet;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.ProcessInstanceMeta;

public class ProcessInstanceMetaMarshaller implements MessageMarshaller<ProcessInstanceMeta> {

    @Override
    public ProcessInstanceMeta readFrom(ProtoStreamReader reader) throws IOException {
        ProcessInstanceMeta pi = new ProcessInstanceMeta();
        pi.setId(reader.readString("id"));
        pi.setProcessId(reader.readString("processId"));
        pi.setRoles(reader.readCollection("roles", new HashSet<>(), String.class));
        pi.setEndpoint(reader.readString("endpoint"));
        pi.setState(reader.readInt("state"));
        pi.setStart(reader.readDate("start"));
        pi.setEnd(reader.readDate("end"));
        pi.setRootProcessInstanceId(reader.readString("rootProcessInstanceId"));
        pi.setRootProcessId(reader.readString("rootProcessId"));
        pi.setParentProcessInstanceId(reader.readString("parentProcessInstanceId"));
        return pi;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, ProcessInstanceMeta pi) throws IOException {
        writer.writeString("id", pi.getId());
        writer.writeString("processId", pi.getProcessId());
        writer.writeCollection("roles", pi.getRoles(), String.class);
        writer.writeString("endpoint", pi.getEndpoint());
        writer.writeInt("state", pi.getState());
        writer.writeDate("start", pi.getStart());
        writer.writeDate("end", pi.getEnd());
        writer.writeString("rootProcessInstanceId", pi.getRootProcessInstanceId());
        writer.writeString("rootProcessId", pi.getRootProcessId());
        writer.writeString("parentProcessInstanceId", pi.getParentProcessInstanceId());
    }

    @Override
    public Class<? extends ProcessInstanceMeta> getJavaClass() {
        return ProcessInstanceMeta.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
