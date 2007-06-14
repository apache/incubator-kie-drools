package org.drools.spi;

import java.io.Serializable;
import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

public interface DataProvider extends Serializable {

    public Declaration[] getRequiredDeclarations();

    public Iterator getResults(Tuple tuple,
                               WorkingMemory wm,
                               PropagationContext ctx);

}
