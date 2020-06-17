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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

public class ProcessInstanceErrorMarshaller extends AbstractMarshaller implements MessageMarshaller<ProcessInstanceError> {

    public ProcessInstanceErrorMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public ProcessInstanceError readFrom(ProtoStreamReader reader) throws IOException {
        ProcessInstanceError error = new ProcessInstanceError();
        error.setNodeDefinitionId(reader.readString("nodeDefinitionId"));
        error.setMessage(reader.readString("message"));
        return error;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, ProcessInstanceError error) throws IOException {
        writer.writeString("nodeDefinitionId", error.getNodeDefinitionId());
        writer.writeString("message", error.getMessage());
    }

    @Override
    public Class<? extends ProcessInstanceError> getJavaClass() {
        return ProcessInstanceError.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
