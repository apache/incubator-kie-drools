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
package org.drools.verifier.core.index.keys;

import java.util.ArrayList;

import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.RetractHandler;
import org.drools.verifier.core.maps.util.HasKeys;

public class UUIDKey
        extends Key {

    public static final KeyDefinition UNIQUE_UUID = KeyDefinition.newKeyDefinition().withId("unique---uuid").build();

    private ArrayList<RetractHandler> retractHandlers = new ArrayList<>();

    private HasKeys hasKeys;

    UUIDKey(final HasKeys hasKeys,
            final String uuid) {
        super(UNIQUE_UUID,
              uuid);
        this.hasKeys = hasKeys;
    }

    public static UUIDKey getUUIDKey(final Key[] keys) {
        UUIDKey result = null;
        for (final Key key : keys) {
            if (key instanceof UUIDKey) {

                if (result == null) {
                    result = (UUIDKey) key;
                } else {
                    throw new IllegalArgumentException("You can only have one uuid key.");
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("You must set a uuid key.");
        } else {
            return result;
        }
    }

    public void retract() {
        for (final RetractHandler retractHandler : retractHandlers) {
            retractHandler.retract(this);
        }
    }

    public void addRetractHandler(final RetractHandler retractHandler) {
        retractHandlers.add(retractHandler);
    }

    public Key[] getKeys() {
        return hasKeys.keys();
    }

    @Override
    public int compareTo(final Key key) {
        return getSingleValue().compareTo(key.getSingleValue());
    }
}
