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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

public class UserTaskInstanceMarshaller extends AbstractMarshaller implements MessageMarshaller<UserTaskInstance> {

    public UserTaskInstanceMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public UserTaskInstance readFrom(ProtoStreamReader reader) throws IOException {
        UserTaskInstance ut = new UserTaskInstance();
        ut.setId(reader.readString("id"));
        ut.setDescription(reader.readString("description"));
        ut.setName(reader.readString("name"));
        ut.setPriority(reader.readString("priority"));
        ut.setProcessInstanceId(reader.readString("processInstanceId"));
        ut.setProcessId(reader.readString("processId"));
        ut.setRootProcessInstanceId(reader.readString("rootProcessInstanceId"));
        ut.setRootProcessId(reader.readString("rootProcessId"));
        ut.setState(reader.readString("state"));
        ut.setActualOwner(reader.readString("actualOwner"));
        ut.setAdminGroups(reader.readCollection("adminGroups", new HashSet<>(), String.class));
        ut.setAdminUsers(reader.readCollection("adminUsers", new HashSet<>(), String.class));
        ut.setCompleted(dateToZonedDateTime(reader.readDate("completed")));
        ut.setStarted(dateToZonedDateTime(reader.readDate("started")));
        ut.setExcludedUsers(reader.readCollection("excludedUsers", new HashSet<>(), String.class));
        ut.setPotentialGroups(reader.readCollection("potentialGroups", new HashSet<>(), String.class));
        ut.setPotentialUsers(reader.readCollection("potentialUsers", new HashSet<>(), String.class));
        ut.setInputs(jsonFromString(reader.readString("inputs")));
        ut.setOutputs(jsonFromString(reader.readString("outputs")));
        ut.setReferenceName(reader.readString("referenceName"));
        ut.setLastUpdate(dateToZonedDateTime(reader.readDate("lastUpdate")));
        return ut;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, UserTaskInstance ut) throws IOException {
        writer.writeString("id", ut.getId());
        writer.writeString("description", ut.getDescription());
        writer.writeString("name", ut.getName());
        writer.writeString("priority", ut.getPriority());
        writer.writeString("processInstanceId", ut.getProcessInstanceId());
        writer.writeString("processId", ut.getProcessId());
        writer.writeString("rootProcessInstanceId", ut.getRootProcessInstanceId());
        writer.writeString("rootProcessId", ut.getRootProcessId());
        writer.writeString("state", ut.getState());
        writer.writeString("actualOwner", ut.getActualOwner());
        writer.writeCollection("adminGroups", ut.getAdminGroups(), String.class);
        writer.writeCollection("adminUsers", ut.getAdminUsers(), String.class);
        writer.writeDate("completed", zonedDateTimeToDate(ut.getCompleted()));
        writer.writeDate("started", zonedDateTimeToDate(ut.getStarted()));
        writer.writeCollection("excludedUsers", ut.getExcludedUsers(), String.class);
        writer.writeCollection("potentialGroups", ut.getPotentialGroups(), String.class);
        writer.writeCollection("potentialUsers", ut.getPotentialUsers(), String.class);
        writer.writeString("inputs", ut.getInputs() == null ? null : ut.getInputs().toString());
        writer.writeString("outputs", ut.getOutputs() == null ? null : ut.getOutputs().toString());
        writer.writeString("referenceName", ut.getReferenceName());
        writer.writeDate("lastUpdate", zonedDateTimeToDate(ut.getLastUpdate()));
    }

    @Override
    public Class<? extends UserTaskInstance> getJavaClass() {
        return UserTaskInstance.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
