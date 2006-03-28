package org.drools.common;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Rule;
import org.drools.spi.Activation;

public interface InternalWorkingMemoryActions extends WorkingMemory {
    public void modifyObject(FactHandle handle,
                             Object object,
                             Rule rule,
                             Activation activation) throws FactException;
    
    public void retractObject(FactHandle handle,
                              boolean removeLogical,
                              boolean updateEqualsMap,
                              Rule rule,
                              Activation activation) throws FactException;
    
    FactHandle assertObject(Object object,
                            boolean dynamic,
                            boolean logical,
                            Rule rule,
                            Activation activation) throws FactException;    
    
    public FactHandle assertLogicalObject(Object object,
                                          boolean dynamic) throws FactException;
}
