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
package org.drools.verifier.core.relations;

import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasUUID;

public class SubsumptionResolver {

    private final InspectorList collection;
    private final SubsumptionBlockers subsumptionBlockers;

    public SubsumptionResolver(final InspectorList collection,
                               final boolean record) {
        this.collection = collection;
        subsumptionBlockers = new SubsumptionBlockers(record);
    }

    public boolean listSubsumesOther(final InspectorList otherList) {

        final SubsumptionBlocker blocker = subsumptionBlockers.get(otherList.getUuidKey());

        if (blocker != null) {
            return revalidateBlocker(otherList,
                                     blocker);
        } else {
            return resolve(otherList);
        }
    }

    private boolean resolve(final InspectorList otherList) {
        final SubsumptionBlocker blocker = resolveSubsumption(otherList);

        if (blocker.foundIssue()) {
            subsumptionBlockers.add(blocker);
            return false;
        } else {
            return true;
        }
    }

    private boolean revalidateBlocker(final InspectorList otherList,
                                      final SubsumptionBlocker first) {
        if (first.doesRelationStillExist()) {
            return false;
        } else {
            subsumptionBlockers.remove(first);

            return listSubsumesOther(otherList);
        }
    }

    private SubsumptionBlocker resolveSubsumption(final InspectorList otherList) {
        // Every object in other collection is subsumed by an object in collection.
        for (final Object object : otherList) {
            final SubsumptionBlocker blocker = SubsumptionResolver.isSubsumedByAnObjectInThisList(collection,
                                                                                                  (HasUUID) object);
            if (blocker.foundIssue()) {

                return new SubsumptionBlocker(collection,
                                              otherList,
                                              blocker);
            }
        }

        return SubsumptionBlocker.EMPTY;
    }

    public static SubsumptionBlocker isSubsumedByAnObjectInThisList(final InspectorList otherCollection,
                                                                    final HasUUID object) {
        if (object instanceof IsSubsuming) {
            if (subsumesItem(otherCollection,
                             (IsSubsuming) object)) {
                return SubsumptionBlocker.EMPTY;
            } else {
                return new SubsumptionBlocker(otherCollection,
                                              object);
            }
        } else {
            if (otherCollection.contains(object)) {
                return new SubsumptionBlocker(otherCollection,
                                              object);
            } else {
                return SubsumptionBlocker.EMPTY;
            }
        }
    }

    private static boolean subsumesItem(final InspectorList otherCollection,
                                        final IsSubsuming object) {
        return otherCollection.stream().anyMatch(other -> object.subsumes(other));
    }
}
