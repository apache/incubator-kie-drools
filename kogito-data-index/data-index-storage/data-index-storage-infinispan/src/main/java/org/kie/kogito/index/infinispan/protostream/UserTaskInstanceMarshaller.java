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
import java.util.ArrayList;
import java.util.HashSet;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserTaskInstanceMarshaller extends AbstractMarshaller implements MessageMarshaller<UserTaskInstance> {

    protected static final String ID = "id";
    protected static final String DESCRIPTION = "description";
    protected static final String NAME = "name";
    protected static final String PRIORITY = "priority";
    protected static final String PROCESS_INSTANCE_ID = "processInstanceId";
    protected static final String PROCESS_ID = "processId";
    protected static final String ROOT_PROCESS_INSTANCE_ID = "rootProcessInstanceId";
    protected static final String ROOT_PROCESS_ID = "rootProcessId";
    protected static final String STATE = "state";
    protected static final String ACTUAL_OWNER = "actualOwner";
    protected static final String ADMIN_GROUPS = "adminGroups";
    protected static final String ADMIN_USERS = "adminUsers";
    protected static final String COMPLETED = "completed";
    protected static final String STARTED = "started";
    protected static final String EXCLUDED_USERS = "excludedUsers";
    protected static final String POTENTIAL_GROUPS = "potentialGroups";
    protected static final String POTENTIAL_USERS = "potentialUsers";
    protected static final String INPUTS = "inputs";
    protected static final String OUTPUTS = "outputs";
    protected static final String REFERENCE_NAME = "referenceName";
    protected static final String LAST_UPDATE = "lastUpdate";
    protected static final String ENDPOINT = "endpoint";
    protected static final String COMMENTS = "comments";
    protected static final String ATTACHMENTS = "attachments";
    protected static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    public UserTaskInstanceMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public UserTaskInstance readFrom(ProtoStreamReader reader) throws IOException {
        UserTaskInstance ut = new UserTaskInstance();
        ut.setId(reader.readString(ID));
        ut.setDescription(reader.readString(DESCRIPTION));
        ut.setName(reader.readString(NAME));
        ut.setPriority(reader.readString(PRIORITY));
        ut.setProcessInstanceId(reader.readString(PROCESS_INSTANCE_ID));
        ut.setProcessId(reader.readString(PROCESS_ID));
        ut.setRootProcessInstanceId(reader.readString(ROOT_PROCESS_INSTANCE_ID));
        ut.setRootProcessId(reader.readString(ROOT_PROCESS_ID));
        ut.setState(reader.readString(STATE));
        ut.setActualOwner(reader.readString(ACTUAL_OWNER));
        ut.setAdminGroups(reader.readCollection(ADMIN_GROUPS, new HashSet<>(), String.class));
        ut.setAdminUsers(reader.readCollection(ADMIN_USERS, new HashSet<>(), String.class));
        ut.setCompleted(dateToZonedDateTime(reader.readDate(COMPLETED)));
        ut.setStarted(dateToZonedDateTime(reader.readDate(STARTED)));
        ut.setExcludedUsers(reader.readCollection(EXCLUDED_USERS, new HashSet<>(), String.class));
        ut.setPotentialGroups(reader.readCollection(POTENTIAL_GROUPS, new HashSet<>(), String.class));
        ut.setPotentialUsers(reader.readCollection(POTENTIAL_USERS, new HashSet<>(), String.class));
        ut.setInputs((ObjectNode) jsonFromString(reader.readString(INPUTS)));
        ut.setOutputs((ObjectNode) jsonFromString(reader.readString(OUTPUTS)));
        ut.setReferenceName(reader.readString(REFERENCE_NAME));
        ut.setLastUpdate(dateToZonedDateTime(reader.readDate(LAST_UPDATE)));
        ut.setEndpoint(reader.readString(ENDPOINT));
        ut.setComments(reader.readCollection(COMMENTS, new ArrayList<>(), Comment.class));
        ut.setAttachments(reader.readCollection(ATTACHMENTS, new ArrayList<>(), Attachment.class));
        ut.setExternalReferenceId(reader.readString(EXTERNAL_REFERENCE_ID));
        return ut;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, UserTaskInstance ut) throws IOException {
        writer.writeString(ID, ut.getId());
        writer.writeString(DESCRIPTION, ut.getDescription());
        writer.writeString(NAME, ut.getName());
        writer.writeString(PRIORITY, ut.getPriority());
        writer.writeString(PROCESS_INSTANCE_ID, ut.getProcessInstanceId());
        writer.writeString(PROCESS_ID, ut.getProcessId());
        writer.writeString(ROOT_PROCESS_INSTANCE_ID, ut.getRootProcessInstanceId());
        writer.writeString(ROOT_PROCESS_ID, ut.getRootProcessId());
        writer.writeString(STATE, ut.getState());
        writer.writeString(ACTUAL_OWNER, ut.getActualOwner());
        writer.writeCollection(ADMIN_GROUPS, ut.getAdminGroups(), String.class);
        writer.writeCollection(ADMIN_USERS, ut.getAdminUsers(), String.class);
        writer.writeDate(COMPLETED, zonedDateTimeToDate(ut.getCompleted()));
        writer.writeDate(STARTED, zonedDateTimeToDate(ut.getStarted()));
        writer.writeCollection(EXCLUDED_USERS, ut.getExcludedUsers(), String.class);
        writer.writeCollection(POTENTIAL_GROUPS, ut.getPotentialGroups(), String.class);
        writer.writeCollection(POTENTIAL_USERS, ut.getPotentialUsers(), String.class);
        writer.writeString(INPUTS, ut.getInputs() == null ? null : ut.getInputs().toString());
        writer.writeString(OUTPUTS, ut.getOutputs() == null ? null : ut.getOutputs().toString());
        writer.writeString(REFERENCE_NAME, ut.getReferenceName());
        writer.writeDate(LAST_UPDATE, zonedDateTimeToDate(ut.getLastUpdate()));
        writer.writeString(ENDPOINT, ut.getEndpoint());
        writer.writeCollection(COMMENTS, ut.getComments(), Comment.class);
        writer.writeCollection(ATTACHMENTS, ut.getAttachments(), Attachment.class);
        writer.writeString(EXTERNAL_REFERENCE_ID, ut.getExternalReferenceId());
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
