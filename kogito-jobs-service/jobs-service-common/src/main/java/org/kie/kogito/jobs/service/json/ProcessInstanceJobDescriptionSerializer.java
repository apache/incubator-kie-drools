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
package org.kie.kogito.jobs.service.json;

import java.io.IOException;

import org.kie.kogito.jobs.ProcessInstanceJobDescription;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ProcessInstanceJobDescriptionSerializer extends StdSerializer<ProcessInstanceJobDescription> {

    private static final long serialVersionUID = -8307549297456060422L;

    public ProcessInstanceJobDescriptionSerializer() {
        super(ProcessInstanceJobDescription.class);
    }

    @Override
    public void serialize(ProcessInstanceJobDescription value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.id());
        jgen.writeStringField("timerId", value.timerId());
        jgen.writeObjectField("expirationTime", value.expirationTime());
        jgen.writeNumberField("priority", value.priority());
        jgen.writeStringField("processInstanceId", value.processInstanceId());
        jgen.writeStringField("rootProcessInstanceId", value.rootProcessInstanceId());
        jgen.writeStringField("processId", value.processId());
        jgen.writeStringField("rootProcessId", value.rootProcessId());
        jgen.writeStringField("nodeInstanceId", value.nodeInstanceId());
        jgen.writeEndObject();
    }

}
