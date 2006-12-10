package org.drools.spi;

import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;

public interface DataProvider {

    public Declaration[] getRequiredDeclarations();

    public Iterator getResults(Tuple tuple,
                               WorkingMemory wm,
                               PropagationContext ctx);

}
