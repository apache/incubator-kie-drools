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
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceEventBody;

import static org.kie.kogito.events.mongodb.codec.CodecUtils.codec;
import static org.kie.kogito.events.mongodb.codec.CodecUtils.encodeDataEvent;

public class ProcessInstanceDataEventCodec implements CollectibleCodec<ProcessInstanceDataEvent> {

    @Override
    public ProcessInstanceDataEvent generateIdIfAbsentFromDocument(ProcessInstanceDataEvent processInstanceDataEvent) {
        return processInstanceDataEvent;
    }

    @Override
    public boolean documentHasId(ProcessInstanceDataEvent processInstanceDataEvent) {
        return processInstanceDataEvent.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(ProcessInstanceDataEvent processInstanceDataEvent) {
        return new BsonString(processInstanceDataEvent.getId());
    }

    @Override
    public ProcessInstanceDataEvent decode(BsonReader bsonReader, DecoderContext decoderContext) {
        // The events persist in an outbox collection
        // The events are deleted immediately (in the same transaction)
        // "decode" is not supposed to take place in any scenario
        return null;
    }

    @Override
    public void encode(BsonWriter bsonWriter, ProcessInstanceDataEvent processInstanceDataEvent, EncoderContext encoderContext) {
        Document doc = new Document();
        encodeDataEvent(doc, processInstanceDataEvent);
        doc.put("kogitoProcessType", processInstanceDataEvent.getKogitoProcessType());
        doc.put("kogitoProcessInstanceVersion", processInstanceDataEvent.getKogitoProcessInstanceVersion());
        doc.put("kogitoParentProcessinstanceId", processInstanceDataEvent.getKogitoParentProcessInstanceId());
        doc.put("kogitoProcessinstanceState", processInstanceDataEvent.getKogitoProcessInstanceState());
        doc.put("kogitoReferenceId", processInstanceDataEvent.getKogitoReferenceId());
        doc.put("kogitoStartFromNode", processInstanceDataEvent.getKogitoStartFromNode());
        doc.put("kogitoIdentity", processInstanceDataEvent.getKogitoIdentity());
        doc.put("data", encodeData(processInstanceDataEvent.getData()));
        codec().encode(bsonWriter, doc, encoderContext);
    }

    private Document encodeData(ProcessInstanceEventBody data) {
        Document doc = new Document();
        doc.put("id", data.getId());
        doc.put("version", data.getVersion());
        doc.put("parentInstanceId", data.getParentInstanceId());
        doc.put("rootInstanceId", data.getRootInstanceId());
        doc.put("processId", data.getProcessId());
        doc.put("processType", data.getProcessType());
        doc.put("rootProcessId", data.getRootProcessId());
        doc.put("processName", data.getProcessName());
        doc.put("startDate", data.getStartDate());
        doc.put("endDate", data.getEndDate());
        doc.put("state", data.getState());
        doc.put("businessKey", data.getBusinessKey());
        doc.put("roles", data.getRoles());
        doc.put("identity", data.getIdentity());

        if (data.getVariables() != null) {
            doc.put("variables", new Document(data.getVariables()));
        }

        if (data.getNodeInstances() != null) {
            doc.put("nodeInstances",
                    data.getNodeInstances().stream().map(ni -> {
                        Document niDoc = new Document();
                        niDoc.put("id", ni.getId());
                        niDoc.put("nodeId", ni.getNodeId());
                        niDoc.put("nodeDefinitionId", ni.getNodeDefinitionId());
                        niDoc.put("nodeName", ni.getNodeName());
                        niDoc.put("nodeType", ni.getNodeType());
                        niDoc.put("triggerTime", ni.getTriggerTime());
                        if (ni.getLeaveTime() != null) {
                            niDoc.put("leaveTime", ni.getLeaveTime());
                        }
                        return niDoc;
                    }).collect(Collectors.toSet()));
        }

        if (data.getError() != null) {
            Document eDoc = new Document();
            eDoc.put("errorMessage", data.getError().getErrorMessage());
            eDoc.put("nodeDefinitionId", data.getError().getNodeDefinitionId());
            doc.put("error", eDoc);
        }

        if (data.getMilestones() != null) {
            doc.put("milestones",
                    data.getMilestones().stream().map(m -> {
                        Document mDoc = new Document();
                        mDoc.put("id", m.getId());
                        mDoc.put("name", m.getName());
                        mDoc.put("status", m.getStatus());
                        return mDoc;
                    }).collect(Collectors.toSet()));
        }

        return doc;
    }

    @Override
    public Class<ProcessInstanceDataEvent> getEncoderClass() {
        return ProcessInstanceDataEvent.class;
    }
}
