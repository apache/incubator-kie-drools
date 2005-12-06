package org.drools.spi;

import org.drools.FactHandle;
import org.drools.rule.Declaration;

public interface ReturnValueExpressionConstraint
{       
    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Declaration[] declarations,
                             Tuple tuple,
                             ConstraintComparator comparator);    
}
