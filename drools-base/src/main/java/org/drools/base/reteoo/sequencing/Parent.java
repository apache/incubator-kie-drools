package org.drools.base.reteoo.sequencing;

import org.drools.base.reteoo.sequencing.steps.SubsequenceStep;

public interface Parent {
    public boolean childFinished(SubsequenceStep step);
}
