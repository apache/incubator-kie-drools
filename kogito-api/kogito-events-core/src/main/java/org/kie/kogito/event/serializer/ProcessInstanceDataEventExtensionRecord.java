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
package org.kie.kogito.event.serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

import org.kie.kogito.event.process.KogitoMarshallEventSupport;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;

import static org.kie.kogito.event.process.KogitoEventBodySerializationHelper.*;

class ProcessInstanceDataEventExtensionRecord implements KogitoMarshallEventSupport {

    // referenceId and startFromNode are not used by process instance events
    private String id;
    private String instanceId;
    private String version;
    private String state;
    private String type;
    private String parentInstanceId;
    private String rootId;
    private String rootInstanceId;
    private String businessKey;
    private String identity;
    private URI source;
    private OffsetDateTime time;
    private String addons;
    private transient int ordinal;

    public ProcessInstanceDataEventExtensionRecord() {
    }

    public ProcessInstanceDataEventExtensionRecord(int ordinal, ProcessInstanceDataEvent<?> dataEvent) {
        this.ordinal = ordinal;
        id = dataEvent.getKogitoProcessId();
        instanceId = dataEvent.getKogitoProcessInstanceId();
        version = dataEvent.getKogitoProcessInstanceVersion();
        state = dataEvent.getKogitoProcessInstanceState();
        type = dataEvent.getKogitoProcessType();
        parentInstanceId = dataEvent.getKogitoParentProcessInstanceId();
        rootId = dataEvent.getKogitoRootProcessId();
        rootInstanceId = dataEvent.getKogitoRootProcessInstanceId();
        businessKey = dataEvent.getKogitoBusinessKey();
        identity = dataEvent.getKogitoIdentity();
        time = dataEvent.getTime();
        source = dataEvent.getSource();
        addons = dataEvent.getKogitoAddons();
    }

    public int getOrdinal() {
        return ordinal;
    }

    public String getId() {
        return id;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getVersion() {
        return version;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getParentInstanceId() {
        return parentInstanceId;
    }

    public String getRootId() {
        return rootId;
    }

    public String getRootInstanceId() {
        return rootInstanceId;
    }

    public String getIdentity() {
        return identity;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public URI getSource() {
        return source;
    }

    public String getAddons() {
        return addons;
    }

    @Override
    public void writeEvent(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(instanceId);
        out.writeUTF(version);
        out.writeUTF(state);
        writeUTF(out, type);
        writeUTF(out, parentInstanceId);
        writeUTF(out, rootId);
        writeUTF(out, rootInstanceId);
        writeUTF(out, businessKey);
        writeUTF(out, identity);
        writeTime(out, time);
        out.writeUTF(source.toString());
        writeUTF(out, addons);
    }

    @Override
    public void readEvent(DataInput in) throws IOException {
        id = in.readUTF();
        instanceId = in.readUTF();
        version = in.readUTF();
        state = in.readUTF();
        type = readUTF(in);
        parentInstanceId = readUTF(in);
        rootId = readUTF(in);
        rootInstanceId = readUTF(in);
        businessKey = readUTF(in);
        identity = readUTF(in);
        time = readTime(in);
        source = URI.create(in.readUTF());
        addons = readUTF(in);
    }

    @Override
    public String toString() {
        return "ProcessInstanceDataEventExtensionRecord [id=" + id + ", instanceId=" + instanceId + ", version="
                + version + ", state=" + state + ", type=" + type + ", parentInstanceId=" + parentInstanceId
                + ", rootId=" + rootId + ", rootInstanceId=" + rootInstanceId + ", businessKey=" + businessKey
                + ", identity=" + identity + ", source=" + source + ", time=" + time + ", addons=" + addons + "]";
    }

}
