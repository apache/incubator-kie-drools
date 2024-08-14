package org.drools.base.reteoo.sequencing;

import org.drools.base.reteoo.DynamicFilter;

public interface DynamicFilters {
    DynamicFilter getActiveDynamicFilter(int filterIndex);

    void removeActiveFilter(DynamicFilter filter);
}
