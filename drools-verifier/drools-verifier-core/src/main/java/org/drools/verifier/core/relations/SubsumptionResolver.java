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
