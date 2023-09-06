/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.NodeMetadata;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeMetadataMarshaller extends AbstractMarshaller implements MessageMarshaller<NodeMetadata> {

    public static final String ACTION = "action";
    public static final String BRANCH = "branch";
    public static final String STATE = "state";
    public static final String UNIQUE_ID = "UniqueId";

    public NodeMetadataMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public NodeMetadata readFrom(ProtoStreamReader reader) throws IOException {
        NodeMetadata metadata = new NodeMetadata();
        metadata.setUniqueId(reader.readString(UNIQUE_ID));
        metadata.setState(reader.readString(STATE));
        metadata.setBranch(reader.readString(BRANCH));
        metadata.setAction(reader.readString(ACTION));
        return metadata;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, NodeMetadata node) throws IOException {
        writer.writeString(UNIQUE_ID, node.getUniqueId());
        writer.writeString(STATE, node.getState());
        writer.writeString(BRANCH, node.getBranch());
        writer.writeString(ACTION, node.getAction());
    }

    @Override
    public Class<? extends NodeMetadata> getJavaClass() {
        return NodeMetadata.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
