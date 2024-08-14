package org.drools.base.reteoo;

import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;

public class DynamicFilterProto {
    private AlphaNodeFieldConstraint constraint;
    private int                      filterIndex;

    public DynamicFilterProto(AlphaNodeFieldConstraint constraint, int filterIndex) {
        this.constraint  = constraint;
        this.filterIndex = filterIndex;
    }

    public AlphaNodeFieldConstraint getConstraint() {
        return constraint;
    }

    public int getFilterIndex() {
        return filterIndex;
    }
}
