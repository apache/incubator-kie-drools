package org.drools.spi;

import java.io.Serializable;
import java.util.Iterator;

import org.drools.rule.Declaration;
import org.drools.WorkingMemory;

public interface DataProvider
    extends
    Serializable,
    Cloneable {

    public Declaration[] getRequiredDeclarations();

    public Object createContext();

    public Iterator getResults(Tuple tuple,
                               WorkingMemory wm,
                               PropagationContext ctx,
                               Object providerContext);

    public DataProvider clone();

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved);

}
