package org.drools.verifier.core.relations;

import org.drools.verifier.core.maps.InspectorList;

public class RelationResolver {

    private final ConflictResolver conflictResolver;
    private final SubsumptionResolver subsumptionResolver;

    private final InspectorList list;

    public RelationResolver(final InspectorList list) {
        this(list,
             false);
    }

    public RelationResolver(final InspectorList list,
                            final boolean record) {
        this.list = list;
        conflictResolver = new ConflictResolver(list,
                                                record);
        subsumptionResolver = new SubsumptionResolver(list,
                                                      record);
    }

    public boolean isConflicting(final InspectorList otherCollection) {
        return resolveConflict(otherCollection).foundIssue();
    }

    public Conflict resolveConflict(final InspectorList otherCollection) {
        return conflictResolver.resolveConflict(otherCollection);
    }

    public boolean subsumes(final InspectorList otherList) {
        return list != null && otherList != null &&
                !isConflicting(otherList) &&
                subsumptionResolver.listSubsumesOther(otherList);
    }

    public boolean isRedundant(final InspectorList otherList) {
        return subsumes(otherList)
                && otherList.subsumes(list);
    }
}
