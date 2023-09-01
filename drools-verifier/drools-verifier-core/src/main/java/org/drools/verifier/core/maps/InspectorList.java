/**
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
package org.drools.verifier.core.maps;

import java.util.ArrayList;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.maps.util.HasUUID;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.IsOverlapping;
import org.drools.verifier.core.relations.IsRedundant;
import org.drools.verifier.core.relations.IsSubsuming;
import org.drools.verifier.core.relations.RelationResolver;

import static java.util.stream.Collectors.joining;

public class InspectorList<InspectorType extends HasUUID>
        extends ArrayList<InspectorType>
        implements IsOverlapping,
                   IsSubsuming<InspectorList>,
                   IsRedundant<InspectorList>,
                   IsConflicting<InspectorList>,
                   HasKeys {

    private final UUIDKey uuidKey;

    private final RelationResolver relationResolver;

    public InspectorList(final AnalyzerConfiguration configuration) {
        this(false,
             configuration);
    }

    public InspectorList(final boolean record,
                         final AnalyzerConfiguration configuration) {
        this.relationResolver = new RelationResolver(this,
                                                     record);
        this.uuidKey = configuration.getUUID(this);
    }

    @Override
    public boolean overlaps(final Object other) {
        return false;
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey
        };
    }

    @Override
    public boolean conflicts(final InspectorList other) {
        return relationResolver.isConflicting(other);
    }

    @Override
    public boolean isRedundant(final InspectorList other) {
        return relationResolver.isRedundant(other);
    }

    @Override
    public boolean subsumes(final InspectorList other) {
        return relationResolver.subsumes(other);
    }

    @Override
    public boolean add(final InspectorType inspector) {
        return super.add(inspector);
    }

    @Override
    public String toString() {
        return stream().map(Object::toString).collect(joining(", "));
    }
}
