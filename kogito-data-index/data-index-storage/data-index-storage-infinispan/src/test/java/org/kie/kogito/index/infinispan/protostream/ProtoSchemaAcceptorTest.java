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

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.infinispan.schema.ProtoSchemaAcceptor;
import org.kie.kogito.persistence.api.schema.SchemaType;

import static org.kie.kogito.persistence.infinispan.Constants.INFINISPAN_STORAGE;
import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

class ProtoSchemaAcceptorTest {

    ProtoSchemaAcceptor protoSchemaAcceptor = new ProtoSchemaAcceptor();

    @Test
    void supportedStorageTypeAndSchemaType() {
        protoSchemaAcceptor.storageType = Optional.of(INFINISPAN_STORAGE);
        assertTrue(protoSchemaAcceptor.accept(new SchemaType(ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE)));
    }

    @Test
    void unsupportedSchemaType() {
        protoSchemaAcceptor.storageType = Optional.of(INFINISPAN_STORAGE);
        assertFalse(protoSchemaAcceptor.accept(new SchemaType("test")));
    }

    @Test
    void unsupportedStorageType() {
        protoSchemaAcceptor.storageType = Optional.of("test");
        assertFalse(protoSchemaAcceptor.accept(new SchemaType(ProtoSchemaAcceptor.PROTO_SCHEMA_TYPE)));
    }
}