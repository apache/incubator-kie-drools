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

import java.util.Collection;
import java.util.HashSet;

import org.drools.verifier.core.index.keys.UUIDKey;

public class UUIDKeySet
        extends HashSet<UUIDKey>
        implements RetractHandler {

    private KeyTreeMap keyTreeMap;

    public UUIDKeySet(final KeyTreeMap keyTreeMap) {
        this.keyTreeMap = keyTreeMap;
    }

    public UUIDKeySet() {
    }

    @Override
    public boolean add(final UUIDKey uuidKey) {

        uuidKey.addRetractHandler(this);

        return super.add(uuidKey);
    }

    @Override
    public boolean addAll(final Collection<? extends UUIDKey> keys) {
        for (final UUIDKey uuidKey : keys) {
            uuidKey.addRetractHandler(this);
        }

        return super.addAll(keys);
    }

    @Override
    public void retract(final UUIDKey uuidKey) {
        keyTreeMap.remove(uuidKey);
    }
}
