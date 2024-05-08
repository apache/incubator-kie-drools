package org.drools.core.reteoo.sequencing;

import org.drools.core.reteoo.DynamicFilter;

public interface DynamicFilters {
    DynamicFilter getActiveDynamicFilter(int filterIndex);

    void removeActiveFilter(DynamicFilter filter);
}
