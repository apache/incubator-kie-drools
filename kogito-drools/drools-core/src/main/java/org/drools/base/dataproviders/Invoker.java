package org.drools.base.dataproviders;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public interface Invoker {
    public Object invoke(Tuple tuple,
                         WorkingMemory wm,
                         PropagationContext ctx);

    public Declaration[] getRequiredDeclarations();
}
