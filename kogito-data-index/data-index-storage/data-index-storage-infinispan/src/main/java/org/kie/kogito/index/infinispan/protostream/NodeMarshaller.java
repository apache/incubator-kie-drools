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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.NodeMetadata;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeMarshaller extends AbstractMarshaller implements MessageMarshaller<Node> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String UNIQUE_ID = "uniqueId";
    private static final String METADATA = "metadata";

    public NodeMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Node readFrom(ProtoStreamReader reader) throws IOException {
        Node node = new Node();
        node.setId(reader.readString(ID));
        node.setName(reader.readString(NAME));
        node.setType(reader.readString(TYPE));
        node.setUniqueId(reader.readString(UNIQUE_ID));
        node.setMetadata(toNodeMetadataMap().apply(reader.readObject(METADATA, NodeMetadata.class)));
        return node;
    }

    Function<NodeMetadata, Map<String, String>> toNodeMetadataMap() {
        return m -> {
            if (m == null) {
                return null;
            }
            Map<String, String> meta = new HashMap<>();
            meta.put(NodeMetadataMarshaller.ACTION, m.getAction());
            meta.put(NodeMetadataMarshaller.STATE, m.getState());
            meta.put(NodeMetadataMarshaller.UNIQUE_ID, m.getUniqueId());
            meta.put(NodeMetadataMarshaller.BRANCH, m.getBranch());
            return meta;
        };
    }

    Function<Map<String, String>, NodeMetadata> toNodeMetadata() {
        return m -> {
            if (m == null) {
                return null;
            }
            NodeMetadata meta = new NodeMetadata();
            meta.setAction(m.get(NodeMetadataMarshaller.ACTION));
            meta.setState(m.get(NodeMetadataMarshaller.STATE));
            meta.setUniqueId(m.get(NodeMetadataMarshaller.UNIQUE_ID));
            meta.setBranch(m.get(NodeMetadataMarshaller.BRANCH));
            return meta;
        };
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Node node) throws IOException {
        writer.writeString(ID, node.getId());
        writer.writeString(NAME, node.getName());
        writer.writeString(TYPE, node.getType());
        writer.writeString(UNIQUE_ID, node.getUniqueId());
        writer.writeObject(METADATA, toNodeMetadata().apply(node.getMetadata()), NodeMetadata.class);
    }

    @Override
    public Class<? extends Node> getJavaClass() {
        return Node.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
