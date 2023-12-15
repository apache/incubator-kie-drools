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
package org.kie.kogito.index.infinispan.schema;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.persistence.api.schema.SchemaAcceptor;
import org.kie.kogito.persistence.api.schema.SchemaType;

import jakarta.enterprise.context.ApplicationScoped;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.infinispan.Constants.INFINISPAN_STORAGE;

@ApplicationScoped
public class ProtoSchemaAcceptor implements SchemaAcceptor {

    public static final String PROTO_SCHEMA_TYPE = "proto";

    @ConfigProperty(name = PERSISTENCE_TYPE_PROPERTY)
    public Optional<String> storageType;

    @Override
    public boolean accept(SchemaType type) {
        return storageType.isPresent() && INFINISPAN_STORAGE.equals(storageType.get()) && PROTO_SCHEMA_TYPE.equals(type.getType());
    }
}
