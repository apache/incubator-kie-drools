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
package org.kie.kogito.index.mongodb.model;

import org.bson.Document;
import org.kie.kogito.persistence.mongodb.model.ModelUtils;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MAPPER;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.jsonNodeToDocument;

public class DomainEntityMapper implements MongoEntityMapper<ObjectNode, Document> {

    static final String ID = "id";

    @Override
    public Class<Document> getEntityClass() {
        return Document.class;
    }

    @Override
    public Document mapToEntity(String key, ObjectNode value) {
        if (value == null) {
            return null;
        }

        ObjectNode n = value.deepCopy();
        n.remove(ID);
        return jsonNodeToDocument(n).append(MONGO_ID, key);
    }

    @Override
    public ObjectNode mapToModel(Document entity) {
        if (entity == null) {
            return null;
        }

        Object idObj = entity.remove(MONGO_ID);
        if (idObj != null) {
            ObjectNode result = MAPPER.createObjectNode();
            result.put(ID, idObj.toString());
            result.setAll(ModelUtils.documentToJsonNode(entity));
            return result;
        }
        return ModelUtils.documentToJsonNode(entity);
    }
}
