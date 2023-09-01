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
import java.util.Collection;
import java.util.HashMap;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasUUID;

public class UpdatableInspectorList<InspectorType extends HasUUID, InspectedType extends HasUUID>
        extends InspectorList<InspectorType> {

    private InspectorFactory<InspectorType, InspectedType> inspectorFactory;

    private HashMap<UUIDKey, InspectorType> map = new HashMap<>();

    public UpdatableInspectorList(final InspectorFactory<InspectorType, InspectedType> inspectorFactory,
                                  final AnalyzerConfiguration configuration) {
        super(configuration);
        this.inspectorFactory = inspectorFactory;
    }

    public void update(final Collection<InspectedType> updates) {

        final ArrayList<UUIDKey> originalItems = new ArrayList<>(map.keySet());

        for (final InspectedType updatable : updates) {

            final InspectorType inspector = map.get(updatable.getUuidKey());

            if (inspector != null) {
                // Everything up to date.
                originalItems.remove(updatable.getUuidKey());
            } else {
                final InspectorType newInspector = inspectorFactory.make(updatable);
                add(newInspector);
                map.put(updatable.getUuidKey(),
                        newInspector);
            }
        }

        // Remove left overs, they were not in updates
        for (final UUIDKey originalItem : originalItems) {
            remove(map.remove(originalItem));
        }
    }
}
