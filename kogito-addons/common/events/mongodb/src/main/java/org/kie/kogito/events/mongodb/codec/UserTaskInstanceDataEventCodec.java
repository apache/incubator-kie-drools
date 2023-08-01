/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.events.mongodb.codec;

import java.util.stream.Collectors;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceEventBody;

import static org.kie.kogito.events.mongodb.codec.CodecUtils.codec;
import static org.kie.kogito.events.mongodb.codec.CodecUtils.encodeDataEvent;

public class UserTaskInstanceDataEventCodec implements CollectibleCodec<UserTaskInstanceDataEvent> {

    @Override
    public UserTaskInstanceDataEvent generateIdIfAbsentFromDocument(UserTaskInstanceDataEvent userTaskInstanceDataEvent) {
        return userTaskInstanceDataEvent;
    }

    @Override
    public boolean documentHasId(UserTaskInstanceDataEvent userTaskInstanceDataEvent) {
        return userTaskInstanceDataEvent.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(UserTaskInstanceDataEvent userTaskInstanceDataEvent) {
        return new BsonString(userTaskInstanceDataEvent.getId());
    }

    @Override
    public UserTaskInstanceDataEvent decode(BsonReader bsonReader, DecoderContext decoderContext) {
        // The events persist in an outbox collection
        // The events are deleted immediately (in the same transaction)
        // "decode" is not supposed to take place in any scenario
        return null;
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserTaskInstanceDataEvent userTaskInstanceDataEvent, EncoderContext encoderContext) {
        Document doc = new Document();
        encodeDataEvent(doc, userTaskInstanceDataEvent);
        doc.put("kogitoUserTaskinstanceId", userTaskInstanceDataEvent.getKogitoUserTaskinstanceId());
        doc.put("kogitoUserTaskinstanceState", userTaskInstanceDataEvent.getKogitoUserTaskinstanceState());
        doc.put("data", encodeData(userTaskInstanceDataEvent.getData()));
        codec().encode(bsonWriter, doc, encoderContext);
    }

    private Document encodeData(UserTaskInstanceEventBody data) {
        Document doc = new Document();
        doc.put("id", data.getId());
        doc.put("taskName", data.getTaskName());
        doc.put("taskDescription", data.getTaskDescription());
        doc.put("taskPriority", data.getTaskPriority());
        doc.put("referenceName", data.getReferenceName());
        doc.put("startDate", data.getStartDate());
        doc.put("completeDate", data.getCompleteDate());
        doc.put("state", data.getState());
        doc.put("actualOwner", data.getActualOwner());
        doc.put("potentialUsers", data.getPotentialUsers());
        doc.put("potentialGroups", data.getPotentialGroups());
        doc.put("excludedUsers", data.getExcludedUsers());
        doc.put("adminUsers", data.getAdminUsers());
        doc.put("adminGroups", data.getAdminGroups());
        doc.put("inputs", new Document(data.getInputs()));
        doc.put("outputs", new Document(data.getOutputs()));
        doc.put("processInstanceId", data.getProcessInstanceId());
        doc.put("rootProcessInstanceId", data.getRootProcessInstanceId());
        doc.put("processId", data.getProcessId());
        doc.put("rootProcessId", data.getRootProcessId());
        doc.put("identity", data.getIdentity());

        if (data.getComments() != null) {
            doc.put("comments",
                    data.getComments().stream().map(c -> {
                        Document cDoc = new Document();
                        cDoc.put("id", c.getId());
                        cDoc.put("content", c.getContent());
                        cDoc.put("updatedAt", c.getUpdatedAt());
                        cDoc.put("updatedBy", c.getUpdatedBy());
                        return cDoc;
                    }).collect(Collectors.toSet()));
        }

        if (data.getAttachments() != null) {
            doc.put("attachments",
                    data.getAttachments().stream().map(a -> {
                        Document aDoc = new Document();
                        aDoc.put("id", a.getId());
                        aDoc.put("content", a.getContent());
                        aDoc.put("updatedAt", a.getUpdatedAt());
                        aDoc.put("updatedBy", a.getUpdatedBy());
                        aDoc.put("name", a.getName());
                        return aDoc;
                    }).collect(Collectors.toSet()));
        }

        return doc;
    }

    @Override
    public Class<UserTaskInstanceDataEvent> getEncoderClass() {
        return UserTaskInstanceDataEvent.class;
    }
}
