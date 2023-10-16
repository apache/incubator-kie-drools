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
package org.kie.kogito.persistence.mongodb.index;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.schema.SchemaType;

import static org.kie.kogito.persistence.mongodb.Constants.MONGODB_STORAGE;
import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

class IndexSchemaAcceptorTest {

    IndexSchemaAcceptor indexSchemaAcceptor = new IndexSchemaAcceptor();

    @Test
    void supportedStorageType() {
        indexSchemaAcceptor.storageType = Optional.of(MONGODB_STORAGE);
        assertTrue(indexSchemaAcceptor.accept(new SchemaType("test")));
    }

    @Test
    void unsupportedStorageType() {
        indexSchemaAcceptor.storageType = Optional.of("test");
        assertFalse(indexSchemaAcceptor.accept(new SchemaType("test")));
    }
}