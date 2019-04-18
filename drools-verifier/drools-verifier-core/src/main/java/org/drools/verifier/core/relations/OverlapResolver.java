/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.relations;

import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasUUID;

public class OverlapResolver {

    private final InspectorList collection;
    private final OverlapBlockers overlapBlockers;

    public OverlapResolver(final InspectorList collection,
                           final boolean record) {
        this.collection = collection;
        overlapBlockers = new OverlapBlockers(record);
    }

    public boolean listOverlapsOther(final InspectorList otherList) {

        final OverlapBlocker blocker = overlapBlockers.get(otherList.getUuidKey());

        if (blocker != null) {
            return revalidateBlocker(otherList,
                                     blocker);
        } else {
            return resolve(otherList);
        }
    }

    private boolean resolve(final InspectorList otherList) {
        final OverlapBlocker blocker = resolveOverlap(otherList);

        if (blocker.foundIssue()) {
            overlapBlockers.add(blocker);
            return false;
        } else {
            return true;
        }
    }

    private boolean revalidateBlocker(final InspectorList otherList,
                                      final OverlapBlocker first) {
        if (first.doesRelationStillExist()) {
            return false;
        } else {
            overlapBlockers.remove(first);

            return listOverlapsOther(otherList);
        }
    }

    private OverlapBlocker resolveOverlap(final InspectorList otherList) {
        // Every object in other collection is overlapped by an object in collection.
        for (final Object object : otherList) {
            final OverlapBlocker blocker = OverlapResolver.isOverlappedByAnObjectInThisList(collection,
                                                                                            (HasUUID) object);
            if (blocker.foundIssue()) {

                return new OverlapBlocker(collection,
                                          otherList,
                                          blocker);
            }
        }

        return OverlapBlocker.EMPTY;
    }

    public static OverlapBlocker isOverlappedByAnObjectInThisList(final InspectorList otherCollection,
                                                                  final HasUUID object) {
        if (object instanceof IsOverlapping) {
            if (overlapsItem(otherCollection,
                             (IsOverlapping) object)) {
                return OverlapBlocker.EMPTY;
            } else {
                return new OverlapBlocker(otherCollection,
                                          object);
            }
        } else {
            if (otherCollection.contains(object)) {
                return new OverlapBlocker(otherCollection,
                                          object);
            } else {
                return OverlapBlocker.EMPTY;
            }
        }
    }

    private static boolean overlapsItem(final InspectorList otherCollection,
                                        final IsOverlapping object) {
        return otherCollection.stream().anyMatch(other -> object.overlaps(other));
    }
}
