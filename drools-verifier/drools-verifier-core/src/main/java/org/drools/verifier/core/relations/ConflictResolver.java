package org.drools.verifier.core.relations;

import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasUUID;

public class ConflictResolver {

    private final InspectorList collection;
    private final Conflicts conflicts;

    public ConflictResolver(final InspectorList list,
                            final boolean record) {
        this.collection = list;
        conflicts = new Conflicts(record);
    }

    private Conflict isConflicting(final InspectorList otherCollection) {

        if (collection == null || otherCollection == null) {
            return Conflict.EMPTY;
        }

        for (Object o : collection) {
            if (o instanceof IsConflicting) {
                final Conflict conflict = hasConflictingObjectInList(otherCollection,
                                                                     (IsConflicting) o);
                if (conflict.foundIssue()) {
                    return new Conflict(collection,
                                        otherCollection,
                                        conflict);
                }
            }
        }

        return Conflict.EMPTY;
    }

    private static Conflict getConflictingObjects(final InspectorList collection,
                                                  final IsConflicting isConflicting) {

        if (isConflicting == null || collection == null) {
            return Conflict.EMPTY;
        }

        for (final Object other : collection) {
            final Conflict conflicting = isConflicting(isConflicting,
                                                       (HasUUID) other);
            if (conflicting.foundIssue()) {
                return conflicting;
            }
        }

        return Conflict.EMPTY;
    }

    private static Conflict hasConflictingObjectInList(final InspectorList collection,
                                                       final IsConflicting isConflicting) {
        return getConflictingObjects(collection,
                                     isConflicting);
    }

    static Conflict isConflicting(final HasUUID isConflicting,
                                  final HasUUID other) {
        if (isConflicting instanceof IsConflicting) {
            if (((IsConflicting) isConflicting).conflicts(other)) {
                return new Conflict(isConflicting,
                                    other);
            }
        }
        return Conflict.EMPTY;
    }

    public Conflict resolveConflict(final InspectorList otherCollection) {
        final Conflict first = conflicts.get(otherCollection.getUuidKey());

        if (first != null) {
            if (first.doesRelationStillExist()) {
                return first;
            } else {
                // Clean conflict
                conflicts.remove(first);

                // Restart resolution
                return resolveConflict(otherCollection);
            }
        } else {

            final Conflict conflict = isConflicting(otherCollection);

            if (conflict.foundIssue()) {
                conflicts.add(conflict);
                return conflict;
            } else {
                return Conflict.EMPTY;
            }
        }
    }
}
