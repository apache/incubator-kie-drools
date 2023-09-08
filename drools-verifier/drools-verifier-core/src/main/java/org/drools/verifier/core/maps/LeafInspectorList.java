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

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.maps.util.HasConflicts;
import org.drools.verifier.core.maps.util.HasRedundancy;
import org.drools.verifier.core.maps.util.RedundancyResult;
import org.drools.verifier.core.relations.Conflict;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.IsRedundant;

public class LeafInspectorList<T extends IsConflicting & IsRedundant>
        extends InspectorList<T>
        implements HasConflicts,
                   HasRedundancy {

    public LeafInspectorList(final AnalyzerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Conflict hasConflicts() {
        int index = 1;
        for (final T inspector : this) {
            for (int j = index; j < size(); j++) {
                if (inspector.conflicts(get(j))) {
                    return new Conflict(inspector,
                                        get(j));
                }
            }
            index++;
        }

        return Conflict.EMPTY;
    }

    @Override
    public RedundancyResult hasRedundancy() {

        for (int i = 0; i < size(); i++) {

            final T inspector = get(i);

            for (int j = i + 1; j < size(); j++) {
                final T other = get(j);
                if (inspector.isRedundant(other)) {
                    return new RedundancyResult(inspector,
                                                get(j));
                }
            }
        }

        return RedundancyResult.EMPTY;
    }
}
